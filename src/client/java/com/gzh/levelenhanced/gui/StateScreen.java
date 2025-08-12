package com.gzh.levelenhanced.gui;

import com.gzh.LevelEnhancedClient;
import com.gzh.levelenhanced.config.Config;
import com.gzh.levelenhanced.config.ConfigData;
import com.gzh.levelenhanced.config.PlayerConfigData;
import com.gzh.levelenhanced.network.ClientPacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class StateScreen extends Screen {
    private static final int WIDTH = 340;
    private static final int HEIGHT = 320;
    private int x;
    private int y;

    private static final int COLUMN_WIDTH = 150;
    private static final int COLUMN_SPACING = 10;
    private static final int ITEM_SPACING = 22;
    private static final int BUTTON_WIDTH = 30;
    private static final int BUTTON_HEIGHT = 20;
    private static final int INFO_COLUMN_WIDTH = 160;
    private static final int TEXT_POINTS_SPACING = 8;

    // 存储按钮与对应属性的映射关系
    private final List<AttributeButton> attributeButtons = new ArrayList<>();

    public StateScreen() {
        super(Text.literal("角色状态面板"));
    }

    @Override
    protected void init() {
        super.init();
        this.x = (this.width - WIDTH) / 2;
        this.y = (this.height - HEIGHT) / 2;
        attributeButtons.clear();

        PlayerConfigData data = LevelEnhancedClient.playerConfigData;
        if (data == null) {
            close();
            return;
        }

        addTitle();
        addStatusInfo(data);
        addAttributeItems(data);
        addCloseButton();
    }

    public void refreshScreen(PlayerConfigData data) {
        clearChildren();
        init();
    }

    private void addTitle() {
        Text titleText = Text.literal("§6角色状态");
        int textWidth = textRenderer.getWidth(titleText);
        addDrawableChild(new TextWidget(
                x + (WIDTH - textWidth) / 2, y + 10, textWidth, 20,
                titleText,
                textRenderer
        ));
    }

    private void addStatusInfo(PlayerConfigData data) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;

        int baseY = y + 40;

        addDrawableChild(new TextWidget(
                x + (WIDTH - (INFO_COLUMN_WIDTH * 2)) / 2, baseY, INFO_COLUMN_WIDTH, 20,
                Text.literal("§b等级: " + data.getLevel()),
                textRenderer
        ));

        addDrawableChild(new TextWidget(
                x + (WIDTH - (INFO_COLUMN_WIDTH * 2)) / 2 + INFO_COLUMN_WIDTH, baseY, INFO_COLUMN_WIDTH, 20,
                Text.literal("§b经验: " + String.format("%.1f", data.getExperience()) + "/" + String.format("%.1f", data.getRequiredExperience())),
                textRenderer
        ));

        addDrawableChild(new TextWidget(
                x + (WIDTH - (INFO_COLUMN_WIDTH * 2)) / 2, baseY + 20, INFO_COLUMN_WIDTH, 20,
                Text.literal("§b生命值: " + String.format("%.1f", player.getHealth())),
                textRenderer
        ));

        addDrawableChild(new TextWidget(
                x + (WIDTH - (INFO_COLUMN_WIDTH * 2)) / 2 + INFO_COLUMN_WIDTH, baseY + 20, INFO_COLUMN_WIDTH, 20,
                Text.literal("§b护甲值: " + player.getArmor()),
                textRenderer
        ));

        Text attrTitleText = Text.literal("§7属性值");
        int attrTitleWidth = textRenderer.getWidth(attrTitleText);
        addDrawableChild(new TextWidget(
                x + (WIDTH - attrTitleWidth) / 2,
                baseY + 30,
                attrTitleWidth,
                20,
                attrTitleText,
                textRenderer
        ));

        Text pointsText = Text.literal("§a可用属性点: " + data.getPoints());
        int pointsTextWidth = textRenderer.getWidth(pointsText);
        addDrawableChild(new TextWidget(
                x + (WIDTH - pointsTextWidth) / 2,
                baseY + 45,
                pointsTextWidth,
                20,
                pointsText,
                textRenderer
        ));
    }

    private void addAttributeItems(PlayerConfigData data) {
        int totalColumnsWidth = COLUMN_WIDTH * 2 + COLUMN_SPACING;
        int columnsStartX = x + (WIDTH - totalColumnsWidth) / 2;

        int firstColumnX = columnsStartX;
        int secondColumnX = columnsStartX + COLUMN_WIDTH + COLUMN_SPACING;
        int startY = y + 110;

        // 第一列属性
        addAttributeItem(firstColumnX, startY,
                "§7最大生命值: " + String.format("%.1f", data.getMaxHealth()),
                data.getHealthPoints(),
                ClientPacketSender::addHealthPoint,
                AttributeType.HEALTH);

        addAttributeItem(firstColumnX, startY + ITEM_SPACING,
                "§7恢复: " + String.format("%.2f/秒", data.getHealingRate()),
                data.getHealingPoints(),
                ClientPacketSender::addHealingPoint,
                AttributeType.HEALING);

        addAttributeItem(firstColumnX, startY + ITEM_SPACING * 2,
                "§7暴击率: " + String.format("%.1f%%", data.getCritChance() * 100),
                data.getCritChancePoints(),
                ClientPacketSender::addCritChancePoint,
                AttributeType.CRIT_CHANCE);

        addAttributeItem(firstColumnX, startY + ITEM_SPACING * 3,
                "§7防御倍率: " + String.format("%.1f%%", data.getDefenseReduction() * 100),
                data.getDefensePoints(),
                ClientPacketSender::addDefensePoint,
                AttributeType.DEFENSE);

        addAttributeItem(firstColumnX, startY + ITEM_SPACING * 4,
                "§7饥饿抗性: " + String.format("%.1f%%", data.getHungerResistMultiplier() * 100),
                data.getHungerResistPoints(),
                ClientPacketSender::addHungerResistPoint,
                AttributeType.HUNGER_RESIST);

        // 第二列属性
        addAttributeItem(secondColumnX, startY,
                "§7攻击倍率: " + String.format("%.1f%%", data.getAttackMultiplier() * 100),
                data.getAttackPoints(),
                ClientPacketSender::addAttackPoint,
                AttributeType.ATTACK);

        addAttributeItem(secondColumnX, startY + ITEM_SPACING,
                "§7箭术倍率: " + String.format("%.1f%%", data.getArcheryMultiplier() * 100),
                data.getArcheryPoints(),
                ClientPacketSender::addArcheryPoint,
                AttributeType.ARCHERY);

        addAttributeItem(secondColumnX, startY + ITEM_SPACING * 2,
                "§7暴击伤害: " + String.format("%.1f%%", data.getCritDamageMultiplier() * 100),
                data.getCritDamagePoints(),
                ClientPacketSender::addCritDamagePoint,
                AttributeType.CRIT_DAMAGE);

        addAttributeItem(secondColumnX, startY + ITEM_SPACING * 3,
                "§7移动速度: " + String.format("%.3f", data.getMovementSpeed()),
                data.getSpeedPoints(),
                ClientPacketSender::addSpeedPoint,
                AttributeType.SPEED);

        addAttributeItem(secondColumnX, startY + ITEM_SPACING * 4,
                "§7挖掘速度: " + String.format("%.1f%%", data.getMiningSpeedMultiplier() * 100),
                data.getMiningPoints(),
                ClientPacketSender::addMiningPoint,
                AttributeType.MINING);
    }

    private void addAttributeItem(int xPos, int yPos, String text, int points, Runnable onClick, AttributeType type) {
        Text displayText = Text.literal(text);
        int textWidth = textRenderer.getWidth(displayText);

        Text pointsText = Text.literal("§e[" + points + "]");
        int pointsWidth = textRenderer.getWidth(pointsText);

        int textX = xPos;
        int pointsX = textX + textWidth + TEXT_POINTS_SPACING;
        int buttonX = xPos + COLUMN_WIDTH - BUTTON_WIDTH;
        int buttonY = yPos + (20 - BUTTON_HEIGHT) / 2;

        addDrawableChild(new TextWidget(
                textX, yPos, textWidth, 20,
                displayText,
                textRenderer
        ));

        addDrawableChild(new TextWidget(
                pointsX, yPos, pointsWidth, 20,
                pointsText,
                textRenderer
        ));

        // 创建自定义属性按钮并添加到列表
        ButtonWidget button = ButtonWidget.builder(Text.literal("+"), button2 -> onClick.run())
                .position(buttonX, buttonY)
                .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();

        attributeButtons.add(new AttributeButton(button, type, points));
        addDrawableChild(button);
    }

    private void addCloseButton() {
        addDrawableChild(ButtonWidget.builder(Text.literal("关闭"), button -> {
                    this.close();
                }).position(x + (WIDTH - 100) / 2, y + HEIGHT - 30)
                .size(100, 20)
                .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        // 渲染工具提示
        renderTooltips(context, mouseX, mouseY);
    }

    private void renderTooltips(DrawContext context, int mouseX, int mouseY) {
        PlayerConfigData data = LevelEnhancedClient.playerConfigData;
        if (data == null) return;

        for (AttributeButton attrButton : attributeButtons) {
            ButtonWidget button = attrButton.button;
            if (button.isHovered()) {
                List<Text> tooltip = createAttributeTooltip(attrButton.type);
                context.drawTooltip(textRenderer, tooltip, mouseX, mouseY);
                break;
            }
        }
    }

    private List<Text> createAttributeTooltip(AttributeType type) {
        List<Text> tooltip = new ArrayList<>();
        ConfigData config = Config.getConfigData();

        // 添加属性名称
        tooltip.add(Text.literal(type.getDisplayName()));
        tooltip.add(Text.literal("")); // 空行分隔

        // 添加基础描述
        tooltip.add(Text.literal("§7每级增加: " + type.getPerLevelDesc()));

        // 添加上限信息
        if (type.hasMaxLimit()) {
            float maxValue = type.calculateMaxValue(config);
            tooltip.add(Text.literal("§7上限: " + type.formatMaxValue(maxValue)));

            if (!type.changeTo.equals("NULL")) {
                tooltip.add(Text.literal(""));
                tooltip.add(Text.literal("§6溢出转换: 超出上限部分将以" + String.format("%.1f", type.per * 100)+ "%比例"));
                tooltip.add(Text.literal("§6转换为" + type.changeTo + "属性点"));
            }
        } else {
            tooltip.add(Text.literal("§7无上限"));
        }

        return tooltip;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    private record AttributeButton(ButtonWidget button, AttributeType type, int currentPoints) {}


    // 属性类型枚举：包含所有属性的元数据
    public enum AttributeType {
        HEALTH("最大生命值", "1.0点生命值", false) {
            @Override
            public float calculateMaxValue(ConfigData config) {
                return 0;
            }

            @Override
            public String formatMaxValue(float value) {
                return "";
            }
        },
        ATTACK("攻击倍率", "1%攻击伤害", false) {
            @Override
            public float calculateMaxValue(ConfigData config) {
                return 0;
            }

            @Override
            public String formatMaxValue(float value) {
                return "";
            }
        },
        HEALING("恢复", "0.01点/秒", false) {
            @Override
            public float calculateMaxValue(ConfigData config) {
                return 0;
            }

            @Override
            public String formatMaxValue(float value) {
                return "";
            }
        },
        ARCHERY("箭术倍率", "1.5%弹射物伤害", false) {
            @Override
            public float calculateMaxValue(ConfigData config) {
                return 0;
            }

            @Override
            public String formatMaxValue(float value) {
                return "";
            }
        },
        DEFENSE("防御倍率", "0.75%伤害减免", true, "恢复", 0.5f) {
            @Override
            public float calculateMaxValue(ConfigData config) {
                return config.MAX_DEFENSE;
            }

            @Override
            public String formatMaxValue(float value) {
                return String.format("%.0f%%", value * 100);
            }
        },
        HUNGER_RESIST("饥饿抗性", "5%饥饿减缓", false) {
            @Override
            public float calculateMaxValue(ConfigData config) {
                return 0;
            }

            @Override
            public String formatMaxValue(float value) {
                return "";
            }
        },
        MINING("挖掘速度", "1.5%挖掘速度", false) {
            @Override
            public float calculateMaxValue(ConfigData config) {
                return 0;
            }

            @Override
            public String formatMaxValue(float value) {
                return "";
            }
        },
        SPEED("移动速度", "1.5%移动速度", false) {
            @Override
            public float calculateMaxValue(ConfigData config) {
                return 0;
            }

            @Override
            public String formatMaxValue(float value) {
                return "";
            }
        },
        CRIT_CHANCE("暴击率", "0.75%暴击概率", true, "暴击伤害", 0.5f) {
            @Override
            public float calculateMaxValue(ConfigData config) {
                return 0.5f; // 50%暴击率
            }

            @Override
            public String formatMaxValue(float value) {
                return String.format("%.0f%%", value * 100);
            }
        },
        CRIT_DAMAGE("暴击伤害", "1%暴击伤害", false) {
            @Override
            public float calculateMaxValue(ConfigData config) {
                return 0;
            }

            @Override
            public String formatMaxValue(float value) {
                return "";
            }
        };

        private final String displayName;
        private final String perLevelDesc;
        private final boolean hasMaxLimit;
        private final String changeTo;
        private final float per;

        AttributeType(String displayName, String perLevelDesc, boolean hasMaxLimit) {
            this.displayName = displayName;
            this.perLevelDesc = perLevelDesc;
            this.hasMaxLimit = hasMaxLimit;
            this.changeTo = "NULL";
            this.per = 0;
        }

        AttributeType(String displayName, String perLevelDesc, boolean hasMaxLimit, String changeTo, float per) {
            this.displayName = displayName;
            this.perLevelDesc = perLevelDesc;
            this.hasMaxLimit = hasMaxLimit;
            this.changeTo = changeTo;
            this.per = per;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getPerLevelDesc() {
            return perLevelDesc;
        }

        public boolean hasMaxLimit() {
            return hasMaxLimit;
        }

        public String getChangeTo() {
            return changeTo;
        }

        public float getPer() {
            return per;
        }

        public abstract float calculateMaxValue(ConfigData config);

        public abstract String formatMaxValue(float value);
    }
}