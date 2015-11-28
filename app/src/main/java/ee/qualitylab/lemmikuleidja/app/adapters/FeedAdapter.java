package ee.qualitylab.lemmikuleidja.app.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ee.qualitylab.lemmikuleidja.app.R;
import ee.qualitylab.lemmikuleidja.app.objects.Post;
import ee.qualitylab.lemmikuleidja.app.utilities.Utils;
import ee.qualitylab.lemmikuleidja.app.view.SendingProgressView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_DEFAULT = 1;
    private static final int VIEW_TYPE_LOADER = 2;

    private Context context;
    private int lastAnimatedPosition = -1;
    private int itemsCount = 0;
    private boolean animateItems = false;

    private boolean showLoadingView = false;
    private int loadingViewSize = Utils.dpToPx(200);
    private List<Post> posts;

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat format = new SimpleDateFormat("MMM dd HH:mm");

    public FeedAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_feed, parent, false);
        final CellFeedViewHolder cellFeedViewHolder = new CellFeedViewHolder(view);
        if (viewType == VIEW_TYPE_LOADER) {
            View bgView = new View(context);
            bgView.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            ));
            bgView.setBackgroundColor(0x77ffffff);
            cellFeedViewHolder.vImageRoot.addView(bgView);
            cellFeedViewHolder.vProgressBg = bgView;

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(loadingViewSize, loadingViewSize);
            params.gravity = Gravity.CENTER;
            SendingProgressView sendingProgressView = new SendingProgressView(context);
            sendingProgressView.setLayoutParams(params);
            cellFeedViewHolder.vImageRoot.addView(sendingProgressView);
            cellFeedViewHolder.vSendingProgress = sendingProgressView;
        }
        return cellFeedViewHolder;
    }

    public void clearAdapter() {
        lastAnimatedPosition = -1;
        itemsCount = 0;
        posts.clear();
    }

    private void runEnterAnimation(View view, int position) {
        if (position > lastAnimatedPosition && position < posts.size()) {
            lastAnimatedPosition = position;
            view.setTranslationY(Utils.getScreenHeight(context));
            view.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(700)
                    .start();
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        runEnterAnimation(viewHolder.itemView, position);
        final CellFeedViewHolder holder = (CellFeedViewHolder) viewHolder;
        if (getItemViewType(position) != VIEW_TYPE_LOADER) {
            bindDefaultFeedItem(position, holder);
        } else if (getItemViewType(position) == VIEW_TYPE_LOADER) {
            bindLoadingFeedItem(holder);
        }
    }

    private void bindDefaultFeedItem(final int position, CellFeedViewHolder holder) {

        final ImageView imageView = holder.ivFeedCenter;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inSampleSize = 2;
        ViewTreeObserver vto = imageView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                Bitmap original = posts.get(position).getPicBitmap();
                int targetWidth = imageView.getMeasuredWidth();
                int targetHeight = imageView.getMeasuredHeight();
                Bitmap decoded = Bitmap.createScaledBitmap(original, targetWidth, targetHeight, false);
                imageView.setImageBitmap(decoded);
                return true;
            }
        });
        holder.dateTextView.setText(format.format(posts.get(position).getTime()));
        holder.locationTextView.setText(posts.get(position).getAddress() + ", " + posts.get(position).getCity() + ", " + posts.get(position).getCountry());
        holder.feedDescriptionTextView.setText(posts.get(position).getDescription());
    }

    private void bindLoadingFeedItem(final CellFeedViewHolder holder) {
        Post last = Post.getLastlyAddedPost();

        if (last != null) {
            posts.add(0, last);
            holder.ivFeedCenter.setImageBitmap(last.getPicBitmap());
            holder.dateTextView.setText(format.format(last.getTime()));
            holder.locationTextView.setText(last.getAddress() + ", " + last.getCity() + ", " + last.getCountry());
            holder.feedDescriptionTextView.setText(last.getDescription());

            holder.ivFeedCenter.setTag(holder);
            holder.vSendingProgress.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    holder.vSendingProgress.getViewTreeObserver().removeOnPreDrawListener(this);
                    holder.vSendingProgress.startProgress();
                    return true;
                }
            });
            holder.vSendingProgress.setOnLoadingFinishedListener(new SendingProgressView.OnLoadingFinishedListener() {
                @Override
                public void onLoadingFinished() {
                    holder.vSendingProgress.animate().scaleY(0).scaleX(0).setDuration(200).setStartDelay(100);
                    holder.vProgressBg.animate().alpha(0.f).setDuration(200).setStartDelay(100)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    holder.vSendingProgress.setScaleX(1);
                                    holder.vSendingProgress.setScaleY(1);
                                    holder.vProgressBg.setAlpha(1);
                                    showLoadingView = false;
                                    notifyItemChanged(0);
                                }
                            })
                            .start();
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (showLoadingView && position == 0) {
            return VIEW_TYPE_LOADER;
        } else {
            return VIEW_TYPE_DEFAULT;
        }
    }

    @Override
    public int getItemCount() {
        return itemsCount;
    }


    public void updateItems(boolean animated) {
        itemsCount = posts.size();
        animateItems = animated;
        notifyDataSetChanged();
    }

    public void showLoadingView() {
        showLoadingView = true;
        notifyItemChanged(0);
    }

    public static class CellFeedViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.ivFeedCenter)
        ImageView ivFeedCenter;
        @InjectView(R.id.vImageRoot)
        FrameLayout vImageRoot;
        @InjectView(R.id.feed_date_textview)
        TextView dateTextView;
        @InjectView(R.id.feed_location_textview)
        TextView locationTextView;
        @InjectView(R.id.feed_description)
        TextView feedDescriptionTextView;

        SendingProgressView vSendingProgress;
        View vProgressBg;

        public CellFeedViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
