package com.example.george.krustybet;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    Button button1;
    Elements answerers,gameName;
    String word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1 = (Button) findViewById(R.id.button);
         textView = (TextView)findViewById(R.id.textview);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               new doit().execute();
            }
        });
    }

    public class doit extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            String url = "https://www.mcheza.co.ke/#/";

            try {
                Document document = Jsoup.connect(url).get();
             //   String question = document.select("#question .post-text").text();
               // System.out.println("Question: " + question);

               word = document.text();
                System.out.println("gameeeeeeeeeee: " + word);

/*
                answerers = document.select("li#highlightedmatch ");
                int mElementSize = answerers.size();

                for (int i =0; i < mElementSize; i++){
                     gameName = document.getElementsByTag("span");
                    System.out.println("gameeeeeeeeeee" + gameName.text());
                }


                for (Element answerer : answerers) {

                    System.out.println("Answerer: " + answerer.text());
                }
                */


            }catch (Exception e){
                e.printStackTrace();
            }






            return null;
        }

        @Override
        protected  void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);
            System.out.println("Answerer: " + word);
            textView.setMovementMethod(new ScrollingMovementMethod());
            textView.setText(word);
            /*
            for (Element answerer : answerers) {
                System.out.println("Answerer: " + answerer.text());

                textView.setMovementMethod(new ScrollingMovementMethod());
                //textView.setText(answerer.text());
                textView.setText(gameName.text());
                */

            }


        }
    }

