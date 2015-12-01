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
import java.util.Set;

import ee.qualitylab.lemmikuleidja.app.R;
import ee.qualitylab.lemmikuleidja.app.objects.Settings;
import ee.qualitylab.lemmikuleidja.app.utilities.Lemmikuleidja;

public class SettingsAdapter extends ArrayAdapter<Settings> {
    private final Context context;
    private final List<Settings> values;

    public SettingsAdapter(Context context, List<Settings> values) {
        super(context, R.layout.item_notification_list, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;
        if (values.get(position).isHeader()) {
            rowView = inflater.inflate(R.layout.header_separator_list, parent, false);
            TextView header = (TextView) rowView.findViewById(R.id.separator);
            header.setText(values.get(position).getTitle());
        } else {
            rowView = inflater.inflate(R.layout.item_notification_list, parent, false);
            Switch aSwitch = (Switch) rowView.findViewById(R.id.notification_switch);
            aSwitch.setText(values.get(position).getTitle());

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
        }
        return rowView;
    }
}
