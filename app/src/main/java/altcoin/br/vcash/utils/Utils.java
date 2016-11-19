package altcoin.br.vcash.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.CLIPBOARD_SERVICE;

public class Utils {

    public static void textViewLink(TextView tv, String url) {
        // make a textview clickable

        if (tv == null || url == null) return;

        String label = tv.getText().toString();

        tv.setText(Html.fromHtml("<a href=\"" + url + "\">" + label + "</a> "));
        tv.setMovementMethod(LinkMovementMethod.getInstance());

        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(tv.getText());
        ssb.setSpan(new URLSpan("#"), 0, ssb.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(ssb, TextView.BufferType.SPANNABLE);
    }

    public static String numberComplete(double number, int decimalPlaces) {
        return numberComplete("" + number, decimalPlaces);
    }

    public static String numberComplete(String number, int decimalPlaces) {
        // I like to use every value in altcoins always with 8 decimal cases.
        try {
            BigDecimal bd = new BigDecimal(number);

            bd = bd.setScale(decimalPlaces, BigDecimal.ROUND_DOWN);

            return bd.toPlainString();
        } catch (Exception e) {
            return "";
        }
    }

    public static void copyToClipboard(Context context, String text) {

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("wallet", text);
        clipboard.setPrimaryClip(clip);

    }

    public static long timestampLong() {
        return (System.currentTimeMillis() / 1000);
    }

    public static String now() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

    public static void log(String text) {
        log("Globals Log", text);
    }

    private static void log(String tag, String text) {
        Log.e(tag, text);
    }

    public static void writePreference(Context context, String key, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        preferences.edit().putString(key, value).apply();
    }

    public static void writePreference(Context context, String key, Boolean value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        preferences.edit().putBoolean(key, value).apply();
    }

    public static String readPreference(Context context, String key, String defaultValue) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        return preferences.getString(key, defaultValue);
    }

    public static boolean readPreference(Context context, String key, boolean defaultValue) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        return preferences.getBoolean(key, defaultValue);
    }

    public static void answersLog(String contentName, String contentType, String contentId) {
        try {
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName(contentName)
                    .putContentType(contentType)
                    .putContentId(contentId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
