package altcoin.br.vcash;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import altcoin.br.vcash.utils.Utils;

public class AboutActivity extends AppCompatActivity {
    TextView tvAboutVcash;
    TextView tvAboutCoinMarketCap;
    TextView tvAboutBittrex;
    TextView tvAboutPoloniex;
    TextView tvAboutBlinktrade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        instanceObjects();
    }

    void instanceObjects() {
        tvAboutVcash = (TextView) findViewById(R.id.tvAboutVcash);
        tvAboutCoinMarketCap = (TextView) findViewById(R.id.tvAboutCoinMarketCap);
        tvAboutBittrex = (TextView) findViewById(R.id.tvAboutBittrex);
        tvAboutPoloniex = (TextView) findViewById(R.id.tvAboutPoloniex);
        tvAboutBlinktrade = (TextView) findViewById(R.id.tvAboutBlinktrade);

        Utils.textViewLink(tvAboutVcash, "https://v.cash");
        Utils.textViewLink(tvAboutCoinMarketCap, "https://coinmarketcap.com/currencies/vcash/");
        Utils.textViewLink(tvAboutBittrex, "https://bittrex.com/Market/Index?MarketName=BTC-XVC");
        Utils.textViewLink(tvAboutPoloniex, "https://poloniex.com/exchange#btc_xvc");
        Utils.textViewLink(tvAboutBlinktrade, "https://blinktrade.com/docs/?shell#public-rest-api");
    }

}
