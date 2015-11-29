package ee.qualitylab.lemmikuleidja.app.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

public class Utils {
  private static int screenWidth = 0;
  private static int screenHeight = 0;
  private static ProgressDialog mDialog;

  public static int dpToPx(int dp) {
    return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
  }

  public static int getScreenHeight(Context c) {
    if (screenHeight == 0) {
      WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
      Display display = wm.getDefaultDisplay();
      Point size = new Point();
      display.getSize(size);
      screenHeight = size.y;
    }

    return screenHeight;
  }

 public static void showProgressIndicator(Context context, String message){
   mDialog = new ProgressDialog(context);
   mDialog.setMessage(message);
   mDialog.setCancelable(false);
   mDialog.show();
 }

  public static void cancelProgressIndicator(){
      if (mDialog != null){
          mDialog.cancel();
      }
  }

  public static boolean isAndroid5() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
  }
}
