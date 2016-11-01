package altcoin.br.vcash.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

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

            return db.search("select * from wallets where address = 'ADDRESS'".replaceAll("ADDRESS", getAddress())) <= 0
                    && db.exec("insert into wallets (address, last_balance) values('ADDRESS', 'BALANCE')".replaceAll("ADDRESS", getAddress()).replaceAll("BALANCE", getBalance()));

        } catch (Exception e) {
            e.printStackTrace();

            return false;
        } finally {
            db.close();
        }
    }

    public boolean delete(Context c) {
        DBTools db = new DBTools(c);

        try {

            return db.exec("delete from wallets where address = 'ADDRESS'".replaceAll("ADDRESS", getAddress()));

        } catch (Exception e) {
            e.printStackTrace();

            return false;
        } finally {
            db.close();
        }
    }

    public static List<Wallet> loadAll(Context c) {
        DBTools db = new DBTools(c);

        List<Wallet> wallets = new ArrayList<>();

        try {

            for (int i = 0; i < db.search("select address from wallets"); i++)
                wallets.add(new Wallet(db.getData(i, 0)));

            return wallets;

        } catch (Exception e) {
            e.printStackTrace();

            return wallets;
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
