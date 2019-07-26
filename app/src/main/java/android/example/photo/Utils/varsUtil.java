package android.example.photo.Utils;

import android.example.photo.Fragments.PhotosFragment;

import androidx.fragment.app.Fragment;

public class varsUtil {
    private static Integer currentButton = -1;
    private static Fragment currentFragment  = new PhotosFragment();
    private static Boolean reload = false;
    private static String secret_key = "7d770b5724deac228aadb9b9159ebe575c6c9db5dd3abb14a38b01a96e2523ac";
    private static Integer per_page = 50;

    public static Integer getPer_page() {
        return per_page;
    }

    public static String getSecret_key() {
        return secret_key;
    }

    public static Integer getCurrentButton() {
        return currentButton;
    }

    public static void setCurrentButton(Integer currentButton1) {
        currentButton = currentButton1;
    }

    public static Fragment getCurrentFragment() {
        return currentFragment;
    }

    public static void setCurrentFragment(Fragment currentFragment1) {
        currentFragment = currentFragment1;
    }

    public static Boolean getReload() {
        return reload;
    }

    public static void setReload(Boolean reload1) {
        reload = reload1;
    }
}
