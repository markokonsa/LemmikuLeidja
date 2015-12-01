package ee.qualitylab.lemmikuleidja.app.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.InjectView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import ee.qualitylab.lemmikuleidja.app.R;
import ee.qualitylab.lemmikuleidja.app.service.LocationService;
import ee.qualitylab.lemmikuleidja.app.service.PostService;
import ee.qualitylab.lemmikuleidja.app.utilities.Utils;

import java.io.File;

public class PublishActivity extends BaseActivity {
    public static final String ARG_TAKEN_PHOTO_URI = "arg_taken_photo_uri";


    @InjectView(R.id.ivPhoto)
    ImageView ivPhoto;

    @InjectView(R.id.publish_location)
    EditText locationET;

    @InjectView(R.id.etDescription)
    EditText descriptionET;

    private Uri photoUri;
    private int photoSize;

    LocationService locationService;

    public static void openWithPhotoUri(Activity openingActivity, Uri photoUri) {
        Intent intent = new Intent(openingActivity, PublishActivity.class);
        intent.putExtra(ARG_TAKEN_PHOTO_URI, photoUri);
        openingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        locationService = new LocationService(this);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_grey600_24dp);
        photoSize = getResources().getDimensionPixelSize(R.dimen.publish_photo_thumbnail_size);

        if (locationService.canGetLocation()) {
            try {
                Address address = locationService.getLocation();
                locationET.setText(address.getAddressLine(0) + ", " + address.getAddressLine(1) + ", " + address.getAddressLine(2));
                locationET.setTextColor(Color.GREEN);
            } catch (NullPointerException e) {
                locationET.setText("");
            }
        }

        if (savedInstanceState == null) {
            photoUri = getIntent().getParcelableExtra(ARG_TAKEN_PHOTO_URI);
        } else {
            photoUri = savedInstanceState.getParcelable(ARG_TAKEN_PHOTO_URI);
        }
        updateStatusBarColor();

        ivPhoto.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                ivPhoto.getViewTreeObserver().removeOnPreDrawListener(this);
                loadThumbnailPhoto();
                return true;
            }
        });

        locationET.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    try {
                        Address address = locationService.getLocationFromString(locationET.getText().toString());
                        locationET.setText(address.getAddressLine(0) + ", " + address.getAddressLine(1) + ", " + address.getAddressLine(2));
                        locationET.setTextColor(Color.GREEN);
                    } catch (NullPointerException e) {
                        locationET.setTextColor(Color.RED);
                    }
                } else {
                    locationET.setText("");
                    locationET.setTextColor(Color.BLACK);
                }

            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (Utils.isAndroid5()) {
            getWindow().setStatusBarColor(0xff888888);
        }
    }

    private void loadThumbnailPhoto() {
        ivPhoto.setScaleX(0);
        ivPhoto.setScaleY(0);
        Picasso.with(this)
                .load(photoUri)
                .centerCrop()
                .resize(photoSize, photoSize)
                .into(ivPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        ivPhoto.animate()
                                .scaleX(1.f).scaleY(1.f)
                                .setInterpolator(new OvershootInterpolator())
                                .setDuration(400)
                                .setStartDelay(200)
                                .start();
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_publish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_publish) {
            if (validatePost()) {
                uploadPost();
                bringMainActivityToTop();
                return true;
            }
            return false;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private boolean validatePost() {
        if (photoUri.getPath() == null || locationService.getLocationFromString(locationET.getText().toString()) == null) {
            Toast.makeText(getApplicationContext(), "Kontrolli sisestatud andmeid!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void uploadPost() {
        PostService service = new PostService(this);
        service.uploadPost(new File(photoUri.getPath()), locationET.getText().toString(), descriptionET.getText().toString());
    }

    private void bringMainActivityToTop() {
        Intent intent = new Intent(this, FeedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setAction(FeedActivity.ACTION_SHOW_LOADING_ITEM);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_TAKEN_PHOTO_URI, photoUri);
    }


}
