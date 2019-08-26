package fr.colin.arsauth.config;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConfigWrapper {

    String rawConfig;



    public ConfigWrapper() {
        InputStream in = getClass().getResourceAsStream("/config.json");
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        rawConfig = StringUtils.join(br.lines().toArray());
    }

    public Config getConfig(){
        return new Gson().fromJson(rawConfig, Config.class);
    }



}
