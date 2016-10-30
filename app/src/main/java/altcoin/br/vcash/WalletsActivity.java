package altcoin.br.vcash;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import altcoin.br.vcash.adapter.AdapterWallets;
import altcoin.br.vcash.model.Wallet;

public class WalletsActivity extends AppCompatActivity {

    private List<Wallet> wallets;
    private AdapterWallets adapter;
    private RecyclerView rvWallets;

    private Button bWalletsAddSave;
    private EditText etWalletsAdd;

    private SwipeRefreshLayout srWallets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallets);

        instanceObjects();

        prepareListeners();
    }

    void instanceObjects() {
        srWallets = (SwipeRefreshLayout) findViewById(R.id.srWallets);

        bWalletsAddSave = (Button) findViewById(R.id.bWalletsAddSave);
        etWalletsAdd = (EditText) findViewById(R.id.etWalletsAdd);

        wallets = Wallet.loadAll(this);

        // list of rich wallets from block experts. Used when I need to test it
        
        // wallets.add(new Wallet("Vm97M6Aryyfpdj3xA2FHq99FGZadMVsTkF"));
        // wallets.add(new Wallet("VocXwXjdMJuv6cerbit9FBXkwp3ShgBxzk"));
        // wallets.add(new Wallet("VafRjT9uiVUasRPVdQi4KiKJJYW81BuDLQ"));

        adapter = new AdapterWallets(this, wallets);

        rvWallets = (RecyclerView) findViewById(R.id.rvWallets);

        rvWallets.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvWallets.setLayoutManager(linearLayoutManager);
        rvWallets.setAdapter(adapter);
    }

    void prepareListeners() {
        srWallets.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                rvWallets.invalidate();

                adapter.notifyDataSetChanged();

                srWallets.setRefreshing(false);
            }
        });

        bWalletsAddSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();

                if (verifyEditTextNull(etWalletsAdd)) {
                    Wallet wallet = new Wallet(etWalletsAdd.getText().toString().trim());

                    if (wallet.save(WalletsActivity.this)) {
                        wallets.add(wallet);

                        adapter.notifyDataSetChanged();

                        etWalletsAdd.setText("");
                    }
                }
            }
        });

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int pos = viewHolder.getAdapterPosition();

                if (wallets.get(pos).delete(WalletsActivity.this)) {
                    wallets.remove(pos);

                    adapter.notifyDataSetChanged();

                    Toast.makeText(WalletsActivity.this, "Wallet removed", Toast.LENGTH_LONG).show();
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);

        itemTouchHelper.attachToRecyclerView(rvWallets);
    }

    private void hideKeyboard() {
        try {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean verifyEditTextNull(EditText et) {
        if (et.getText().toString().equals("")) {
            Toast.makeText(this, "You need to fill the box", Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;
    }
}
