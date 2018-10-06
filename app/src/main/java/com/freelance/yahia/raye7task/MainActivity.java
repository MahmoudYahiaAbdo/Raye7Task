package com.freelance.yahia.raye7task;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.Toast;

import java.net.URL;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    DatePickerDialog datePickerDialog;
    GetJsonArray getJsonArray;
    static SharedPreferences pref;
    static SharedPreferences.Editor editor;
    static Set<String> favSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        favSet = pref.getStringSet("fav_set", new HashSet<String>());


        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLayoutManager);
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        try{
            final URL url = new URL("https://newsapi.org/v2/everything?q=google&domains=usatoday.com&apiKey=08325634f1454ab28f88e1bf4cceef3b");
            if(networkInfo != null)
            {getJsonArray = new GetJsonArray(getApplicationContext(), 0, 0);
            getJsonArray .execute(url);}
            else
                Toast.makeText(getApplicationContext(),  " Check your Connection", Toast.LENGTH_LONG).show();

        } catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Calendar calender = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override

            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                Calendar selected = Calendar.getInstance();
                selected.set(i, i1, i2, 0, 0);
                long seltime = selected.getTimeInMillis();
                try{
                    ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = cm.getActiveNetworkInfo();

                    final URL url = new URL("https://newsapi.org/v2/everything?q=google&domains=usatoday.com&apiKey=08325634f1454ab28f88e1bf4cceef3b");
                    if(networkInfo != null)
                    {getJsonArray = new GetJsonArray(getApplicationContext(), 2, seltime);
                        getJsonArray .execute(url);}
                    else
                        Toast.makeText(getApplicationContext(),  " Check your Connection", Toast.LENGTH_LONG).show();

                } catch (Exception e){
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        };

         datePickerDialog = new DatePickerDialog(MainActivity.this, onDateSetListener, calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH));



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.fav) {

            try{

                final URL url = new URL("https://newsapi.org/v2/everything?q=google&domains=usatoday.com&apiKey=08325634f1454ab28f88e1bf4cceef3b");
                ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                if(networkInfo != null)
                {getJsonArray = new GetJsonArray(getApplicationContext(), 1, 0);
                    getJsonArray .execute(url);}
                else
                    Toast.makeText(getApplicationContext(),  " Check your Connection", Toast.LENGTH_LONG).show();

            } catch (Exception e){
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }

            return true;
        }

        if (id == R.id.pickday) {

            datePickerDialog.show();


            return true;}

        if (id == R.id.refresh) {
        try{

            final URL url = new URL("https://newsapi.org/v2/everything?q=google&domains=usatoday.com&apiKey=08325634f1454ab28f88e1bf4cceef3b");
            ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if(networkInfo != null)
            {getJsonArray = new GetJsonArray(getApplicationContext(), 0, 0);
                getJsonArray .execute(url);}
            else
                Toast.makeText(getApplicationContext(),  " Check your Connection", Toast.LENGTH_LONG).show();

        } catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
            return true;}
        return super.onOptionsItemSelected(item);
    }
}
