package com.ruinscraft.bookverify;

import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.Set;

public class BookInteractListener implements Listener {

    private static boolean isWrittenBook(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() != Material.WRITTEN_BOOK || !itemStack.hasItemMeta()) {
            return false;
        } else {
            return true;
        }
    }

    @EventHandler
    public void onBookEdit(PlayerEditBookEvent event) {
        BookMeta bookMeta = event.getNewBookMeta();

        if (event.isSigning()) {
            BookSignature bookSignature = new BookSignature(bookMeta);
            BookSignatureUtil.write(bookMeta, bookSignature);
            event.setNewBookMeta(bookMeta);
        }
    }

    @EventHandler
    public void onBookInteract(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();

        if (!isWrittenBook(itemStack)) {
            return;
        }

        Player player = event.getPlayer();

        boolean wasRightClick = false;

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            wasRightClick = true;
        }

        if (!wasRightClick) {
            return;
        }

        BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();

        if (bookMeta == null) {
            return;
        }

        BookVerifyConfig bvConfig = BookVerifyAPI.getConfig();
        BookSignature bookSignature = BookSignatureUtil.read(bookMeta);

        if (bookSignature == null) {
            if (bvConfig.notifyActionBarIfUnsigned) {
                BookVerifyAPI.notifyUnsigned(player, ChatMessageType.ACTION_BAR);
            }

            if (bvConfig.notifyChatIfUnsigned) {
                BookVerifyAPI.notifyUnsigned(player, ChatMessageType.CHAT);
            }

            if (bvConfig.removeBookIfUnsigned) {
                BookVerifyAPI.removeBookMeta(player, itemStack);
            }

            return;
        }

        Set<BookSignatureElement> changedElements = bookSignature.getChangedElements(bookMeta);

        if (changedElements.isEmpty()) {
            if (bvConfig.notifyActionBarIfOk) {
                BookVerifyAPI.notifyOk(player, ChatMessageType.ACTION_BAR, bookSignature);
            }

            if (bvConfig.notifyChatIfOk) {
                BookVerifyAPI.notifyOk(player, ChatMessageType.CHAT, bookSignature);
            }
        } else {
            if (bvConfig.notifyActionBarIfForged) {
                BookVerifyAPI.notifyAltered(player, ChatMessageType.ACTION_BAR, bookSignature);
            }

            if (bvConfig.notifyChatIfForged) {
                BookVerifyAPI.notifyAltered(player, ChatMessageType.CHAT, bookSignature);
            }

            if (bvConfig.removeBookIfForged) {
                BookVerifyAPI.removeBookMeta(player, itemStack);
            }
        }
    }

}
