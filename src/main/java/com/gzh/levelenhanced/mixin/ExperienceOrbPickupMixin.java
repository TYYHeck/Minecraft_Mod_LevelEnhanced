package com.gzh.levelenhanced.mixin;

import com.gzh.LevelEnhanced;
import com.gzh.levelenhanced.config.Config;
import com.gzh.levelenhanced.config.PlayerConfigData;
import com.gzh.levelenhanced.manager.LevelDataManager;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Date 2025/8/9 下午11:51
 * Author:gzh
 * Description: 经验球拾取
 */

@Mixin(ExperienceOrbEntity.class)
public class ExperienceOrbPickupMixin {
    @Inject(method = "onPlayerCollision", at = @At("HEAD"))
    public void onPlayerCollision(PlayerEntity player, CallbackInfo ci) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            ExperienceOrbEntity experienceOrbEntity = (ExperienceOrbEntity) (Object) this;
            int expAmount = experienceOrbEntity.getExperienceAmount();

            if (expAmount > 0) {
                LevelDataManager levelDataManager = LevelDataManager.get(serverPlayer.getServerWorld());
                PlayerConfigData data = levelDataManager.getPlayerConfigData(player.getUuid());
                data.addExperience(serverPlayer, expAmount * Config.getConfigData().EXPERIENCE_EXP);
            }
        }
    }


    static {
        LevelEnhanced.LOGGER.info("LevelEnhanced -> ExperienceOrbPickupMixin initialized!");
    }
}
