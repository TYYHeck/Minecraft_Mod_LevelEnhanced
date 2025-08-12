package com.gzh;

import com.gzh.levelenhanced.config.PlayerConfigData;
import com.gzh.levelenhanced.events.ClientLevelEvents;
import com.gzh.levelenhanced.key.KeyBindings;
import com.gzh.levelenhanced.network.ClientPacketSender;
import net.fabricmc.api.ClientModInitializer;

public class LevelEnhancedClient implements ClientModInitializer {
	public static PlayerConfigData playerConfigData;

	@Override
	public void onInitializeClient() {
		// 按键
		KeyBindings.initialize();

		// 事件
		ClientLevelEvents.initialize();

		// 网络包
		ClientPacketSender.initialize();
	}
}