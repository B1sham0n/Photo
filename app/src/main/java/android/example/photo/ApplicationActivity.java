package android.example.photo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.example.photo.Fragments.FavoritesFragment;
import android.example.photo.Fragments.InfoFragment;
import android.example.photo.Fragments.PhotosFragment;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApplicationActivity extends AppCompatActivity {

    DBHelper dbHelper;
    SQLiteDatabase db;

    private boolean loadFragment(Fragment fragment){
        if(fragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();

            return true;
        }
        return false;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_favorites:
                    fragment = new FavoritesFragment();
                    break;
                case R.id.navigation_photos:
                    fragment = new PhotosFragment();
                    break;
                case R.id.navigation_info:
                    fragment = new InfoFragment();
                    break;
            }
            return loadFragment(fragment);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setSelectedItemId(R.id.navigation_photos);
        loadFragment(new PhotosFragment());
        final String[] url = {null};
        dbHelper = new DBHelper(this);
        getRandomPhotoUrl();
    }
    private String getRandomPhotoUrl(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.unsplash.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Call<Post> call = jsonPlaceHolderApi.getPosts();
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (!response.isSuccessful()) {
                    System.out.println("!!! I am error");
                    System.out.println("Code: " + response.code());
                    return;
                }
                //System.out.println(response.body().getUrls().getFull());
                setUrlOnDB(response.body().getUrls().getFull());
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                System.out.println("!!! I am onFailure");
                System.out.println(t.getMessage());
            }
        });
        System.out.println("!!! I am onResponse" + call.request().body());
        return "";
    }
    private void setUrlOnDB(String url){
        System.out.println("_______________________________________" + url);
        ContentValues cv = new ContentValues();
        db = dbHelper.getWritableDatabase();
        cv.put("url", url);
        //db.insert("urlsTable", null, cv);
    }
    private String getUrlFromDB(Integer id){
        String url = null;
        Cursor c = db.query("urlsTable", null,null, null,
                null, null, null);
        Integer i = 0;
        if(c.moveToFirst()) {
            do{
                if(id == i){
                    url = c.getString(c.getColumnIndex("url"));
                    return url;
                }
            }while(c.moveToNext());
        }
        else
            System.out.println("EMPTY");
        c.close();
        return "error";
    }
    public static class DBHelper extends SQLiteOpenHelper {
        //вроде был private not static
        public DBHelper(Context context) {
            // конструктор суперкласса
            super(context, "photoDB", null, 1);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            //Log.d(LOG_TAG, "--- onCreate database ---");
            // создаем таблицу с полями
            db.execSQL("create table urlsTable ("
                    + "id integer primary key autoincrement,"
                    + "url text" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
}

}
