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
    return addresses.get(0);
  }


}
