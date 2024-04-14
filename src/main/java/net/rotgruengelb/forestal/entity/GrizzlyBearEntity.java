package net.rotgruengelb.forestal.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class GrizzlyBearEntity extends AnimalEntity implements GeoEntity {

	private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

	public GrizzlyBearEntity(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
	}

	public static DefaultAttributeContainer.Builder createGrizzlyBearAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0)
				.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20.0)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
				.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0);
	}

	@Nullable
	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
		return ForestalEntities.GRIZZLY_BEAR.create(world);
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_POLAR_BEAR_STEP, 0.15f, 1.0f);
	}

	public boolean isBreedingItem(ItemStack stack) {
		return stack.isOf(Items.SWEET_BERRIES);
	}

	@Override
	public void initGoals() {
		super.initGoals();
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new EscapeDangerGoal(this, 1.4));
		this.goalSelector.add(2, new AnimalMateGoal(this, 1.1));
		this.goalSelector.add(3, new TemptGoal(this, 1.1, Ingredient.ofItems(Items.SWEET_BERRIES), false));
		this.goalSelector.add(4, new FollowParentGoal(this, 1.15));
		this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
		this.goalSelector.add(6, new LookAroundGoal(this));
	}

	@Override
	public SoundEvent getAmbientSound() {
		if (this.isBaby()) {
			return SoundEvents.ENTITY_POLAR_BEAR_AMBIENT_BABY;
		}
		return SoundEvents.ENTITY_POLAR_BEAR_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_POLAR_BEAR_HURT;
	}

	public boolean isAsleep() {
		return false;
	}

	@Override
	protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_POLAR_BEAR_DEATH; }

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
	}

	private PlayState predicate(AnimationState<GrizzlyBearEntity> animationState) {
		if (animationState.isMoving()) {
			animationState.getController().setAnimation(RawAnimation.begin()
					.then("animation" + ".grizzly_bear.walk", Animation.LoopType.LOOP));
			return PlayState.CONTINUE;
		}

		animationState.getController().setAnimation(RawAnimation.begin()
				.then("animation" + ".grizzly_bear.idle", Animation.LoopType.LOOP));
		return PlayState.CONTINUE;
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}
