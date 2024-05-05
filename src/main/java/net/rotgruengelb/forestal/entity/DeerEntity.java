package net.rotgruengelb.forestal.entity;

import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.rotgruengelb.forestal.sound.ForestalSoundEvents;
import net.rotgruengelb.forestal.util.ForestalTags;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.*;
import java.util.function.Predicate;

public class DeerEntity extends AnimalEntity implements GeoEntity {

	private static final TrackedData<Byte> DEER_FLAGS = DataTracker.registerData(DeerEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final int SITTING_FLAG = 1;
	private static final Predicate<Entity> NOTICEABLE_PLAYER_FILTER = entity -> !entity.isSneaky() && EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(entity);
	private static final TrackedData<Optional<UUID>> TRUSTED = DataTracker.registerData(DeerEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
	private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

	public DeerEntity(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
		setStepHeight(1.0f);
	}

	public static DefaultAttributeContainer.Builder createDeerAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
				.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20.0)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
				.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0);
	}

	public static boolean isValidDeerSpawn(EntityType<DeerEntity> deerEntityEntityType, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		boolean bl = SpawnReason.isTrialSpawner(spawnReason) || AnimalEntity.isLightLevelValidForNaturalSpawn(world, pos);
		return world.getBlockState(pos.down()).isIn(BlockTags.ANIMALS_SPAWNABLE_ON) && bl;
	}

	public boolean isSitting() {
		return this.getDeerFlag(SITTING_FLAG);
	}

	void setSitting(boolean sitting) {
		this.setDeerFlag(SITTING_FLAG, sitting);
	}

	private boolean getDeerFlag(int bitmask) {
		return (this.dataTracker.get(DEER_FLAGS) & bitmask) != 0;
	}

	private void setDeerFlag(int mask, boolean value) {
		if (value) {
			this.dataTracker.set(DEER_FLAGS, (byte) (this.dataTracker.get(DEER_FLAGS) | mask));
		} else {
			this.dataTracker.set(DEER_FLAGS, (byte) (this.dataTracker.get(DEER_FLAGS) & ~mask));
		}

	}

	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(TRUSTED, Optional.empty());
		this.dataTracker.startTracking(DEER_FLAGS, (byte) 0);
	}

	@Nullable
	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
