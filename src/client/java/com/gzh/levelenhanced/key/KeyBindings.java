package com.gzh.levelenhanced.key;

import com.gzh.levelenhanced.config.Config;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

/**
 * Date 2025/8/10 上午12:27
 * Author:gzh
 * Description: 快捷键
 */

public class KeyBindings {
    public static KeyBinding OPEN_STATE_SCREEN;

    public static void initialize() {
        InputUtil.Key open_state_screen = InputUtil.fromKeyCode(Config.getConfigData().KEY_OPEN_STATE_SCREEN, 0);

        OPEN_STATE_SCREEN = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.levelenhanced.open_state_screen",
                open_state_screen.getCode(),
                "category.levelenhanced"
        ));
    }
}
