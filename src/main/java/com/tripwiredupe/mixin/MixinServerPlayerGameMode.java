package com.tripwiredupe.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TripWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Restores MC-129055 for string farms.
 *
 * <p>In vanilla 1.21.1, shearing a tripwire that is attached to a tripwire hook disarms it
 * and then still destroys it. This mixin intercepts {@code ServerPlayerGameMode.destroyBlock}
 * at its head: when the player shears an attached, non-disarmed tripwire, it instead only sets
 * {@code disarmed=true} (with a neighbor update) and cancels the break, leaving the tripwire in
 * place so a string farm can use it.</p>
 *
 * <p>Tripwires that are not attached (or are already disarmed) are left untouched, so shears
 * still break them normally.</p>
 */
@Mixin(ServerPlayerGameMode.class)
public class MixinServerPlayerGameMode {

    @Shadow
    private ServerLevel level;

    @Shadow
    private ServerPlayer player;

    @Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
    private void tripwiredupe$disarmInsteadOfBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockState state = this.level.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof TripWireBlock) {
            ItemStack item = this.player.getMainHandItem();
            if (!item.isEmpty() && item.is(Items.SHEARS)) {
                boolean disarmed = (Boolean) state.getValue(TripWireBlock.DISARMED);
                boolean attached = (Boolean) state.getValue(TripWireBlock.ATTACHED);
                if (!disarmed && attached) {
                    BlockState disarmedState = (BlockState) state.setValue(TripWireBlock.DISARMED, true);
                    this.level.setBlock(pos, disarmedState, 2);
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
