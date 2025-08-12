package com.gzh.levelenhanced.network;

import com.gzh.LevelEnhanced;
import com.gzh.levelenhanced.config.PlayerConfigData;
import com.gzh.levelenhanced.manager.LevelDataManager;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Date 2025/8/9 下午9:05
 * Author:gzh
 * Description: 网络包
 */

public class PacketHandler {
    // 数据包标识
    public static final Identifier ADD_HEALTH = Identifier.of(LevelEnhanced.MOD_ID, "add_health");
    public static final Identifier ADD_ATTACK = Identifier.of(LevelEnhanced.MOD_ID, "add_attack");
    public static final Identifier ADD_HEALING = Identifier.of(LevelEnhanced.MOD_ID, "add_healing");
    public static final Identifier ADD_ARCHERY = Identifier.of(LevelEnhanced.MOD_ID, "add_archery");
    public static final Identifier ADD_DEFENSE = Identifier.of(LevelEnhanced.MOD_ID, "add_defense");
    public static final Identifier ADD_SPEED = Identifier.of(LevelEnhanced.MOD_ID, "add_speed");
    public static final Identifier ADD_CRIT_CHANCE = Identifier.of(LevelEnhanced.MOD_ID, "add_crit_chance");
    public static final Identifier ADD_CRIT_DAMAGE = Identifier.of(LevelEnhanced.MOD_ID, "add_crit_damage");
    public static final Identifier ADD_HUNGER_RESIST = Identifier.of(LevelEnhanced.MOD_ID, "add_hunger_resist");
    public static final Identifier ADD_MINING = Identifier.of(LevelEnhanced.MOD_ID, "add_mining");
    public static final Identifier LEVEL_UPDATE_PACKET = Identifier.of(LevelEnhanced.MOD_ID,"level_update_packet");

