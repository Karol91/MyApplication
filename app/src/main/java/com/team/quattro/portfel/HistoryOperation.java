package com.team.quattro.portfel;

/**
 * Created by Karol on 2016-01-01.
 */
public class HistoryOperation {

    String date;
    String typeOperationName;
    String userLogin;
    String value;
    String walletCode;

    public void setTypeOperationName(String typeOperationName) {
        this.typeOperationName = typeOperationName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setWalletCode(String walletCode) {
        this.walletCode = walletCode;
    }



    public String getDate() {
        return date;
    }

    public String getTypeOperationName() {
        return typeOperationName;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public String getValue() {
        return value;
    }

    public String getWalletCode() {
        return walletCode;
    }


}
