package ee.qualitylab.lemmikuleidja.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import ee.qualitylab.lemmikuleidja.app.R;

/**
 * Created by Marko on 30.11.2015.
 */
public class SettingsAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> values;

    public SettingsAdapter(Context context, List<String> values) {
        super(context, R.layout.item_notification_list, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_notification_list, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.notificationCityName);
        Switch aSwitch = (Switch) rowView.findViewById(R.id.notification_switch);
        textView.setText(values.get(position));
        return rowView;
    }
}
