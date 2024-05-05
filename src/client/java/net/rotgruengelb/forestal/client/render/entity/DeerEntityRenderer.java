package net.rotgruengelb.forestal.client.render.entity;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.rotgruengelb.forestal.Forestal;
import net.rotgruengelb.forestal.client.render.entity.model.DeerEntityModel;
import net.rotgruengelb.forestal.entity.DeerEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DeerEntityRenderer extends GeoEntityRenderer<DeerEntity> {

	public DeerEntityRenderer(EntityRendererFactory.Context renderManager) {
		super(renderManager, new DeerEntityModel());
	}

	@Override
	public Identifier getTextureLocation(DeerEntity grizzlyBear) {
		return new Identifier(Forestal.MOD_ID, "textures/entity/deer.png");
	}

	@Override
	public void render(DeerEntity deerEntity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
		if (deerEntity.isBaby()) {
			poseStack.scale(0.6F, 0.6F, 0.6F);
		}
		if (deerEntity.isSleeping()) {
			poseStack.translate(0, -0.5, 0);
		}
		super.render(deerEntity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
}
