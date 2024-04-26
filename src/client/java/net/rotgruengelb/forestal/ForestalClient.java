package net.rotgruengelb.forestal;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.rotgruengelb.forestal.client.particle.ForestalParticleFactories;
import net.rotgruengelb.forestal.client.render.entity.GrizzlyBearEntityRenderer;
import net.rotgruengelb.forestal.entity.ForestalEntities;

public class ForestalClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {

		EntityRendererRegistry.register(ForestalEntities.GRIZZLY_BEAR, GrizzlyBearEntityRenderer::new);
		ForestalParticleFactories.registerModParticleFactories();
	}
}