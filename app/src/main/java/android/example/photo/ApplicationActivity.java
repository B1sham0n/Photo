package android.example.photo;

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

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.MenuItem;

import java.util.List;

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

        final String[] url = {null};

        dbHelper = new DBHelper(this);
        dbHelper.setNameTable("urlsTable");
        deleteDB();
        getRandomPhotoUrl();
        readDB();
        //deleteDB();
        //readDB();
        navView.setSelectedItemId(R.id.navigation_photos);
        loadFragment(new PhotosFragment());
    }
    private String getRandomPhotoUrl(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.unsplash.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<List<Post>> call = jsonPlaceHolderApi.getPosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (!response.isSuccessful()) {
                    System.out.println("!!! I am error");
                    System.out.println("Code: " + response.code());
                    return;
                }
                for(int i = 0; i < 50; i++){
                    //System.out.println("Its" + response.body().get(i).getCreated_at());
                    setUrlAndIdOnDB(response.body().get(i).getUrls().getRegular(), response.body().get(i).getId(), response.body().get(i).getCreated_at());
                }
                //setUrlOnDB(response.body().getUrls().getFull());
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                System.out.println("!!! I am onFailure");
                System.out.println(t.getMessage());
            }
        });
        System.out.println("!!! I am onResponse" + call.request().body());
        return "";
    }
    private void readDB() {
        db = dbHelper.getWritableDatabase();
        Cursor c = db.query("urlsTable", null, null, null,
                null, null, null);

        //далее в цикле создаем childView до тех пор, пока не закончится бд
        //при этом каждый раз перемещаем курсор и берем новые значения строки
        if (c.moveToFirst()) {
            do {
                System.out.println("id = " + c.getInt(c.getColumnIndex("id")));
                System.out.println("Name = " + c.getString(c.getColumnIndex("url")));
                System.out.println("________________________");


            } while (c.moveToNext());
        }
    }
    private void deleteDB(){
        db = dbHelper.getWritableDatabase();
        db.delete("urlsTable", null,null);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'urlsTable'");
    }
    private void  setUrlAndIdOnDB(String url, String photo_id, String created){
        //System.out.println("_______________________________________" + created);
        ContentValues cv = new ContentValues();
        db = dbHelper.getWritableDatabase();
        cv.put("url", url);
        cv.put("photo_id", photo_id);
        cv.put("created", created);
        db.insert("urlsTable", null, cv);
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
        String nameTable = "urlsTable";
        public void setNameTable(String nameTable) {
            this.nameTable = nameTable;
        }

        public DBHelper(Context context) {
            // конструктор суперкласса
            super(context, "photoDB", null, 1);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            //Log.d(LOG_TAG, "--- onCreate database ---");
            // создаем таблицу с полями
            db.execSQL("create table " + nameTable + " ("
                    + "id integer primary key autoincrement,"
                    + "url text,"
                    + "photo_id text,"
                    + "created text" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
}

}