    public static void initialize() {
        // 注册数据包
        // S2C
        PayloadTypeRegistry.playS2C().register(LevelUpdatePayload.ID, LevelUpdatePayload.CODEC);

        // C2S
        PayloadTypeRegistry.playC2S().register(AddHealthPayload.ID, AddHealthPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(AddAttackPayload.ID, AddAttackPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(AddHealingPayload.ID, AddHealingPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(AddArcheryPayload.ID, AddArcheryPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(AddDefensePayload.ID, AddDefensePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(AddSpeedPayload.ID, AddSpeedPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(AddCritChancePayload.ID, AddCritChancePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(AddCritDamagePayload.ID, AddCritDamagePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(AddHungerResistPayload.ID, AddHungerResistPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(AddMiningPayload.ID, AddMiningPayload.CODEC);

        // 注册服务器端数据包处理器
        ServerPlayConnectionEvents.INIT.register((handler, server) -> {
            ServerPlayNetworking.registerGlobalReceiver(AddHealthPayload.ID, (payload, context) -> {
                ServerPlayerEntity player = context.player();
                handleAttributeAddition(player, PlayerConfigData::addHealthPoint);
            });

            ServerPlayNetworking.registerGlobalReceiver(AddAttackPayload.ID, (payload, context) -> {
                ServerPlayerEntity player = context.player();
                handleAttributeAddition(player, PlayerConfigData::addAttackPoint);
            });

            ServerPlayNetworking.registerGlobalReceiver(AddHealingPayload.ID, (payload, context) -> {
                ServerPlayerEntity player = context.player();
                handleAttributeAddition(player, PlayerConfigData::addHealingPoint);
            });

            ServerPlayNetworking.registerGlobalReceiver(AddArcheryPayload.ID, (payload, context) -> {
                ServerPlayerEntity player = context.player();
                handleAttributeAddition(player, PlayerConfigData::addArcheryPoint);
            });

            ServerPlayNetworking.registerGlobalReceiver(AddDefensePayload.ID, (payload, context) -> {
                ServerPlayerEntity player = context.player();
                handleAttributeAddition(player, PlayerConfigData::addDefensePoint);
            });

            ServerPlayNetworking.registerGlobalReceiver(AddSpeedPayload.ID, (payload, context) -> {
                ServerPlayerEntity player = context.player();
                handleAttributeAddition(player, PlayerConfigData::addSpeedPoint);
            });

            ServerPlayNetworking.registerGlobalReceiver(AddCritChancePayload.ID, (payload, context) -> {
                ServerPlayerEntity player = context.player();
                handleAttributeAddition(player, PlayerConfigData::addCritChancePoint);
            });

            ServerPlayNetworking.registerGlobalReceiver(AddCritDamagePayload.ID, (payload, context) -> {
                ServerPlayerEntity player = context.player();
                handleAttributeAddition(player, PlayerConfigData::addCritDamagePoint);
            });

            ServerPlayNetworking.registerGlobalReceiver(AddHungerResistPayload.ID, (payload, context) -> {
                ServerPlayerEntity player = context.player();
                handleAttributeAddition(player, PlayerConfigData::addHungerResistPoint);
            });

            ServerPlayNetworking.registerGlobalReceiver(AddMiningPayload.ID, (payload, context) -> {
                ServerPlayerEntity player = context.player();
                handleAttributeAddition(player, PlayerConfigData::addMiningPoint);
            });
        });
    }

    // 服务端 -> 客户端：同步等级数据
    public record LevelUpdatePayload(PlayerConfigData data) implements CustomPayload {
        public static final Id<LevelUpdatePayload> ID = new CustomPayload.Id<>(LEVEL_UPDATE_PACKET);

        public static final PacketCodec<PacketByteBuf, LevelUpdatePayload> CODEC = new PacketCodec<>() {
            @Override
            public LevelUpdatePayload decode(PacketByteBuf buf) {
                PlayerConfigData data = new PlayerConfigData();

                // 按照编码顺序读取字段
                data.setLevel(buf.readInt());
                data.setExperience(buf.readFloat());
                data.setTotalExperience(buf.readFloat());
                data.setRequiredExperience(buf.readFloat());
                data.setPoints(buf.readInt());
                data.setHealthPoints(buf.readInt());
                data.setAttackPoints(buf.readInt());
                data.setHealingPoints(buf.readInt());
                data.setArcheryPoints(buf.readInt());
                data.setDefensePoints(buf.readInt());
                data.setHungerResistPoints(buf.readInt());
                data.setMiningPoints(buf.readInt());
                data.setSpeedPoints(buf.readInt());
                data.setCritChancePoints(buf.readInt());
                data.setCritDamagePoints(buf.readInt());
                return new LevelUpdatePayload(data);
            }

            @Override
            public void encode(PacketByteBuf buf, LevelUpdatePayload value) {
                PlayerConfigData data = value.data();

                // 按照顺序写入所有需要同步的字段
                buf.writeInt(data.getLevel());
                buf.writeFloat(data.getExperience());
                buf.writeFloat(data.getTotalExperience());
                buf.writeFloat(data.getRequiredExperience());
                buf.writeInt(data.getPoints());
                buf.writeInt(data.getHealthPoints());
                buf.writeInt(data.getAttackPoints());
                buf.writeInt(data.getHealingPoints());
                buf.writeInt(data.getArcheryPoints());
                buf.writeInt(data.getDefensePoints());
                buf.writeInt(data.getHungerResistPoints());
                buf.writeInt(data.getMiningPoints());
                buf.writeInt(data.getSpeedPoints());
                buf.writeInt(data.getCritChancePoints());
                buf.writeInt(data.getCritDamagePoints());
            }
        };

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public record AddHealthPayload() implements CustomPayload {
        public static final Id<AddHealthPayload> ID = new Id<>(ADD_HEALTH);
        public static final PacketCodec<PacketByteBuf, AddHealthPayload> CODEC = PacketCodec.unit(new AddHealthPayload());

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public record AddAttackPayload() implements CustomPayload {
        public static final Id<AddAttackPayload> ID = new Id<>(ADD_ATTACK);
        public static final PacketCodec<PacketByteBuf, AddAttackPayload> CODEC = PacketCodec.unit(new AddAttackPayload());

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public record AddHealingPayload() implements CustomPayload {
        public static final Id<AddHealingPayload> ID = new Id<>(ADD_HEALING);
        public static final PacketCodec<PacketByteBuf, AddHealingPayload> CODEC = PacketCodec.unit(new AddHealingPayload());

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public record AddDefensePayload() implements CustomPayload {
        public static final Id<AddDefensePayload> ID = new Id<>(ADD_DEFENSE);
        public static final PacketCodec<PacketByteBuf, AddDefensePayload> CODEC = PacketCodec.unit(new AddDefensePayload());

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public record AddArcheryPayload() implements CustomPayload {
        public static final Id<AddArcheryPayload> ID = new Id<>(ADD_ARCHERY);
        public static final PacketCodec<PacketByteBuf, AddArcheryPayload> CODEC = PacketCodec.unit(new AddArcheryPayload());

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public record AddSpeedPayload() implements CustomPayload {
        public static final Id<AddSpeedPayload> ID = new Id<>(ADD_SPEED);
        public static final PacketCodec<PacketByteBuf, AddSpeedPayload> CODEC = PacketCodec.unit(new AddSpeedPayload());

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public record AddCritChancePayload() implements CustomPayload {
        public static final Id<AddCritChancePayload> ID = new Id<>(ADD_CRIT_CHANCE);
        public static final PacketCodec<PacketByteBuf, AddCritChancePayload> CODEC = PacketCodec.unit(new AddCritChancePayload());

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public record AddCritDamagePayload() implements CustomPayload {
        public static final Id<AddCritDamagePayload> ID = new Id<>(ADD_CRIT_DAMAGE);
        public static final PacketCodec<PacketByteBuf, AddCritDamagePayload> CODEC = PacketCodec.unit(new AddCritDamagePayload());

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public record AddHungerResistPayload() implements CustomPayload {
        public static final Id<AddHungerResistPayload> ID = new Id<>(ADD_HUNGER_RESIST);
        public static final PacketCodec<PacketByteBuf, AddHungerResistPayload> CODEC = PacketCodec.unit(new AddHungerResistPayload());

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public record AddMiningPayload() implements CustomPayload {
        public static final Id<AddMiningPayload> ID = new Id<>(ADD_MINING);
        public static final PacketCodec<PacketByteBuf, AddMiningPayload> CODEC = PacketCodec.unit(new AddMiningPayload());

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }


    // 处理属性点增加的通用方法
    private static void handleAttributeAddition(ServerPlayerEntity player, Consumer<PlayerConfigData> attributeAdder) {
        Objects.requireNonNull(player.getServer()).execute(() -> {
            LevelDataManager manager = LevelDataManager.get(player.getServerWorld());
            PlayerConfigData data = manager.getPlayerConfigData(player.getUuid());

            // 检查是否有可用属性点
            if (data.getPoints() > 0) {
                // 执行属性点增加操作
                attributeAdder.accept(data);
                // 更新玩家状态并同步数据
                data.updateLevelState(player);
                manager.syncData(player);
                manager.markDirty();
            }
        });
    }
}
