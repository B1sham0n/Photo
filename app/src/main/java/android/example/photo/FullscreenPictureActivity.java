package android.example.photo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ContentValues;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.example.photo.Fragments.PhotosFragment;
import android.example.photo.Retrofit.JsonPlaceHolderApi;
import android.example.photo.Retrofit.Post;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenPictureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_picture);
        //TODO: compareWithFavorites можно вынести в отдельный класс Util
        String table = getIntent().getStringExtra("table");

        Util.DBHelper dbHelper = new Util.DBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("urlsTable", null, null, null,
                null, null, null);

        assert table != null;
        if(table.equals("favoritesTable")) {
            System.out.println(table + "+");
            Util.DBHelperFav dbHelperFav = new Util.DBHelperFav(getApplicationContext());
            db = dbHelperFav.getWritableDatabase();
            c = db.query("favoritesTable", null, null, null,
                    null, null, null);
        }
        Integer i = 0;
        Integer id = getIntent().getIntExtra("id", 0);
        id -=1;//т.к. в БД счет с 1, а не с 0
        String photo_id = "-evnbLwQ5hk";//значение по умолчанию
        if (c.moveToFirst()) {
            do {
                if(i == id){
                    photo_id = c.getString(c.getColumnIndex("photo_id"));
                    break;
                }
                i++;
            } while (c.moveToNext());
        }
        final Button btnLike = findViewById(R.id.btnLikeFull);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(compareWithFavorites(photo_id))
                btnLike.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.colorLikeDark));
            else
                btnLike.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.colorLikeLight));
        }
        btnLike.setOnClickListener(btnLikeListener);
        Retrofit retrofit = new Retrofit.Builder()
               .baseUrl("https://api.unsplash.com/")
               .addConverterFactory(GsonConverterFactory.create())
               .build();
        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<Post> call = jsonPlaceHolderApi.getPostOnId(photo_id);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (!response.isSuccessful()) {
                    System.out.println("!!! I am error");
                    System.out.println("Code: " + response.code());
                    return;
                }
                ImageView iv = findViewById(R.id.wvPicture);
                Picasso
                        .get()
                        .load(response.body().getUrls().getRegular())
                        .placeholder(R.drawable.ic_photo)
                        .error(R.drawable.ic_info)
                        .into(iv);
                TextView tv = findViewById(R.id.username);
                tv.setText("created: " + response.body().getCreated_at());
                btnLike.setTag(response.body().getId());
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                System.out.println("!!! I am onFailure");
                System.out.println(t.getMessage());
            }
        });
    }

    View.OnClickListener btnLikeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ColorStateList colorStateList = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                colorStateList = view.getBackgroundTintList();
                if(colorStateList == ContextCompat.getColorStateList(getApplicationContext(), R.color.colorLikeLight)){
                    view.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.colorLikeDark));
                    addToDB(view.getTag().toString());
                }
                else{
                    view.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.colorLikeLight));
                    deleteFromDB(view.getTag().toString());
                }
            }

        }
    };

    private void addToDB(String photo_id) {
        ContentValues cv = new ContentValues();
        Util.DBHelperFav dbHelperFav = new Util.DBHelperFav(getApplicationContext());
        Util.DBHelper dbHelper = new Util.DBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SQLiteDatabase db2 = dbHelperFav.getWritableDatabase();

        //Integer id = Integer.parseInt();
        //id -= 1;
        //System.out.println("id = " + id);
        //Integer i = 0;
        String url = "https://images.unsplash.com/photo-1556228453-6ecff5553887?ixlib=rb-1.2.1&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&ixid=eyJhcHBfaWQiOjgyODgzfQ",
                created = "22-02-2019";
        Cursor c = db.query("urlsTable", null, null, null,
                null, null, null);
        if (c.moveToFirst()) {
            do {
                if (c.getString(c.getColumnIndex("photo_id")).equals(photo_id)) {
                    url = c.getString(c.getColumnIndex("url"));
                    created = c.getString(c.getColumnIndex("created"));
                }
            } while (c.moveToNext());
        }
        cv.put("url", url);
        cv.put("photo_id", photo_id);
        cv.put("created", created);
        db2.insert("favoritesTable", null, cv);
    }

    private void deleteFromDB(String photo_id){
        Util.DBHelperFav dbHelper = new Util.DBHelperFav(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("favoritesTable", null, null, null,
                null, null, null);
        Integer i = 0;
        if(c.moveToFirst()){
            do {
                if(c.getString(c.getColumnIndex("photo_id")).equals(photo_id)){
                    db.delete("favoritesTable","id = " + c.getInt(c.getColumnIndex("id")),null);
                    db.execSQL("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE NAME = 'favoritesTable'");//обновление столбца с id
                    System.out.println("its id = " +  c.getInt(c.getColumnIndex("id")) + "!1111!!!!!!!!!!!!!!!");
                    //scrollLayout.removeViewAt(i);
                    break;
                }
                i++;
            }while (c.moveToNext());
        }
    }
    private Boolean compareWithFavorites(String photo_id){
        Util.DBHelperFav dbHelperFav = new Util.DBHelperFav(getApplicationContext());
        SQLiteDatabase db = dbHelperFav.getWritableDatabase();
        Cursor c = db.query("favoritesTable", null, null, null,
                null, null, null);
        if(c.moveToFirst()){
            do {
                if(c.getString(c.getColumnIndex("photo_id")).equals(photo_id)){
                    return true;
                }
            }while(c.moveToNext());
        }
        return false;
    }

}
