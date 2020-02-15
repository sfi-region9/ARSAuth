package fr.charlotte.arsauth.utils;

public class Register {

    private String name;
    private String username;
    private String password;
    private String vessel;
    private String email;
    private String scc;

    public Register(String name, String username, String password, String vessel, String email, String scc) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.vessel = vessel;
        this.email = email;
        this.scc = scc;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getVessel() {
        return vessel;
    }

    public String getEmail() {
        return email;
    }

    public String getScc() {
        return scc;
    }
}
