package de.chasenet.minecraft.silverfishremover.mixins.blocks;

import de.chasenet.minecraft.silverfishremover.SilverfishRemover;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SilverfishBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {SilverfishBlock.class})
public abstract class SilverfishMixin extends AbstractBlock {
    private static final Logger LOGGER = LogManager.getLogger();

    public SilverfishMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "spawnAdditionalDrops", at = @At(value = "HEAD"), cancellable = true)
    public void spawnAdditionalDrops(BlockState state, ServerWorld worldIn, BlockPos pos, ItemStack stack, CallbackInfo ci) {
        LOGGER.info("Silverfish not spawned");
        ci.cancel();
    }
}
