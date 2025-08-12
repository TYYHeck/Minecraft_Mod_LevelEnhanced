package com.gzh.levelenhanced.mixin;

import com.gzh.LevelEnhanced;
import com.gzh.levelenhanced.manager.LevelDataManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.SERVER)
@Mixin(PlayerEntity.class)
public class MiningSpeedMixin {
    @Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), cancellable = true)
    private void modifyMiningSpeed(BlockState block, CallbackInfoReturnable<Float> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        float originSpeed = cir.getReturnValue();
        float multiplier = LevelDataManager.get((ServerWorld) player.getWorld())
                    .getPlayerConfigData(player.getUuid())
                    .getMiningSpeedMultiplier();
        cir.setReturnValue(originSpeed * multiplier);
    }

    static {
        LevelEnhanced.LOGGER.info("LevelEnhanced -> MiningSpeedMixin initialized!");
    }
}