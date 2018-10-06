package com.freelance.yahia.raye7task;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.Executor;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder>{


        String linksarray[];

    private class DownloadImageTask extends AsyncTask<String, Bitmap, Void> {

        int count;

        public DownloadImageTask(int pos) {
            count = pos;
        }

        protected Void doInBackground(String... urls) {
            for(int i = 0; i < urls.length; i++){

                String urldisplay = urls[i];

                Bitmap mIcon11 = null;
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) new URL(urldisplay).openConnection();
                InputStream in = urlConnection.getInputStream();

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inSampleSize = 2;
                mIcon11 = BitmapFactory.decodeStream(in, null, options);

                publishProgress(mIcon11);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
                mIcon11 = BitmapFactory.decodeResource(GetJsonArray.context.getResources(), R.drawable.ic_launcher_background);
                publishProgress(mIcon11);
            }

        }
        return null;
        }

        @Override
        protected void onProgressUpdate(Bitmap... bitmaps) {

            super.onProgressUpdate(bitmaps);
            count++;
            imagesBM.set(count, bitmaps[0]);
            DataAdapter.this.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }


    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> times = new ArrayList<>();
    ArrayList<String> links = new ArrayList<>();
    ArrayList<String> newslinks = new ArrayList<>();
    ArrayList<Bitmap> imagesBM = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout itemView;


        public ViewHolder(RelativeLayout relativeLayout) {
            super(relativeLayout);
            itemView = relativeLayout;
        }
    }
    public DataAdapter(Cursor curs) {
        try {
            int a = curs.getColumnIndex(GetJsonArray.TITLE);
            int b = curs.getColumnIndex(GetJsonArray.TIME);
            int c = curs.getColumnIndex(GetJsonArray.IMAGEURL);
            int d = curs.getColumnIndex(GetJsonArray.URLS);
            for (int z = 0; z < curs.getCount(); z++) {
                titles.add(curs.getString(a));
                times.add(String.valueOf(curs.getString(b)));
                links.add(curs.getString(c));
                newslinks.add(curs.getString(d));
                imagesBM.add(BitmapFactory.decodeResource(GetJsonArray.context.getResources(), R.drawable.ic_launcher_background));
                curs.moveToNext();

            }
            linksarray = new String[curs.getCount()];
            for (int i = 0; i < curs.getCount(); i++) {
                linksarray[i] = links.get(i);
            }
            DownloadImageTask downloadImageTask = new DownloadImageTask(-1);
            downloadImageTask.execute(linksarray);
            Toast.makeText(GetJsonArray.context, String.valueOf(imagesBM.size()), Toast.LENGTH_LONG).show();
        } catch (Exception e){Toast.makeText(GetJsonArray.context, "er1" + e.toString(), Toast.LENGTH_LONG).show();}

    }
    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);


        ViewHolder vh = new ViewHolder(v);
        return vh;
    }



    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

    }

    public void onBindViewHolder(ViewHolder holder, final int position) {

        try{
        TextView title = holder.itemView.findViewById(R.id.title);
        final TextView timeView = holder.itemView.findViewById(R.id.timeView);
        ImageView imageView = holder.itemView.findViewById(R.id.image);
        final Button addFav = holder.itemView.findViewById(R.id.addFav);
        title.setText(titles.get(position));
        timeView.setText(times.get(position));
        imageView.setImageBitmap(imagesBM.get(position));

            if(MainActivity.favSet.contains(times.get(position)))
                addFav.setText("Remove Fav");
            else
                addFav.setText("Add To Fav.");
        addFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MainActivity.favSet.add(times.get(position))){
                MainActivity.editor.remove("fav_set");
                MainActivity.editor.commit();
                MainActivity.editor.putStringSet("fav_set", MainActivity.favSet);
                MainActivity.editor.commit();
                addFav.setText("Remove Fav");
            }
                else {
                        MainActivity.favSet.remove(times.get(position));
                        MainActivity.editor.remove("fav_set");
                        MainActivity.editor.commit();
                        MainActivity.editor.putStringSet("fav_set", MainActivity.favSet);
                        MainActivity.editor.commit();
                        addFav.setText("Add To Fav.");
                }
            }
        });
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(newslinks.get(position)));
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        GetJsonArray.context.startActivity(i);
                    }catch (Exception e){Toast.makeText(GetJsonArray.context, "er2" + e.toString(), Toast.LENGTH_LONG).show();}
                }

            });
    } catch (Exception e) {
            Log.e("ZtonaHolder", e.getMessage());
            e.printStackTrace();}}

    @Override
    public int getItemCount() {
        return titles.size();
    }
}

