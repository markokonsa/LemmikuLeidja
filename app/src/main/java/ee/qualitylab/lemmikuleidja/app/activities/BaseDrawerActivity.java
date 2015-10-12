package ee.qualitylab.lemmikuleidja.app.activities;

import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.InjectView;
import ee.qualitylab.lemmikuleidja.app.R;

public class BaseDrawerActivity extends BaseActivity {

  @InjectView(R.id.drawerLayout)
  DrawerLayout drawerLayout;

  @Override
  public void setContentView(int layoutResID) {
    super.setContentViewWithoutInject(R.layout.activity_drawer);
    ViewGroup viewGroup = (ViewGroup) findViewById(R.id.flContentRoot);
    LayoutInflater.from(this).inflate(layoutResID, viewGroup, true);
    injectViews();

  }

  @Override
  protected void setupToolbar() {
    super.setupToolbar();
    if (getToolbar() != null) {
      getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          drawerLayout.openDrawer(Gravity.LEFT);
        }
      });
    }
  }

}