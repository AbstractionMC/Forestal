package net.rotgruengelb.forestal.client.render.entity.model;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.rotgruengelb.forestal.Forestal;
import net.rotgruengelb.forestal.entity.DeerEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class DeerEntityModel extends GeoModel<DeerEntity> {
	@Override
	public Identifier getModelResource(DeerEntity animatable) {
		return new Identifier(Forestal.MOD_ID, "geo/deer.geo.json");
	}

	@Override
	public Identifier getTextureResource(DeerEntity grizzlyBear) {
		return new Identifier(Forestal.MOD_ID, "textures/entity/deer.png");
	}

	@Override
	public Identifier getAnimationResource(DeerEntity animatable) {
		return new Identifier(Forestal.MOD_ID, "animations/deer.animation.json");
	}

	@Override
	public void setCustomAnimations(DeerEntity animatable, long instanceId, AnimationState<DeerEntity> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("head");

		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
			head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
		}
	}
}
