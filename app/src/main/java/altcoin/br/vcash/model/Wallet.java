package altcoin.br.vcash.model;

import android.content.Context;

import altcoin.br.vcash.data.DBTools;

public class Wallet {
    private String address;
    private String balance;

    public Wallet() {
        this.address = "";
        this.balance = "0";
    }

    public Wallet(String address) {
        this.address = address;
        this.balance = "0";
    }

    public boolean save(Context c) {
        DBTools db = new DBTools(c);

        try {

            return db.exec("insert into wallets (address) values('ADDRESS')".replaceAll("ADDRESS", getAddress()));

        } catch (Exception e) {
            e.printStackTrace();

            return false;
        } finally {
            db.close();
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
