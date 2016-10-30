package altcoin.br.vcash;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import altcoin.br.vcash.utils.InternetRequests;
import altcoin.br.vcash.utils.Utils;

public class CalculatorActivity extends AppCompatActivity {

    private Button bConvertBtcTo;
    private Button bConvertUsdTo;
    private Button bConvertXvcTo;
    private EditText etValueToConvertBtc;
    private EditText etValueToConvertUsd;
    private EditText etValueToConvertXvc;
    private TextView tvCalcBtcInXvc;
    private TextView tvCalcUsdInXvc;
    private TextView tvCalcXvcInBtc;
    private TextView tvCalcXvcInUsd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        intanceObjects();

        prepareListeners();
    }

    private void intanceObjects() {

        bConvertBtcTo = (Button) findViewById(R.id.bConvertBtcTo);
        bConvertUsdTo = (Button) findViewById(R.id.bConvertUsdTo);
        bConvertXvcTo = (Button) findViewById(R.id.bConvertXvcTo);

        etValueToConvertBtc = (EditText) findViewById(R.id.etValueToConvertBtc);
        etValueToConvertUsd = (EditText) findViewById(R.id.etValueToConvertUsd);
        etValueToConvertXvc = (EditText) findViewById(R.id.etValueToConvertXvc);

        tvCalcBtcInXvc = (TextView) findViewById(R.id.tvCalcBtcInXvc);
        tvCalcUsdInXvc = (TextView) findViewById(R.id.tvCalcUsdInXvc);
        tvCalcXvcInBtc = (TextView) findViewById(R.id.tvCalcXvcInBtc);
        tvCalcXvcInUsd = (TextView) findViewById(R.id.tvCalcXvcInUsd);

        // load in the lasts values used

        etValueToConvertBtc.setText(Utils.readPreference(CalculatorActivity.this, "etValueToConvertBtc", "0"));
        etValueToConvertUsd.setText(Utils.readPreference(CalculatorActivity.this, "etValueToConvertUsd", "0"));
        etValueToConvertXvc.setText(Utils.readPreference(CalculatorActivity.this, "etValueToConvertXvc", "0"));
    }

    private void execApiCall(Response.Listener<String> listener) {

        String url = "https://api.coinmarketcap.com/v1/ticker/vcash/";

        InternetRequests internetRequests = new InternetRequests();
        internetRequests.executeGet(url, listener);
    }

    private void prepareListeners() {

        bConvertBtcTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyEditTextNull(etValueToConvertBtc)) {
                    hideKeyboard();

                    Utils.writePreference(CalculatorActivity.this, "etValueToConvertBtc", etValueToConvertBtc.getText().toString());

                    Response.Listener<String> listener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                JSONObject obj = new JSONArray(response).getJSONObject(0);

                                double quantity = Double.parseDouble(etValueToConvertBtc.getText().toString());

                                tvCalcBtcInXvc.setText(Utils.numberComplete(String.format("%s", quantity / Double.parseDouble(obj.getString("price_btc"))), 8));

                            } catch (Exception e) {
                                e.printStackTrace();

                                Toast.makeText(CalculatorActivity.this, "Error while converting", Toast.LENGTH_LONG).show();
                            }
                        }
                    };

                    execApiCall(listener);
                }
            }
        });

        bConvertUsdTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyEditTextNull(etValueToConvertUsd)) {
                    hideKeyboard();

                    Utils.writePreference(CalculatorActivity.this, "etValueToConvertUsd", etValueToConvertUsd.getText().toString());

                    Response.Listener<String> listener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                JSONObject obj = new JSONArray(response).getJSONObject(0);

                                double quantity = Double.parseDouble(etValueToConvertUsd.getText().toString());

                                tvCalcUsdInXvc.setText(Utils.numberComplete(String.format("%s", quantity / Double.parseDouble(obj.getString("price_usd"))), 8));

                            } catch (Exception e) {
                                e.printStackTrace();

                                Toast.makeText(CalculatorActivity.this, "Error while converting", Toast.LENGTH_LONG).show();
                            }
                        }
                    };

                    execApiCall(listener);
                }
            }
        });

        bConvertXvcTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyEditTextNull(etValueToConvertXvc)) {
                    hideKeyboard();

                    Utils.writePreference(CalculatorActivity.this, "etValueToConvertXvc", etValueToConvertXvc.getText().toString());

                    Response.Listener<String> listener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                JSONObject obj = new JSONArray(response).getJSONObject(0);

                                double quantity = Double.parseDouble(etValueToConvertXvc.getText().toString());

                                tvCalcXvcInBtc.setText(Utils.numberComplete(String.format("%s", quantity * Double.parseDouble(obj.getString("price_btc"))), 8));

                                tvCalcXvcInUsd.setText(Utils.numberComplete(String.format("%s", quantity * Double.parseDouble(obj.getString("price_usd"))), 4));

                            } catch (Exception e) {
                                e.printStackTrace();

                                Toast.makeText(CalculatorActivity.this, "Error while converting", Toast.LENGTH_LONG).show();
                            }
                        }
                    };

                    execApiCall(listener);
                }
            }
        });

    }

    private boolean verifyEditTextNull(EditText et) {
        if (et.getText().toString().equals("")) {
            Toast.makeText(this, "You need to fill the box", Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;
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
}
