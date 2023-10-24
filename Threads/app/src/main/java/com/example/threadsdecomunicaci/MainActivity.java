package com.example.threadsdecomunicaci;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    private Button b;
    private TextView t1;
    private ImageView iv1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Asegúrate de que el layout esté correctamente configurado
        t1=new TextView(this);
        t1=findViewById(R.id.t1);
        iv1=new ImageView(this);
        iv1=findViewById(R.id.imageView);
        b= new Button(this);
        b = findViewById(R.id.button); // Buscar el botón por su ID
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "¡Hiciste clic en el botón!", Toast.LENGTH_SHORT).show();
                ExecutorService executor = Executors.newSingleThreadExecutor();

                // Ejecuta la tarea en segundo plano.
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        // Realiza la solicitud de red en segundo plano.
                        String data = getDataFromUrl("https://api.myip.com");

                        // Muestra los datos en la consola de debug.
                        Log.i("NetworkResponse", data);

                        // Actualiza el TextView con los datos en el hilo principal.
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                t1.setText(data);
                            }
                        });

                        String urldisplay = "https://randomfox.ca/images/122.jpg";
                        Bitmap bitmap=loadBitmapFromUrl(urldisplay);

                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                iv1.setImageBitmap(bitmap);
                            }
                        });

                    }
                });




            }
        });



    }
    private Bitmap loadBitmapFromUrl(String imageUrl) {
        try {
            InputStream in = new URL(imageUrl).openStream();
            return BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    String error = ""; // string field
    private String getDataFromUrl(String demoIdUrl) {

        String result = null;
        int resCode;
        InputStream in;
        try {
            URL url = new URL(demoIdUrl);
            URLConnection urlConn = url.openConnection();

            HttpsURLConnection httpsConn = (HttpsURLConnection) urlConn;
            httpsConn.setAllowUserInteraction(false);
            httpsConn.setInstanceFollowRedirects(true);
            httpsConn.setRequestMethod("GET");
            httpsConn.connect();
            resCode = httpsConn.getResponseCode();

            if (resCode == HttpURLConnection.HTTP_OK) {
                in = httpsConn.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        in, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                in.close();
                result = sb.toString();
            } else {
                error += resCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


}