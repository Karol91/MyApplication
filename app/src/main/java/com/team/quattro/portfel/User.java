package com.team.quattro.portfel;

import java.io.Serializable;

/**
 * Created by Karol on 2015-12-29.
 */
public class User implements Serializable {

    public String login;
    public String password;
    public String restKey;
    public Integer status;

    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }
}
