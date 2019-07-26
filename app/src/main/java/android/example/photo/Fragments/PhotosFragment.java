package android.example.photo.Fragments;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.example.photo.ApplicationActivity;
import android.example.photo.FullscreenPictureActivity;
import android.example.photo.MainActivity;
import android.example.photo.R;
import android.example.photo.Retrofit.JsonPlaceHolderApi;
import android.example.photo.Retrofit.Post;
import android.example.photo.Util;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhotosFragment extends Fragment {
    //TODO: ставишь лайк в фуллактивит - не отображается в фотоактивити до перезагрузки
    private Util.DBHelper dbHelper;
    private SQLiteDatabase db;
    private SwipeRefreshLayout swipePhotos;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("created");
        return inflater.inflate(R.layout.photo, null);

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(savedInstanceState != null){
            System.out.println("recreate");
            getFragmentManager()
                    .beginTransaction()
                    .detach(PhotosFragment.this)
                    .attach(PhotosFragment.this)
                    .commit();
        }
        System.out.println("inViewCreated_______________________");
        //TODO: можно потом добавить номер картинки где-то (или при нажатии шобы появлялся, мона брать ид из бд)
        //deleteDB();
        LinearLayout scrollLayout = view.findViewById(R.id.container_photo);

        swipePhotos = view.findViewById(R.id.swipePhotos);
        swipePhotos.setOnRefreshListener(refreshListener);
        swipePhotos.setColorSchemeResources(R.color.colorPrimary, R.color.colorLikeLight, R.color.colorLikeDark);
        swipePhotos.setRefreshing(false);

        dbHelper = new Util.DBHelper(getContext());
        dbHelper.setNameTable("urlsTable");
        db = dbHelper.getWritableDatabase();
        Cursor c = db.query("urlsTable", null, null, null,
               null, null, null);
            if (c.moveToFirst()) {
                System.out.println("i am here111" + c.getCount());
                do {
                    ConstraintLayout cl = new ConstraintLayout(getActivity());
                    //cl.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT));
                    cl.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    //WebView wv = new WebView(getActivity());
                    String url = c.getString(c.getColumnIndex("url"));
                    ImageView iv = new ImageView(getActivity());
                    iv.setScaleType(ImageView.ScaleType.CENTER);
                    Picasso.get()
                            .load(url)
                            .placeholder(R.drawable.ic_photo)
                            .error(R.drawable.splash_logo)
                            .into(iv);
                    iv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    iv.setOnLongClickListener(onClickPicture);
                    iv.setTag(c.getInt(c.getColumnIndex("id")));
                    iv.setAdjustViewBounds(true);

                    cl.addView(iv);
                    Button btn = new Button(getActivity());
                    btn.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
                    btn.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite));
                    btn.setTag(c.getInt(c.getColumnIndex("id")));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if(Util.compareWithFavorites(c.getString(c.getColumnIndex("photo_id")), getActivity()))
                            btn.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorLikeDark));
                        else
                            btn.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorLikeLight));
                    }
                    btn.setOnClickListener(btnLikeListener);
                    //scrollLayout.addView(btn);
                    cl.addView(btn);
                    scrollLayout.addView(cl);
                } while (c.moveToNext());
        }
    }
    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            swipePhotos.setRefreshing(true);
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
                        Toast.makeText(getActivity(), "Error code: " +  response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Util.deleteDB("urlsTable", getActivity());
                    for(int i = 0; i < 50; i++){
                        Util.setUrlAndIdOnDB(response.body().get(i).getUrls().getRegular(), response.body().get(i).getId(), response.body().get(i).getCreated_at(), getActivity());
                    }
                }
                @SuppressLint("ShowToast")
                @Override
                public void onFailure(Call<List<Post>> call, Throwable t) {
                    Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            //нужна задержка для обновления БД
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getFragmentManager()
                            .beginTransaction()
                            .detach(PhotosFragment.this)
                            .attach(PhotosFragment.this)
                            .commit();
                }
            }, 2000);

        }
    };
    private View.OnClickListener btnLikeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ColorStateList colorStateList = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                colorStateList = view.getBackgroundTintList();
                if (colorStateList == ContextCompat.getColorStateList(getActivity(), R.color.colorLikeLight)) {
                    view.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorLikeDark));
                    view.setEnabled(false);
                    Util.DBHelperFav dbHelperFav = new Util.DBHelperFav(getActivity());
                    dbHelperFav.setNameTable("favoritesTable");
                    ContentValues cv = new ContentValues();
                    SQLiteDatabase db2 = dbHelperFav.getWritableDatabase();

                    Integer id = Integer.parseInt(view.getTag().toString());
                    id -= 1;
                    System.out.println("id = " + id);
                    Integer i = 0;
                    String url = null, photo_id = null, created = null;
                    db = dbHelper.getWritableDatabase();
                    Cursor c = db.query("urlsTable", null, null, null,
                            null, null, null);
                    if (c.moveToFirst()) {
                        do {
                            if (i.equals(id)) {
                                url = c.getString(c.getColumnIndex("url"));
                                photo_id = c.getString(c.getColumnIndex("photo_id"));
                                created = c.getString(c.getColumnIndex("created"));
                            }
                            i++;
                        } while (c.moveToNext());
                    }
                    cv.put("url", url);
                    cv.put("photo_id", photo_id);
                    cv.put("created", created);
                    db2.insert("favoritesTable", null, cv);
                    //TODO: возможность убирать лайк (или сделать ее только в большом фото)
                }
            }
        }
    };

    private View.OnLongClickListener onClickPicture = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            loadIntent((ImageView) view);
            return false;
        }
    };

    private void loadIntent(ImageView iv) {
        Intent intent = new Intent(getActivity(), FullscreenPictureActivity.class);
        intent.putExtra("id", Integer.parseInt(iv.getTag().toString()));
        intent.putExtra("table", "urlsTable");
        startActivity(intent);
    }

}
