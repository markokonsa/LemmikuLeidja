package ee.qualitylab.lemmikuleidja.app.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import butterknife.InjectView;
import butterknife.OnClick;
import ee.qualitylab.lemmikuleidja.app.R;
import ee.qualitylab.lemmikuleidja.app.adapters.FeedAdapter;
import ee.qualitylab.lemmikuleidja.app.objects.Post;
import ee.qualitylab.lemmikuleidja.app.service.LocationService;
import ee.qualitylab.lemmikuleidja.app.service.PostService;
import ee.qualitylab.lemmikuleidja.app.utilities.Utils;
import ee.qualitylab.lemmikuleidja.app.view.FeedContextMenu;
import ee.qualitylab.lemmikuleidja.app.view.FeedContextMenuManager;

import java.util.ArrayList;
import java.util.List;


public class FeedActivity extends BaseDrawerActivity implements FeedAdapter.OnFeedItemClickListener,
        FeedContextMenu.OnFeedContextMenuItemClickListener {
  public static final String ACTION_SHOW_LOADING_ITEM = "action_show_loading_item";

  private static final int ANIM_DURATION_TOOLBAR = 300;
  private static final int ANIM_DURATION_FAB = 400;
  public static boolean progressAnimating = false;

  @InjectView(R.id.rvFeed)
  RecyclerView rvFeed;
  @InjectView(R.id.btnCreate)
  FloatingActionButton fabCreate;

  private FeedAdapter feedAdapter;
  private PostService postService;
  private LocationService locationService;

  private boolean pendingIntroAnimation;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_feed);
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
    postService = new PostService(this);
    locationService = new LocationService(this);

    List<Post> postList = new ArrayList<>();
    if (locationService.canGetLocation()){

    }

    feedAdapter = new FeedAdapter(this, postService.generateFeed());
    rvFeed.setAdapter(feedAdapter);
    rvFeed.setOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        FeedContextMenuManager.getInstance().onScrolled(recyclerView, dx, dy);
      }
    });
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    if (ACTION_SHOW_LOADING_ITEM.equals(intent.getAction())) {
      showFeedLoadingItemDelayed();
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
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    if (pendingIntroAnimation) {
      pendingIntroAnimation = false;
      startIntroAnimation();
    }
    return true;
  }

  private void startIntroAnimation() {
    fabCreate.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));

    int actionbarSize = Utils.dpToPx(56);
    getToolbar().setTranslationY(-actionbarSize);

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
            .start();
    feedAdapter.updateItems(true);
  }

  @Override
  public void onMoreClick(View v, int itemPosition) {
    FeedContextMenuManager.getInstance().toggleContextMenuFromView(v, itemPosition, this);
  }

  @Override
  public void onReportClick(int feedItem) {
    FeedContextMenuManager.getInstance().hideContextMenu();
  }

  @Override
  public void onSharePhotoClick(int feedItem) {
    FeedContextMenuManager.getInstance().hideContextMenu();
  }

  @Override
  public void onCopyShareUrlClick(int feedItem) {
    FeedContextMenuManager.getInstance().hideContextMenu();
  }

  @Override
  public void onCancelClick(int feedItem) {
    FeedContextMenuManager.getInstance().hideContextMenu();
  }

  @OnClick(R.id.btnCreate)
  public void onTakePhotoClick() {
    int[] startingLocation = new int[2];
    fabCreate.getLocationOnScreen(startingLocation);
    startingLocation[0] += fabCreate.getWidth() / 2;
    TakePhotoActivity.startCameraFromLocation(startingLocation, this);
    overridePendingTransition(0, 0);
  }


}
