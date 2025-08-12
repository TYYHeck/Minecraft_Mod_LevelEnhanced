package com.gzh.levelenhanced.mixin;

import com.gzh.levelenhanced.manager.LevelDataManager;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Date 2025/8/10 上午1:42
 * Author:gzh
 * Description: 饥饿修改稿
 */

@Mixin(HungerManager.class)
public class HungerMixin {
    @ModifyVariable(
            method = "update",
            at = @At("LOAD"),
            ordinal = 0
    )
    private float modifyThreshold(float threshold, PlayerEntity player) {
        System.out.println(threshold);
        // 仅在服务端处理数据
        if (!(player instanceof ServerPlayerEntity) || !(player.getWorld() instanceof ServerWorld)) {
            return threshold;
        }
        return threshold * LevelDataManager.get((ServerWorld) player.getWorld()).getPlayerConfigData(player.getUuid()).getHungerResistMultiplier();
    }
}
