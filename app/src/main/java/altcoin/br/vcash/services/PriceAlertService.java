package altcoin.br.vcash.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.SpannableString;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import altcoin.br.vcash.MainActivity;
import altcoin.br.vcash.R;
import altcoin.br.vcash.data.DBTools;
import altcoin.br.vcash.model.Alert;
import altcoin.br.vcash.utils.Utils;

public class PriceAlertService extends Service {
    private Timer timer = new Timer();

    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onCreate() {
        super.onCreate();

        int minutes = 5;

//        if (minutes < 1) minutes = 1;
//
//        if (minutes >= 9999) minutes = 9999;

        timer.scheduleAtFixedRate(new mainTask(), 0, minutes * 60 * 1000);

        // timer.scheduleAtFixedRate(new mainTask(), 0, 5 * 1000); // 5 segundos (para testes)
    }

    private class mainTask extends TimerTask {
        public void run() {
            List<Alert> alerts = new ArrayList<>();

            DBTools db = new DBTools(getApplicationContext());

            try {

                int count = db.search("select _id, coin, awhen, value, active from alerts where active = 1 order by coin ");

                Alert alert;

                for (int i = 0; i < count; i++) {
                    alert = new Alert(getApplicationContext());

                    alert.setId(db.getData(i, 0));
                    alert.setWhen(db.getData(i, 2));
                    alert.setValue(db.getData(i, 3));
                    alert.setActive(Utils.isTrue(db.getData(i, 4)));

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

    private void createNotification(String coin, String title, String text, String bigText, int id, int alertId) {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainActivity.class);

        TaskStackBuilder stack = TaskStackBuilder.create(this);
        stack.addNextIntent(intent);

        PendingIntent pendingIntent = stack.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(title);
        builder.setContentText(text);
        builder.setSmallIcon(R.drawable.ic_monetization_on_white_24dp);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_monetization_on_white_24dp));

        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setContentIntent(pendingIntent);

        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(bigText));

        Notification n = builder.build();

        n.ledARGB = 0xFFffff00;
        n.ledOnMS = 500;
        n.ledOffMS = 1000;

        n.vibrate = new long[]{150, 300, 150, 300};

        n.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;

        nm.notify(id, n);

        try {
            Uri song = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Ringtone ringtone = RingtoneManager.getRingtone(this, song);

            ringtone.play();
        } catch (Exception ignored) {
        }
    }

    private void createNotification2(int id, String title, String contentText, SpannableString line1, SpannableString line2, SpannableString line3) {
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
        // builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_monetization_on_white_24dp));

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

    private void prepareNotification(final List<Alert> alerts) {
        for (int i = 0; i < alerts.size(); i++) {

            final Alert alert = alerts.get(i);

            // ler valor e preparar notificacao aqui

        }
    }
}
