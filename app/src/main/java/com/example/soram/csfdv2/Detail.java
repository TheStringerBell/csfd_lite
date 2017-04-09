package com.example.soram.csfdv2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Detail extends AppCompatActivity {
    TextView name;
    TextView percent;
    TextView genre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String avg = getIntent().getStringExtra("Avg");
        String genres = getIntent().getStringExtra("Genre");
        String title = getIntent().getStringExtra("Title");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Spanned text = Html.fromHtml("<b>" + title + "</b>" +  "<br />" +
                "<small>" + genres + "</small>");

        name = (TextView) findViewById(R.id.name);
        percent = (TextView) findViewById(R.id.percent);
        genre = (TextView) findViewById(R.id.genre);
        name.setText(text);
        percent.setText(avg);
//        genre.setText(poster);

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(R.anim.from_left, R.anim.to_right);
        finish();
    }
}
