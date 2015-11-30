package ee.qualitylab.lemmikuleidja.app.dao;

import android.content.Context;
import com.parse.*;
import ee.qualitylab.lemmikuleidja.app.objects.Post;
import ee.qualitylab.lemmikuleidja.app.view.SendingProgressView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PostDao {

  public static String POSTS_TAG = "Posts";
  public static String LOCATION_TAG = "location";
  public static String ADDRESS_TAG = "address";
  public static String PICTURE_TAG = "photo";
  public static String CITY_TAG = "city";
  public static String COUNTRY_TAG = "country";
  public static String DESCRIPTION_TAG = "description";

  public Context context;
  private SendingProgressView progressView;

  public PostDao(Context context){
    this.context = context;
  }

  public List<ParseObject> getPostsInLocality(String locality) throws ParseException {
    ParseQuery<ParseObject> query = ParseQuery.getQuery(POSTS_TAG);
    query.orderByDescending("createdAt");
    query.whereEqualTo(CITY_TAG, locality);
    return query.find();
  }

  public List<ParseObject> getPostsInRadius(ParseGeoPoint current, double radius) throws ParseException {
    ParseQuery<ParseObject> query = ParseQuery.getQuery(POSTS_TAG);
    query.orderByDescending("createdAt");
    query.whereWithinKilometers(LOCATION_TAG, current, radius);
    return query.find();
  }

  public void uploadPost(Post post) throws ParseException {

    progressView = new SendingProgressView(context);
    progressView.setCurrentProgress(1);

    ParseObject object = new ParseObject(POSTS_TAG);
    object.put(ADDRESS_TAG, post.getAddress());
    object.put(PICTURE_TAG, post.getPicture());
    object.put(CITY_TAG, post.getCity());
    object.put(COUNTRY_TAG, post.getCountry());
    object.put(LOCATION_TAG, post.getGeoPoint());
    object.put(DESCRIPTION_TAG, post.getDescription());
    object.saveInBackground(new SaveCallback() {

      @Override
      public void done(ParseException e) {
        //e.printStackTrace();
      }
    });
  }
}