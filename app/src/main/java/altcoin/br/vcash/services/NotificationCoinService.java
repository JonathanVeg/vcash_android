package altcoin.br.vcash.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import altcoin.br.vcash.MainActivity;
import altcoin.br.vcash.R;
import altcoin.br.vcash.utils.InternetRequests;
import altcoin.br.vcash.utils.Utils;

public class NotificationCoinService extends Service {
    private Timer timer = new Timer();

    public IBinder onBind(Intent arg0) {
        return null;
    }

    private String usdPrice;
    private String btcPrice;
    private String usdVolume24h;
    private String usdMarketCap;

    public void onCreate() {
        super.onCreate();

        int minutes = 1;

        timer.scheduleAtFixedRate(new mainTask(), 0, minutes * 60 * 1000);
    }

    private class mainTask extends TimerTask {
        public void run() {

            usdPrice = "0";
            btcPrice = "0";
            usdVolume24h = "0";
            usdMarketCap = "0";

            loadSummary();

        }
    }

    private void loadSummary() {
        String url = "https://api.coinmarketcap.com/v1/ticker/vcash/";

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                new atParseSummaryData(response).execute();
            }
        };

        InternetRequests internetRequests = new InternetRequests();
        internetRequests.executeGet(url, listener);
    }

    private class atParseSummaryData extends AsyncTask<Void, Void, Void> {
        String response;

        atParseSummaryData(String response) {
            this.response = response;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {

                JSONObject obj = new JSONArray(response).getJSONObject(0);

                usdPrice = Utils.numberComplete(obj.getString("price_usd"), 4);
                btcPrice = Utils.numberComplete(obj.getString("price_btc"), 8);
                usdVolume24h = Utils.numberComplete(obj.getString("24h_volume_usd"), 4);
                usdMarketCap = Utils.numberComplete(obj.getString("market_cap_usd"), 4);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            createNotification();
        }
    }

    private void createNotification() {
        Context context = getApplicationContext();
        int id = 1;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, MainActivity.class);

        TaskStackBuilder stack = TaskStackBuilder.create(context);
        stack.addParentStack(MainActivity.class);
        stack.addNextIntent(intent);

        PendingIntent pendingIntent = stack.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);

        SpannableString sb1, sb2, sb3, sb4;

        String text = "VCASH (XVC)";
        sb1 = new SpannableString(text);
        sb1.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, text.indexOf(":") + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        text = "Vol. in USD: " + Utils.numberComplete(usdVolume24h, 4);

        sb4 = new SpannableString(text);
        sb4.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, text.indexOf(":") + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(TextUtils.concat("XVC", " | " + sb1));

        builder.setSmallIcon(R.drawable.xvc_icon);
        // builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_monetization_on_white_24dp));

        builder.setPriority(Notification.PRIORITY_MIN);
        builder.setContentIntent(pendingIntent);

        text = "Last value: LASTVALUE".replaceAll("LASTVALUE", Utils.numberComplete(btcPrice, 8));
        sb1 = new SpannableString(text);
        sb1.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, text.indexOf(":") + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        text = "Vol in BTC: " + Utils.numberComplete("64564", 8);
        sb2 = new SpannableString(text);
        sb2.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, text.indexOf(":") + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        sb3 = new SpannableString(text);
        sb3.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, text.indexOf(":") + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        NotificationCompat.Style inboxStyle = new NotificationCompat.InboxStyle()
                .addLine(sb2)
                .addLine(sb3)
                .setSummaryText("Vcash Values");

        builder.setStyle(inboxStyle);

        Notification notification = builder.build();

        notification.flags = Notification.FLAG_NO_CLEAR;

        notificationManager.notify(id, notification);
    }
}
