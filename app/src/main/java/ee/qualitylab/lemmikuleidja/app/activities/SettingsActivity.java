package ee.qualitylab.lemmikuleidja.app.activities;

import android.app.ListActivity;
import android.os.Bundle;

import ee.qualitylab.lemmikuleidja.app.R;
import ee.qualitylab.lemmikuleidja.app.adapters.SettingsAdapter;
import ee.qualitylab.lemmikuleidja.app.utilities.Lemmikuleidja;

/**
 * Created by Marko on 30.11.2015.
 */
public class SettingsActivity  extends ListActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SettingsAdapter adapter = new SettingsAdapter(this, Lemmikuleidja.getSubscribedChannels());
        setListAdapter(adapter);
    }


}
