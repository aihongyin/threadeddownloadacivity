package edu.sjsu.android.hw5;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.app.Activity;
import android.view.View.OnClickListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import javax.net.ssl.HttpsURLConnection;
import android.widget.ProgressBar;
import android.os.Handler;
import android.widget.Toast;


public class MainActivity extends Activity {
    private final static String TAG = "ThreadingAsyncTask";

    String url = "https://images.pexels.com/photos/7333174/pexels-photo-7333174.jpeg?cs=srgb&dl=pexels-evgeniy-alekseyev-7333174.jpg&fm=jpg";
    ImageView imageView;
    ImageView defaultView;
    private int mDelay = 1000;
    Button resetButton;
    Handler mainHandler = new Handler();
    ProgressDialog progressDialog;



    //messgae
    private final static int SET_PROGRESS_BAR_VISIBILITY = 0;
    private final static int PROGRESS_UPDATE = 1;
    private final static int SET_BITMAP = 2;
    //private ImageView mImageView;
    private ProgressBar mProgressBar;
    private int nDelay = 500;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SET_PROGRESS_BAR_VISIBILITY:_VISIBILITY: {
                    mProgressBar.setVisibility((Integer) msg.obj);
                    break;
                }
                case PROGRESS_UPDATE: {
                    mProgressBar.setProgress((Integer) msg.obj);
                    break;
                }
                case SET_BITMAP: {
                    imageView.setImageBitmap((Bitmap) msg.obj);
                    break;
                }
            }
        }
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addListenerOnButton();

        Button btnRunRunnable = (Button) findViewById(R.id.btnRunRunnable);
        btnRunRunnable.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText)findViewById(R.id.editText);
                String url = editText.getText().toString();
                String urlTest = "https://images.pexels.com/photos/7333174/pexels-photo-7333174.jpeg?cs=srgb&dl=pexels-evgeniy-alekseyev-7333174.jpg&fm=jpg";

                if(TextUtils.isEmpty(url)){
                    Toast.makeText(getApplicationContext(),"Empty URL! Please enter an image URL!",Toast.LENGTH_SHORT).show();
                }else {
                    // url has value
                    new FetchImage(url).start();
                }
            }
        });




        mProgressBar = (ProgressBar) findViewById
                (R.id.progressBar);
        final Button button = (Button) findViewById
                (R.id.btnRunMessage);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                EditText editText = (EditText)findViewById(R.id.editText);
                String url = editText.getText().toString();
                String urlTest = "https://images.pexels.com/photos/830829/pexels-photo-830829.jpeg?cs=srgb&dl=pexels-frans-van-heerden-830829.jpg&fm=jpg";

                if(TextUtils.isEmpty(url)){
                    Toast.makeText(getApplicationContext(),"Empty URL! Please enter an image URL!",Toast.LENGTH_SHORT).show();
                }else {
                    // url has value
                    new Thread(new LoadIconTask(url,
                            handler)).start();
                }

            }
        });


    }





    //This is for the message
    private class LoadIconTask implements Runnable {
        //private final int resId;
        String URL;
        Bitmap myBitmap;
        private final Handler handler;

        LoadIconTask(String URL, Handler handler) {
            this.URL = URL;
            this.handler = handler;
        }

        public void run() {
            Message msg = handler.obtainMessage
                    (SET_PROGRESS_BAR_VISIBILITY,
                            ProgressBar.VISIBLE);


            handler.sendMessage(msg);


            try {
                java.net.URL url = new java.net.URL(URL);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                myBitmap = BitmapFactory.decodeStream(input);



            } catch (IOException e) {
                e.printStackTrace();

            }

            //final Bitmap tmp = BitmapFactory.decodeResource
            //(getResources(), resId);
            for (int i = 1; i < 11; i++) {
                sleep();
                msg = handler.obtainMessage(PROGRESS_UPDATE, i *
                        10);
                handler.sendMessage(msg);
            }
            msg = handler.obtainMessage(SET_BITMAP, myBitmap);
            handler.sendMessage(msg);
            msg = handler.obtainMessage
                    (SET_PROGRESS_BAR_VISIBILITY,
                            ProgressBar.INVISIBLE);



            handler.sendMessage(msg);
        }


        private void sleep() {
            try {
                Thread.sleep(nDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }





    //This is for the run runnable
    class FetchImage extends Thread{

        String URL;
        Bitmap bitmap;
        FetchImage(String URL){
            this.URL = URL;
        }

        @Override
        public void run(){

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    progressDialog=new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Download\ndownloading via Run Runnable");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            });

            //HttpsURLConnection httpsURLConnection;
            //InputStream inputStream;
            try {
                InputStream inputStream;
                inputStream = new URL(URL).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
                    imageView.setImageBitmap(bitmap);
                }
            });

        }
    }



    //This is for the runAsyncTask
    public void runAsyncTask(View view){
        DownloadImage downloadImage = new DownloadImage();
        imageView = findViewById(R.id.download_image);
        defaultView = findViewById(R.id.default_image);

        try {

            EditText editText = (EditText)findViewById(R.id.editText);
            String url = editText.getText().toString();



            if(TextUtils.isEmpty(url)){
                Toast.makeText(getApplicationContext(),"Empty URL! Please enter an image URL!",Toast.LENGTH_SHORT).show();
            }else {
                // string has value
                Bitmap bitmap = downloadImage.execute(url).get();
                imageView.setImageBitmap(bitmap);
                defaultView.setVisibility(View.INVISIBLE);


            }


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public class DownloadImage extends AsyncTask<String, Long, Bitmap>{
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        String waitMsg = "Download\ndownloading via Async Task";



        protected void onPreExecute() {
            this.dialog.setMessage(waitMsg);
            //this.dialog.setCancelable(false); //outside touch doesn't dismiss you
            this.dialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            URL url;
            HttpsURLConnection httpURLConnection;
            InputStream in;
            try {
                sleep();
                url = new URL(strings[0]);
                httpURLConnection = (HttpsURLConnection) url.openConnection();
                in = httpURLConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(in);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }



        protected void onProgressUpdate(Long... value){
            super.onProgressUpdate(value);
            dialog.setMessage(waitMsg+value[0]);

        }

        // can use UI thread here
        protected void onPostExecute(Bitmap result) {

                this.dialog.dismiss();
            imageView.setImageBitmap(result);


        }


        private void sleep() {
            try {
                Thread.sleep(mDelay);
            } catch (InterruptedException e) {
                Log.e(TAG, e.toString());
            }
        }


    }



    public void addListenerOnButton() {

        imageView = (ImageView) findViewById(R.id.default_image);
        resetButton = (Button) findViewById(R.id.btnResetImage);
        resetButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
               imageView.setImageResource(R.drawable.default_image);


            }

        });

    }

}
