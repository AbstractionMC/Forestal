package net.rotgruengelb.forestal.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.rotgruengelb.forestal.Forestal;

public class ForestalBlocks {

	public static final Block GRIZZLY_PLUSHIE = registerBlock("grizzly_plushie",
			new PlushieBlock(FabricBlockSettings.create()
			.nonOpaque()));

	private static Block registerBlockNoItem(String name, Block block) {
		return Registry.register(Registries.BLOCK, new Identifier(Forestal.MOD_ID, name), block);
	}

	private static Block registerBlock(String name, Block block) {
		registerBlockItem(name, new BlockItem(block, new FabricItemSettings()));
		return Registry.register(Registries.BLOCK, new Identifier(Forestal.MOD_ID, name), block);
	}

	private static BlockItem registerBlockItem(String name, BlockItem blockItem) {
		return Registry.register(Registries.ITEM, new Identifier(Forestal.MOD_ID, name), blockItem);
	}

	public static void registerModBlocks() {
		Forestal.LOGGER.debug("Registering ForestalBlocks for " + Forestal.MOD_ID);
	}
}
