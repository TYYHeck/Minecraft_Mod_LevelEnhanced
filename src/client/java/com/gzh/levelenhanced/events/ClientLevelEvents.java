package com.gzh.levelenhanced.events;

import com.gzh.levelenhanced.gui.StateScreen;
import com.gzh.levelenhanced.key.KeyBindings;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

/**
 * Date 2025/8/10 下午1:05
 * Author:gzh
 * Description: 注册事件
 */

public class ClientLevelEvents {
    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (KeyBindings.OPEN_STATE_SCREEN.wasPressed()) {
                client.setScreen(new StateScreen());
            }
        });
    }
}
