package net.rotgruengelb.forestal.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.rotgruengelb.forestal.Forestal;

public class ForestalEntities {

	public static final EntityType<GrizzlyBearEntity> GRIZZLY_BEAR = Registry.register(Registries.ENTITY_TYPE, new Identifier(Forestal.MOD_ID, "grizzly_bear"), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, GrizzlyBearEntity::new)
			.dimensions(EntityDimensions.fixed(1.5f, 1.5f)).build());

	public static void registerModEntities() {
		Forestal.LOGGER.debug("Registering ForestalEntities for " + Forestal.MOD_ID);
	}
}
