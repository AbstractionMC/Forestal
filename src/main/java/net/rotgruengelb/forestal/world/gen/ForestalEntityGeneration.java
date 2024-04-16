package net.rotgruengelb.forestal.world.gen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.world.Heightmap;
import net.rotgruengelb.forestal.entity.ForestalEntities;
import net.rotgruengelb.forestal.entity.GrizzlyBearEntity;
import net.rotgruengelb.forestal.util.ForestalTags;

public class ForestalEntityGeneration {
	public static void addSpawns() {
		BiomeModifications.addSpawn(BiomeSelectors.tag(ForestalTags.Biomes.SPAWNS_GRIZZLY_BEAR.COMMON), SpawnGroup.CREATURE, ForestalEntities.GRIZZLY_BEAR, 30, 1, 2);

		BiomeModifications.addSpawn(BiomeSelectors.tag(ForestalTags.Biomes.SPAWNS_GRIZZLY_BEAR.RARE), SpawnGroup.CREATURE, ForestalEntities.GRIZZLY_BEAR, 7, 1, 1);

		SpawnRestriction.register(ForestalEntities.GRIZZLY_BEAR, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GrizzlyBearEntity::isValidGrizzlyBearSpawn);
	}
}
