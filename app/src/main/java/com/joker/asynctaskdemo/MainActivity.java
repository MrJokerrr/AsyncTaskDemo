package com.joker.asynctaskdemo;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mLoadImage;
    private ImageView mImageView;
    private ProgressDialog mDialog;
    private String IMAGE_PATH = "http://ww2.sinaimg.cn/mw690/69c7e018jw1e6hd0vm3pej20fa0a674c.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadImage = (Button) findViewById(R.id.btn_load);
        mImageView = (ImageView) findViewById(R.id.iv_image);
        mDialog = new ProgressDialog(this);
        mDialog.setTitle("提示信息");
        mDialog.setMessage("正在加载,请稍后...");
        mDialog.setCancelable(false);
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        mLoadImage.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_load:
                new MyAsyncTask().execute(IMAGE_PATH);
                break;
        }
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, byte[]>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
        }

        @Override
        protected byte[] doInBackground(String... params) {
            URL url;
            HttpURLConnection conn = null;
            byte[] image = new byte[]{};
            try {
                url = new URL(IMAGE_PATH);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                int code = conn.getResponseCode();
                if (code == 200){
                    int fileLength = conn.getContentLength();
                    int totalLength = 0;
                    InputStream in = conn.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length = 0;
                    while ((length = in.read(buffer)) != -1){
                        // 每读一次,将totalLength累加
                        totalLength += length;
                        bos.write(buffer, 0, length);
                        // 得到图片下载进度
                        int progress = (int) ((totalLength/(float)fileLength)*100);
                        // 将进度传给onProgressUpdate
                        publishProgress(progress);
                    }
                    image = bos.toByteArray();
                    in.close();
                    bos.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                conn.disconnect();
            }
            return image;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            mDialog.dismiss();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            mImageView.setImageBitmap(bitmap);
        }
    }
}
