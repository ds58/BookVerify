package com.ruinscraft.bookverify;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class SignBookCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        ItemStack holding = player.getInventory().getItemInMainHand();

        if (!BookInteractListener.isWrittenBook(holding)) {
            TextComponent notAWrittenBook = BookVerifyAPI.getMessage(true, "SIGNBOOK_MUST_BE_HOLDING_BOOK");
            player.spigot().sendMessage(notAWrittenBook);
            return true;
        }

        BookMeta bookMeta = (BookMeta) holding.getItemMeta();
        final String target;

        if (args.length > 0) {
            if (!player.hasPermission("bookverify.sign.others")) {
                TextComponent noPermission = BookVerifyAPI.getMessage(true, "SIGNBOOK_NO_PERMISSION_FOR_OTHER");
                player.spigot().sendMessage(noPermission);
                return true;
            }
            target = args[0];
            bookMeta.setAuthor(target);
        } else {
            if (!player.getName().equals(bookMeta.getAuthor())) {
                TextComponent notAuthor = BookVerifyAPI.getMessage(true, "SIGNBOOK_NOT_AUTHOR");
                player.spigot().sendMessage(notAuthor);
                return true;
            }
            target = player.getName();
        }

        BookSignature bookSignature = new BookSignature(bookMeta);
        bookSignature.setAuthor(target);
        BookSignatureUtil.write(bookMeta, bookSignature);
        holding.setItemMeta(bookMeta);

        final TextComponent signed;

        if (target.equals(player.getName())) {
            signed = BookVerifyAPI.getMessage(false, "SIGNBOOK_SIGNED");
        } else {
            signed = BookVerifyAPI.getMessage(false, "SIGNBOOK_SIGNED_OTHER", target);
        }

        player.spigot().sendMessage(signed);

        return true;
    }

}
