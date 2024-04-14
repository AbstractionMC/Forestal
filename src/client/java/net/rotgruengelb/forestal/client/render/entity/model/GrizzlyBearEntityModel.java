package net.rotgruengelb.forestal.client.render.entity.model;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.rotgruengelb.forestal.Forestal;
import net.rotgruengelb.forestal.entity.GrizzlyBearEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class GrizzlyBearEntityModel extends GeoModel<GrizzlyBearEntity> {
	@Override
	public Identifier getModelResource(GrizzlyBearEntity animatable) {
		return new Identifier(Forestal.MOD_ID, "geo/grizzly_bear.geo.json");
	}

	@Override
	public Identifier getTextureResource(GrizzlyBearEntity grizzlyBear) {
		if (grizzlyBear.isAsleep()) {
			return new Identifier(Forestal.MOD_ID, "textures/entity/grizzly_bear/grizzly_bear_sleeping.png");
		}
		return new Identifier(Forestal.MOD_ID, "textures/entity/grizzly_bear/grizzly_bear_normal.png");
	}

	@Override
	public Identifier getAnimationResource(GrizzlyBearEntity animatable) {
		return new Identifier(Forestal.MOD_ID, "animations/grizzly_bear.animation.json");
	}

	@Override
	public void setCustomAnimations(GrizzlyBearEntity animatable, long instanceId, AnimationState<GrizzlyBearEntity> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("head");

		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
			head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
		}
	}
}
