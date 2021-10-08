package com.example.dubaothoitiet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private TextView id,name,url;
    private ImageView hinh;
    private String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setcontrol();
        //Starts Retrofit
        final GitHubApi gitHubApi = GitHubApi.retrofit.create(GitHubApi.class);

        //Sets up up the API call
        Call<List<UserModel>> call = gitHubApi.loadUser();
        //Runs the call on a different thread
        call.enqueue(new Callback<List<UserModel>>() {
            @Override
            //Once the call has finished
            public void onResponse(Call<List<UserModel>> call, Response<List<UserModel>> response) {
                //Gets the list of followers
                List<UserModel> followers = response.body();

                //Loads a random follower and assigns it to the TextView
                int index = (int) (Math.random() * followers.size());
                String text = "Random follower - " + followers.get(index).toString();
                id.setText("ID: "+followers.get(index).getId());
                name.setText("NAME: "+followers.get(index).getLogin());
                link = followers.get(index).getUrl();
                url.setText(link);
                Picasso.get()
                        .load(followers.get(index).getAvatar_url())
                        .fit()
//                                .transform(transformation)
                        .into(hinh);

                Log.e("test", text);
            }

            @Override
            //If the call failed
            public void onFailure(Call<List<UserModel>> call, Throwable t) {
                Log.e("test", "fail");
            }
        });
        url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(browserIntent);
            }
        });

    }

    private void setcontrol() {
        id=findViewById(R.id.id);
        name=findViewById(R.id.ten);
        url=findViewById(R.id.link);
        hinh=findViewById(R.id.anh);
    }


}