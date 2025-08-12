package com.gzh;

import com.gzh.levelenhanced.commands.LevelCommands;
import com.gzh.levelenhanced.config.Config;
import com.gzh.levelenhanced.events.Events;
import com.gzh.levelenhanced.network.PacketHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LevelEnhanced implements ModInitializer {
	public static final String MOD_ID = "levelenhanced";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// 初始化配置
		Config.loadConfig();

		// 事件注册
		Events.initialize();

		// 网络注册
		PacketHandler.initialize();

		// 指令注册
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> LevelCommands.initialize(dispatcher));

		LOGGER.info("LevelEnhanced -> Initialized!");
	}
}