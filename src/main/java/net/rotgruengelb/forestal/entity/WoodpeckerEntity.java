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
import net.rotgruengelb.forestal.sound.ForestalSoundEvents;
import net.rotgruengelb.forestal.util.ForestalTags;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class WoodpeckerEntity extends AnimalEntity implements GeoEntity {

	private static final TrackedData<Byte> WOODPECKER_FLAGS = DataTracker.registerData(WoodpeckerEntity.class, TrackedDataHandlerRegistry.BYTE);
	private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

	public WoodpeckerEntity(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
		setStepHeight(1.0f);
	}

	public static DefaultAttributeContainer.Builder createGrizzlyBearAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 8.0)
				.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20.0)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
				.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0);
	}

	public static boolean isValidGrizzlyBearSpawn(EntityType<GrizzlyBearEntity> grizzlyBearEntityEntityType, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		boolean bl = SpawnReason.isTrialSpawner(spawnReason) || AnimalEntity.isLightLevelValidForNaturalSpawn(world, pos);
		return (world.getBlockState(pos.down())
				.isIn(BlockTags.ANIMALS_SPAWNABLE_ON) || world.getBlockState(pos.down())
				.isIn(BlockTags.ICE)) && bl;
	}

	private boolean getWoodpecker(int bitmask) {
		return (this.dataTracker.get(WOODPECKER_FLAGS) & bitmask) != 0;
	}

	private void setFoxFlag(int mask, boolean value) {
		if (value) {
			this.dataTracker.set(WOODPECKER_FLAGS, (byte) (this.dataTracker.get(WOODPECKER_FLAGS) | mask));
		} else {
			this.dataTracker.set(WOODPECKER_FLAGS, (byte) (this.dataTracker.get(WOODPECKER_FLAGS) & ~mask));
		}
	}

	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(WOODPECKER_FLAGS, (byte) 0);
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
		this.goalSelector.add(6, new FollowParentGoal(this, 0.95));
		this.goalSelector.add(7, new WanderAroundFarGoal(this, 0.9));
		this.goalSelector.add(8, new WanderAroundGoal(this, 0.85));
		this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f, 0.017f));
		this.goalSelector.add(10, new LookAtEntityGoal(this, VillagerEntity.class, 4.0f, 0.007f));
		this.goalSelector.add(11, new LookAroundGoal(this));
	}

	@Override
	public void tickMovement() {
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
		super.playAmbientSound();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return ForestalSoundEvents.ENTITY_GRIZZLY_BEAR_HURT;
	}

	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		return super.interactMob(player, hand);
	}

	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
	}

	@Override
	protected SoundEvent getDeathSound() { return ForestalSoundEvents.ENTITY_GRIZZLY_BEAR_DEATH; }

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
	}

	private PlayState predicate(AnimationState<WoodpeckerEntity> animationState) {
		if (animationState.isMoving()) {
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
}