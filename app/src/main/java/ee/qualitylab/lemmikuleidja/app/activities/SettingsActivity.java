package ee.qualitylab.lemmikuleidja.app.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import ee.qualitylab.lemmikuleidja.app.R;
import ee.qualitylab.lemmikuleidja.app.adapters.SettingsAdapter;
import ee.qualitylab.lemmikuleidja.app.utilities.Lemmikuleidja;

/**
 * Created by Marko on 30.11.2015.
 */
public class SettingsActivity  extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SettingsAdapter adapter = new SettingsAdapter(this, Lemmikuleidja.getSubscribedChannels());
        ListView notificationList = (ListView) findViewById(R.id.notification_list);
        notificationList.setAdapter(adapter);
    }


}
