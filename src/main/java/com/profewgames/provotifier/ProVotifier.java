package com.profewgames.provotifier;

import com.profewgames.provotifier.crypto.RSAIO;
import com.profewgames.provotifier.crypto.RSAKeygen;
import com.profewgames.provotifier.model.ListenerLoader;
import com.profewgames.provotifier.model.VoteListener;
import com.profewgames.provotifier.net.VoteReceiver;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProVotifier {

    private static ProVotifier instance;

    public static ProVotifier getInstance() {
        return instance;
    }

    public static void initialize(boolean bungeecord) {
        instance = new ProVotifier(bungeecord);
    }

    static {
        (LOG = Logger.getLogger("ProVotifier")).setFilter(new LogFilter("[ProVotifier] "));
    }

    private static final Logger LOG;
    private final List<VoteListener> listeners;
    private VoteReceiver voteReceiver;
    private KeyPair keyPair;
    private boolean debug;
    private final boolean bungeecord;

    // General methods

    private ProVotifier(boolean bungeecord) {
        this.bungeecord = bungeecord;
        this.listeners = new ArrayList<>();
        ProVotifier.LOG.info("Running ProVotifier in " + (bungeecord ? "BUNGEECORD" : "BUKKIT") + " mode");

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        final File config = new File(getDataFolder() + "/config.yml");
        String hostAddr = "0.0.0.0";
        String listenerDirectory = getDataFolder().toString().replace("\\", "/") + "/listeners";
        try {
            if (!config.exists()) {
                ProVotifier.LOG.info("Configuring ProVotifier for the first time...");
                config.createNewFile();
                load(config);
                set("host", hostAddr);
                set("port", 8192);
                set("debug", false);
                ProVotifier.LOG.info("------------------------------------------------------------------------------");
                ProVotifier.LOG.info("Assigning ProVotifier to listen on port 8192. If you are hosting your server on a");
                ProVotifier.LOG.info("shared host please check with your hosting provider to verify that this port");
                ProVotifier.LOG.info("is available for your use. Chances are that your hosting provider will assign");
                ProVotifier.LOG.info("a different port, which you need to specify in config.yml");
                ProVotifier.LOG.info("------------------------------------------------------------------------------");
                set("listener_folder", listenerDirectory);
                save(config);
            } else {
                load(config);
            }
        } catch (Exception e) {
            ProVotifier.LOG.log(Level.SEVERE, "Error creating configuration file", e);
            this.gracefulExit();
            return;
        }
        if (checkNull()) {
            this.gracefulExit();
            return;
        }
        final File rsaDirectory = new File(getDataFolder() + "/rsa");
        try {
            if (!rsaDirectory.exists()) {
                rsaDirectory.mkdirs();
                new File(listenerDirectory).mkdirs();
                RSAIO.save(rsaDirectory, this.keyPair = RSAKeygen.generate(2048));
            } else {
                this.keyPair = RSAIO.load(rsaDirectory);
            }
        } catch (Exception e) {
            ProVotifier.LOG.log(Level.SEVERE, "Error reading configuration file or RSA keys");
            this.gracefulExit();
            return;
        }
        listenerDirectory = (String) get("listener_folder");
        this.listeners.addAll(ListenerLoader.load(listenerDirectory));
        final String host = (String) get("host", hostAddr);
        final int port = (int) get("port", 8192);
        this.debug = (Boolean) get("debug", false);
        if (debug) {
            ProVotifier.LOG.info("DEBUG mode enabled!");
        }
        try {
            (this.voteReceiver = new VoteReceiver(this, host, port)).start();
            ProVotifier.LOG.info("ProVotifier enabled.");
        } catch (Exception e) {
            this.gracefulExit();
        }
    }

    public void onDisable() {
        if (this.voteReceiver != null) {
            this.voteReceiver.shutdown();
        }
        ProVotifier.LOG.info("ProVotifier disabled.");
    }

    private void gracefulExit() {
        ProVotifier.LOG.log(Level.SEVERE, "ProVotifier did not initialize properly!");
    }

    // Getters


    public List<VoteListener> getListeners() {
        return listeners;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public boolean isBungeecord() {
        return bungeecord;
    }

    public boolean isDebug() {
        return debug;
    }


    // Configuration

    private net.md_5.bungee.config.Configuration configuration;
    private org.bukkit.configuration.file.YamlConfiguration yamlConfiguration;

    private void set(String key, Object val) {
        if (bungeecord) {
            configuration.set(key, val);
        } else {
            yamlConfiguration.set(key, val);
        }
    }

    private Object get(String key, Object defVal) {
        if (bungeecord) {
            return configuration.get(key, defVal);
        } else {
            return yamlConfiguration.get(key, defVal);
        }
    }

    private Object get(String key) {
        if (bungeecord) {
            return configuration.get(key);
        } else {
            return yamlConfiguration.get(key);
        }
    }

    private void load(File config) throws IOException {
        if (bungeecord) {
            configuration = net.md_5.bungee.config.ConfigurationProvider.getProvider(net.md_5.bungee.config.YamlConfiguration.class).load(config);
        } else {
            yamlConfiguration = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(config);
        }
    }

    private void save(File config) throws IOException {
        if (bungeecord) {
            net.md_5.bungee.config.ConfigurationProvider.getProvider(net.md_5.bungee.config.YamlConfiguration.class).save(configuration, config);
        } else {
            yamlConfiguration.save(config);
        }
    }

    private File getDataFolder() {
        return new File("plugins/ProVotifier");
    }

    private boolean checkNull() {
        if (bungeecord) {
            return configuration == null;
        } else {
            return yamlConfiguration == null;
        }
    }
}