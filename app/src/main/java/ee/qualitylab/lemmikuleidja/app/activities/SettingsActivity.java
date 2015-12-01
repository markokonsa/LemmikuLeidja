package ee.qualitylab.lemmikuleidja.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ee.qualitylab.lemmikuleidja.app.R;
import ee.qualitylab.lemmikuleidja.app.adapters.SettingsAdapter;
import ee.qualitylab.lemmikuleidja.app.objects.Settings;
import ee.qualitylab.lemmikuleidja.app.utilities.Lemmikuleidja;
import ee.qualitylab.lemmikuleidja.app.utilities.Utils;

/**
 * Created by Marko on 30.11.2015.
 */
public class SettingsActivity extends Activity {

    SharedPreferences sharedpreferences;

    EditText radiusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = getSharedPreferences(Utils.MyPREFERENCES, Context.MODE_PRIVATE);
        setContentView(R.layout.activity_settings);
        radiusText = (EditText) findViewById(R.id.radius_edittext);
        radiusText.setText(sharedpreferences.getString("radius", "15"));
        radiusText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        SettingsAdapter adapter = new SettingsAdapter(this, generateSettingsList(Lemmikuleidja.getSubscribedChannels()));
        ListView notificationList = (ListView) findViewById(R.id.notification_list);
        notificationList.setAdapter(adapter);

        radiusText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                int result = actionId & EditorInfo.IME_MASK_ACTION;

                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                        saveSettings(v);
                        break;
                }
                return true;
            }
        });
    }


        private List<Settings> generateSettingsList (List < String > channels) {
            List<Settings> settingsList = new ArrayList<>();
            settingsList.add(new Settings(true, "Aktiivsed teated"));
            if (channels != null) {
                if (!channels.isEmpty()) {
                    for (String channel : channels) {
                        settingsList.add(new Settings(false, channel));
                    }
                }
            }
            return settingsList;
        }

    public void saveSettings(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("radius", radiusText.getText().toString());
        Toast.makeText(this, "Raadius on nüüd " + radiusText.getText().toString(), Toast.LENGTH_SHORT).show();
        editor.apply();
    }
}
