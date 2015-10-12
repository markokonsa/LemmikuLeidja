package ee.qualitylab.lemmikuleidja.app.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.InjectView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import ee.qualitylab.lemmikuleidja.app.R;
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

  private boolean propagatingToggleState = false;
  private Uri photoUri;
  private int photoSize;

  public static void openWithPhotoUri(Activity openingActivity, Uri photoUri) {
    Intent intent = new Intent(openingActivity, PublishActivity.class);
    intent.putExtra(ARG_TAKEN_PHOTO_URI, photoUri);
    openingActivity.startActivity(intent);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_publish);
    toolbar.setNavigationIcon(R.drawable.ic_arrow_back_grey600_24dp);
    photoSize = getResources().getDimensionPixelSize(R.dimen.publish_photo_thumbnail_size);

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
      uploadPost();
      bringMainActivityToTop();
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
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
