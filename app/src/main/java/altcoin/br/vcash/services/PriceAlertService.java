package altcoin.br.vcash.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.SpannableString;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import altcoin.br.vcash.MainActivity;
import altcoin.br.vcash.R;
import altcoin.br.vcash.data.DBTools;
import altcoin.br.vcash.model.Alert;
import altcoin.br.vcash.utils.InternetRequests;
import altcoin.br.vcash.utils.Utils;

public class PriceAlertService extends Service {
    private Timer timer = new Timer();

    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onCreate() {
        super.onCreate();

        int minutes = 5;

        // if (minutes < 1) minutes = 1;
        // if (minutes >= 9999) minutes = 9999;

        // timer.scheduleAtFixedRate(new mainTask(), 0, minutes * 60 * 1000);

        timer.scheduleAtFixedRate(new mainTask(), 0, 20 * 1000); // 20 segundos (para testes)
    }

    private class mainTask extends TimerTask {
        public void run() {
            List<Alert> alerts = new ArrayList<>();

            DBTools db = new DBTools(getApplicationContext());

            try {
                int count = db.search("select _id, awhen, value, active from alerts where active = 1");

                Alert alert;

                for (int i = 0; i < count; i++) {
                    alert = new Alert(getApplicationContext());

                    alert.setId(db.getData(i, 0));
                    alert.setWhen(db.getData(i, 1));
                    alert.setValue(db.getData(i, 2));
                    alert.setActive(Utils.isTrue(db.getData(i, 3)));

                    if (alert.isActive())
                        alerts.add(alert);
                }

                prepareNotification(alerts);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.close();
            }
        }
    }

    private void createNotification(int id, String title, String contentText, SpannableString line1, SpannableString line2, SpannableString line3) {
        Context context = getApplicationContext();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, MainActivity.class);

        TaskStackBuilder stack = TaskStackBuilder.create(context);
        stack.addNextIntent(intent);

        PendingIntent pendingIntent = stack.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(title);

        builder.setContentText(contentText);

        builder.setSmallIcon(R.drawable.ic_monetization_on_white_36dp);

        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setContentIntent(pendingIntent);

        NotificationCompat.Style inboxStyle = new NotificationCompat.InboxStyle()
                .addLine(line1)
                .addLine(line2)
                .addLine(line3)
                .setSummaryText("Vcash");

        builder.setStyle(inboxStyle);

        Notification notification = builder.build();

        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_AUTO_CANCEL;

        notification.ledARGB = 0xFFffff00;
        notification.ledOnMS = 500;
        notification.ledOffMS = 1000;

        notification.vibrate = new long[]{150, 300, 150, 300};

        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;

        try {
            Uri song = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Ringtone ringtone = RingtoneManager.getRingtone(this, song);

            ringtone.play();
        } catch (Exception ignored) {
        }

