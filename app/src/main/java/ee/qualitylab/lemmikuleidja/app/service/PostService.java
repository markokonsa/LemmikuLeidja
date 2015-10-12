package ee.qualitylab.lemmikuleidja.app.service;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import com.parse.*;
import ee.qualitylab.lemmikuleidja.app.dao.PostDao;
import ee.qualitylab.lemmikuleidja.app.objects.Post;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostService {

  PostDao dao;
  private Context context;
  private LocationService locationService;

  public PostService(Context context) {
    this.context = context;
    this.locationService = new LocationService(context);
  }

  public List<Post> generateFeed() {
    List<ParseObject> objects = getPostsFromDao();

    List<Post> posts = new ArrayList<>();
    for (ParseObject object : objects) {
      Post post = new Post();
      post.setAddress(object.getString(PostDao.ADDRESS_TAG));
      post.setPicture(object.getParseFile(PostDao.PICTURE_TAG));
      post.setTime(object.getCreatedAt());
      getImageByteArray(post);
      post.setCountry(object.getString(PostDao.COUNTRY_TAG));
      post.setCity(object.getString(PostDao.CITY_TAG));
      posts.add(post);
    }
    return posts;
  }

  private List<ParseObject> getPostsFromDao() {
    dao = new PostDao(context);
    try {

      if (locationService.canGetLocation()) {
        Address address = locationService.getLocation();
        double latitude = address.getLatitude();
        double longitude = address.getLongitude();
        return dao.getPostsInRadius(new ParseGeoPoint(latitude, longitude), 15);
      }

      return dao.getPosts();
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  private void getImageByteArray(final Post post) {
    post.getPicture().getDataInBackground(new GetDataCallback() {

      @Override
      public void done(byte[] data, com.parse.ParseException e) {
        if (e == null) {
          post.setPicBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
        } else {
          post.setPicBitmap(null);
        }
      }
    });
  }

  public void uploadPost(File photo, String location, String description) {

    Post post = new Post();
    post.setTime(new Date());
    post.setPicture(new ParseFile(photo));

    Address address = getLocationFromString(location);
    post.setAddress(address.getAddressLine(0));
    post.setCity(address.getLocality());
    post.setCountry(address.getCountryName());
    post.setDescription(description);
    post.setGeoPoint(new ParseGeoPoint(address.getLatitude(),address.getLongitude()));
    try {
      dao = new PostDao(context);
      dao.uploadPost(post);
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }


  private Address getLocationFromString(String location) {
    Geocoder geocoder = new Geocoder(context);
    List<Address> addresses;
    try {
      addresses = geocoder.getFromLocationName(location, 1);
    } catch (IOException e) {
      addresses = null;
    }
    assert addresses != null;
    return addresses.get(0);
  }
}
