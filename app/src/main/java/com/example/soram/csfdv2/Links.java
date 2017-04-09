package com.example.soram.csfdv2;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Links extends AppCompatActivity {

    ListView lv;
    ArrayAdapter<String> adapter;
    String csfd = "http://www.csfd.cz";
    int movieNumber;
    String fullLink;
    String average;
    String poster;
    String movieName;
    int count = 0;
    int maxRetries = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_links);
        lv = (ListView) findViewById(R.id.listView);


        final ArrayList<String> together = getIntent().getStringArrayListExtra("together");
        final ArrayList<String> titles = getIntent().getStringArrayListExtra("titles");
        adapter = new ArrayAdapter<String>(this, R.layout.simplerow, together);
        lv.setAdapter(adapter);
        lv.setClickable(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(), "Number: " + position, Toast.LENGTH_SHORT).show();
                movieNumber = position;
                movieName = titles.get(position);
                new getLink().execute();

            }
        });
    }

    public class getLink extends AsyncTask<String, String, String>{
        ArrayList<String> links = getIntent().getStringArrayListExtra("links");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
            toDetail();
        }

        @Override
        protected String doInBackground(String... params) {
            fullLink = csfd + links.get(movieNumber);
            while (true) {
                try {
                    Document doc = Jsoup.connect(fullLink).get();
                    Elements avg = doc.select("h2.average");
                    average = avg.text();
                    Elements elms = doc.select(".genre");
                    poster = elms.text();
                    break;

                } catch (IOException e) {
                    if (++count == maxRetries) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }

    public void toDetail(){
        Intent intent = new Intent(this, Detail.class);
        intent.putExtra("Avg", average);
        intent.putExtra("Genre", poster);
        intent.putExtra("Title", movieName);

        startActivity(intent);
        overridePendingTransition(R.anim.from_right, R.anim.to_left);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.from_left, R.anim.to_right);
        finish();
    }
}
