package net.rotgruengelb.forestal.particle;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.rotgruengelb.forestal.Forestal;

public class ForestalParticleTypes {

	public static DefaultParticleType SLEEPING_ZZZ = register("sleeping_zzz", false);

	private static DefaultParticleType register(String name, boolean alwaysSpawn) {
		return Registry.register(Registries.PARTICLE_TYPE, new Identifier("forestal", name), FabricParticleTypes.simple(alwaysSpawn));
	}

	public static void registerModParticleTypes() {
		Forestal.LOGGER.debug("Registering ModParticleTypes for " + Forestal.MOD_ID);
	}
}
