package net.rotgruengelb.forestal.client.render.entity;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.rotgruengelb.forestal.Forestal;
import net.rotgruengelb.forestal.client.render.entity.model.GrizzlyBearEntityModel;
import net.rotgruengelb.forestal.entity.GrizzlyBearEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GrizzlyBearEntityRenderer extends GeoEntityRenderer<GrizzlyBearEntity> {

	public GrizzlyBearEntityRenderer(EntityRendererFactory.Context renderManager) {
		super(renderManager, new GrizzlyBearEntityModel());
	}

	@Override
	public Identifier getTextureLocation(GrizzlyBearEntity grizzlyBear) {
		if (grizzlyBear.isSleeping()) {
			return new Identifier(Forestal.MOD_ID, "textures/entity/grizzly_bear/grizzly_bear_sleeping.png");
		}
		return new Identifier(Forestal.MOD_ID, "textures/entity/grizzly_bear/grizzly_bear_normal.png");
	}

	@Override
	public void render(GrizzlyBearEntity grizzlyBear, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
		if (grizzlyBear.isBaby()) {
			poseStack.scale(0.6F, 0.6F, 0.6F);
		}
		if (grizzlyBear.isSleeping()) {
			poseStack.translate(0, -0.5, 0);
		}
		super.render(grizzlyBear, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
}
