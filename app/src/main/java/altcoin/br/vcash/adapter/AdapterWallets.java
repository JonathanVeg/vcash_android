package altcoin.br.vcash.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import altcoin.br.vcash.MainActivity;
import altcoin.br.vcash.R;
import altcoin.br.vcash.model.Wallet;
import altcoin.br.vcash.utils.InternetRequests;
import altcoin.br.vcash.utils.Utils;

public class AdapterWallets extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<Wallet> wallets;

    public AdapterWallets(Context context, List<Wallet> wallets) {
        this.wallets = wallets;

        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.row_wallets, parent, false);

        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        myViewHolder h = ((myViewHolder) holder);

        try {
            // preencher os valores aqui
            Wallet wallet = wallets.get(holder.getAdapterPosition());

            h.tvWalletsAddress.setText(wallet.getAddress());

            loadBalance(wallet, h);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return wallets.size();
    }

    private class myViewHolder extends RecyclerView.ViewHolder {

        TextView tvWalletsAddress;
        TextView tvWalletsBalance;
        TextView tvWalletsBalanceInBtc;
        TextView tvWalletsBalanceInUsd;

        myViewHolder(View v) {
            super(v);

            tvWalletsAddress = (TextView) v.findViewById(R.id.tvWalletsAddress);
            tvWalletsBalance = (TextView) v.findViewById(R.id.tvWalletsBalance);
            tvWalletsBalanceInBtc = (TextView) v.findViewById(R.id.tvWalletsBalanceInBtc);
            tvWalletsBalanceInUsd = (TextView) v.findViewById(R.id.tvWalletsBalanceInUsd);
        }
    }

    private void loadBalance(final Wallet wallet, final myViewHolder h) {
        String url = "https://www.blockexperts.com/api?coin=xvc&action=getbalance&address=ADDRESS".replaceAll("ADDRESS", wallet.getAddress());

        InternetRequests ir = new InternetRequests();

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                wallet.setBalance(response);

                h.tvWalletsBalance.setText(Utils.numberComplete(wallet.getBalance(), 8));

                String url = "https://api.coinmarketcap.com/v1/ticker/vcash/";

                Response.Listener<String> listener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        new atParseSummaryData(response, h, wallet).execute();
                    }
                };

                InternetRequests internetRequests = new InternetRequests();
                internetRequests.executeGet(url, listener);
            }
        };

        ir.executeGet(url, listener);
    }

    private class atParseSummaryData extends AsyncTask<Void, Void, Void> {
        String response;

        double usdPrice;
        double btcPrice;

        myViewHolder h;

        Wallet wallet;

        atParseSummaryData(String response, myViewHolder h, Wallet wallet) {
            this.response = response;

            this.h = h;

            this.wallet = wallet;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {

                JSONObject obj = new JSONArray(response).getJSONObject(0);

                usdPrice = obj.getDouble("price_usd");
                btcPrice = obj.getDouble("price_btc");

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            h.tvWalletsBalanceInBtc.setText(Utils.numberComplete(Double.parseDouble(wallet.getBalance()) * btcPrice, 8));

            h.tvWalletsBalanceInUsd.setText(Utils.numberComplete(Double.parseDouble(wallet.getBalance()) * usdPrice, 4));
            ;
        }
    }
}
