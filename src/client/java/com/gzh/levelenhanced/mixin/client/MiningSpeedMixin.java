package com.gzh.levelenhanced.mixin.client;

import com.gzh.LevelEnhanced;
import com.gzh.LevelEnhancedClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntity.class)
public class MiningSpeedMixin {
    @Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), cancellable = true)
    private void modifyMiningSpeed(BlockState block, CallbackInfoReturnable<Float> cir) {
        float originSpeed = cir.getReturnValue();
        float multiplier = LevelEnhancedClient.playerConfigData.getMiningSpeedMultiplier();
        cir.setReturnValue(originSpeed * multiplier);
    }

    static {
        LevelEnhanced.LOGGER.info("LevelEnhanced -> MiningSpeedMixin initialized!");
    }
}