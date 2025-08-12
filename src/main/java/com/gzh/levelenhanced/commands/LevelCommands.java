package com.gzh.levelenhanced.commands;

import com.gzh.levelenhanced.config.PlayerConfigData;
import com.gzh.levelenhanced.manager.LevelDataManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * Date 2025/8/10 下午1:13
 * Author:gzh
 * Description:  注册指令
 */

public class LevelCommands {
    public static void initialize(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("level")
                .then(CommandManager.literal("set")
                        .then(CommandManager.literal("level")
                            .then(CommandManager.argument("player", EntityArgumentType.player())
                                    .then(CommandManager.argument("level", IntegerArgumentType.integer(0))
                                            .executes(ctx -> {
                                                ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                                int level = IntegerArgumentType.getInteger(ctx, "level");

                                                PlayerConfigData data = LevelDataManager.get(player.getServerWorld())
                                                        .getPlayerConfigData(player.getUuid());
                                                data.setLevel(level);
                                                data.updateLevelState(player);

                                                ctx.getSource().sendFeedback(() -> Text.literal("已设置" + player.getName().getString() + "的等级为" + level), true);
                                                LevelDataManager.get(player.getServerWorld()).syncData(player);
                                                return 1;
                                            }))
                            )
                        )
                        .then(CommandManager.literal("point")
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .then(CommandManager.argument("type", StringArgumentType.word())
                                            .then(CommandManager.argument("amount", IntegerArgumentType.integer(0))
                                                .executes(ctx -> {
                                                    ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                                    PlayerConfigData playerConfigData = LevelDataManager.get(player.getServerWorld()).getPlayerConfigData(player.getUuid());
                                                    int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                                    String type = StringArgumentType.getString(ctx, "type");
                                                    switch (type) {
                                                        case "point": playerConfigData.setPoints(amount); break;
                                                        case "health": playerConfigData.setHealthPoints(amount); break;
                                                        case "healing": playerConfigData.setHealingPoints(amount); break;
                                                        case "attack": playerConfigData.setAttackPoints(amount); break;
                                                        case "archery": playerConfigData.setArcheryPoints(amount); break;
                                                        case "defense": playerConfigData.setDefensePoints(amount); break;
                                                        case "critChance": playerConfigData.setCritChancePoints(amount); break;
                                                        case "critDamage": playerConfigData.setCritDamagePoints(amount); break;
                                                        case "mining": playerConfigData.setMiningPoints(amount); break;
                                                        case "speed": playerConfigData.setSpeedPoints(amount); break;
                                                    }
                                                    playerConfigData.updateLevelState(player);

                                                    ctx.getSource().sendFeedback(() -> Text.literal("已设置" + type + "的数量为" + amount), true);
                                                    LevelDataManager.get(player.getServerWorld()).syncData(player);
                                                    return 1;
                                                }))
                                        )
                                )
                        )
                )
                // 添加经验指令
                .then(CommandManager.literal("add")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .then(CommandManager.argument("amount", FloatArgumentType.floatArg(0))
                                        .executes(ctx -> {
                                            ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                            float amount = FloatArgumentType.getFloat(ctx, "amount");

                                            LevelDataManager.get(player.getServerWorld())
                                                    .getPlayerConfigData(player.getUuid())
                                                    .addExperience(player, amount);

                                            ctx.getSource().sendFeedback(() -> Text.literal("已添加" + amount + "经验给" + player.getName().getString()), true);
                                            LevelDataManager.get(player.getServerWorld()).syncData(player);
                                            return 1;
                                        }))
                        )
                )
                .then(CommandManager.literal("reset")
                    .then(CommandManager.argument("player", EntityArgumentType.player())
                        .executes(ctx -> {
                            ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                            LevelDataManager.get(player.getServerWorld()).getPlayerConfigData(player.getUuid())
                                    .reset();
                            ctx.getSource().sendFeedback(() -> Text.literal("已重置" + player.getName().getString() + "的等级"), true);
                            LevelDataManager.get(player.getServerWorld()).syncData(player);
                            return 1;
                        })
                    )
                )
                .then(CommandManager.literal("status")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes(ctx -> {
                                    PlayerEntity targetPlayer = EntityArgumentType.getPlayer(ctx, "player");
                                    ServerCommandSource source = ctx.getSource();
                                    LevelDataManager manager = LevelDataManager.get(source.getWorld());
                                    PlayerConfigData data = manager.getPlayerConfigData(targetPlayer.getUuid());

                                    String message = "\n" +
                                            "§6=== " + targetPlayer.getName().getString() + " 的状态详情 ===\n" +
                                            "§b等级: §r" + data.getLevel() + "\n" +
                                            "§b经验: §r" + String.format("%.1f/%.1f", data.getExperience(), data.getRequiredExperience()) + "\n" +
                                            "§b生命值: §r" + String.format("%.1f/%.1f", targetPlayer.getHealth(), data.getMaxHealth()) + "\n" +
                                            "§b护甲值: §r" + targetPlayer.getArmor() +
                                            "§b可用属性点: §a" + data.getPoints() + "\n\n" +
                                            "§6=== 属性详情 ===\n" +
                                            "§c最大生命值: §r" + String.format("%.1f",data.getMaxHealth()) + "\n" +
                                            "§e攻击倍率: §r" + String.format("%.1f%%", data.getAttackMultiplier() * 100) + "\n" +
                                            "§e箭术倍率: §r" + String.format("%.1f%%", data.getArcheryMultiplier() * 100) + "\n" +
                                            "§2防御倍率: §r" + String.format("%.1f%%", data.getDefenseReduction() * 100) + "\n" +
                                            "§1移动速度: §r" + String.format("%.2f", data.getMovementSpeed()) + "\n" +
                                            "§5暴击率: §r" + String.format("%.1f%%", data.getCritChance() * 100) + "\n" +
                                            "§d暴击伤害: §r" + String.format("%.1f%%", data.getCritDamageMultiplier() * 100) + "\n" +
                                            "§a恢复: §r" + String.format("%.2f/秒", data.getHungerResistMultiplier() * 100) + "\n" +
                                            "§a饥饿抵抗: §r" + String.format("%.1f%%", data.getHungerResistMultiplier() * 100) + "\n" +
                                            "§8挖掘速度: §r" + String.format("%.1f%%", data.getMiningSpeedMultiplier() * 100) + "\n";

                                    // 发送信息给指令执行者
                                    source.sendFeedback(() -> Text.literal(message), false);
                                    return 1;
                                }))
                )
        );
    }
}
