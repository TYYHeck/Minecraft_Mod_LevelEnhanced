package com.gzh.levelenhanced.manager;

import com.gzh.levelenhanced.config.PlayerConfigData;
import com.gzh.levelenhanced.network.PacketHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Date 2025/8/9 下午9:19
 * Author:gzh
 * Description:
 */

public class LevelDataManager extends PersistentState {
    private final Map<UUID, PlayerConfigData> playerConfigData = new HashMap<>();

    public static LevelDataManager get(ServerWorld serverWorld) {
        PersistentStateManager manager = serverWorld.getPersistentStateManager();
        return manager.getOrCreate(new PersistentState.Type<>(
                LevelDataManager::new,
                (nbt, wrapperLookup) -> {
                    LevelDataManager dataManager = new LevelDataManager();
                    dataManager.readNbt(nbt);
                    return dataManager;
                },
                null
        ), "levelenhanced_data");
    }

    public PlayerConfigData getPlayerConfigData(UUID uuid) {
        return playerConfigData.computeIfAbsent(uuid, k -> new PlayerConfigData());
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList playerConfigDataList = new NbtList();
        for (Map.Entry<UUID, PlayerConfigData> entry : playerConfigData.entrySet()) {
            NbtCompound playerConfigDataTag = new NbtCompound();
            playerConfigDataTag.putUuid("uuid", entry.getKey());
            playerConfigDataTag.put("data", entry.getValue().writeNbt(new NbtCompound()));
            playerConfigDataList.add(playerConfigDataTag);
        }
        nbt.put("playerConfigData", playerConfigDataList);
        return nbt;
    }

    public void readNbt(NbtCompound nbt) {
        playerConfigData.clear();
        NbtList playerList = nbt.getList("playerConfigData", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < playerList.size(); i++) {
            NbtCompound playerConfigDataTag = playerList.getCompound(i);
            UUID uuid = playerConfigDataTag.getUuid("uuid");
            PlayerConfigData data = new PlayerConfigData();
            data.readNbt(playerConfigDataTag.getCompound("data"));
            playerConfigData.put(uuid, data);
        }
    }

    // 发送网络包
    public void syncData(ServerPlayerEntity player) {
        PlayerConfigData data = getPlayerConfigData(player.getUuid());
        ServerPlayNetworking.send(player, new PacketHandler.LevelUpdatePayload(data));
    }
}
