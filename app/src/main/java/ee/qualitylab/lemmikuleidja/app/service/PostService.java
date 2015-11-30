package ee.qualitylab.lemmikuleidja.app.service;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.location.Address;

import com.parse.*;

import ee.qualitylab.lemmikuleidja.app.dao.PostDao;
import ee.qualitylab.lemmikuleidja.app.objects.Post;

import java.io.File;
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

    public List<Post> generateFeed(Address address, boolean withRadius) {
        List<ParseObject> objects = getPostsFromDao(address, withRadius);

        List<Post> posts = new ArrayList<>();
        for (ParseObject object : objects) {
            Post post = new Post();
            post.setAddress(object.getString(PostDao.ADDRESS_TAG));
            post.setPicture(object.getParseFile(PostDao.PICTURE_TAG));
            post.setTime(object.getCreatedAt());
            getImageByteArray(post);
            post.setCountry(object.getString(PostDao.COUNTRY_TAG));
            post.setCity(object.getString(PostDao.CITY_TAG));
            post.setDescription(object.getString(PostDao.DESCRIPTION_TAG));
            posts.add(post);
        }
        return posts;
    }

    private List<ParseObject> getPostsFromDao(Address address, boolean withRadius) {
        dao = new PostDao(context);
        try {
            double latitude = address.getLatitude();
            double longitude = address.getLongitude();
            if (withRadius) {
                return dao.getPostsInRadius(new ParseGeoPoint(latitude, longitude), 15);
            } else {
                String locality;
                if (address.getLocality() != null) locality = address.getLocality();
                else locality = address.getSubAdminArea();
                return dao.getPostsInLocality(locality);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void getImageByteArray(final Post post) {
        byte[] data = new byte[0];
        try {
            data = post.getPicture().getData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        post.setPicBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
    }

    public void uploadPost(File photo, String location, String description) {

        locationService = new LocationService(context);

        Post post = new Post();
        post.setTime(new Date());
        post.setPicture(new ParseFile(photo));

        Address address = locationService.getLocationFromString(location);
        post.setAddress(address.getAddressLine(0));
        if (address.getLocality() != null) {
            post.setCity(address.getLocality());
        } else {
            post.setCity(address.getSubAdminArea());
        }
        post.setCountry(address.getAddressLine(2));
        post.setDescription(description);
        post.setGeoPoint(new ParseGeoPoint(address.getLatitude(), address.getLongitude()));
        post.setPicBitmap(BitmapFactory.decodeFile(photo.getAbsolutePath()));
        Post.setLastlyAddedPost(post);
        try {
            dao = new PostDao(context);
            dao.uploadPost(post);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
