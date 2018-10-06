package com.freelance.yahia.raye7task;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class GetJsonArray  extends AsyncTask<URL, Integer, String>{
    public static RecyclerView.Adapter mAdapter;
    static Context context;
    static int flag;
    long selected;
    public static final String KEY_ID = "_id";
    public static final String TITLE = "TITLE";
    public static final String TIME = "TIME";
    public static final String IMAGEURL = "IMAGEURL";
    public static final String TIMELONG = "TIMELONG";
    public static final String URLS = "URLS";
    public static final String DATABASE_NAME = "myDatabase.db";
    public static final String DATABASE_TABLE = "newsTable";
    public static final String DATABASE_CREATE = "create table if not exists " +
            DATABASE_TABLE + " (" + KEY_ID + " integer primary key autoincrement, " +
            TITLE + " text not null, " +
            TIME + " text, " +
            IMAGEURL + " text, " +
            URLS + " text, " +
            TIMELONG + " long);";
    public static SQLiteDatabase sqLiteDatabase;
    public static Cursor cursor;



    GetJsonArray(Context con, int flagint, long select){

        context = con;
        sqLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME,
                Context.MODE_PRIVATE,
                null);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS '" + DATABASE_TABLE + "'");
        sqLiteDatabase.execSQL(DATABASE_CREATE);
        flag = flagint;
        selected = select;
    };
    @Override
    protected String doInBackground(URL... urls) {

        try {
            HttpURLConnection urlConnection = (HttpURLConnection) urls[0].openConnection();
            InputStream in = urlConnection.getInputStream();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(in, "utf-8"), 8);
            StringBuilder sBuilder = new StringBuilder();

            String line = null;
            while ((line = bReader.readLine()) != null) {
                sBuilder.append(line);
            }

            in.close();
            String fileString = sBuilder.toString();



            return fileString;

        } catch (Exception e){
            return null;
        }

    }

    @Override
    protected void onPostExecute(String fileString) {
        super.onPostExecute(fileString);
        try {
        JSONObject jsonAll = new JSONObject(fileString);
        JSONArray allnews = jsonAll.getJSONArray("articles");


            for(int i = 0; i < allnews.length(); i++){
                ContentValues rowValues = new ContentValues();
                JSONObject article = allnews.getJSONObject(i);
                long dateLong= 500000;
                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    String dateString = article.getString("publishedAt");
                    Date date = format.parse(dateString);
                    dateLong = date.getTime();


                } catch (ParseException e){}
                rowValues.put(TITLE, article.getString("title"));
                rowValues.put(TIME, article.getString("publishedAt"));
                rowValues.put(IMAGEURL, article.getString("urlToImage"));
                rowValues.put(URLS, article.getString("url"));
                rowValues.put(TIMELONG, dateLong);



                sqLiteDatabase.insertOrThrow(DATABASE_TABLE, null, rowValues);
            }

        }catch (Exception e){}

        if(flag == 0)
            cursor = GetJsonArray.sqLiteDatabase.query(DATABASE_TABLE, new String[]{KEY_ID, TITLE, TIME, IMAGEURL, URLS, TIMELONG}, null, null, null, null, null);
        else if(flag == 1) {
            try{
            String[] selectionArgs = new String[MainActivity.favSet.size()];
            String[] columns = {KEY_ID, TITLE, TIME, IMAGEURL, URLS, TIMELONG};
            StringBuilder stringBuilder = new StringBuilder();
            String selection = "TIME IN(";
            stringBuilder.append(selection);
            Iterator<String> iterator = MainActivity.favSet.iterator();
            int count = 0;
            while(iterator.hasNext())
            {
                if (count == 0)
                    stringBuilder.append("?");
                else
                    stringBuilder.append(",?");
                selectionArgs[count] = iterator.next();
                count++;
            };
                stringBuilder.append(")");
                selection = stringBuilder.toString();
            cursor = GetJsonArray.sqLiteDatabase.query(DATABASE_TABLE, columns, selection, selectionArgs, null, null, null);
            }  catch (Exception e) {
                Log.e("drogba", e.getMessage());
                e.printStackTrace();
                Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();}
        }

        else if(flag == 2){
            try{
            long limit = selected + 86400000;
            String[] selectionArgs = {String.valueOf(selected), String.valueOf(limit)};
            String[] columns = {KEY_ID, TITLE, TIME, IMAGEURL, URLS, TIMELONG};
            String selection = "TIMELONG >= ? AND TIMELONG < ?";
            cursor = GetJsonArray.sqLiteDatabase.query(GetJsonArray.DATABASE_TABLE, columns, selection, selectionArgs, null, null, null);
            }  catch (Exception e) {
                Log.e("", e.getMessage());
                e.printStackTrace();}
            }

        cursor.moveToFirst();
        mAdapter = new DataAdapter(cursor);
        MainActivity.mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }
}
