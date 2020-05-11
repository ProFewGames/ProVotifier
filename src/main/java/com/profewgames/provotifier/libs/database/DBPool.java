package com.profewgames.provotifier.libs.database;

import java.io.File;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

public final class DBPool {

    public static final DataSource getDatasource(boolean bungee) {
        return a(bungee);
    }

    private static DataSource openDataSource(String url, String username, String password) {
        BasicDataSource source = new BasicDataSource();
        source.addConnectionProperty("autoReconnect", "true");
        source.addConnectionProperty("allowMultiQueries", "true");
        source.setDefaultTransactionIsolation(2);
        source.setDriverClassName("com.mysql.jdbc.Driver");
        source.setUrl(url);
        source.setUsername(username);
        source.setPassword(password);
        source.setMaxTotal(4);
        source.setMaxIdle(4);
        source.setTimeBetweenEvictionRunsMillis(180000L);
        source.setSoftMinEvictableIdleTimeMillis(180000L);
        return source;
    }

    private static final DataSource a(boolean bungee) {
        File file = new File("settings.yml");
        if (bungee) {
            net.md_5.bungee.config.Configuration config;
            try {
                config = net.md_5.bungee.config.ConfigurationProvider.getProvider(net.md_5.bungee.config.YamlConfiguration.class).load(file);
            } catch (Exception e) {
                config = null;
                System.out.println("Error while parsing configuration for bungeecord! If you think this is an error please contact the developer immediately! Please provide the following stack trace:");
                e.printStackTrace();
            }
            if (config == null) return null;
            if (!config.getBoolean("mysql.enabled"))
                throw new UnsupportedOperationException("MySQL not enabled on this server.");
            return openDataSource("jdbc:mysql://" + config.getString("mysql.url"), config.getString("mysql.username"), config.getString("mysql.password"));
        } else {
            org.bukkit.configuration.file.YamlConfiguration config = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(file);
            if (!config.getBoolean("mysql.enabled"))
                throw new UnsupportedOperationException("MySQL not enabled on this server.");
            return openDataSource("jdbc:mysql://" + config.getString("mysql.url"), config.getString("mysql.username"), config.getString("mysql.password"));
        }
    }
}
