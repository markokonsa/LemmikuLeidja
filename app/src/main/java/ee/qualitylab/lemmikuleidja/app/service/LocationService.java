package ee.qualitylab.lemmikuleidja.app.service;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import ee.qualitylab.lemmikuleidja.app.utilities.GPSTracker;

import java.io.IOException;
import java.util.List;

public class LocationService {

  private Context context;
  private GPSTracker gpsTracker;

  public LocationService(Context context) {
    this.context = context;
    gpsTracker = new GPSTracker(context);
  }

  public boolean canGetLocation() {
    return gpsTracker.canGetLocation();
  }

  public Address getLocation() {
    List<Address> addresses = null;
    if (gpsTracker.canGetLocation()) {
      Geocoder geocoder = new Geocoder(context);
      try {
        addresses = geocoder.getFromLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude(), 1);
      } catch (IOException e) {
        e.printStackTrace();
        addresses = null;
      }
    }
    if (addresses != null && addresses.size()>0) {
      return addresses.get(0);
    }else {
      return null;
    }
  }

  public Address getLocationFromString(String location) {
    Geocoder geocoder = new Geocoder(context);
    List<Address> addresses;
    try {
      addresses = geocoder.getFromLocationName(location, 1);
    } catch (IOException e) {
      addresses = null;
    }
    if (addresses != null && addresses.size()>0) {
      return addresses.get(0);
    }else {
      return null;
    }
  }

}
