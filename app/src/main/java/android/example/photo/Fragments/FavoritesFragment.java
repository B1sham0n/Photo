package android.example.photo.Fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.example.photo.ApplicationActivity;
import android.example.photo.FullscreenPictureActivity;
import android.example.photo.R;
import android.example.photo.Util;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment {
    Util.DBHelperFav dbHelper;
    LinearLayout scrollLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorites, null);
    }
    //TODO: deleteFromDB можно вынести в Utils
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scrollLayout = view.findViewById(R.id.container_favorites);
        dbHelper = new Util.DBHelperFav(getContext());
        dbHelper.setNameTable("favoritesTable");
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("favoritesTable", null, null, null,
                null, null, null);
        //System.out.println("i am here111" + c.getCount());
        if (c.moveToFirst()) {
            do {
                ConstraintLayout cl = new ConstraintLayout(getActivity());
                cl.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
                iv.setTag(c.getInt(c.getColumnIndex("id"))-1);
                iv.setAdjustViewBounds(true);

                cl.addView(iv);
                Button btn = new Button(getActivity());
                btn.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
                btn.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite));
                btn.setTag(c.getString(c.getColumnIndex("photo_id")));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    btn.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorLikeDark));
                }
                btn.setOnClickListener(btnLikeListener);
                //scrollLayout.addView(btn);
                cl.addView(btn);
                scrollLayout.addView(cl);
            } while (c.moveToNext());
        }
    }
    View.OnLongClickListener onClickPicture = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            loadIntent((ImageView) view);
            return false;
        }
    };
    View.OnClickListener btnLikeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //view.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorLikeDark));
                ColorStateList colorStateList = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    colorStateList = view.getBackgroundTintList();
                    if(colorStateList == ContextCompat.getColorStateList(getActivity(), R.color.colorLikeLight)){
                        view.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorLikeDark));
                    }
                    else{
                        view.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorLikeLight));
                        deleteFromDB(view.getTag().toString());
                    }
                }

                //TODO: добавлять фото в базу данных фейворит
                //TODO: возможность убирать лайк (или сделать ее только в большом фото)
            }
        }
    };
    private void deleteFromDB(String photo_id){
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
                 scrollLayout.removeViewAt(i);
                 break;
             }
             i++;
         }while (c.moveToNext());
        }
    }
    public void deleteFromDB2(SQLiteDatabase db, Integer id){
        ContentValues cv = new ContentValues();
        ArrayList<String> arrName = new ArrayList<>();
        ArrayList<String> arrSurname = new ArrayList<>();
        ArrayList<Integer> arrAge = new ArrayList<>();

        Cursor c = db.query("testTable", null,null, null,
                null, null, null);
        int j = 0;
        if(c.moveToFirst()) {
            do{
                arrName.add(j, c.getString(c.getColumnIndex("name")));
                arrSurname.add(j,  c.getString(c.getColumnIndex("surname")));
                arrAge.add(j,  c.getInt(c.getColumnIndex("age")));
                j++;
            }while(c.moveToNext());
        }
        c.moveToFirst();
        for(int i = id; i < j-1; i++){
            arrName.set(i, arrName.get(i+1));
            arrSurname.set(i, arrSurname.get(i+1));
            arrAge.set(i, arrAge.get(i+1));
        }
        db.delete("testTable", null,null);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'testTable'");
        for(int i = 0; i < j-1; i++){
            //cv.put("id", i);
            cv.put("name", arrName.get(i));
            cv.put("surname", arrSurname.get(i));
            cv.put("age", arrAge.get(i));
            db.insert("testTable", null, cv);
        }
        c.close();
    }
    private void loadIntent(ImageView iv) {
        Intent intent = new Intent(getActivity(), FullscreenPictureActivity.class);
        intent.putExtra("id", Integer.parseInt(iv.getTag().toString()));
        intent.putExtra("table", "favoritesTable");
        startActivity(intent);
    }
}
