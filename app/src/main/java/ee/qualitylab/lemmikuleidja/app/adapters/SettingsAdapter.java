package ee.qualitylab.lemmikuleidja.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ee.qualitylab.lemmikuleidja.app.R;
import ee.qualitylab.lemmikuleidja.app.activities.SettingsActivity;
import ee.qualitylab.lemmikuleidja.app.utilities.Lemmikuleidja;

public class SettingsAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> values;

    public SettingsAdapter(Context context, List<String> values) {
        super(context, R.layout.item_notification_list, values);
        this.context = context;
        values.add(0,"Aktiivsed teated");
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;
        if (position > 0) {
            rowView = inflater.inflate(R.layout.item_notification_list, parent, false);
            Switch aSwitch = (Switch) rowView.findViewById(R.id.notification_switch);
            aSwitch.setText(values.get(position));

            aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Lemmikuleidja.subscribe(buttonView.getText().toString());
                        Toast.makeText(getContext(), "Teated on sisse lülitatud linnas: " + buttonView.getText().toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Lemmikuleidja.unsubscribe(buttonView.getText().toString());
                        Toast.makeText(getContext(), "Teated on välja lülitatud linnast: " + buttonView.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            rowView = inflater.inflate(R.layout.header_notification_list, parent, false);
            TextView header = (TextView) rowView.findViewById(R.id.separator);
            header.setText(values.get(position));
        }
        return rowView;
    }
}
