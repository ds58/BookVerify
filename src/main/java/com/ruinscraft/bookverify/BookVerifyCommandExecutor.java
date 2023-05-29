package com.ruinscraft.bookverify;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

public class BookVerifyCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PluginDescriptionFile pdf = BookVerifyPlugin.getInstance().getDescription();
        String version = pdf.getVersion();
        String authors = String.join(", ", pdf.getAuthors());
        sender.sendMessage(ChatColor.GOLD + "BookVerify version " + version + " by " + authors);
        return true;
    }

}
