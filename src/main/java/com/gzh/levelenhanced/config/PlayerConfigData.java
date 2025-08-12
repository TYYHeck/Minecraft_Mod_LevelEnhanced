package com.gzh.levelenhanced.config;

import com.gzh.levelenhanced.manager.LevelDataManager;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Objects;

import static com.gzh.LevelEnhanced.MOD_ID;

/**
 * Date 2025/8/9 下午8:09
 * Author:gzh
 * Description: 玩家数据
 */

public class PlayerConfigData {
    public static final Identifier ID = Identifier.of(MOD_ID, "level_data");

    private int level;
    private float experience;
    private float totalExperience;
    private float requiredExperience;
    private int points;

    // 属性点
    private int healthPoints;
    private int attackPoints;
    private int healingPoints;
    private int archeryPoints;
    private int defensePoints;
    private int hungerResistPoints;
    private int miningPoints;
    private int speedPoints;
    private int critChancePoints;
    private int critDamagePoints;

    public PlayerConfigData() {
        this.level = Config.getConfigData().INIT_LEVEL;
        this.experience = Config.getConfigData().INIT_EXPERIENCE;
        this.totalExperience = Config.getConfigData().INIT_EXPERIENCE;
        this.points = Config.getConfigData().INIT_POINTS;
        this.requiredExperience = Config.getConfigData().BASE_EXPERIENCE_REQUIRED;

        this.healthPoints = 0;
        this.attackPoints = 0;
        this.defensePoints = 0;
        this.hungerResistPoints = 0;
        this.miningPoints = 0;
        this.speedPoints = 0;
        this.critChancePoints = 0;
        this.critDamagePoints = 0;
    }

    public void readNbt(NbtCompound nbt) {
        this.level = nbt.getInt("level");
        this.experience = nbt.getFloat("experience");
        this.totalExperience = nbt.getFloat("totalExperience");
        this.requiredExperience = nbt.getFloat("requiredExperience");
        this.points = nbt.getInt("points");

        this.healthPoints = nbt.getInt("healthPoints");
        this.attackPoints = nbt.getInt("attackPoints");
        this.healingPoints = nbt.getInt("healingPoints");
        this.archeryPoints = nbt.getInt("archeryPoints");
        this.defensePoints = nbt.getInt("defensePoints");
        this.hungerResistPoints = nbt.getInt("hungerResistPoints");
        this.miningPoints = nbt.getInt("miningPoints");
        this.speedPoints = nbt.getInt("speedPoints");
        this.critChancePoints = nbt.getInt("critChancePoints");
        this.critDamagePoints = nbt.getInt("critDamagePoints");
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("level", this.level);
        nbt.putFloat("experience", this.experience);
        nbt.putFloat("totalExperience", this.totalExperience);
        nbt.putFloat("requiredExperience", this.requiredExperience);
        nbt.putFloat("points", this.points);

        nbt.putInt("healthPoints", this.healthPoints);
        nbt.putInt("attackPoints", this.attackPoints);
        nbt.putInt("healingPoints", this.healingPoints);
        nbt.putInt("archeryPoints", this.archeryPoints);
        nbt.putInt("defensePoints", this.defensePoints);
        nbt.putInt("hungerResistPoints", this.hungerResistPoints);
        nbt.putInt("miningPoints", this.miningPoints);
        nbt.putInt("speedPoints", this.speedPoints);
        nbt.putInt("critChancePoints", this.critChancePoints);
        nbt.putInt("critDamagePoints", this.critDamagePoints);

        return nbt;
    }

