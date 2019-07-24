package android.example.photo;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        //TODO: сделать в ImageView через Picasso

        //WebView wv = findViewById(R.id.wvPicture);
        TextView tv = findViewById(R.id.username);
        /*
        wv.setInitialScale(1);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setUseWideViewPort(true);
        wv.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        wv.setScrollbarFadingEnabled(false);*/
        //wv.loadUrl(getIntent().getStringExtra("current_url"));
        ApplicationActivity.DBHelper dbHelper = new ApplicationActivity.DBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("urlsTable", null, null, null,
                null, null, null);
        Integer i = 0;
        Integer id = getIntent().getIntExtra("id", 0);
        id -=1;
        String photo_id = "";
        if (c.moveToFirst()) {
            do {
                if(i == id){
                    String created = c.getString(c.getColumnIndex("created"));
                    photo_id = c.getString(c.getColumnIndex("photo_id"));
                    tv.setText("created: " + created);
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
                //WebView wv = findViewById(R.id.wvPicture);
                //wv.loadUrl(response.body().getUrls().getFull());
                //System.out.println("i am woooorikng! " + wv.getUrl());
                ImageView iv = findViewById(R.id.wvPicture);
                Picasso.get().load(response.body().getUrls().getSmall()).placeholder(R.drawable.ic_photo).error(R.drawable.ic_info).into(iv);
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
