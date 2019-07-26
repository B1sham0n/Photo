package android.example.photo;

import android.annotation.SuppressLint;
import android.example.photo.Fragments.FavoritesFragment;
import android.example.photo.Fragments.InfoFragment;
import android.example.photo.Fragments.PhotosFragment;
import android.example.photo.Utils.varsUtil;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.MenuItem;

public class ApplicationActivity extends AppCompatActivity {
    private boolean loadFragment(Fragment fragment){
        if(fragment != null){
            if(varsUtil.getReload())
                getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return true;
        }
        return false;
    }
    @Override
    protected void onResume() {
        super.onResume();
        varsUtil.setReload(true);
        loadFragment(varsUtil.getCurrentFragment());
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @SuppressLint("RestrictedApi")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_favorites:
                    if(varsUtil.getCurrentButton() != R.id.navigation_favorites){
                        fragment = new FavoritesFragment();
                        varsUtil.setCurrentButton(R.id.navigation_favorites);
                        varsUtil.setCurrentFragment(new FavoritesFragment());
                    }
                    break;
                case R.id.navigation_photos:
                    if(varsUtil.getCurrentButton() != R.id.navigation_photos){
                        fragment = new PhotosFragment();
                        varsUtil.setCurrentButton(R.id.navigation_photos);
                        varsUtil.setCurrentFragment(new PhotosFragment());
                    }
                    break;
                case R.id.navigation_info:
                    if(varsUtil.getCurrentButton() != R.id.navigation_info){
                        fragment = new InfoFragment();
                        varsUtil.setCurrentButton(R.id.navigation_info);
                        varsUtil.setCurrentFragment(new InfoFragment());
                    }
                    break;
            }
            varsUtil.setReload(false);
            return loadFragment(fragment);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        varsUtil.setCurrentFragment(new PhotosFragment());
        navView.setSelectedItemId(R.id.navigation_photos);
        loadFragment(new PhotosFragment());
    }
}