    public void addExperience(ServerPlayerEntity player, float experience) {
        if (Config.getConfigData().LEVEL_CAP_ENABLED && level >= Config.getConfigData().MAX_LEVEL) return;

        this.totalExperience += experience;
        this.experience += experience;
        boolean levelUpdated = false;

        // 升级检查
        while (this.experience >= this.requiredExperience
                && (!Config.getConfigData().LEVEL_CAP_ENABLED || level < Config.getConfigData().MAX_LEVEL)) {
            this.experience -= this.requiredExperience;
            this.requiredExperience *= Config.getConfigData().EXPERIENCE_SCALE;

            level++;
            if (Config.getConfigData().SP_LEVEL && level % Config.getConfigData().SP_LEVEL_SCALE == 0) {
                if (level % (5 * Config.getConfigData().SP_LEVEL_SCALE) == 0) {
                    points += 2 * (Config.getConfigData().POINT_PER_LEVEL + Config.getConfigData().SP_EXTRA_POINTS);
                    player.sendMessage(Text.literal("§6升级了！当前等级: " + level + " 获得"
                            + (2 * (Config.getConfigData().POINT_PER_LEVEL + Config.getConfigData().SP_EXTRA_POINTS)) + "点天赋点 "), false);
                    player.getServerWorld().playSound(
                            null,
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,  // 音效
                            SoundCategory.PLAYERS,
                            1.0F,  // 音量
                            1.0F
                    );
                }
                else
                {
                    points += Config.getConfigData().POINT_PER_LEVEL + Config.getConfigData().SP_EXTRA_POINTS;
                    player.sendMessage(Text.literal("§5升级了！当前等级: " + level + " 获得"
                            + (Config.getConfigData().POINT_PER_LEVEL + Config.getConfigData().SP_EXTRA_POINTS) + "点天赋点 "), false);
                    player.getServerWorld().playSound(
                            null,
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            SoundEvents.ENTITY_PLAYER_LEVELUP,  // 音效
                            SoundCategory.PLAYERS,
                            1.0F,  // 音量
                            1.0F
                    );
                }
            }
            else {
                points += Config.getConfigData().POINT_PER_LEVEL;
                player.sendMessage(Text.literal("§a升级了！当前等级: " + level + " 获得"
                        + Config.getConfigData().POINT_PER_LEVEL + "点天赋点 "), false);
            }

            levelUpdated = true;
        }

        if (levelUpdated) updateLevelState(player);
        LevelDataManager.get(player.getServerWorld()).syncData(player);
    }

