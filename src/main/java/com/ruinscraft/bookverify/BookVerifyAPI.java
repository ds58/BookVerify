package com.ruinscraft.bookverify;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public final class BookVerifyAPI {

    public static BookVerifyConfig getConfig() {
        return BookVerifyPlugin.getInstance().getBvConfig();
    }

    public static BookCheckResult check(BookMeta bookMeta) {
        return BookSignatureUtil.read(bookMeta).check(bookMeta);
    }

    public static void notifyChatOk(Player player, BookSignature bookSignature) {
    }

    public static void notifyActionBarOk(Player player, BookSignature bookSignature) {

    }

    public static void notifyChatUnsigned(Player player, BookSignature bookSignature) {

    }

    public static void notifyActionBarUnsigned(Player player, BookSignature bookSignature) {

    }

    public static void notifyChatForged(Player player, BookSignature bookSignature) {

    }

    public static void notifyActionBarForged(Player player, BookSignature bookSignature) {

    }

    public static void removeBook(Player player, ItemStack book) {
        if (book == null || book.getType() != Material.WRITTEN_BOOK) {
            return;
        }

        player.getInventory().remove(book);
    }

}
