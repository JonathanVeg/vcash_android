package altcoin.br.vcash;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import altcoin.br.vcash.adapter.AdapterWallets;
import altcoin.br.vcash.model.Wallet;

public class WalletsActivity extends AppCompatActivity {

    private List<Wallet> wallets;
    private AdapterWallets adapter;
    private RecyclerView rvWallets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallets);

        instanceObjects();
    }

    void instanceObjects() {
        wallets = new ArrayList<>();

        wallets.add(new Wallet("Vm97M6Aryyfpdj3xA2FHq99FGZadMVsTkF"));
        wallets.add(new Wallet("VocXwXjdMJuv6cerbit9FBXkwp3ShgBxzk"));
        wallets.add(new Wallet("VafRjT9uiVUasRPVdQi4KiKJJYW81BuDLQ"));

        adapter = new AdapterWallets(this, wallets);

        rvWallets = (RecyclerView) findViewById(R.id.rvWallets);

        rvWallets.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvWallets.setLayoutManager(linearLayoutManager);
        rvWallets.setAdapter(adapter);
    }

}
