package net.rotgruengelb.forestal.util;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.rotgruengelb.forestal.Forestal;

public class ForestalTags {
	public static class Biomes {
		private static TagKey<Biome> of(String id) {
			return TagKey.of(RegistryKeys.BIOME, new Identifier(Forestal.MOD_ID, id));
		}

		public static class SPAWNS_GRIZZLY_BEAR {
			public static final TagKey<Biome> COMMON = of("spawns_entity/grizzly_bear/common");
			public static final TagKey<Biome> RARE = of("spawns_entity/grizzly_bear/rare");
			public static final TagKey<Biome> ALL = of("spawns_entity/grizzly_bear");
		}
	}

	public static class Items {

		public static final TagKey<Item> GRIZZLY_BEAR_BRUSH = of("grizzly_bear_brush");
		public static final TagKey<Item> GRIZZLY_BEAR_FOOD = of("grizzly_bear_food");

		private static TagKey<Item> of(String id) {
			return TagKey.of(RegistryKeys.ITEM, new Identifier(Forestal.MOD_ID, id));
		}
	}
}
