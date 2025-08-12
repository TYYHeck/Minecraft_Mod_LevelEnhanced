package com.gzh.levelenhanced.config;

import org.lwjgl.glfw.GLFW;

/**
 * Date 2025/8/9 下午7:42
 * Author:gzh
 * Description: 配置类
 */

public class ConfigData {
    // 基础配置
    public int INIT_LEVEL = 0;
    public int INIT_POINTS = 5;
    public int POINT_PER_LEVEL = 3;
    public boolean LEVEL_CAP_ENABLED = false; // 等级上限
    public int MAX_LEVEL = 100;
    public boolean SP_LEVEL = true;
    public int SP_LEVEL_SCALE = 5;
    public int SP_EXTRA_POINTS = 2;
    public float MAX_DEFENSE = 0.95f;

    // 经验配置
    public float INIT_EXPERIENCE = 0f;
    public float BASE_EXPERIENCE_REQUIRED = 100f;
    public float EXPERIENCE_SCALE = 1.05f;
    public float BLOCK_BREAK_EXP = 2f;
    public float ENTITY_KILL_EXP = 5f;
    public float EXPERIENCE_EXP = 2f;

    // 属性基础值
    public float BASE_HEALTH = 10.0F;
    public float BASE_ATTACK_MUL = 0.75f;
    public float BASE_HEALING = 0f;
    public float BASE_ARCHERY_MUL = 0.5f;
    public float BASE_DEFENSE = 0f;
    public float BASE_HUNGER_RESIST = 0.75f;
    public float BASE_MINING_SPEED = 0.75f;
    public float BASE_MOVEMENT_SPEED = 0.75f;
    public float BASE_CRIT_CHANGE = 0f;
    public float BASE_CRIT_DAMAGE_MUL = 1.5f;

    // 属性增长
    public float ADDITION_HEALTH = 1.0f;
    public float ADDITION_ATTACK_MUL = 0.01f;
    public float ADDITION_HEALING = 0.01f;
    public float ADDITION_ARCHERY_MUL = 0.015f;
    public float ADDITION_DEFENSE = 0.0075f;
    public float ADDITION_HUNGER_RESIST = 0.05f;
    public float ADDITION_MINING_SPEED = 0.01f;
    public float ADDITION_MOVEMENT_SPEED = 0.015f;
    public float ADDITION_CRIT_CHANGE = 0.0075f;
    public float ADDITION_CRIT_DAMAGE_MUL = 0.01f;

    // 快捷键绑定
    public int KEY_OPEN_STATE_SCREEN = GLFW.GLFW_KEY_O;
}
