package android.example.photo;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.example.photo.Retrofit.JsonPlaceHolderApi;
import android.example.photo.Retrofit.Post;
import android.os.Bundle;
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

        TextView tv = findViewById(R.id.username);
        ApplicationActivity.DBHelper dbHelper = new ApplicationActivity.DBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("urlsTable", null, null, null,
                null, null, null);
        Integer i = 0;
        Integer id = getIntent().getIntExtra("id", 0);
        id -=1;//т.к. в БД счет с 1, а не с 0
        String photo_id = "";
        if (c.moveToFirst()) {
            do {
                if(i == id){
                    photo_id = c.getString(c.getColumnIndex("photo_id"));
                    break;
                }
                i++;
            } while (c.moveToNext());
        }
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
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                System.out.println("!!! I am onFailure");
                System.out.println(t.getMessage());
            }
        });
    }

}
