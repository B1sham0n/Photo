package android.example.photo.Fragments;

import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PhotosFragment extends Fragment {

    ApplicationActivity.DBHelper dbHelper;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dbHelper = new ApplicationActivity.DBHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        return inflater.inflate(R.layout.photo, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View childView;
        //TODO: добавить цикл на 50 элементов, который берет ссылки из БД
        //TODO: добавить онкликлистенер и при нажатии на него ссылка (или ид в БД) передается в интент, который создает активити без тулбара
        //TODO: можно потом добавить номер картинки где-то (или при нажатии шобы появлялся, мона брать ид из бд)
        LayoutInflater inflaterCurrent = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View main = inflaterCurrent.inflate(R.layout.photo, null);
        TextView tv = main.findViewById(R.id.tvTest);
        System.out.println(tv.getText());
        tv.setText("hello");
        LinearLayout scrollLayout = view.findViewById(R.id.container_photo);
        //Cursor c = db.query("testTable", null,null, null,
        //        null, null, null);
        childView = inflaterCurrent.inflate(R.layout.inflate_picture_in_list, null);
        WebView wv = new WebView(getActivity());//childView.findViewById(R.id.wvInList);////
        //wv.loadUrl("https://images.unsplash.com/photo-1561842318-18fbed8d47eb?ixlib=rb-1.2.1&q=85&fm=jpg&crop=entropy&cs=srgb&ixid=eyJhcHBfaWQiOjgyODgzfQ");
        wv.loadUrl("https://images.unsplash.com/photo-1561957119-d628e4bff3a9?ixlib=rb-1.2.1&q=85&fm=jpg&crop=entropy&cs=srgb&ixid=eyJhcHBfaWQiOjgyODgzfQ");
        //https://images.unsplash.com/photo-1561957119-d628e4bff3a9?ixlib=rb-1.2.1&q=85&fm=jpg&crop=entropy&cs=srgb&ixid=eyJhcHBfaWQiOjgyODgzfQ

        //wv.setInitialScale(30);
        wv.setInitialScale(1);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setUseWideViewPort(true);
        wv.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        wv.setScrollbarFadingEnabled(false);
        //wv.getSettings().setLoadWithOverviewMode(true);
        //wv.getSettings().setUseWideViewPort(true);
        scrollLayout.addView(wv);
        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) wv.getLayoutParams();
        p.bottomMargin = 30;
        wv.setLayoutParams(p);
        WebView wv1 = new WebView(getActivity());//childView.findViewById(R.id.wvInList);////
        wv1.loadUrl("https://images.unsplash.com/photo-1561842318-18fbed8d47eb?ixlib=rb-1.2.1&q=85&fm=jpg&crop=entropy&cs=srgb&ixid=eyJhcHBfaWQiOjgyODgzfQ");
        //wv1.loadUrl("https://images.unsplash.com/photo-1561957119-d628e4bff3a9?ixlib=rb-1.2.1&q=85&fm=jpg&crop=entropy&cs=srgb&ixid=eyJhcHBfaWQiOjgyODgzfQ");
        //https://images.unsplash.com/photo-1561957119-d628e4bff3a9?ixlib=rb-1.2.1&q=85&fm=jpg&crop=entropy&cs=srgb&ixid=eyJhcHBfaWQiOjgyODgzfQ
        wv1.setInitialScale(30);
        wv1.setInitialScale(1);
        wv1.getSettings().setJavaScriptEnabled(true);
        wv1.getSettings().setLoadWithOverviewMode(true);
        wv1.getSettings().setUseWideViewPort(true);
        wv1.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        wv1.setScrollbarFadingEnabled(false);
        //wv.getSettings().setLoadWithOverviewMode(true);
        //wv.getSettings().setUseWideViewPort(true);
        scrollLayout.addView(wv1);


    }
}
