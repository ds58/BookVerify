package com.ruinscraft.bookverify;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.Properties;

public final class BookVerifyAPI {

    private static final ChatColor COLOR_WARN = ChatColor.RED;
    private static final ChatColor COLOR_BASE = ChatColor.GOLD;

    public static BookVerifyConfig getConfig() {
        return BookVerifyPlugin.getInstance().getBvConfig();
    }

    public static TextComponent getMessage(boolean warn, String key, String... replacements) {
        Properties messages = BookVerifyPlugin.getInstance().getMessages();

        if (!messages.containsKey(key)) {
            BookVerifyPlugin.getInstance().getLogger().warning("Unknown message key: " + key);
            return null;
        }

        String message = messages.getProperty(key);
        message = String.format(message, replacements);

        TextComponent textComponent = new TextComponent(message);

        if (warn) {
            textComponent.setColor(COLOR_WARN);
        } else {
            textComponent.setColor(COLOR_BASE);
        }

        return textComponent;
    }

    public static void notifyAltered(Player player, ChatMessageType mType, BookSignature bookSignature) {
        TextComponent message = getMessage(true, "NOTIFY_ALTERED", bookSignature.toString());
        player.spigot().sendMessage(mType, message);
    }

    public static void notifyOk(Player player, ChatMessageType mType, BookSignature bookSignature) {
        TextComponent message = getMessage(false, "NOTIFY_OK", bookSignature.toString());
        player.spigot().sendMessage(mType, message);
    }

    public static void notifyUnsigned(Player player, ChatMessageType mType) {
        TextComponent message = getMessage(true, "NOTIFY_UNSIGNED");
        player.spigot().sendMessage(mType, message);
    }

}
