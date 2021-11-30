package de.chasenet.minecraft.silverfishremover;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SilverfishBlock;
import net.minecraft.entity.monster.SilverfishEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("silverfish-remover")
public class SilverfishRemover {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public SilverfishRemover() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class TickEventListener {

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
}
