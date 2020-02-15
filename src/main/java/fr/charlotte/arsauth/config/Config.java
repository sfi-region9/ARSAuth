package fr.charlotte.arsauth.config;

import com.google.gson.Gson;
import fr.charlotte.arsauth.utils.Database;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Config {

    private String dbHost;
    private String dbUser;
    private String dbPassword;
    private String dbName;

    public Config(String dbHost, String dbUser, String dbPassword, String dbName) {
        this.dbHost = dbHost;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dbName = dbName;
    }

    private Database setupDatabaseConnection() {
        return new Database(dbHost, dbName, dbUser, dbPassword);
    }

    public static Database loadConfiguration() throws IllegalAccessException {
        InputStream inputStream = Config.class.getResourceAsStream("/config.json");
        if (inputStream == null)
            throw new IllegalAccessException("You must specify a configuration");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String rawConfig = StringUtils.join(reader.lines().toArray());
        return new Gson().fromJson(rawConfig, Config.class).setupDatabaseConnection();
    }
}
