package android.example.photo.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.widget.LinearLayout;

public class Util {
    public static Boolean compareWithFavorites(String photo_id, Context context){
        DBHelperFav dbHelperFav = new DBHelperFav(context);
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
    public static void  setUrlAndIdOnDB(String url, String photo_id, String created, Context context){
        ContentValues cv = new ContentValues();
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        cv.put("url", url);
        cv.put("photo_id", photo_id);
        cv.put("created", created);
        db.insert("urlsTable", null, cv);
    }
    public static void deleteDB(String table, Context context){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(table, null,null);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + table + "'");
    }
    public static void deleteFromFavDB(String photo_id, LinearLayout scrollLayout, Context context){
        DBHelperFav dbHelper = new DBHelperFav(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("favoritesTable", null, null, null,
                null, null, null);
        Integer i = 0;
        if(c.moveToFirst()){
            do {
                if(c.getString(c.getColumnIndex("photo_id")).equals(photo_id)){
                    db.delete("favoritesTable","id = " + c.getInt(c.getColumnIndex("id")),null);
                    db.execSQL("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE NAME = 'favoritesTable'");//обновление столбца с id
                    if(scrollLayout != null)
                        scrollLayout.removeViewAt(i);
                    break;
                }
                i++;
            }while (c.moveToNext());
        }
    }
    public static void addToDB(String photo_id, Context context) {
        ContentValues cv = new ContentValues();
        DBHelperFav dbHelperFav = new DBHelperFav(context);
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SQLiteDatabase db2 = dbHelperFav.getWritableDatabase();
        String url = "https://images.unsplash.com/photo-1556228453-6ecff5553887?ixlib=rb-1.2.1&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&ixid=eyJhcHBfaWQiOjgyODgzfQ",
                created = "00-00-2000";
        Cursor c = db.query("urlsTable", null, null, null,
                null, null, null);
        if (c.moveToFirst()) {
            do {
                if (c.getString(c.getColumnIndex("photo_id")).equals(photo_id)) {
                    url = c.getString(c.getColumnIndex("url"));
                    created = c.getString(c.getColumnIndex("created"));
                }
            } while (c.moveToNext());
        }
        cv.put("url", url);
        cv.put("photo_id", photo_id);
        cv.put("created", created);
        db2.insert("favoritesTable", null, cv);
    }
    public static String rebuildCreatedTime(String created){
        String[] rebuild = null;
        created = created.replace("-04:00","");
        rebuild = created.split("T");
        return rebuild[0] + " in " + rebuild[1];
    }
    public static class DBHelperFav extends SQLiteOpenHelper {
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
            // создаем таблицу с полями
            db.execSQL("create table favoritesTable ("
                    + "id integer primary key autoincrement,"
                    + "url text,"
                    + "photo_id text,"
                    + "created text" + ");");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
    }
    public static class DBHelper extends SQLiteOpenHelper {
        String nameTable = "urlsTable";
        public void setNameTable(String nameTable) {
            this.nameTable = nameTable;
        }
        public DBHelper(Context context) {
            // конструктор суперкласса
            super(context, "photoDB", null, 1);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            // создаем таблицу с полями
            db.execSQL("create table " + nameTable + " ("
                    + "id integer primary key autoincrement,"
                    + "url text,"
                    + "photo_id text,"
                    + "created text" + ");");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
    }
}
