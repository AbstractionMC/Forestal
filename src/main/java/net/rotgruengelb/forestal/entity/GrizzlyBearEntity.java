package net.rotgruengelb.forestal.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.rotgruengelb.forestal.item.ForestalItems;
import net.rotgruengelb.forestal.sound.ForestalSoundEvents;
import net.rotgruengelb.forestal.util.ForestalTags;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.EnumSet;

public class GrizzlyBearEntity extends AnimalEntity implements GeoEntity {

	private static final TrackedData<Byte> GRIZZLY_BEAR_FLAGS = DataTracker.registerData(GrizzlyBearEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final int SLEEPING_FLAG = 1;
	private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

	public GrizzlyBearEntity(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
		setStepHeight(1.0f);
	}

	public static DefaultAttributeContainer.Builder createGrizzlyBearAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0)
				.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20.0)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
				.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0);
	}

	public static boolean isValidGrizzlyBearSpawn(EntityType<GrizzlyBearEntity> grizzlyBearEntityEntityType, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		return WoodpeckerEntity.isValidGrizzlyBearSpawn(null, world, spawnReason, pos, null);
	}

	public boolean isSleeping() {
		return this.getGrizzlyBearFlag(SLEEPING_FLAG);
	}

	void setSleeping(boolean sleeping) {
		this.setFoxFlag(SLEEPING_FLAG, sleeping);
	}

	private boolean getGrizzlyBearFlag(int bitmask) {
		return (this.dataTracker.get(GRIZZLY_BEAR_FLAGS) & bitmask) != 0;
	}

	private void setFoxFlag(int mask, boolean value) {
		if (value) {
			this.dataTracker.set(GRIZZLY_BEAR_FLAGS, (byte) (this.dataTracker.get(GRIZZLY_BEAR_FLAGS) | mask));
		} else {
			this.dataTracker.set(GRIZZLY_BEAR_FLAGS, (byte) (this.dataTracker.get(GRIZZLY_BEAR_FLAGS) & ~mask));
		}

	}

	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(GRIZZLY_BEAR_FLAGS, (byte) 0);
	}

	@Nullable
	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
		return ForestalEntities.GRIZZLY_BEAR.create(world);
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
		this.goalSelector.add(1, new EscapeDangerGoal(this, 1.1));
		this.goalSelector.add(2, new AnimalMateGoal(this, 1.05));
		this.goalSelector.add(3, new TemptGoal(this, 0.9, Ingredient.fromTag(ForestalTags.Items.GRIZZLY_BEAR_FOOD), false));
		this.goalSelector.add(4, new FindSleepingPlaceGoal(0.85));
		this.goalSelector.add(5, new SleepGoal());
		this.goalSelector.add(6, new FollowParentGoal(this, 0.95));
		this.goalSelector.add(7, new WanderAroundFarGoal(this, 0.9));
		this.goalSelector.add(8, new WanderAroundGoal(this, 0.85));
		this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f, 0.017f));
		this.goalSelector.add(10, new LookAtEntityGoal(this, VillagerEntity.class, 4.0f, 0.007f));
		this.goalSelector.add(11, new LookAroundGoal(this));
	}

	@Override
	public void tick() {
		super.tick();
		if (this.canMoveVoluntarily() && this.isTouchingWater()) {
			this.setSleeping(false);
		}
	}

	@Override
	public void tickMovement() {
		if (this.isSleeping() || this.isImmobile()) {
			this.jumping = false;
			this.sidewaysSpeed = 0.0F;
			this.forwardSpeed = 0.0F;
		}

		super.tickMovement();
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
		if (this.isSleeping()) { return; }
		super.playAmbientSound();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return ForestalSoundEvents.ENTITY_GRIZZLY_BEAR_HURT;
	}

	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("Sleeping", this.isSleeping());
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (!this.isBaby() && itemStack.isIn(ForestalTags.Items.GRIZZLY_BEAR_BRUSH)) {
			// TODO: SoundEvent and LootTable
			if (!this.getWorld().isClient) {
				if (this.random.nextInt(3) == 0) {
					this.dropStack(new ItemStack(ForestalItems.GRIZZLY_BEAR_HAIR, this.random.nextInt(3)), 1.4f);
				}
				itemStack.damage(8, player, (playerEntity) -> playerEntity.sendToolBreakStatus(hand));
			}
			return ActionResult.success(this.getWorld().isClient);
		}
		return super.interactMob(player, hand);
	}

	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setSleeping(nbt.getBoolean("Sleeping"));
	}

	@Override
	protected SoundEvent getDeathSound() { return ForestalSoundEvents.ENTITY_GRIZZLY_BEAR_DEATH; }

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
	}

	private PlayState predicate(AnimationState<GrizzlyBearEntity> animationState) {
		if (animationState.getAnimatable().isSleeping()) {
			animationState.getController().setAnimation(RawAnimation.begin()
					.then("animation.grizzly_bear.sleep", Animation.LoopType.LOOP));
		} else if (animationState.isMoving() && !this.isSleeping()) {
			animationState.getController().setAnimation(RawAnimation.begin()
					.then("animation.grizzly_bear.walk", Animation.LoopType.LOOP));
		} else {
			animationState.getController().setAnimation(RawAnimation.begin()
					.then("animation.grizzly_bear.idle", Animation.LoopType.LOOP));
		}

		return PlayState.CONTINUE;
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

	private class SleepGoal extends Goal {
		private static final int MAX_CALM_DOWN_TIME = toGoalTicks(280);
		private int timer;

		public SleepGoal() {
			super();
			this.timer = GrizzlyBearEntity.this.random.nextInt(MAX_CALM_DOWN_TIME);
			this.setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP));
		}

		protected boolean isAtFavoredLocation() {
			BlockPos blockPos = BlockPos.ofFloored(GrizzlyBearEntity.this.getX(), GrizzlyBearEntity.this.getBoundingBox().maxY, GrizzlyBearEntity.this.getZ());
			return !GrizzlyBearEntity.this.getWorld()
					.isSkyVisible(blockPos) && GrizzlyBearEntity.this.getPathfindingFavor(blockPos) >= 0.0f;
		}

		public boolean canStart() {
			if (GrizzlyBearEntity.this.sidewaysSpeed == 0.0F && GrizzlyBearEntity.this.upwardSpeed == 0.0F && GrizzlyBearEntity.this.forwardSpeed == 0.0F && GrizzlyBearEntity.this.getWorld()
					.isNight()) {
				return this.canNotSleep() || !GrizzlyBearEntity.this.isSleeping();
			} else {
				return false;
			}
		}

		public boolean shouldContinue() {
			return this.canNotSleep();
		}

		private boolean canNotSleep() {
			if (this.timer > 0) {
				--this.timer;
				return false;
			} else {
				return this.isAtFavoredLocation() && !GrizzlyBearEntity.this.inPowderSnow;
			}
		}

		public void stop() {
			this.timer = GrizzlyBearEntity.this.random.nextInt(MAX_CALM_DOWN_TIME);
			GrizzlyBearEntity.this.setSleeping(false);
		}

		public void start() {
			GrizzlyBearEntity.this.setJumping(false);
			GrizzlyBearEntity.this.setSleeping(true);
			GrizzlyBearEntity.this.getNavigation().stop();
			GrizzlyBearEntity.this.getMoveControl()
					.moveTo(GrizzlyBearEntity.this.getX(), GrizzlyBearEntity.this.getY(), GrizzlyBearEntity.this.getZ(), 0.0);
		}
	}

	class FindSleepingPlaceGoal extends EscapeSunlightGoal {
		private int timer = toGoalTicks(100);

		public FindSleepingPlaceGoal(double speed) {
			super(GrizzlyBearEntity.this, speed);
		}

		public boolean canStart() {
			if (!GrizzlyBearEntity.this.isSleeping() && GrizzlyBearEntity.this.getWorld()
					.isNight()) {
				if (GrizzlyBearEntity.this.getWorld().isSkyVisible(this.mob.getBlockPos())) {
					return this.targetShadedPos();
				} else if (this.timer > 0) {
					--this.timer;
					return false;
				} else {
					this.timer = 100;
					BlockPos blockPos = this.mob.getBlockPos();
					return GrizzlyBearEntity.this.getWorld()
							.isSkyVisible(blockPos) && !((ServerWorld) GrizzlyBearEntity.this.getWorld()).isNearOccupiedPointOfInterest(blockPos) && this.targetShadedPos();
				}
			} else {
				return false;
			}
		}

		public void start() {
			GrizzlyBearEntity.this.setSleeping(false);
			super.start();
		}
	}
}
