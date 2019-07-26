package android.example.photo.Fragments;

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
    MainActivity.DBHelper dbHelper;
    SQLiteDatabase db;
    SwipeRefreshLayout swipePhotos;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("created");
        return inflater.inflate(R.layout.photo, null);

    }
    Integer currentSavedInstanceState = 2;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentSavedInstanceState = 3;
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

        dbHelper = new MainActivity.DBHelper(getContext());
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
                        if(compareWithFavorites(c.getString(c.getColumnIndex("photo_id"))))
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
            deleteDB();
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
                        System.out.println("!!! I am error");
                        System.out.println("Code: " + response.code());
                        Toast.makeText(getActivity(), "Error code: " +  response.code(), Toast.LENGTH_SHORT);
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
                    Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT);
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
            }, 1000);

        }
    };
    View.OnClickListener btnLikeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ColorStateList colorStateList = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                colorStateList = view.getBackgroundTintList();
                if (colorStateList == ContextCompat.getColorStateList(getActivity(), R.color.colorLikeLight)) {
                    view.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorLikeDark));
                    view.setEnabled(false);
                    DBHelperFav dbHelperFav = new DBHelperFav(getActivity());
                    dbHelperFav.setNameTable("favoritesTable");
                    ContentValues cv = new ContentValues();
                    SQLiteDatabase db2 = dbHelperFav.getWritableDatabase();

                    Integer id = Integer.parseInt(view.getTag().toString());
                    id -= 1;
                    System.out.println("id = " + id);
                    Integer i = 0;
                    String url = "https://images.unsplash.com/photo-1556228453-6ecff5553887?ixlib=rb-1.2.1&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&ixid=eyJhcHBfaWQiOjgyODgzfQ",
                            photo_id = "nophotoid",
                            created = "22-02-2019";

                    db = dbHelper.getWritableDatabase();
                    Cursor c = db.query("urlsTable", null, null, null,
                            null, null, null);
                    if (c.moveToFirst()) {
                        do {
                            if (i == id) {
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

    View.OnLongClickListener onClickPicture = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            loadIntent((ImageView) view);
            return false;
        }
    };
    private Boolean compareWithFavorites(String photo_id){
        DBHelperFav dbHelperFav = new DBHelperFav(getActivity());
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
    private void  setUrlAndIdOnDB(String url, String photo_id, String created){
        //System.out.println("_______________________________________" + created);
        ContentValues cv = new ContentValues();
        MainActivity.DBHelper dbHelper = new MainActivity.DBHelper(getActivity());
        //SQLiteDatabase db = dbHelper.getWritableDatabase();
        cv.put("url", url);
        cv.put("photo_id", photo_id);
        cv.put("created", created);
        db.insert("urlsTable", null, cv);
    }
    private void deleteDB(){
        MainActivity.DBHelper dbHelper = new MainActivity.DBHelper(getActivity());
        db = dbHelper.getWritableDatabase();
        db.delete("urlsTable", null,null);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'urlsTable'");
    }
    private void loadIntent(ImageView iv) {
        Intent intent = new Intent(getActivity(), FullscreenPictureActivity.class);
        intent.putExtra("id", Integer.parseInt(iv.getTag().toString()));
        intent.putExtra("table", "urlsTable");
        startActivity(intent);
    }
    public static class DBHelperFav extends SQLiteOpenHelper {
        //вроде был private not static
        String nameTable = "favoritesTable";
        public void setNameTable(String nameTable) {
            this.nameTable = nameTable;
        }

        public DBHelperFav(Context context) {
            // конструктор суперкласса
            super(context, "favDB", null, 1);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            //Log.d(LOG_TAG, "--- onCreate database ---");
            // создаем таблицу с полями
            db.execSQL("create table favoritesTable ("
                    + "id integer primary key autoincrement,"
                    + "url text,"
                    + "photo_id text,"
                    + "created text" + ");");
            System.out.println("Creating " + nameTable + "____________________");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

    }
}
