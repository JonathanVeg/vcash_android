package altcoin.br.vcash.widget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Iterator;

import altcoin.br.vcash.MainActivity;
import altcoin.br.vcash.R;
import altcoin.br.vcash.data.DBTools;
import altcoin.br.vcash.utils.Bitcoin;
import altcoin.br.vcash.utils.InternetRequests;
import altcoin.br.vcash.utils.Utils;

public class CoinWidgetProvider extends AppWidgetProvider {

    private static String WIDGET_BUTTON = "android.appwidget.action.UPDATE_XVC_WIDGET";

    @Override
    public void onReceive(Context context, Intent intent) {
        // AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        if (WIDGET_BUTTON.equals(intent.getAction())) {
            try {
                Toast.makeText(context, "Updating widget", Toast.LENGTH_LONG).show();

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());

                ComponentName thisWidget = new ComponentName(context.getApplicationContext(), CoinWidgetProvider.class);

                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

                if (appWidgetIds != null && appWidgetIds.length > 0) {
                    onUpdate(context, appWidgetManager, appWidgetIds);
                }

                Utils.answersLog("manuallyWidgetUpdate", "manuallyWidgetUpdate", "0002");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(final Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        DBTools db = new DBTools(context);

        for (final int appWidgetId : appWidgetIds) {

            if (db.search("select exchange, fiat from coin_widgets where widget_id = WID".replaceAll("WID", appWidgetId + "")) > 0) {

                if (db.getData(0).equalsIgnoreCase("poloniex"))
                    loadDataFromPoloniex(context, appWidgetManager, appWidgetId, db.getData(1));
                else if (db.getData(0).equalsIgnoreCase("bittrex"))
                    loadDataFromBittrex(context, appWidgetManager, appWidgetId, db.getData(1));

            } else
                loadDataFromPoloniex(context, appWidgetManager, appWidgetId, db.getData(1));
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
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

    private void loadDataFromPoloniex(final Context context, AppWidgetManager appWidgetManager, final int appWidgetId, final String fiat) {
        final AppWidgetManager manager = appWidgetManager;

        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_coin);

        views.setTextViewText(R.id.tvWidNameCoin, "XVC - Polo - " + getHour());

        String url = "https://poloniex.com/public?command=returnTicker";

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = getSpecificSummary(response);

                    final String last = obj.getString("last");

                    views.setTextViewText(R.id.tvWidValInBtc, Utils.numberComplete(last, 8));

                    Response.Listener<String> listener2 = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject obj = new JSONObject(response);

                                views.setTextViewText(R.id.tvWidValInFiat, Utils.numberComplete(Double.parseDouble(last) * obj.getDouble("last"), 4));

                                Intent openApp = new Intent(context, MainActivity.class);

                                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openApp, 0);

                                views.setOnClickPendingIntent(R.id.tvWidNameCoin, pendingIntent);

                                Intent intent = new Intent(WIDGET_BUTTON);
                                PendingIntent pendingIntentUpdate = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                views.setOnClickPendingIntent(R.id.ivWidLogo, pendingIntentUpdate);

                                manager.updateAppWidget(appWidgetId, views);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    if (fiat != null && fiat.equalsIgnoreCase("BRL")) {
                        views.setTextViewText(R.id.tvWidFiatName, "BRL: ");
                        Bitcoin.convertBtcToBrl(listener2);
                    } else {
                        views.setTextViewText(R.id.tvWidFiatName, "USD: ");
                        Bitcoin.convertBtcToUsd(listener2);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {

                    views.setTextViewText(R.id.tvWidValInBtc, "...");
                    views.setTextViewText(R.id.tvWidValInFiat, "...");

                } catch (Exception e) {
                    e.printStackTrace();
                }

                manager.updateAppWidget(appWidgetId, views);
            }
        };

        InternetRequests internetRequests = new InternetRequests();
        internetRequests.executePost(url, listener, errorListener);
    }

    private void loadDataFromBittrex(final Context context, AppWidgetManager appWidgetManager, final int appWidgetId, final String fiat) {
        final AppWidgetManager manager = appWidgetManager;

        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_coin);

        views.setTextViewText(R.id.tvWidNameCoin, "XVC - Trex - " + getHour());

        String url = "https://bittrex.com/api/v1.1/public/getmarketsummary?market=BTC-XVC";

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject obj = new JSONObject(response);

                    if (obj.getBoolean("success")) {
                        obj = obj.getJSONArray("result").getJSONObject(0);

                        final String last = obj.getString("Last");

                        views.setTextViewText(R.id.tvWidValInBtc, Utils.numberComplete(obj.getString("Last"), 8));

                        Response.Listener<String> listener2 = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject obj = new JSONObject(response);

                                    views.setTextViewText(R.id.tvWidValInFiat, Utils.numberComplete(Double.parseDouble(last) * obj.getDouble("last"), 4));

                                    Intent openApp = new Intent(context, MainActivity.class);

                                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openApp, 0);

                                    views.setOnClickPendingIntent(R.id.tvWidNameCoin, pendingIntent);

                                    Intent intent = new Intent(WIDGET_BUTTON);
                                    PendingIntent pendingIntentUpdate = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    views.setOnClickPendingIntent(R.id.ivWidLogo, pendingIntentUpdate);

                                    manager.updateAppWidget(appWidgetId, views);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };

                        if (fiat != null && fiat.equalsIgnoreCase("BRL")) {
                            views.setTextViewText(R.id.tvWidFiatName, "BRL: ");
                            Bitcoin.convertBtcToBrl(listener2);
                        } else {
                            views.setTextViewText(R.id.tvWidFiatName, "USD: ");
                            Bitcoin.convertBtcToUsd(listener2);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {

                    views.setTextViewText(R.id.tvWidValInBtc, "...");
                    views.setTextViewText(R.id.tvWidValInFiat, "...");

                } catch (Exception e) {
                    e.printStackTrace();
                }

                manager.updateAppWidget(appWidgetId, views);
            }
        };

        InternetRequests internetRequests = new InternetRequests();
        internetRequests.executePost(url, listener, errorListener);
    }

    private String getHour() {
        Calendar c = Calendar.getInstance();

        String h = "" + c.get(Calendar.HOUR);
        String m = "" + c.get(Calendar.MINUTE);

        if (h.length() == 1) h = "0" + h;

        if (m.length() == 1) m = "0" + m;

        if (h.equals("00")) {
            int a = c.get(Calendar.AM_PM);

            if (a == Calendar.PM)
                h = "12";
        }

        return h + ":" + m;
    }

}
