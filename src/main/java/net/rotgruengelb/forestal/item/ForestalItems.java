package net.rotgruengelb.forestal.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.rotgruengelb.forestal.Forestal;
import net.rotgruengelb.forestal.entity.ForestalEntities;
import net.rotgruengelb.forestal.sound.ForestalSoundEvents;

import static net.rotgruengelb.forestal.block.ForestalBlocks.GRIZZLY_PLUSHIE;

public class ForestalItems {
	public static final Item GRIZZLY_BEAR_SPAWN_EGG = registerItem("grizzly_bear_spawn_egg", new SpawnEggItem(ForestalEntities.GRIZZLY_BEAR, 0x966240, 0x553a28, new FabricItemSettings()));
	public static final Item GRIZZLY_BEAR_HAIR = registerItem("grizzly_bear_hair", new Item(new FabricItemSettings()));
	public static final Item MUSIC_DISC_REVERB = registerMusicDisc("reverb", 14, ForestalSoundEvents.MUSIC_DISC_REVERB, 79);
	public static final Item MUSIC_DISC_SUNRAYS = registerMusicDisc("sunrays", 15, ForestalSoundEvents.MUSIC_DISC_SUNRAYS, 240);

	private static Item registerItem(String name, Item item) {
		return Registry.register(Registries.ITEM, new Identifier(Forestal.MOD_ID, name), item);
	}

	private static Item registerMusicDisc(String name, int comparatorOutput, SoundEvent soundEvent, int lengthInSeconds) {
		return registerItem("music_disc_" + name, new MusicDiscItem(comparatorOutput, soundEvent, new FabricItemSettings().maxCount(1)
				.rarity(Rarity.RARE), lengthInSeconds));
	}

	public static void addItemsToItemGroup() {
		addToItemGroup(ItemGroups.SPAWN_EGGS, GRIZZLY_BEAR_SPAWN_EGG);
		addToItemGroup(ItemGroups.FUNCTIONAL, GRIZZLY_PLUSHIE.asItem());
		addToItemGroup(ItemGroups.INGREDIENTS, GRIZZLY_BEAR_HAIR);
		addToItemGroup(ItemGroups.TOOLS, MUSIC_DISC_REVERB);
		addToItemGroup(ItemGroups.TOOLS, MUSIC_DISC_SUNRAYS);
	}

	private static void addToItemGroup(RegistryKey<ItemGroup> group, Item item) {
		ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(item));
	}

	public static void registerModItems() {
		Forestal.LOGGER.debug("Registering ForestalItems for " + Forestal.MOD_ID);

		addItemsToItemGroup();
	}
}