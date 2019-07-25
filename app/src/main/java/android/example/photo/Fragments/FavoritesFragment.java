package android.example.photo.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.example.photo.ApplicationActivity;
import android.example.photo.FullscreenPictureActivity;
import android.example.photo.R;
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

public class FavoritesFragment extends Fragment {
    PhotosFragment.DBHelperFav dbHelper;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorites, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout scrollLayout = view.findViewById(R.id.container_favorites);
        dbHelper = new PhotosFragment.DBHelperFav(getContext());
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
                iv.setTag(c.getInt(c.getColumnIndex("id")));
                iv.setAdjustViewBounds(true);

                cl.addView(iv);
                Button btn = new Button(getActivity());
                btn.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
                btn.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite));
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
                view.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorLikeDark));
                //TODO: добавлять фото в базу данных фейворит
                //TODO: возможность убирать лайк (или сделать ее только в большом фото)
            }
        }
    };

    private void loadIntent(ImageView iv) {
        Intent intent = new Intent(getActivity(), FullscreenPictureActivity.class);
        intent.putExtra("id", Integer.parseInt(iv.getTag().toString()));
        startActivity(intent);
    }
}
