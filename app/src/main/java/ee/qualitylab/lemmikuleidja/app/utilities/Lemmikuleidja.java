package ee.qualitylab.lemmikuleidja.app.utilities;

import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;
import com.parse.*;

import static com.parse.Parse.initialize;

public class Lemmikuleidja extends Application {

  public static ParseUser currentUser;

  @Override
  public void onCreate() {
    super.onCreate();
    initialize(this, "tX1mrowGfe2zCN1r2D6XvNCujzpLIPnRfsAp9qBs", "5LC0ETO6yhuw2RgZHWhpfMQhtGt3OPZ4duz7hIiI");
    Login();
  }

  private void Register(String uuid) {

    ParseUser user = new ParseUser();
    user.setUsername(uuid);
    user.setPassword(uuid);
    user.signUpInBackground(new SignUpCallback() {
      @Override
      public void done(com.parse.ParseException e) {
        currentUser = ParseUser.getCurrentUser();
      }
    });
  }

  private void Login() {

    final String uuid = getDeviceUuid();

    ParseUser.logInInBackground(uuid, uuid, new LogInCallback() {
      public void done(ParseUser user, ParseException e) {
        if (user != null) {
          currentUser = ParseUser.getCurrentUser();
        } else {
          Register(uuid);
        }
      }
    });

  }

  private String getDeviceUuid() {
    TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    return tManager.getDeviceId();
  }

}
