package net.rotgruengelb.forestal.client.particle;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.particle.DefaultParticleType;
import net.rotgruengelb.forestal.particle.ForestalParticleTypes;

public class ForestalParticleFactories {

	public static void registerModParticleFactories() {
		registerSleepingParticle(ForestalParticleTypes.SLEEPING_ZZZ);
	}

	public static void registerSleepingParticle(DefaultParticleType particleType) {
		ParticleFactoryRegistry.getInstance()
				.register(particleType, provider -> (parameters, world, x, y, z, velocityX, velocityY, velocityZ) -> {
					SleepingParticle.Factory factory = new SleepingParticle.Factory(provider);
					return factory.createParticle(particleType, world, x, y, z, velocityX, velocityY, velocityZ);
				});
	}
}
