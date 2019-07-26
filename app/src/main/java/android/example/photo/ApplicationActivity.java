package android.example.photo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.example.photo.Fragments.FavoritesFragment;
import android.example.photo.Fragments.InfoFragment;
import android.example.photo.Fragments.PhotosFragment;
import android.example.photo.Retrofit.JsonPlaceHolderApi;
import android.example.photo.Retrofit.Post;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.MenuItem;
import android.widget.FrameLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApplicationActivity extends AppCompatActivity {
    //TODO: почистить туду
    //TODO можно вынести переменные private в отдельный класс
    //TODO: почистить код от ненужной фигни и вынести что-то в отдельные классы
    //TODO: проверить работоспособность приложения, скинуть Яхубу
    //DBHelper dbHelper;
    SQLiteDatabase db;
    private Integer currentButton = -1;
    private Fragment currentFragment = new PhotosFragment();
    private Boolean reload = false;
    private boolean loadFragment(Fragment fragment){
        if(fragment != null){
            System.out.println("fragment loaded");
            if(reload)
                getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return true;
        }
        return false;
    }
    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume ");
        reload = true;
        loadFragment(currentFragment);
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @SuppressLint("RestrictedApi")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            System.out.println("application created2");
            switch (item.getItemId()) {
                case R.id.navigation_favorites:
                    if(currentButton != R.id.navigation_favorites){
                        fragment = new FavoritesFragment();
                        currentButton = R.id.navigation_favorites;
                        currentFragment = new FavoritesFragment();
                    }
                    break;
                case R.id.navigation_photos:
                    if(currentButton != R.id.navigation_photos){
                        fragment = new PhotosFragment();
                        currentButton = R.id.navigation_photos;
                        currentFragment = new PhotosFragment();
                    }
                    break;
                case R.id.navigation_info:
                    if(currentButton != R.id.navigation_info){
                        fragment = new InfoFragment();
                        currentButton = R.id.navigation_info;
                        currentFragment = new InfoFragment();
                    }
                    break;
            }
            reload = false;
            return loadFragment(fragment);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        currentFragment = new PhotosFragment();
        navView.setSelectedItemId(R.id.navigation_photos);
        loadFragment(new PhotosFragment());
        currentFragment = new PhotosFragment();
    }
}
