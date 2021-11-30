package de.chasenet.minecraft.silverfishremover;

import net.minecraft.block.BlockState;
import net.minecraft.block.SilverfishBlock;
import net.minecraft.entity.monster.SilverfishEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventListener {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onBreakBlock(final BlockEvent.BreakEvent breakEvent) {
        final BlockState state = breakEvent.getState();
        if (state.getBlock() instanceof SilverfishBlock) {
            breakEvent.setCanceled(true);
            breakEvent.getWorld().removeBlock(breakEvent.getPos(), false);
        }
    }

    @SubscribeEvent
    public static void onExplosion(final ExplosionEvent.Detonate detonateEvent) {
        List<BlockPos> positionsToRemove = detonateEvent.getAffectedBlocks().stream()
                .filter(Objects::nonNull)
                .filter(blockPos -> detonateEvent.getWorld().getBlockState(blockPos).getBlock() instanceof SilverfishBlock)
                .peek(blockPos -> detonateEvent.getWorld().removeBlock(blockPos, false)).collect(Collectors.toList());

        detonateEvent.getAffectedBlocks().removeAll(positionsToRemove);
    }

    @SubscribeEvent
    public static void checkSpawnEvent(final LivingSpawnEvent.CheckSpawn checkSpawnEvent) {
        if (checkSpawnEvent.getEntity() instanceof SilverfishEntity) {
            LOGGER.info("Denied Spawn");
            checkSpawnEvent.setResult(Event.Result.DENY);
        }
    }
}
