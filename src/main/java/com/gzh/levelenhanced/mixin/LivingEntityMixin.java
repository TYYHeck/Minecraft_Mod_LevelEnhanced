package com.gzh.levelenhanced.mixin;

import com.gzh.LevelEnhanced;
import com.gzh.levelenhanced.config.Config;
import com.gzh.levelenhanced.config.PlayerConfigData;
import com.gzh.levelenhanced.manager.LevelDataManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Date 2025/8/9 下午11:31
 * Author:gzh
 * Description: 生物监听方法
 */

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow public abstract boolean damage(DamageSource source, float amount);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void onTick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity)(Object)this;
        if (entity instanceof ServerPlayerEntity player && !player.isDead()) {
            PlayerConfigData data = LevelDataManager.get(player.getServerWorld())
                    .getPlayerConfigData(player.getUuid());
            float healAmount = data.getHealingRate() / 20f;
            if (healAmount > 0 && player.getHealth() < player.getMaxHealth()) {
                player.heal(healAmount);
            }
        }
    }

    @Inject(method = "onDeath", at = @At("RETURN"))
    public void onDeath(DamageSource source, CallbackInfo ci) {
        if (source.getAttacker() instanceof ServerPlayerEntity player) {
            LivingEntity entity = (LivingEntity)(Object)this;

            // 排除玩家
            if (entity instanceof ServerPlayerEntity) return;

            LevelDataManager levelDataManager = LevelDataManager.get(player.getServerWorld());
            PlayerConfigData data = levelDataManager.getPlayerConfigData(player.getUuid());

            data.addExperience(player, Config.getConfigData().ENTITY_KILL_EXP);
        }
    }

    // 修改受到的玩家伤害
    @ModifyVariable(
            method = "damage",
            at = @At(value = "HEAD"),
            index = 2,
            argsOnly = true
    )
    public float modifyDamage(float amount, DamageSource source) {
        // 处理攻击
        if (source.getAttacker() instanceof ServerPlayerEntity player
                && !(source.getSource() instanceof PersistentProjectileEntity)
                && !player.getWorld().isClient) {
            PlayerConfigData data = LevelDataManager.get(player.getServerWorld())
                    .getPlayerConfigData(player.getUuid());
            float attackMul = data.getAttackMultiplier();
            boolean isCritical = this.random.nextFloat() < data.getCritChance();
            if (isCritical) {
                ServerWorld world = player.getServerWorld();
                LivingEntity target = (LivingEntity)(Object)this;

                amount *= data.getCritDamageMultiplier();
                // 生成暴击粒子
                spawnCriticalParticles(world, target, amount * attackMul);
                world.playSound(
                        null,
                        target.getX(),
                        target.getY(),
                        target.getZ(),
                        SoundEvents.ENTITY_PLAYER_ATTACK_CRIT,  // 暴击音效
                        SoundCategory.PLAYERS,
                        1.5F + Math.min(1F, (int)(amount * attackMul / 200)),  // 音量
                        1.15F + Math.min(0.35F, (int)(amount * attackMul / 200 * 0.35F))  // 音调（稍高一些更有暴击感）
                );
            }
            amount *= attackMul;
        }

        // 处理飞道攻击
        if (source.getSource() instanceof PersistentProjectileEntity projectile
                && projectile.getOwner() instanceof ServerPlayerEntity player
                && !player.getWorld().isClient) {
            PlayerConfigData data = LevelDataManager.get(player.getServerWorld())
                    .getPlayerConfigData(player.getUuid());
            float archeryMul = data.getArcheryMultiplier();
            boolean isCritical = this.random.nextFloat() < data.getCritChance();
            if (isCritical) {
                ServerWorld world = player.getServerWorld();
                LivingEntity target = (LivingEntity)(Object)this;

                amount *= data.getCritDamageMultiplier();
                // 生成暴击粒子
                spawnCriticalParticles(world, target, amount * archeryMul);
                world.playSound(
                        null,
                        target.getX(),
                        target.getY(),
                        target.getZ(),
                        SoundEvents.ENTITY_PLAYER_ATTACK_CRIT,  // 暴击音效
                        SoundCategory.PLAYERS,
                        1.5F + Math.min(1F, (int)(amount * archeryMul / 200)),  // 音量
                        1.15F + Math.min(0.35F, (int)(amount * archeryMul / 200 * 0.35F))  // 音调（稍高一些更有暴击感）
                );
            }
            amount *= archeryMul;
        }

        // 处理防御
        LivingEntity entity = (LivingEntity)(Object)this;
        if (entity instanceof ServerPlayerEntity player && !player.getWorld().isClient) {
            PlayerConfigData data = LevelDataManager.get(player.getServerWorld())
                    .getPlayerConfigData(player.getUuid());
            amount *= 1.0f - data.getDefenseReduction();
        }
        return amount;
    }

    @Unique
    private void spawnCriticalParticles(ServerWorld world, LivingEntity target, float damage) {
        // 获取目标实体的位置和大小
        double x = target.getX();
        double y = target.getY();
        double z = target.getZ();
        float width = target.getWidth();
        float height = target.getHeight();

        // 粒子类型
        ParticleEffect particleType = ParticleTypes.CRIT;

        // 创建单个粒子数据包，包含多个粒子
        ParticleS2CPacket particlePacket = new ParticleS2CPacket(
                particleType,
                false,  // 不使用长距离
                x, y + height / 2, z,  // 粒子中心位置（目标实体中心）
                width / 2.5f, height / 2.5f, width / 2.5f,  // 偏移范围（基于实体大小）
                0.15F,  // 粒子速度
                12 + Math.min(20, (int)Math.floor(damage / 10f))     // 粒子数量
        );

        // 发送给周围32格内的所有玩家
        world.getServer().getPlayerManager().sendToAround(
                null,  // 不排除任何玩家
                x, y, z,  // 中心点
                32.0D,    // 发送范围
                world.getRegistryKey(),
                particlePacket
        );
    }

    static {
        LevelEnhanced.LOGGER.info("LevelEnhanced -> LivingEntityMixin initialized!");
    }
}
