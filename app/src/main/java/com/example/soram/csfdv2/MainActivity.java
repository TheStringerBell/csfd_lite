package com.example.soram.csfdv2;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    Button button;
    TextView movieText;
    ArrayList<String> array = new ArrayList<String>();
    ArrayList<String> linkArray = new ArrayList<String>();
    ArrayList<String> dateCreated = new ArrayList<String>();
    ArrayList<String> finalArray = new ArrayList<String>();
    boolean tryAgain = true;
    int count = 0;
    int maxRetries = 3;
    String movie;
    String hledat = "http://www.csfd.cz/hledat/?q=";
    String csfd = "http://www.csfd.cz";
    ProgressBar bar;
    int forBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_2);
        final EditText input = (EditText) findViewById(R.id.editText3);
        button = (Button) findViewById(R.id.button3);
        bar = (ProgressBar) findViewById(R.id.progressBar);
        movieText = (TextView) findViewById(R.id.movies);
        LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout2);
        AnimationDrawable anim = (AnimationDrawable) ll.getBackground();
        anim.setEnterFadeDuration(0);
        anim.setExitFadeDuration(2500);
        anim.start();
        bar.setVisibility(View.GONE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setVisibility(View.VISIBLE);
                bar.setMax(100);
                String search = input.getText().toString();
                movie = hledat + search;
                new getLink().execute();
            }
        });
    }

    public class getLink extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
            if (!tryAgain){
                Toast.makeText(MainActivity.this, "Try again", Toast.LENGTH_SHORT).show();
            }
            else {
                bar.setProgress(100);
                bar.setMax(array.size());
                new getDateCreated().execute();
            }
        }
        @Override
        protected String doInBackground(String... params) {

            try {
                Document doc = Jsoup.connect(movie).get();
                Elements elms = doc.select(".film");

                if (elms.isEmpty()) {
                    tryAgain = false;
                } else {
                    tryAgain = true;
                    int limit = Math.min(16, elms.size());
                    for (int j = 0; j < limit; j++) {
                        array.add(elms.get(j).text());
                        linkArray.add(elms.get(j).attr("href"));
                        publishProgress(Integer.toString(j));
                    }
                }
            }
                catch(IOException e){
                    e.printStackTrace();
                }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            bar.setProgress(Integer.parseInt(values[0])*8);
        }
    }

    public class getDateCreated extends AsyncTask<String, String, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            for (int j = 0; j < array.size()-1; j++) {
                finalArray.add(array.get(j) + " / " + dateCreated.get(j));
            }
            bar.setProgress(100);
            bar.setVisibility(View.GONE);
            switchScreen();
        }

        @Override
        protected String doInBackground(String... params) {

            for (int j = 0; j < linkArray.size()-1; j++) {
                publishProgress(Integer.toString(j), array.get(j));
                while (true) {
                    try {
                        Document doc = Jsoup.connect(csfd + linkArray.get(j)).get();
                        Elements elms = doc.select("span[itemprop=dateCreated]");
                        dateCreated.add(elms.text());
                        break;
                    } catch (IOException e) {
                        if (++count == maxRetries) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            bar.setProgress(Integer.parseInt(values[0]));
            movieText.setText(values[1]);
        }
    }
    public void switchScreen() {
        Intent intent = new Intent(this, Links.class);
        intent.putStringArrayListExtra("titles", array);
        intent.putStringArrayListExtra("together", finalArray);
        intent.putStringArrayListExtra("links", linkArray);
        startActivity(intent);
        overridePendingTransition(R.anim.from_right, R.anim.to_left);
        finish();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        new AlertDialog.Builder(this)
                .setTitle("Do you really want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                    }
                }).create().show();

    }
}