    // 更新玩家属性
    public void updateLevelState(ServerPlayerEntity player) {
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH))
                .setBaseValue(getMaxHealth());
        if (player.getHealth() > player.getMaxHealth()) player.setHealth(player.getMaxHealth());

        // 更新移动速度
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED))
                .setBaseValue(getMovementSpeed());
    }


    // 属性点分配方法
    public void addHealthPoint() {
        if (points > 0) {
            healthPoints++;
            points--;
        }
    }

    public void addAttackPoint() {
        if (points > 0) {
            attackPoints++;
            points--;
        }
    }

    public void addDefensePoint() {
        if (points > 0) {
            defensePoints++;
            points--;
        }
    }

    public void addHungerResistPoint() {
        if (points > 0) {
            hungerResistPoints++;
            points--;
        }
    }

    public void addMiningPoint() {
        if (points > 0) {
            miningPoints++;
            points--;
        }
    }

    public void addSpeedPoint() {
        if (points > 0) {
            speedPoints++;
            points--;
        }
    }

    public void addCritChancePoint() {
        if (points > 0) {
            critChancePoints++;
            points--;
        }
    }

    public void addCritDamagePoint() {
        if (points > 0) {
            critDamagePoints++;
            points--;
        }
    }

    public void addHealingPoint() {
        if (points > 0) {
            healingPoints++;
            points--;
        }
    }

    public void addArcheryPoint() {
        if (points > 0) {
            archeryPoints++;
            points--;
        }
    }

    // 获取各种计算后的属性值
    public float getMaxHealth() {
        return Config.getConfigData().BASE_HEALTH + healthPoints * Config.getConfigData().ADDITION_HEALTH;
    }

    public float getAttackMultiplier() {
        return Config.getConfigData().BASE_ATTACK_MUL + attackPoints * Config.getConfigData().ADDITION_ATTACK_MUL;
    }

    public float getHealingRate() {
        return Config.getConfigData().BASE_HEALING + healingPoints * Config.getConfigData().ADDITION_HEALING
                + (Config.getConfigData().BASE_DEFENSE + defensePoints * Config.getConfigData().ADDITION_DEFENSE > Config.getConfigData().MAX_DEFENSE ?
                    (Config.getConfigData().BASE_DEFENSE + defensePoints * Config.getConfigData().ADDITION_DEFENSE - Config.getConfigData().MAX_DEFENSE) * 0.5f
                    : 0);
    }

    public float getArcheryMultiplier() {
        return Config.getConfigData().BASE_ARCHERY_MUL + archeryPoints * Config.getConfigData().ADDITION_ARCHERY_MUL;
    }

    public float getDefenseReduction() {
        return Math.min(Config.getConfigData().MAX_DEFENSE, Config.getConfigData().BASE_DEFENSE + defensePoints * Config.getConfigData().ADDITION_DEFENSE);
    }

    public float getHungerResistMultiplier() {
        return Config.getConfigData().BASE_HUNGER_RESIST + hungerResistPoints * Config.getConfigData().ADDITION_HUNGER_RESIST;
    }

    public float getMiningSpeedMultiplier() {
        return Config.getConfigData().BASE_MINING_SPEED + miningPoints * Config.getConfigData().ADDITION_MINING_SPEED;
    }

    public float getMovementSpeed() {
        return 0.1f * (Config.getConfigData().BASE_MOVEMENT_SPEED + speedPoints * Config.getConfigData().ADDITION_MOVEMENT_SPEED);
    }

    public float getCritChance() {
        return Config.getConfigData().BASE_CRIT_CHANGE + critChancePoints * Config.getConfigData().ADDITION_CRIT_CHANGE;
    }

    public float getCritDamageMultiplier() {
        return Config.getConfigData().BASE_CRIT_DAMAGE_MUL + critDamagePoints * Config.getConfigData().ADDITION_CRIT_DAMAGE_MUL + (getCritChance() > 1f ? (getCritChance() - 1f) * 0.5f : 0);
    }

    // Setter方法
    public void setLevel(int level) {
        this.level = level;
    }

    public void setExperience(float experience) {
        this.experience = experience;
    }

    public void setTotalExperience(float totalExperience) {
        this.totalExperience = totalExperience;
    }

    public void setRequiredExperience(float requiredExperience) {
        this.requiredExperience = requiredExperience;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setHealthPoints(int healthPoints) {
        this.healthPoints = healthPoints;
    }

    public void setAttackPoints(int attackPoints) {
        this.attackPoints = attackPoints;
    }

    public void setDefensePoints(int defensePoints) {
        this.defensePoints = defensePoints;
    }

    public void setHealingPoints(int healingPoints) {
        this.healingPoints = healingPoints;
    }

    public void setArcheryPoints(int archeryPoints) {
        this.archeryPoints = archeryPoints;
    }

    public void setHungerResistPoints(int hungerResistPoints) {
        this.hungerResistPoints = hungerResistPoints;
    }

    public void setMiningPoints(int miningPoints) {
        this.miningPoints = miningPoints;
    }

    public void setSpeedPoints(int speedPoints) {
        this.speedPoints = speedPoints;
    }

    public void setCritChancePoints(int critChancePoints) {
        this.critChancePoints = critChancePoints;
    }

    public void setCritDamagePoints(int critDamagePoints) {
        this.critDamagePoints = critDamagePoints;
    }

    // Getter方法
    public int getLevel() { return level; }
    public float getExperience() { return experience; }

    public int getHealingPoints() {
        return healingPoints;
    }

    public int getArcheryPoints() {
        return archeryPoints;
    }

    public float getTotalExperience() { return totalExperience; }
    public float getRequiredExperience() { return requiredExperience; }
    public int getPoints() { return points; }

    public int getHealthPoints() { return healthPoints; }
    public int getAttackPoints() { return attackPoints; }
    public int getDefensePoints() { return defensePoints; }
    public int getHungerResistPoints() { return hungerResistPoints; }
    public int getMiningPoints() { return miningPoints; }
    public int getSpeedPoints() { return speedPoints; }
    public int getCritChancePoints() { return critChancePoints; }
    public int getCritDamagePoints() { return critDamagePoints; }

    public void reset() {
        this.level = Config.getConfigData().INIT_LEVEL;
        this.experience = Config.getConfigData().INIT_EXPERIENCE;
        this.totalExperience = Config.getConfigData().INIT_EXPERIENCE;
        this.points = Config.getConfigData().INIT_POINTS;
        this.requiredExperience = Config.getConfigData().BASE_EXPERIENCE_REQUIRED;

        this.healthPoints = 0;
        this.attackPoints = 0;
        this.defensePoints = 0;
        this.hungerResistPoints = 0;
        this.miningPoints = 0;
        this.speedPoints = 0;
        this.critChancePoints = 0;
        this.critDamagePoints = 0;
    }
}
