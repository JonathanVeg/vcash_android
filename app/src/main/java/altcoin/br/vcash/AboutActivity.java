package altcoin.br.vcash;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import altcoin.br.vcash.utils.Utils;

public class AboutActivity extends AppCompatActivity {
    private TextView tvAboutDeveloper;
    private TextView tvAboutCode;
    private TextView tvAboutVcash;
    private TextView tvAboutCoinMarketCap;
    private TextView tvAboutBittrex;
    private TextView tvAboutPoloniex;
    private TextView tvAboutBlinktrade;
    private TextView tvAboutBlockexperts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        instanceObjects();

        prepareLinks();
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
    }

}
