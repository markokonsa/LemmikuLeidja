package ee.qualitylab.lemmikuleidja.app.activities;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import ee.qualitylab.lemmikuleidja.app.R;

public class BaseActivity extends AppCompatActivity {

  @Optional
  @InjectView(R.id.toolbar)
  Toolbar toolbar;

  @Override
  public void setContentView(int layoutResID) {
    super.setContentView(layoutResID);
    injectViews();
  }

  protected void injectViews() {
    ButterKnife.inject(this);
    setupToolbar();
  }

  public void setContentViewWithoutInject(int layoutResId) {
    super.setContentView(layoutResId);
  }

  protected void setupToolbar() {
    if (toolbar != null) {
      setSupportActionBar(toolbar);
      toolbar.setNavigationIcon(R.drawable.ic_menu_white);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  public Toolbar getToolbar() {
    return toolbar;
  }
}
