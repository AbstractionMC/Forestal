package net.rotgruengelb.forestal.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.CherryLeavesBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.client.util.ParticleUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class PuffedDandelionBlock extends FlowerBlock {

	public PuffedDandelionBlock(FabricBlockSettings settings) {
		super(StatusEffects.SATURATION, 7, settings);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		super.randomDisplayTick(state, world, pos, random);
		if (random.nextInt(10) != 0) {
			return;
		}
		BlockPos blockPos = pos.down();
		BlockState blockState = world.getBlockState(blockPos);
		if (CherryLeavesBlock.isFaceFullSquare(blockState.getCollisionShape(world, blockPos), Direction.UP)) {
			return;
		}
		ParticleUtil.spawnParticle(world, pos, random, ParticleTypes.CHERRY_LEAVES);
	}
}
