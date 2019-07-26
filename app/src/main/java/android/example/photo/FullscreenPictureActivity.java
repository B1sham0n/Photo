package android.example.photo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.res.ColorStateList;
import android.example.photo.Retrofit.JsonPlaceHolderApi;
import android.example.photo.Retrofit.Post;
import android.example.photo.Utils.Util;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FullscreenPictureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_picture);
        String photo_id = getIntent().getStringExtra("photo_id");
        final Button btnLike = findViewById(R.id.btnLikeFull);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(Util.compareWithFavorites(photo_id, getApplicationContext()))
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
        Call<Post> call = jsonPlaceHolderApi.getPostOnId(photo_id, "7d770b5724deac228aadb9b9159ebe575c6c9db5dd3abb14a38b01a96e2523ac");
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Error code: " +  response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                ImageView iv = findViewById(R.id.wvPicture);
                Picasso
                        .get()
                        .load(response.body().getUrls().getRegular())
                        .placeholder(R.drawable.ic_photo)
                        .error(R.drawable.ic_failed)
                        .into(iv);
                TextView tv = findViewById(R.id.created);
                tv.setText("created: " + Util.rebuildCreatedTime(response.body().getCreated_at()));
                btnLike.setTag(response.body().getId());
            }
            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Util.addToDB(view.getTag().toString(), getApplicationContext());
                }
                else{
                    view.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.colorLikeLight));
                    Util.deleteFromFavDB(view.getTag().toString(), null, getApplicationContext());
                }
            }
        }
    };


}
