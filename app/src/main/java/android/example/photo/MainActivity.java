package android.example.photo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.example.photo.Retrofit.JsonPlaceHolderApi;
import android.example.photo.Retrofit.Post;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;
    DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        dbHelper = new DBHelper(this);
        deleteDB();
        getRandomPhotoUrl();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, ApplicationActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3500);
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
                    Toast.makeText(getApplicationContext(), "Error code: " +  response.code(), Toast.LENGTH_SHORT);
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
                //System.out.println("!!! I am onFailure");
                //System.out.println(t.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT);
            }
        });
        System.out.println("!!! I am onResponse" + call.request().body());
        return "";
    }
    private void  setUrlAndIdOnDB(String url, String photo_id, String created){
        //System.out.println("_______________________________________" + created);
        ContentValues cv = new ContentValues();
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        cv.put("url", url);
        cv.put("photo_id", photo_id);
        cv.put("created", created);
        db.insert("urlsTable", null, cv);
    }
    private void deleteDB(){
        db = dbHelper.getWritableDatabase();
        db.delete("urlsTable", null,null);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'urlsTable'");
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
