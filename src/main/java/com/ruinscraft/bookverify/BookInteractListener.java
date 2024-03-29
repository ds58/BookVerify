package com.ruinscraft.bookverify;

import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.Set;

public class BookInteractListener implements Listener {

    public static boolean isWrittenBook(ItemStack itemStack) {
        return itemStack != null && itemStack.getType() == Material.WRITTEN_BOOK && itemStack.hasItemMeta();
    }

    private static void checkInventory(Inventory inventory) throws Exception {
        if (inventory == null) {
            return;
        }

        BookVerifyConfig bvConfig = BookVerifyAPI.getConfig();

        for (ItemStack itemStack : inventory.getContents()) {
            if (!isWrittenBook(itemStack)) {
                continue;
            }

            BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
            assert bookMeta != null;
            BookSignature bookSignature = BookSignatureUtil.read(bookMeta);

            if (bookSignature == null) {
                if (bvConfig.removeBookIfUnsigned) {
                    inventory.removeItem(itemStack);
                }

                continue;
            }

            Set<BookSignatureElement> changedElements = bookSignature.getChangedElements(bookMeta);

            if (!changedElements.isEmpty()) {
                if (bvConfig.removeBookIfForged) {
                    inventory.removeItem(itemStack);
                }
            }
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
    public void onInventoryOpen(InventoryOpenEvent event) {
        try {
            checkInventory(event.getInventory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onBookInteract(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();

        if (!isWrittenBook(itemStack)) {
            return;
        }

        Player player = event.getPlayer();

        boolean wasRightClick = event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK;

        if (!wasRightClick) {
            return;
        }

        BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();

        if (bookMeta == null) {
            return;
        }

        BookVerifyConfig bvConfig = BookVerifyAPI.getConfig();
        BookSignature bookSignature;
        try {
            bookSignature = BookSignatureUtil.read(bookMeta);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (bookSignature == null) {
            if (bvConfig.notifyActionBarIfUnsigned) {
                BookVerifyAPI.notifyUnsigned(player, ChatMessageType.ACTION_BAR);
            }

            if (bvConfig.notifyChatIfUnsigned) {
                BookVerifyAPI.notifyUnsigned(player, ChatMessageType.CHAT);
            }

            if (bvConfig.removeBookIfUnsigned) {
                player.getInventory().removeItem(itemStack);
                event.setCancelled(true);
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
                player.getInventory().removeItem(itemStack);
                event.setCancelled(true);
            }
        }
    }

}
