package com.ruinscraft.bookverify;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class BookInteractListener implements Listener {

    @EventHandler
    public void onBookInteract(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();

        if (itemStack == null || itemStack.getType() != Material.WRITTEN_BOOK) {
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

        switch (bookSignature.check(bookMeta)) {
            case OK:
                if (bvConfig.notifyActionBarIfOk) {
                    BookVerifyAPI.notifyActionBarOk(player, bookSignature);
                }

                if (bvConfig.notifyChatIfOk) {
                    BookVerifyAPI.notifyChatOk(player, bookSignature);
                }

                break;
            case UNSIGNED:
                if (bvConfig.notifyActionBarIfUnsigned) {
                    BookVerifyAPI.notifyActionBarUnsigned(player, bookSignature);
                }

                if (bvConfig.notifyChatIfUnsigned) {
                    BookVerifyAPI.notifyChatUnsigned(player, bookSignature);
                }

                if (bvConfig.removeBookIfUnsigned) {
                    BookVerifyAPI.removeBook(player, itemStack);
                }

                break;
            case CONTENT_CHANGED:
            case AUTHOR_CHANGED:
                if (bvConfig.notifyActionBarIfForged) {
                    BookVerifyAPI.notifyActionBarForged(player, bookSignature);
                }

                if (bvConfig.notifyChatIfForged) {
                    BookVerifyAPI.notifyChatForged(player, bookSignature);
                }

                if (bvConfig.removeBookIfForged) {
                    BookVerifyAPI.removeBook(player, itemStack);
                }

                break;
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

}