        notificationManager.notify(id, notification);
    }

    private void loadBittrexData(final Alert alert) {
        String url = "https://bittrex.com/api/v1.1/public/getmarketsummary?market=BTC-XVC";

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                new atParseBittrexData(response, alert).execute();
            }
        };

        InternetRequests internetRequests = new InternetRequests();
        internetRequests.executePost(url, listener);
    }

    private void loadPoloniexData(final Alert alert) {
        String url = "https://poloniex.com/public?command=returnTicker";

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                new atParsePoloniexData(response, alert).execute();
            }
        };

        InternetRequests internetRequests = new InternetRequests();
        internetRequests.executePost(url, listener);
    }

    private class atParseBittrexData extends AsyncTask<Void, Void, Void> {
        String response;

        double last;

        Alert alert;

        atParseBittrexData(String response, Alert alert) {
            this.response = response;
            this.alert = alert;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {

                JSONObject obj = new JSONObject(response);

                if (obj.getBoolean("success")) {
                    obj = obj.getJSONArray("result").getJSONObject(0);

                    last = Double.parseDouble(obj.getString("Last"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            String nameCoin = "XVC";

            String text = getString(R.string.alert_reached_for).replaceAll("COIN", nameCoin).replaceAll("EXCHANGE", "Bittrex");

            if ((alert.getWhen() == Alert.GREATER && last > alert.getValueDouble()) || (alert.getWhen() == Alert.LOWER && last < alert.getValueDouble())) {

                SpannableString line1 = new SpannableString(Html.fromHtml(text));

                int notificationId;

                if (alert.getWhen() == Alert.GREATER) {
                    notificationId = hash("bittrexgreater" + alert.getValue());

                    text = getString(R.string.gets_greater_than).replace("COIN", nameCoin).replace("VALUE", Utils.numberComplete(alert.getValueDouble(), 8));
                } else {
                    notificationId = hash("bittrexlower" + alert.getValue());

                    text = getString(R.string.gets_lower_than).replace("COIN", nameCoin).replace("VALUE", Utils.numberComplete(alert.getValueDouble(), 8));
                }

                SpannableString line2 = new SpannableString(Html.fromHtml(text));

                text = getString(R.string.alert_last_value).replace("VALUE", Utils.numberComplete(last, 8));
                SpannableString line3 = new SpannableString(Html.fromHtml(text));

                text = getString(R.string.alert_reached_for).replace("COIN", nameCoin).replaceAll("EXCHANGE", "Bittrex");

                createNotification(notificationId, "VCash - Alert", Html.fromHtml(text).toString(), line1, line2, line3);

                alert.setActive(false);

                alert.save();
            }
        }
    }

    private class atParsePoloniexData extends AsyncTask<Void, Void, Void> {
        String response;

        double last;

        Alert alert;

        atParsePoloniexData(String response, Alert alert) {
            this.response = response;
            this.alert = alert;
        }

        JSONObject getSpecificSummary(String response) {
            try {
                String coin = "XVC";

                JSONObject jObject = new JSONObject(response);

                Iterator<?> keys = jObject.keys();

                JSONObject jsonObj;

                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    if (jObject.get(key) instanceof JSONObject) {
                        jsonObj = (JSONObject) jObject.get(key);

                        if (key.startsWith("BTC_") && key.toLowerCase().contains(coin.toLowerCase())) {

                            return jsonObj;

                        }
                    }
                }

                return null;
            } catch (Exception e) {
                e.printStackTrace();

                return null;
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                JSONObject obj = getSpecificSummary(response);

                last = Double.parseDouble(obj.getString("last"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            String nameCoin = "XVC";

            String text = getString(R.string.alert_reached_for).replaceAll("COIN", nameCoin).replaceAll("EXCHANGE", "Poloniex");

            if ((alert.getWhen() == Alert.GREATER && last > alert.getValueDouble()) || (alert.getWhen() == Alert.LOWER && last < alert.getValueDouble())) {

                SpannableString line1 = new SpannableString(Html.fromHtml(text));

                int notificationId;

                if (alert.getWhen() == Alert.GREATER) {
                    notificationId = hash("poloniexgreater" + alert.getValue());

                    text = getString(R.string.gets_greater_than).replace("COIN", nameCoin).replace("VALUE", Utils.numberComplete(alert.getValueDouble(), 8));
                } else {
                    notificationId = hash("poloniexlower" + alert.getValue());

                    text = getString(R.string.gets_lower_than).replace("COIN", nameCoin).replace("VALUE", Utils.numberComplete(alert.getValueDouble(), 8));
                }

                SpannableString line2 = new SpannableString(Html.fromHtml(text));

                text = getString(R.string.alert_last_value).replace("VALUE", Utils.numberComplete(last, 8));
                SpannableString line3 = new SpannableString(Html.fromHtml(text));

                text = getString(R.string.alert_reached_for).replace("COIN", nameCoin).replaceAll("EXCHANGE", "Poloniex");

                createNotification(notificationId, "VCash - Alert", Html.fromHtml(text).toString(), line1, line2, line3);

                alert.setActive(false);

                alert.save();
            }
        }

    }

    private void prepareNotification(final List<Alert> alerts) {
        for (int i = 0; i < alerts.size(); i++) {

            final Alert alert = alerts.get(i);

            loadBittrexData(alert);
            loadPoloniexData(alert);
        }
    }

    private int hash(String str) {
        String s = str.replaceAll(" ", "");

        int h = 0;

        for (int i = 0; i < s.length(); i++)
            h = 31 * h + s.charAt(i);

        return h;
    }
}
