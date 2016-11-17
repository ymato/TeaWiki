package com.agini.teawiki.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.agini.teawiki.R;
import com.agini.teawiki.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetailsActivity extends AppCompatActivity {
    private String url = "http://sns.maimaicha.com/api?apikey=b4f4ee31a8b9acc866ef2afb754c33e6&format=json&method=news.getNewsContent&id=";
    private String id="";
    private String title="";
    private String source="";
    private String time="";
    private WebView mWebView;
    private TextView titleTextView,sourceTextView,timeTextView;
    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        initView();
        initData();
        initWebView();

    }



   /* List<String> list = new ArrayList<>();
    private void initWebView() {
        new Thread() {
            public void run() {
                byte[] data = HttpUtils.getByteFromUrl(url + id);
                if (data != null) {
                    list = parserDetailJson(new String(data));
                }
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        mWebView.loadDataWithBaseURL(null,
                                list.get(0), "text/html", "utf-8",
                                null);
                    }
                });
            }

        }.start();
    }

    // 解析详情页的数据
    private List<String> parserDetailJson(String string) {
        try {
            JSONObject object = null;
            try {
                object = new JSONObject(string);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject object2 = object.getJSONObject("data");
            List<String> list = new ArrayList<>();
           // map.put("wap_content", object2.getString("wap_content"));
            list.add(object2.getString("wap_content"));
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
*/
   private Map<String, String> map;
   private void initWebView() {
       new Thread() {
           public void run() {
               byte[] data = HttpUtils.getByteFromUrl(url + id);
               if (data != null) {
                   map = parserDetailJson(new String(data));
               }
               mHandler.post(new Runnable() {

                   @Override
                   public void run() {
                       mWebView.loadDataWithBaseURL(null,
                               map.get("wap_content"), "text/html", "utf-8",
                               null);
                   }
               });
           }

       }.start();
   }

    // 解析详情页的数据
    private Map<String, String> parserDetailJson(String string) {
        try {
            JSONObject object = new JSONObject(string);
            JSONObject object2 = object.getJSONObject("data");
            Map<String, String> map = new HashMap<String, String>();
            map.put("wap_content", object2.getString("wap_content"));
            return map;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }




    private void initView() {
        mWebView= (WebView) findViewById(R.id.detailWeb);
        titleTextView= (TextView) findViewById(R.id.title);
        sourceTextView= (TextView) findViewById(R.id.source);
        timeTextView= (TextView) findViewById(R.id.cteateTime);
    }

    private void initData() {
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        title=bundle.getString("title");
        Log.d("flag", "-------------->initData:bundle.getString " +bundle.getString("title"));
        source=bundle.getString("source");
        time=bundle.getString("time");
         id=bundle.getString("id");

        titleTextView.setText(title);
        sourceTextView.setText(source);
        timeTextView.setText(time);


    }

    public void detailBtn(View view) {
        switch (view.getId()){
            case R.id.back:
                this.finish();
                break;
            case R.id.share:

                break;
            case R.id.collection:

                break;
        }
    }
}
