package ee.qualitylab.lemmikuleidja.app.utilities;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.parse.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ee.qualitylab.lemmikuleidja.app.activities.FeedActivity;

import static com.parse.Parse.initialize;
import static com.parse.ParseInstallation.*;

public class Lemmikuleidja extends Application {

    public static final String PARSE_DATA_KEY = "com.parse.Data";

    public static List<String> getSubscribedChannels() {
        return ParseInstallation.getCurrentInstallation().getList("channels");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initialize(this, "tX1mrowGfe2zCN1r2D6XvNCujzpLIPnRfsAp9qBs", "5LC0ETO6yhuw2RgZHWhpfMQhtGt3OPZ4duz7hIiI");
        getCurrentInstallation().saveInBackground();
    }

    public static void subscribe(String channel) {
        ParsePush.subscribeInBackground(replaceSpecialCharacters(channel));
    }

    public static void unsubscribe(String channel) {
        ParsePush.unsubscribeInBackground(replaceSpecialCharacters(channel));
    }

    public static String getDataFromIntent(Intent intent) {
        String city = null;
        try {
            if (intent.getExtras() != null && intent.getExtras().getString(PARSE_DATA_KEY) != null) {
                JSONObject data = new JSONObject(intent.getExtras().getString(PARSE_DATA_KEY));
                city = data.get("city").toString();
            }
        } catch (JSONException e) {
            // Json was not readable...
        }
        return city;
    }

    public static String replaceSpecialCharacters(String oldValue){
        String string = oldValue;
        if(oldValue.contains("ä")){
            string = oldValue.replaceAll("ä","a");
        }else if (oldValue.contains("õ")){
            string = oldValue.replaceAll("õ","o");
        }else if (oldValue.contains("ü")){
            string = oldValue.replaceAll("ü","u");
        }else if (oldValue.contains("ö")){
            string = oldValue.replaceAll("ö","o");
        }
        return string;
    }

}
