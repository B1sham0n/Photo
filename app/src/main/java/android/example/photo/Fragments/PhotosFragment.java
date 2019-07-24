package android.example.photo.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.example.photo.ApplicationActivity;
import android.example.photo.FullscreenPictureActivity;
import android.example.photo.R;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
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

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

public class PhotosFragment extends Fragment {

    ApplicationActivity.DBHelper dbHelper;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.photo, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //TODO: добавить онкликлистенер и при нажатии на него ссылка (или ид в БД) передается в интент, который создает активити без тулбара
        //TODO: можно потом добавить номер картинки где-то (или при нажатии шобы появлялся, мона брать ид из бд)


        LinearLayout scrollLayout = view.findViewById(R.id.container_photo);
        dbHelper = new ApplicationActivity.DBHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor c = db.query("urlsTable", null, null, null,
                    null, null, null);
        System.out.println("i am here111" + c.getCount());
            if (c.moveToFirst()) {
                do {
                    //TODO: use Picasso
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        btn.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorLikeLight));
                    }
                    btn.setOnClickListener(btnLikeListener);
                    //scrollLayout.addView(btn);
                    cl.addView(btn);
                    scrollLayout.addView(cl);
                } while (c.moveToNext());
        }
    }
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
    View.OnLongClickListener onClickPicture = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            loadIntent((ImageView) view);
            return false;
        }
    };

    private void loadIntent(ImageView iv) {
        Intent intent = new Intent(getActivity(), FullscreenPictureActivity.class);
        intent.putExtra("id", Integer.parseInt(iv.getTag().toString()));
        startActivity(intent);
    }
}
