package altcoin.br.vcash.widget;


import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

import altcoin.br.vcash.R;
import altcoin.br.vcash.utils.InternetRequests;
import altcoin.br.vcash.utils.Utils;

public class CoinWidgetProvider extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        // AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        final AppWidgetManager manager = appWidgetManager;

        for (final int appWidgetId : appWidgetIds) {
            Utils.log(" ::: Widget Update");

            final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_coin);

            views.setTextViewText(R.id.tvWidNameCoin, "XVC - " + getHour());

            String url = "https://api.coinmarketcap.com/v1/ticker/vcash/";

            Response.Listener<String> listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject obj = new JSONArray(response).getJSONObject(0);

                        views.setTextViewText(R.id.tvWidValInBtc, Utils.numberComplete(obj.getString("price_btc"), 8));
                        views.setTextViewText(R.id.tvWidValInUsd, Utils.numberComplete(obj.getString("price_usd"), 4));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    manager.updateAppWidget(appWidgetId, views);
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {

                        views.setTextViewText(R.id.tvWidValInBtc, "Error");
                        views.setTextViewText(R.id.tvWidValInUsd, "Error");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    manager.updateAppWidget(appWidgetId, views);
                }
            };

            InternetRequests internetRequests = new InternetRequests();
            internetRequests.executeGet(url, listener);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
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
