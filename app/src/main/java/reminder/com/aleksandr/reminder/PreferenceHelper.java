package reminder.com.aleksandr.reminder;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by aleksandr on 9/16/15.
 */
public class PreferenceHelper {

    public static final String SPLASH_IS_INVISIBLE = "splash_is_invisible";

    private static PreferenceHelper instatnce;

    private Context context;

    private SharedPreferences sharedPreferences;

    private PreferenceHelper() {

    }

    public static PreferenceHelper getInstatnce() {
        if (instatnce == null) {
            instatnce = new PreferenceHelper();
        }
        return instatnce;
    }

    public void init(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }
}
