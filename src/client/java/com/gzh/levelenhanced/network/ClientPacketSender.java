package com.gzh.levelenhanced.network;

import com.gzh.LevelEnhancedClient;
import com.gzh.levelenhanced.gui.StateScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;


/**
 * Date 2025/8/10 下午1:22
 * Author:gzh
 * Description:
 */

public class ClientPacketSender {

    // 注册客户端网络处理器
    public static void initialize() {
        ClientPlayNetworking.registerGlobalReceiver(PacketHandler.LevelUpdatePayload.ID, (payload, context) -> {
            LevelEnhancedClient.playerConfigData = payload.data();
            // 更新客户端玩家状态
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                client.execute(() -> {
                    // 更新客户端显示的生命值
                    client.player.setHealth(Math.min(client.player.getHealth(),
                            LevelEnhancedClient.playerConfigData.getMaxHealth()));
                    // 如果当前屏幕是状态面板，刷新它
                    if (client.currentScreen instanceof StateScreen stateScreen) {
                        stateScreen.refreshScreen(LevelEnhancedClient.playerConfigData);
                    }
                });
            }
        });
    }

    public static void addHealthPoint() {
        if (MinecraftClient.getInstance().getNetworkHandler() != null) {
            ClientPlayNetworking.send(new PacketHandler.AddHealthPayload());
        }
    }

    // 发送增加攻击力的数据包
    public static void addAttackPoint() {
        if (MinecraftClient.getInstance().getNetworkHandler() != null) {
            ClientPlayNetworking.send(new PacketHandler.AddAttackPayload());
        }
    }

    public static void addHealingPoint() {
        if (MinecraftClient.getInstance().getNetworkHandler() != null) {
            ClientPlayNetworking.send(new PacketHandler.AddHealingPayload());
        }
    }

    // 发送增加箭术的数据包
    public static void addArcheryPoint() {
        if (MinecraftClient.getInstance().getNetworkHandler() != null) {
            ClientPlayNetworking.send(new PacketHandler.AddArcheryPayload());
        }
    }


    // 发送增加防御力的数据包
    public static void addDefensePoint() {
        if (MinecraftClient.getInstance().getNetworkHandler() != null) {
            ClientPlayNetworking.send(new PacketHandler.AddDefensePayload());
        }
    }

    // 发送增加速度的数据包
    public static void addSpeedPoint() {
        if (MinecraftClient.getInstance().getNetworkHandler() != null) {
            ClientPlayNetworking.send(new PacketHandler.AddSpeedPayload());
        }
    }

    // 发送增加暴击率的数据包
    public static void addCritChancePoint() {
        if (MinecraftClient.getInstance().getNetworkHandler() != null) {
            ClientPlayNetworking.send(new PacketHandler.AddCritChancePayload());
        }
    }

    // 发送增加暴击伤害的数据包
    public static void addCritDamagePoint() {
        if (MinecraftClient.getInstance().getNetworkHandler() != null) {
            ClientPlayNetworking.send(new PacketHandler.AddCritDamagePayload());
        }
    }

    // 发送增加饥饿抗性的数据包
    public static void addHungerResistPoint() {
        if (MinecraftClient.getInstance().getNetworkHandler() != null) {
            ClientPlayNetworking.send(new PacketHandler.AddHungerResistPayload());
        }
    }

    // 发送增加 mining 的数据包
    public static void addMiningPoint() {
        if (MinecraftClient.getInstance().getNetworkHandler() != null) {
            ClientPlayNetworking.send(new PacketHandler.AddMiningPayload());
        }
    }
}
