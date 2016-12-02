package altcoin.br.vcash;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import altcoin.br.vcash.utils.Utils;

public class AboutActivity extends AppCompatActivity {
    private static String TAG = "Vcash AboutActivity";

    private TextView tvAboutDeveloper;
    private TextView tvAboutCode;
    private TextView tvAboutVcash;
    private TextView tvAboutCoinMarketCap;
    private TextView tvAboutBittrex;
    private TextView tvAboutPoloniex;
    private TextView tvAboutBlinktrade;
    private TextView tvAboutBlockexperts;
    private TextView tvAboutZeroSlot;

    private LinearLayout llAboutDonate;
    private TextView tvAboutDonateWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        instanceObjects();

        prepareLinks();

        prepareFirebasePart();

        prepareListeners();
    }

    private void instanceObjects() {
        tvAboutDeveloper = (TextView) findViewById(R.id.tvAboutDeveloper);
        tvAboutCode = (TextView) findViewById(R.id.tvAboutCode);

        tvAboutVcash = (TextView) findViewById(R.id.tvAboutVcash);
        tvAboutCoinMarketCap = (TextView) findViewById(R.id.tvAboutCoinMarketCap);
        tvAboutBittrex = (TextView) findViewById(R.id.tvAboutBittrex);
        tvAboutPoloniex = (TextView) findViewById(R.id.tvAboutPoloniex);
        tvAboutBlinktrade = (TextView) findViewById(R.id.tvAboutBlinktrade);
        tvAboutBlockexperts = (TextView) findViewById(R.id.tvAboutBlockexperts);
        tvAboutZeroSlot = (TextView) findViewById(R.id.tvAboutZeroSlot);

        tvAboutDonateWallet = (TextView) findViewById(R.id.tvAboutDonateWallet);
        llAboutDonate = (LinearLayout) findViewById(R.id.llDonate);
    }


    private void prepareLinks() {
        Utils.textViewLink(tvAboutDeveloper, "https://twitter.com/jonathanveg2");
        Utils.textViewLink(tvAboutCode, "https://github.com/JonathanVeg/vcash_android");

        Utils.textViewLink(tvAboutVcash, "https://v.cash");
        Utils.textViewLink(tvAboutCoinMarketCap, "https://coinmarketcap.com/currencies/vcash/");
        Utils.textViewLink(tvAboutBittrex, "https://bittrex.com/Market/Index?MarketName=BTC-XVC");
        Utils.textViewLink(tvAboutPoloniex, "https://poloniex.com/exchange#btc_xvc");
        Utils.textViewLink(tvAboutBlinktrade, "https://blinktrade.com/docs/?shell#public-rest-api");
        Utils.textViewLink(tvAboutBlockexperts, "https://www.blockexperts.com/xvc");
        Utils.textViewLink(tvAboutZeroSlot, "http://zeroslot.com/");
    }

    private void prepareFirebasePart() {
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference showWallet = database.getReference("donation").child("show_wallet");

            // Read from the database
            showWallet.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    Boolean value = dataSnapshot.getValue(Boolean.class);

                    if (value)
                        llAboutDonate.setVisibility(View.VISIBLE);
                    else
                        llAboutDonate.setVisibility(View.GONE);

                    showWallet.keepSynced(true);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });

            final DatabaseReference wallet = database.getReference("donation").child("wallet");

            // Read from the database
            wallet.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    String value = dataSnapshot.getValue(String.class);

                    tvAboutDonateWallet.setText(value);

                    wallet.keepSynced(true);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepareListeners() {
        tvAboutDonateWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String wallet = tvAboutDonateWallet.getText().toString();

                    Utils.copyToClipboard(AboutActivity.this, wallet);

                    Toast.makeText(AboutActivity.this, "Wallet WALLET copied to clipboard".replaceAll("WALLET", wallet), Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    e.printStackTrace();

                    Toast.makeText(AboutActivity.this, "Error while copying wallet", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
