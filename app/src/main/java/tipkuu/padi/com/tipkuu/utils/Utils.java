package tipkuu.padi.com.tipkuu.utils;

import android.content.Context;
import android.content.SharedPreferences;

import tipkuu.padi.com.tipkuu.models.LoginInfo;

/**
 * Created by jedld on 12/2/17.
 */

public class Utils {
    public static void savePreferences(Context context, LoginInfo info) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("user", info.toString()).commit();
    }

    public static void logOut(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear();
    }

    public static LoginInfo getLoginInfo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return LoginInfo.fromString(sharedPreferences.getString("user", "{}"));
    }
}
