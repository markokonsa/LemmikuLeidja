package ee.qualitylab.lemmikuleidja.app.objects;

import android.graphics.Bitmap;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;

import java.util.Date;

public class Post {

  private String address;
  private Date time;
  private ParseFile picture;
  private Bitmap picBitmap;
  private ParseGeoPoint geoPoint;
  private String city;
  private String country;
  private String description;

  public ParseGeoPoint getGeoPoint() {
    return geoPoint;
  }

  public void setGeoPoint(ParseGeoPoint geoPoint) {
    this.geoPoint = geoPoint;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Bitmap getPicBitmap() {
    return picBitmap;
  }

  public void setPicBitmap(Bitmap picBitmap) {
    this.picBitmap = picBitmap;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Date getTime() {
    return time;
  }

  public void setTime(Date time) {
    this.time = time;
  }

  public ParseFile getPicture() {
    return picture;
  }

  public void setPicture(ParseFile picture) {
    this.picture = picture;
  }
}
