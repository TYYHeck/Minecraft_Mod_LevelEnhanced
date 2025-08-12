package com.gzh.levelenhanced.events;

import com.gzh.levelenhanced.config.Config;
import com.gzh.levelenhanced.config.PlayerConfigData;
import com.gzh.levelenhanced.manager.LevelDataManager;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

/**
 * Date 2025/8/9 下午9:17
 * Author:gzh
 * Description:
 */

public class Events {
    public static void initialize() {
        // 载入存档
        ServerPlayerEvents.JOIN.register((player) -> {
            LevelDataManager levelDataManager = LevelDataManager.get(player.getServerWorld());
            PlayerConfigData data = levelDataManager.getPlayerConfigData(player.getUuid());
            levelDataManager.syncData(player);
            data.updateLevelState(player);
        });

        // 方块破坏
        PlayerBlockBreakEvents.AFTER.register(((world, playerEntity, blockPos, blockState, blockEntity) -> {
            LevelDataManager levelDataManager = LevelDataManager.get((ServerWorld) world);
            PlayerConfigData data = levelDataManager.getPlayerConfigData(playerEntity.getUuid());
            data.addExperience((ServerPlayerEntity)playerEntity, Config.getConfigData().BLOCK_BREAK_EXP);
        }));
    }
}
