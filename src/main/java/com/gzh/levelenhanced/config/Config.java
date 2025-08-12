package com.gzh.levelenhanced.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gzh.LevelEnhanced;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Date 2025/8/9 下午7:41
 * Author:gzh
 * Description: 配置管理
 */

public class Config {
    // JSON工具
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    // 配置目录
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "levelenhanced.json");
    // 配置实体类
    private static ConfigData configData;

    // 访问配置类
    public static ConfigData getConfigData() {
        if (configData == null) {
            loadConfig();
        }
        return configData;
    }

    // 加载配置
    public static void loadConfig() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                configData = GSON.fromJson(reader, ConfigData.class);
            } catch (IOException e) {
                LevelEnhanced.LOGGER.info("LevelEnhanced -> cannot read config file!");
                configData = new ConfigData();
                saveConfig();
            }
        }
        else {
            configData = new ConfigData();
            saveConfig();
        }
    }

    // 保存配置
    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(configData, writer);
        } catch (IOException e) {
            LevelEnhanced.LOGGER.info("LevelEnhanced -> cannot save config file!");
        }
    }
}
