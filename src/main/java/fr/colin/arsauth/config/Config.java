package fr.colin.arsauth.config;

public class Config {

    private String DB_HOST;
    private String DB_NAME;
    private String DB_USER;


    public String getDB_HOST() {
        return DB_HOST;
    }

    public String getDB_NAME() {
        return DB_NAME;
    }

    public String getDB_USER() {
        return DB_USER;
    }

    public String getDB_PASSWORD() {
        return DB_PASSWORD;
    }


    private String DB_PASSWORD;

    public Config(String DB_HOST, String DB_NAME, String DB_USER, String DB_PASSWORD, String DB_USER_NAME) {
        this.DB_HOST = DB_HOST;
        this.DB_NAME = DB_NAME;
        this.DB_USER = DB_USER;
        this.DB_PASSWORD = DB_PASSWORD;
    }
}
