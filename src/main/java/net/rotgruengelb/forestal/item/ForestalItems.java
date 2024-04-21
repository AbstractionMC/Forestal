package net.rotgruengelb.forestal.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.rotgruengelb.forestal.Forestal;
import net.rotgruengelb.forestal.entity.ForestalEntities;

import static net.rotgruengelb.forestal.block.ForestalBlocks.GRIZZLY_PLUSHIE;

public class ForestalItems {
	public static final Item GRIZZLY_BEAR_SPAWN_EGG = registerItem("grizzly_bear_spawn_egg", new SpawnEggItem(ForestalEntities.GRIZZLY_BEAR, 0x966240, 0x553a28, new FabricItemSettings()));
	public static final Item GRIZZLY_BEAR_HAIR = registerItem("grizzly_bear_hair", new Item(new FabricItemSettings()));

	private static Item registerItem(String name, Item item) {
		return Registry.register(Registries.ITEM, new Identifier(Forestal.MOD_ID, name), item);
	}

	public static void addItemsToItemGroup() {
		addToItemGroup(ItemGroups.SPAWN_EGGS, GRIZZLY_BEAR_SPAWN_EGG);
		addToItemGroup(ItemGroups.FUNCTIONAL, GRIZZLY_PLUSHIE.asItem());
	}

	private static void addToItemGroup(RegistryKey<ItemGroup> group, Item item) {
		ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(item));
	}

	public static void registerModItems() {
		Forestal.LOGGER.debug("Registering ForestalItems for " + Forestal.MOD_ID);

		addItemsToItemGroup();
	}
}