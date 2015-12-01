package ee.qualitylab.lemmikuleidja.app.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import ee.qualitylab.lemmikuleidja.app.R;
import ee.qualitylab.lemmikuleidja.app.adapters.FeedAdapter;
import ee.qualitylab.lemmikuleidja.app.objects.Post;
import ee.qualitylab.lemmikuleidja.app.service.LocationService;
import ee.qualitylab.lemmikuleidja.app.service.PostService;
import ee.qualitylab.lemmikuleidja.app.utilities.Lemmikuleidja;
import ee.qualitylab.lemmikuleidja.app.utilities.Utils;


public class FeedActivity extends BaseDrawerActivity {
    public static final String ACTION_SHOW_LOADING_ITEM = "action_show_loading_item";

    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;

    @InjectView(R.id.rvFeed)
    RecyclerView rvFeed;
    @InjectView(R.id.btnCreate)
    FloatingActionButton fabCreate;
    @InjectView(R.id.errorText)
    TextView errorText;

    Menu mainMenu;

    private LocationService locationService;

    private boolean pendingIntroAnimation;
    private FeedAdapter feedAdapter;
    private List<Post> posts = new ArrayList<>();
    private Address address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        feedAdapter = new FeedAdapter(this, posts);
        rvFeed.setAdapter(feedAdapter);
        setupActionBarToggle();
        setupFeed();

        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
        } else {
            feedAdapter.updateItems(false);
        }
    }

    private void setupFeed() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        rvFeed.setLayoutManager(linearLayoutManager);
        locationService = new LocationService(this);
        posts = new ArrayList<>();
        address = locationService.getLocationFromString(addAddressET.getText().toString());

        if (!addAddressET.getText().toString().isEmpty() || address != null) {
            execute();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (ACTION_SHOW_LOADING_ITEM.equals(intent.getAction())) {
            if (Post.lastlyAddedPost != null)
                if (Post.lastlyAddedPost.getCity().equals(getCityFromAddress(address))) {
                    showFeedLoadingItemDelayed();
                }else {
                    Toast.makeText(this, "Postitust laetakse ülesse linna: " + Post.lastlyAddedPost.getCity(), Toast.LENGTH_SHORT).show();
                }
        }
    }

    public void onNotificationClick(MenuItem item) {
        String title = item.getTitle().toString();
        String city = getCityFromAddress(address);
        if (!city.isEmpty()) {
            if (title.equals("OFF")) {
                item.setTitle("ON");
                Lemmikuleidja.subscribe(city);
                Toast.makeText(this, "Teated on sisselülitatud linnale: " + city, Toast.LENGTH_SHORT).show();

            } else {
                item.setTitle("OFF");
                Lemmikuleidja.unsubscribe(city);
                Toast.makeText(this, "Teated on välja lülitatud linnast: " + city, Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Enne teadete aktiveerimist valige linn, milles soovite aktiveerida.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showFeedLoadingItemDelayed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rvFeed.smoothScrollToPosition(0);
                feedAdapter.showLoadingView();
            }
        }, 500);
        feedAdapter.updateItems(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        mainMenu = menu;
        if (pendingIntroAnimation) {
            pendingIntroAnimation = false;
            startIntroAnimation();
            setupNotificationIcon(getCityFromAddress(address));
        }
        return true;
    }

    private void startIntroAnimation() {
        fabCreate.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));
        getToolbar().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startContentAnimation();
                    }
                })
                .start();
    }

    private void startContentAnimation() {
        fabCreate.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(300)
                .setDuration(ANIM_DURATION_FAB)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Utils.cancelProgressIndicator();
                    }
                })
                .start();
        feedAdapter.updateItems(true);
    }

    private void setupActionBarToggle() {

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
                drawerLayout,                    /* DrawerLayout object */
                R.string.drawer_open,
                R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(drawerView.getWindowToken(), 0);
                address = locationService.getLocationFromString(addAddressET.getText().toString());
                boolean location = address == null;
                if (!addAddressET.getText().toString().isEmpty()) {
                    if (addAddressET.getText().toString().isEmpty() || location) {
                        drawerLayout.openDrawer(Gravity.LEFT);
                        addAddressET.setTextColor(Color.RED);
                    } else {
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        addAddressET.setTextColor(Color.GREEN);

                        execute();
                    }
                }
                else {
                    feedAdapter.clearAdapter();
                    feedAdapter.notifyDataSetChanged();
                    feedAdapter.setPosts(posts);
                    startIntroAnimation();
                    setupNotificationIcon(getCityFromAddress(address));
                    errorText.setVisibility(View.VISIBLE);
                }
            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);
    }

    @OnClick(R.id.btnCreate)
    public void onTakePhotoClick() {
        int[] startingLocation = new int[2];
        fabCreate.getLocationOnScreen(startingLocation);
        startingLocation[0] += fabCreate.getWidth() / 2;
        TakePhotoActivity.startCameraFromLocation(startingLocation, this);
        overridePendingTransition(0, 0);
    }

    private String getCityFromAddress(Address address) {
        String city = "";
        if (address != null) {
            if (address.getLocality() != null) {
                city = address.getLocality();
            } else {
                city = address.getSubAdminArea();
            }
        }
        return city;
    }

    private void execute(){
        new PostsTask().cancel(true);
        new PostsTask().execute(address);
    }

    private void setupNotificationIcon(String city) {
        MenuItem item = mainMenu.findItem(R.id.action_notification);
        List<String> channels = Lemmikuleidja.getSubscribedChannels();
        if (channels != null) {
            if (channels.contains(city) && !city.isEmpty()) {
                item.setTitle("ON");
            } else {
                item.setTitle("OFF");
            }
        }
    }

    public class PostsTask extends AsyncTask<Address, Void, List<Post>> {

        @Override
        protected void onPreExecute() {
            Utils.showProgressIndicator(FeedActivity.this,"Palun oodake...");
        }

        @Override
        protected List<Post> doInBackground(Address... addresses) {
            SharedPreferences sharedPreferences = getSharedPreferences(Utils.MyPREFERENCES, Context.MODE_PRIVATE);
            PostService service = new PostService(FeedActivity.this,sharedPreferences.getString("radius","15"));
            return service.generateFeed(address, BaseDrawerActivity.enteredByHand);
        }

        @Override
        protected void onPostExecute(List<Post> posts) {
            feedAdapter.clearAdapter();
            feedAdapter.notifyDataSetChanged();
            feedAdapter.setPosts(posts);
            startIntroAnimation();
            setupNotificationIcon(getCityFromAddress(address));
            if (posts.isEmpty()) {
                errorText.setVisibility(View.VISIBLE);
            } else {
                errorText.setVisibility(View.GONE);
                rvFeed.smoothScrollToPosition(0);
            }
        }
    }
}