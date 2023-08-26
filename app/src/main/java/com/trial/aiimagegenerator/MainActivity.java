package com.trial.aiimagegenerator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    EditText inputText;
    MaterialButton button;
    ProgressBar progressBar;
    ImageView imageView;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputText=findViewById(R.id.textView);
        button=findViewById(R.id.button);
        progressBar=findViewById(R.id.progressBar);
        imageView=findViewById(R.id.image_view);

        button.setOnClickListener((v -> {
            String text = inputText.getText().toString().trim();
            if (text.isEmpty()) {
                inputText.setError("Text cannot be empty");
                return;
            }
            callAPI(text);
        }));
    }
    void callAPI(String text){
        //API Call
        setInProgress(true);
        JSONObject jsonBody=new JSONObject();
        try {
            jsonBody.put("prompt", text);
            jsonBody.put("size", "256x256");
        } catch (Exception e){
            e.printStackTrace();
        }
        RequestBody requestBody=RequestBody.create(jsonBody.toString(), JSON);
        Request request=new Request.Builder()
                .url("https://api.openai.com/v1/images/generations")
                .header("Authorization", "Bearer sk-AO2**********************************rDQ")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(getApplicationContext(), "Failed to generate image", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try{
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String imageURl=jsonObject.getJSONArray("data").getJSONObject(0).getString("url");
                    loadImage(imageURl);
                    setInProgress(false);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    void setInProgress(boolean inProgress){
        runOnUiThread(()->{
            if(inProgress){
                progressBar.setVisibility(View.VISIBLE);
                button.setVisibility(View.GONE);
            }else{
                progressBar.setVisibility(View.GONE);
                button.setVisibility(View.VISIBLE);
            }

        });

    }

    void loadImage(String url) {
        runOnUiThread(()->{
            Picasso.get().load(url).into(imageView);
        });

    }
}

