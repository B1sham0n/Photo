package android.example.photo.Fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.example.photo.ApplicationActivity;
import android.example.photo.R;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
                    ConstraintLayout cl = new ConstraintLayout(getActivity());
                    //cl.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    WebView wv = new WebView(getActivity());
                    String url = c.getString(c.getColumnIndex("url"));
                    wv.loadUrl(url);
                    wv.setInitialScale(1);
                    wv.getSettings().setJavaScriptEnabled(true);
                    wv.getSettings().setLoadWithOverviewMode(true);
                    wv.getSettings().setUseWideViewPort(true);
                    wv.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
                    wv.setScrollbarFadingEnabled(false);
                    cl.addView(wv);
                    ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) wv.getLayoutParams();
                    p.bottomMargin = 30;
                    wv.setLayoutParams(p);
                    Button btn = new Button(getActivity());
                    btn.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
                    btn.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        btn.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorLike));
                    }
                    //scrollLayout.addView(btn);
                    cl.addView(btn);
                    scrollLayout.addView(cl);


                } while (c.moveToNext());

        }


    }
}
