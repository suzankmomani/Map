package map.android.com.map.Utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by suzan on 01/08/17.
 */

public class UiUtils {


    public static void hideVirtualKeyboard(Activity context) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}
