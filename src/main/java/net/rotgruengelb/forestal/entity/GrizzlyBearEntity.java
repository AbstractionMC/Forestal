package net.rotgruengelb.forestal.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.rotgruengelb.forestal.item.ForestalItems;
import net.rotgruengelb.forestal.sound.ForestalSoundEvents;
import net.rotgruengelb.forestal.util.ForestalTags;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class GrizzlyBearEntity extends BearEntity implements GeoEntity {

	private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

	public GrizzlyBearEntity(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
		setStepHeight(1.0f);
		SOUND_AMBIENT_BABY = ForestalSoundEvents.ENTITY_GRIZZLY_BEAR_AMBIENT_BABY;
		SOUND_AMBIENT = ForestalSoundEvents.ENTITY_GRIZZLY_BEAR_AMBIENT;
		SOUND_STEP = ForestalSoundEvents.ENTITY_GRIZZLY_BEAR_STEP;
		SOUND_HURT = ForestalSoundEvents.ENTITY_GRIZZLY_BEAR_HURT;
		FOOD_TAG = ForestalTags.Items.GRIZZLY_BEAR_FOOD;
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
}
