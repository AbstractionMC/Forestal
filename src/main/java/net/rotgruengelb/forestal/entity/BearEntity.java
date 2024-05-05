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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.rotgruengelb.forestal.sound.ForestalSoundEvents;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class BearEntity extends AnimalEntity {

	public static final SoundEvent SOUND_DEATH = ForestalSoundEvents.ENTITY_GRIZZLY_BEAR_DEATH;
	private static final TrackedData<Byte> BEAR_FLAGS = DataTracker.registerData(BearEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final int SLEEPING_FLAG = 1;
	public static SoundEvent SOUND_AMBIENT_BABY;
	public static SoundEvent SOUND_AMBIENT;
	public static SoundEvent SOUND_STEP;
	public static SoundEvent SOUND_HURT;
	public static TagKey<Item> FOOD_TAG;

	public BearEntity(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
		setStepHeight(1.0f);
	}

	public static DefaultAttributeContainer.Builder createBearAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0)
				.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20.0)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
				.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0);
	}

	public static boolean isValidBearSpawn(EntityType<? extends BearEntity> entityType, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		boolean bl = SpawnReason.isTrialSpawner(spawnReason) || AnimalEntity.isLightLevelValidForNaturalSpawn(world, pos);
		return (world.getBlockState(pos.down())
				.isIn(BlockTags.ANIMALS_SPAWNABLE_ON) || world.getBlockState(pos.down())
				.isIn(BlockTags.ICE)) && bl;
	}

	public boolean isSleeping() {
		return this.getBearFlag(SLEEPING_FLAG);
	}

	void setSleeping(boolean sleeping) {
		this.setBearFlag(SLEEPING_FLAG, sleeping);
	}

	private boolean getBearFlag(int bitmask) {
		return (this.dataTracker.get(BEAR_FLAGS) & bitmask) != 0;
	}

	private void setBearFlag(int mask, boolean value) {
		if (value) {
			this.dataTracker.set(BEAR_FLAGS, (byte) (this.dataTracker.get(BEAR_FLAGS) | mask));
		} else {
			this.dataTracker.set(BEAR_FLAGS, (byte) (this.dataTracker.get(BEAR_FLAGS) & ~mask));
		}

	}

	@Nullable
	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
		return (PassiveEntity) entity.getType().create(world);
	}

	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(BEAR_FLAGS, (byte) 0);
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SOUND_STEP, 0.15f, 1.0f);
	}

	public boolean isBreedingItem(ItemStack stack) {
		return stack.isIn(FOOD_TAG);
	}

	@Override
	protected SoundEvent getDeathSound() { return SOUND_DEATH; }

	@Override
	public void initGoals() {
		super.initGoals();
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new EscapeDangerGoal(this, 1.1));
		this.goalSelector.add(2, new AnimalMateGoal(this, 1.05));
		this.goalSelector.add(3, new TemptGoal(this, 0.9, Ingredient.fromTag(FOOD_TAG), false));
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
		if (this.canMoveVoluntarily() && this.isTouchingWater() || this.getWorld().isDay()) {
			this.setSleeping(false);
		}
	}

	@Override
	public void tickMovement() {
		if (this.isSleeping() || this.isImmobile()) {
			this.jumping = false;
			this.sidewaysSpeed = 0.0F;
			this.forwardSpeed = 0.0F;
			return;
		}
		super.tickMovement();
	}

	@Override
	public SoundEvent getAmbientSound() {
		if (this.isBaby()) {
			return SOUND_AMBIENT_BABY;
		}
		return SOUND_AMBIENT;
	}

	@Override
	public void playAmbientSound() {
		if (this.isSleeping()) { return; }
		super.playAmbientSound();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SOUND_HURT;
	}

	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("Sleeping", this.isSleeping());
	}

	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setSleeping(nbt.getBoolean("Sleeping"));
	}

	private class SleepGoal extends Goal {
		private static final int MAX_CALM_DOWN_TIME = toGoalTicks(280);
		private int timer;

		public SleepGoal() {
			super();
			this.timer = BearEntity.this.random.nextInt(MAX_CALM_DOWN_TIME);
			this.setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP));
		}

		protected boolean isAtFavoredLocation() {
			BlockPos blockPos = BlockPos.ofFloored(BearEntity.this.getX(), BearEntity.this.getBoundingBox().maxY, BearEntity.this.getZ());
			return !BearEntity.this.getWorld()
					.isSkyVisible(blockPos) && BearEntity.this.getPathfindingFavor(blockPos) >= 0.0f;
		}

		public boolean canStart() {
			if (BearEntity.this.sidewaysSpeed == 0.0F && BearEntity.this.upwardSpeed == 0.0F && BearEntity.this.forwardSpeed == 0.0F && BearEntity.this.getWorld()
					.isNight()) {
				return this.canNotSleep() || !BearEntity.this.isSleeping();
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
				return this.isAtFavoredLocation() && !BearEntity.this.inPowderSnow; // &&
			}
		}

		public void stop() {
			this.timer = BearEntity.this.random.nextInt(MAX_CALM_DOWN_TIME);
			BearEntity.this.setSleeping(false);
		}

		public void start() {
			BearEntity.this.setJumping(false);
			BearEntity.this.setSleeping(true);
			BearEntity.this.getNavigation().stop();
			BearEntity.this.getMoveControl()
					.moveTo(BearEntity.this.getX(), BearEntity.this.getY(), BearEntity.this.getZ(), 0.0);
		}
	}

	class FindSleepingPlaceGoal extends EscapeSunlightGoal {
		private int timer = toGoalTicks(100);

		public FindSleepingPlaceGoal(double speed) {
			super(BearEntity.this, speed);
		}

		public boolean canStart() {
			if (!BearEntity.this.isSleeping() && BearEntity.this.getWorld().isNight()) {
				if (BearEntity.this.getWorld().isSkyVisible(this.mob.getBlockPos())) {
					return this.targetShadedPos();
				} else if (this.timer > 0) {
					--this.timer;
					return false;
				} else {
					this.timer = 100;
					BlockPos blockPos = this.mob.getBlockPos();
					return BearEntity.this.getWorld()
							.isSkyVisible(blockPos) && !((ServerWorld) BearEntity.this.getWorld()).isNearOccupiedPointOfInterest(blockPos) && this.targetShadedPos();
				}
			} else {
				return false;
			}
		}

		public void start() {
			BearEntity.this.setSleeping(false);
			super.start();
		}
	}
}