//		return ForestalEntities.DEER.create(world);
		return null;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(ForestalSoundEvents.ENTITY_GRIZZLY_BEAR_STEP, 0.15f, 1.0f);
	}

	public boolean isBreedingItem(ItemStack stack) {
		return stack.isIn(ForestalTags.Items.GRIZZLY_BEAR_FOOD);
	}

	@Override
	public void initGoals() {
		super.initGoals();
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new AnimalMateGoal(this, 1.0));
		this.goalSelector.add(2, new FleeEntityGoal<>(this, PlayerEntity.class, 16.0f, 1.6, 1.4, entity -> NOTICEABLE_PLAYER_FILTER.test((Entity) entity) && !this.canTrust(entity.getUuid())));
		this.goalSelector.add(2, new FleeEntityGoal<>(this, WolfEntity.class, 8.0f, 1.6, 1.4, entity -> !((WolfEntity) entity).isTamed()));
		this.goalSelector.add(2, new FleeEntityGoal<>(this, PolarBearEntity.class, 8.0f, 1.6, 1.4));
		this.goalSelector.add(3, new FollowParentGoal(this, 1.15));
		this.goalSelector.add(4, new WanderAroundFarGoal(this, 1.0));
		this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 24.0f));
		this.goalSelector.add(6, new SitDownAndLookAroundGoal());
	}

	void addTrustedUuid(@Nullable UUID uuid) {
		this.dataTracker.set(TRUSTED, Optional.ofNullable(uuid));
	}

	boolean canTrust(UUID uuid) {
		return this.getTrustedUuids().contains(uuid);
	}

	List<UUID> getTrustedUuids() {
		ArrayList<UUID> list = Lists.newArrayList();
		list.add(this.dataTracker.get(TRUSTED).orElse(null));
		return list;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.canMoveVoluntarily() && this.isTouchingWater()) {
				this.setSitting(false);
		}
	}

	@Override
	public void tickMovement() {
		if (this.isSitting() || this.isImmobile()) {
			this.jumping = false;
			this.sidewaysSpeed = 0.0F;
			this.forwardSpeed = 0.0F;
		} else {
			super.tickMovement();
		}
	}

	@Override
	public SoundEvent getAmbientSound() {
		if (this.isBaby()) {
			return ForestalSoundEvents.ENTITY_GRIZZLY_BEAR_AMBIENT_BABY;
		}
		return ForestalSoundEvents.ENTITY_GRIZZLY_BEAR_AMBIENT;
	}

	@Override
	public void playAmbientSound() {
		if (this.isSitting()) { return; }
		super.playAmbientSound();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return ForestalSoundEvents.ENTITY_GRIZZLY_BEAR_HURT;
	}

	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		List<UUID> trustedUuids = this.getTrustedUuids();
		NbtList trustedUuidsNbtList = new NbtList();
		for (UUID uUID : trustedUuids) {
			if (uUID == null) continue;
			trustedUuidsNbtList.add(NbtHelper.fromUuid(uUID));
		}
		nbt.put("Trusted", trustedUuidsNbtList);
		nbt.putBoolean("Sitting", this.isSitting());
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		return super.interactMob(player, hand);
	}

	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		NbtList trustedUuidsNbtList = nbt.getList("Trusted", NbtElement.INT_ARRAY_TYPE);
		for (NbtElement uuidNbtElement : trustedUuidsNbtList) {
			this.addTrustedUuid(NbtHelper.toUuid(uuidNbtElement));
		}
		this.setSitting(nbt.getBoolean("Sitting"));
	}

	@Override
	protected SoundEvent getDeathSound() { return ForestalSoundEvents.ENTITY_GRIZZLY_BEAR_DEATH; }

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
	}

	private PlayState predicate(AnimationState<DeerEntity> animationState) {
//		if (animationState.getAnimatable().isSitting()) {
//			animationState.getController().setAnimation(RawAnimation.begin()
//					.then("animation.deer.sit", Animation.LoopType.LOOP));
//		} else
			if (animationState.isMoving() && !this.isSitting()) {
			animationState.getController().setAnimation(RawAnimation.begin()
					.then("animation.deer.walk", Animation.LoopType.LOOP));
		} else {
			animationState.getController().setAnimation(RawAnimation.begin()
					.then("animation.deer.idle", Animation.LoopType.LOOP));
		}

		return PlayState.CONTINUE;
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

	private class SitDownAndLookAroundGoal extends CalmDownGoal {
		private double lookX;
		private double lookZ;
		private int timer;
		private int counter;

		public SitDownAndLookAroundGoal() {
			super();
			this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
		}

		public boolean canStart() {
			return DeerEntity.this.getAttacker() == null && DeerEntity.this.getRandom()
					.nextFloat() < 0.02F && DeerEntity.this.getTarget() == null && DeerEntity.this.getNavigation()
					.isIdle() && !this.canCalmDown();
		}

		public boolean shouldContinue() {
			return this.counter > 0;
		}

		public void start() {
			this.chooseNewAngle();
			this.counter = 2 + DeerEntity.this.getRandom().nextInt(3);
			DeerEntity.this.setSitting(true);
			DeerEntity.this.getNavigation().stop();
		}

		public void stop() {
			DeerEntity.this.setSitting(false);
		}

		public void tick() {
			--this.timer;
			if (this.timer <= 0) {
				--this.counter;
				this.chooseNewAngle();
			}

			DeerEntity.this.getLookControl().lookAt(DeerEntity.this.getX() + this.lookX, DeerEntity.this.getEyeY(), DeerEntity.this.getZ() + this.lookZ, (float) DeerEntity.this.getMaxHeadRotation(), (float) DeerEntity.this.getMaxLookPitchChange());
		}

		private void chooseNewAngle() {
			double d = 6.283185307179586 * DeerEntity.this.getRandom().nextDouble();
			this.lookX = Math.cos(d);
			this.lookZ = Math.sin(d);
			this.timer = this.getTickCount(80 + DeerEntity.this.getRandom().nextInt(20));
		}
	}

	public class WorriableEntityFilter implements Predicate<LivingEntity> {
		@Override
		public boolean test(LivingEntity livingEntity) {
			if (livingEntity instanceof DeerEntity) {
				return false;
			}
			if (livingEntity instanceof HostileEntity) {
				return true;
			}
			if (livingEntity instanceof TameableEntity) {
				return !((TameableEntity) livingEntity).isTamed();
			}
			if (livingEntity instanceof PlayerEntity && (livingEntity.isSpectator() || ((PlayerEntity) livingEntity).isCreative())) {
				return false;
			}
			if (DeerEntity.this.canTrust(livingEntity.getUuid())) {
				return false;
			}
			return !livingEntity.isSneaky();
		}

	}

	abstract class CalmDownGoal extends Goal {
		private final TargetPredicate WORRIABLE_ENTITY_PREDICATE = TargetPredicate.createAttackable()
				.setBaseMaxDistance(12.0).ignoreVisibility()
				.setPredicate(DeerEntity.this.new WorriableEntityFilter());

		protected boolean isAtFavoredLocation() {
			BlockPos blockPos = BlockPos.ofFloored(DeerEntity.this.getX(), DeerEntity.this.getBoundingBox().maxY, DeerEntity.this.getZ());
			return DeerEntity.this.getPathfindingFavor(blockPos) >= 0.0F;
		}

		protected boolean canCalmDown() {
			return !DeerEntity.this.getWorld()
					.getTargets(LivingEntity.class, this.WORRIABLE_ENTITY_PREDICATE, DeerEntity.this, DeerEntity.this.getBoundingBox()
							.expand(12.0, 6.0, 12.0)).isEmpty();
		}
	}
}
