package fr.colin.arsauth;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.google.gson.Gson;
import fr.colin.arsauth.config.Config;
import fr.colin.arsauth.config.ConfigWrapper;
import fr.colin.arsauth.utils.Database;
import fr.colin.arsauth.utils.Login;
import fr.colin.arsauth.utils.Register;
import fr.colin.arssdk.ARSdk;
import fr.colin.arssdk.objects.User;
import org.apache.commons.lang3.StringUtils;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

import static spark.Spark.*;

public class ARSAuth {


    private static Database db;
    private static ARSdk sdk = ARSdk.DEFAULT_INSTANCE;

    public static void main(String... args) {
        Config c = new ConfigWrapper().getConfig();
        db = new Database(c.getDB_HOST(), c.getDB_NAME(), c.getDB_USER(), c.getDB_PASSWORD());

        System.out.println("   ");
        System.out.println("Welcome in ARSAuth v1.0");
        System.out.println("   ");

        port(6666);
        setupRoutes();
    }

    public static void setupRoutes() {


        get("hello", (request, response) -> "Hello World");

        post("login", (request, response) -> {
            String string = request.body();
            Login login = new Gson().fromJson(string, Login.class);
            return processLogin(login);
        });

        post("register", (request, response) -> {
            String string = request.body();
            Register register = new Gson().fromJson(string, Register.class);
            return processRegister(register);
        });

        post("destroy_user", (request, response) -> {
            String string = request.body();
            User user = new Gson().fromJson(string, User.class);
            return processDestroy(user);
        });
    }

    public static String processLogin(Login login) throws SQLException {
        String user = login.getUsername();
        String password = login.getPassword();
        String[] log = login(user, password);
        if (Boolean.parseBoolean(log[0])) {
            //TODO : RETURN ALL THE VALUES
            return log[1];
        } else {
            return "Error while login, please try again or on the website https://reports.nwa2coco.fr : " + log[1];
        }
    }

    public static String processDestroy(User user) {
        if (!checkID(user))
            return "Invalid ID";
        db.update("DELETE FROM users WHERE SCC='" + user.getScc() + "'");
        return "User destroyed";
    }

    public static boolean checkID(User user) {
        ResultSet rs = db.getResult("SELECT * FROM users WHERE SCC='" + user.getScc() + "'");
        try {
            if (rs.next()) {
                if (rs.getString("uuid").equalsIgnoreCase(user.getUuid())) {
                    return true;
                }
                return false;
            } else {
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public static String processRegister(Register register) throws IOException, SQLException {
        String name = register.getName();
        String user = register.getUsername();
        String password = register.getPassword();
        String vaisseau = register.getVessel();
        String email = register.getEmail();
        String scc = register.getScc();
        String[] sd = register(name, user, password, vaisseau, email, scc);
        if (Boolean.parseBoolean(sd[0])) {
            sdk.registerUser(new User(name, scc, vaisseau, "", sd[1]));
            return "You are succesfully registred in the database :) !, You can now login with the app or the website";
        } else {
            return "Error while register, " + sd[1];
        }

    }

    private static String[] login(String user, String password) throws SQLException {
        ResultSet rs = db.getResult("SELECT * FROM users WHERE username='" + user + "'");
        if (!rs.next())
            return new String[]{"false", "Unknow username"};
        String pass = (String) db.read("SELECT * FROM users WHERE username='" + user + "'", "password");
        if (BCrypt.verifyer().verify(password.toCharArray(), pass.toCharArray()).verified) {
            String username = rs.getString("username");
            String scc = rs.getString("scc");
            String vesselid = rs.getString("vesselid");
            String name = rs.getString("name");
            String messengerid = rs.getString("messengerid");
            String uuid = rs.getString("uuid");
            String[] s = {username, scc, vesselid, name, messengerid, uuid};
            return new String[]{"true", StringUtils.join(s, "}_}")};
        } else {
            return new String[]{"false", "bad password"};
        }
    }

    public static String[] register(String name, String user, String password, String vessel, String email, String scc) throws SQLException {

        if (name.length() < 4)
            return new String[]{"false", "Name too short"};
        if (user.length() < 4)
            return new String[]{"false", "User too short"};

        ResultSet rs = db.getResult("SELECT * FROM users WHERE username='" + user + "'");
        if (rs.next()) {
            return new String[]{"false", "Username already exist"};
        }
        rs.close();

        ResultSet sd = db.getResult("SELECT * FROM users WHERE mail='" + email + "'");
        if (sd.next())
            return new String[]{"false", "email already exist"};
        sd.close();

        ResultSet sdf = db.getResult("SELECT * FROM users WHERE scc='" + scc + "'");
        if (sdf.next())
            return new String[]{"false", "scc already exist"};
        sd.close();
        String uuid = UUID.randomUUID().toString();
        password = BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(10, password.toCharArray());
        db.update(String.format("INSERT INTO users(username,scc,vesselid,name,mail,password,uuid,messengerid) VALUES('%s','%s','%s','%s','%s','%s','%s','undefined')", user, scc, vessel, name, email, password, uuid));

        return new String[]{"true", uuid};
    }

}
