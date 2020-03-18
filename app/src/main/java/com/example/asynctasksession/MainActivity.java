package com.example.asynctasksession;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.os.Environment.getExternalStorageDirectory;


public class MainActivity extends AppCompatActivity {
    String URL = "";
    ImageView image;
    Button downloadbutton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = findViewById(R.id.downloaded_image);
        downloadbutton = findViewById(R.id.download);
        downloadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (isConnected()) {
                    new DownloadImage().execute(URL);
                } else {
                    Toast.makeText(MainActivity.this, "Internet not connected", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public boolean isConnected() {
        Context context = getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    private class DownloadImage extends AsyncTask<String, Integer, String> {
        ProgressDialog ProgressDialog;
       // private Context context;
        //Bitmap bitmap;
        private String path;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            ProgressDialog = new ProgressDialog(MainActivity.this);
            // Set progressdialog title
            ProgressDialog.setTitle("Download Image Tutorial");
            // Set progressdialog message
            ProgressDialog.setMessage("Loading...");
            ProgressDialog.setIndeterminate(false);
            // Show progressdialog
            ProgressDialog.show();

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            ProgressDialog.setIndeterminate(false);
            ProgressDialog.setMax(100);
            ProgressDialog.setProgress(values[0]);
        }

        @Override
        protected String doInBackground(String... surl) {
//
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(surl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                path = getExternalStorageDirectory().getAbsolutePath() + "/" + "myImage.jpg";
                output = new FileOutputStream(path);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    while (!isConnected()) {

                    }
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }

                    total += count;
                    publishProgress((int) total * 100 / fileLength);
                    output.write(data, 0, count);


                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            //image.setImageBitmap(result);
            // Close progressdialog
            if (isConnected()) {
                path = getExternalStorageDirectory().getAbsolutePath() + "/" + "myImage.jpg";
//                mProgressDialog.dismiss();
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                image.setImageBitmap(bitmap);
            }


        }
    }
}
