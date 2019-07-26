package android.example.photo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.example.photo.Fragments.PhotosFragment;

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
        //System.out.println("_______________________________________" + created);
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
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
    }
    public static class DBHelper extends SQLiteOpenHelper {
        //вроде был private not static
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
            //Log.d(LOG_TAG, "--- onCreate database ---");
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
