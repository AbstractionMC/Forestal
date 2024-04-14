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
	public Identifier getTextureLocation(GrizzlyBearEntity animatable) {
		return new Identifier(Forestal.MOD_ID, "textures/entity/grizzly_bear.png");
	}

	@Override
	public void render(GrizzlyBearEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
		if (entity.isBaby()) {
			poseStack.scale(0.4F, 0.4F, 0.4F);
		}
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
}
