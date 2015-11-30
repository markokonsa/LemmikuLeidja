package ee.qualitylab.lemmikuleidja.app.activities;

import android.graphics.Color;
import android.location.Address;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.InjectView;
import ee.qualitylab.lemmikuleidja.app.R;
import ee.qualitylab.lemmikuleidja.app.service.LocationService;

public class BaseDrawerActivity extends BaseActivity {

    public static boolean enteredByHand = false;

    @InjectView(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    @InjectView(R.id.addAddressET)
    EditText addAddressET;

    LocationService locationService;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentViewWithoutInject(R.layout.activity_drawer);
        locationService = new LocationService(this);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.flContentRoot);
        LayoutInflater.from(this).inflate(layoutResID, viewGroup, true);
        injectViews();
    }

    @Override
    protected void setupToolbar() {
        super.setupToolbar();
        if (getToolbar() != null) {

            if (locationService.canGetLocation()) {
                Address address = locationService.getLocation();
                addAddressET.setText(address.getAddressLine(0));
                addAddressET.setTextColor(Color.GREEN);
            }

            addAddressET.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    addAddressET.setTextColor(Color.RED);
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });


            if (locationService.getLocationFromString(addAddressET.getText().toString()) == null || addAddressET.getText().toString().equals("")) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }

            getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            });
        }


        addAddressET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!addAddressET.getText().toString().equals("")) {
                        enteredByHand = true;
                        try {
                            Address address = locationService.getLocationFromString(addAddressET.getText().toString());
                            addAddressET.setText(address.getAddressLine(0) + ", " + address.getAddressLine(1));
                            addAddressET.setTextColor(Color.GREEN);
                        } catch (NullPointerException e) {
                            addAddressET.setTextColor(Color.RED);
                        }
                    }
                }
                return false;
            }
        });
    }

    public void onCityItemClick(MenuItem item) {
        enteredByHand = false;
        Address address = locationService.getLocationFromString(item.getTitle().toString());
        addAddressET.setText(address.getAddressLine(0) + ", " + address.getAddressLine(1));
        addAddressET.setTextColor(Color.GREEN);
    }
}
