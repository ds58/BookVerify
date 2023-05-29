package com.ruinscraft.bookverify;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Properties;
import java.util.Scanner;

public class BookVerifyPlugin extends JavaPlugin {

    private static final String SECRET_FILE_NAME = "signed_book_secret.key";
    private static BookVerifyPlugin instance;

    private BookVerifyConfig bvConfig;
    private BookVerifyCrypto crypto;
    private Properties messages;

    public static BookVerifyPlugin getInstance() {
        return instance;
    }

    public BookVerifyConfig getBvConfig() {
        return bvConfig;
    }

    public BookVerifyCrypto getCrypto() {
        return crypto;
    }

    public Properties getMessages() {
        return messages;
    }

    @Override
    public void onEnable() {
        instance = this;

        // Check if running Spigot
        try {
            Class.forName("org.spigotmc.SpigotConfig");
        } catch (ClassNotFoundException e) {
            getServer().getPluginManager().disablePlugin(this);
            getLogger().warning("BookVerify requires Spigot! www.spigotmc.org");
            return;
        }

        // Load config
        saveDefaultConfig();
        loadConfig();

        // Load Crypto
        String secret = loadSecret();
        crypto = new BookVerifyCrypto(secret);
        getLogger().info("Loaded secret key for signing written books. Do not share the contents of " + SECRET_FILE_NAME + " with anyone!");

        // Load messages
        loadMessages();

        // Register listeners
        getServer().getPluginManager().registerEvents(new BookInteractListener(), this);

        // Register commands
        getCommand("bookverify").setExecutor(new BookVerifyCommandExecutor());
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    private String loadSecret() {
        File file = new File(getDataFolder(), SECRET_FILE_NAME);

        // Check for a backup file in world directories
        if (!file.exists()) {
            for (World world : getServer().getWorlds()) {
                File backupFile = new File("./" + world.getName() + "/" + SECRET_FILE_NAME);

                if (backupFile.exists()) {
                    getLogger().info("Found backup signed book secret in: " + backupFile.getAbsolutePath());
                    file = backupFile;
                    break;
                }
            }
        }

        String secret = null;

        // If no secret file was found
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            secret = BookVerifyCrypto.generateSecret();

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(secret);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (Scanner scanner = new Scanner(file)) {
                if (scanner.hasNext()) {
                    secret = scanner.next();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (secret == null) {
            getLogger().warning("Unable to load secret.");
            return null;
        }

        if (bvConfig.createSecretBackupsInWorldDirectories) {
            createSecretBackups(secret);
        }

        return secret;
    }

    private void createSecretBackups(String secret) {
        for (World world : getServer().getWorlds()) {
            File backupFile = new File("./" + world.getName() + "/" + SECRET_FILE_NAME);

            if (!backupFile.exists()) {
                try {
                    backupFile.createNewFile();

                    try (FileWriter writer = new FileWriter(backupFile)) {
                        writer.write(secret);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadConfig() {
        bvConfig = new BookVerifyConfig();

        bvConfig.notifyChatIfOk = getConfig().getBoolean("notify-chat-if-ok", bvConfig.notifyChatIfOk);
        bvConfig.notifyActionBarIfOk = getConfig().getBoolean("notify-action-bar-if-ok", bvConfig.notifyActionBarIfOk);
        bvConfig.notifyChatIfUnsigned = getConfig().getBoolean("notify-chat-if-unsigned", bvConfig.notifyChatIfUnsigned);
        bvConfig.notifyActionBarIfUnsigned = getConfig().getBoolean("notify-action-bar-if-unsigned", bvConfig.notifyActionBarIfUnsigned);
        bvConfig.notifyChatIfForged = getConfig().getBoolean("notify-chat-if-forged", bvConfig.notifyChatIfForged);
        bvConfig.notifyActionBarIfForged = getConfig().getBoolean("notify-action-bar-if-forged", bvConfig.notifyActionBarIfForged);
        bvConfig.removeBookIfForged = getConfig().getBoolean("remove-book-if-forged", bvConfig.removeBookIfForged);
        bvConfig.removeBookIfUnsigned = getConfig().getBoolean("remove-book-if-unsigned", bvConfig.removeBookIfUnsigned);
        bvConfig.replaceForgedAuthorWithVerified = getConfig().getBoolean("replace-forged-author-with-verified", bvConfig.replaceForgedAuthorWithVerified);
        bvConfig.createSecretBackupsInWorldDirectories = getConfig().getBoolean("create-secret-backups-in-world-directories", bvConfig.createSecretBackupsInWorldDirectories);
    }

    private void loadMessages() {
        messages = new Properties();

        File messagesFile = new File(getDataFolder(), "messages.properties");

        if (!messagesFile.exists()) {
            saveResource("messages.properties", false);
        }

        try (FileInputStream in = new FileInputStream(messagesFile)) {
            messages.load(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
