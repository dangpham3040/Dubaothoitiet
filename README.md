# API Calls in Android Using Retrofit 2
If you read enough about Android development, you'll inevitably run into a library named [Retrofit](http://square.github.io/retrofit/), a type-safe HTTP client for Android and Java.  Everyone online seems to rave about it, but it can be daunting to learn how or why to use it.  Here we'll start from the ground up and teach you everything you need to know to get up and running with Retrofit.


## Code Example

Let's take a look at some code.  We made a sample app with some practical applications of Retrofit.  The app doesn't do anything fancy, it just takes the followers of a user on GitHub and displays one of them on the screen.

If you want to follow along, create a new app with a blank activity and add a button and a textview to the the activity's layout.

Retrofit is added to the project through the following lines in the the [Gradle file](https://github.com/dangpham3040/Dubaothoitiet/blob/main/app/build.gradle):

```
 implementation 'com.squareup.retrofit2:retrofit:2.1.0'
 implementation 'com.squareup.retrofit2:converter-gson:2.1.0'
 
 implementation 'com.squareup.picasso:picasso:2.71828'
```

Before we start anything else, we need to add the following line to our [Manifest](https://github.com/dangpham3040/Dubaothoitiet/blob/main/app/src/main/AndroidManifest.xml) to grant our app permission to access the internet.

```<uses-permission android:name="android.permission.INTERNET" />```


### Creating the Model Class

In order for Retrofit to extract data from a request. We need to make a model class to store the information in. This is not strictly required, and we could technically just get the raw response back, but storing the information in a model class and making it more easily accessible is one of the areas where Retrofit shines.

Let's look at the reponse we get from running a GET request on a user's followers, specifically:

```
https://api.github.com/users
```

We get a list of users back in the form of [JSON](http://www.json.org/) objects: 

```	
[
  {
    "login": "mojombo",
    "id": 1,
    "node_id": "MDQ6VXNlcjE=",
    "avatar_url": "https://avatars.githubusercontent.com/u/1?v=4",
    "gravatar_id": "",
    "url": "https://api.github.com/users/mojombo",
    "html_url": "https://github.com/mojombo",
    "followers_url": "https://api.github.com/users/mojombo/followers",
    "following_url": "https://api.github.com/users/mojombo/following{/other_user}",
    "gists_url": "https://api.github.com/users/mojombo/gists{/gist_id}",
    "starred_url": "https://api.github.com/users/mojombo/starred{/owner}{/repo}",
    "subscriptions_url": "https://api.github.com/users/mojombo/subscriptions",
    "organizations_url": "https://api.github.com/users/mojombo/orgs",
    "repos_url": "https://api.github.com/users/mojombo/repos",
    "events_url": "https://api.github.com/users/mojombo/events{/privacy}",
    "received_events_url": "https://api.github.com/users/mojombo/received_events",
    "type": "User",
    "site_admin": false
  },
  {
    "login": "defunkt",
    "id": 2,
    "node_id": "MDQ6VXNlcjI=",
    "avatar_url": "https://avatars.githubusercontent.com/u/2?v=4",
    "gravatar_id": "",
    "url": "https://api.github.com/users/defunkt",
    "html_url": "https://github.com/defunkt",
    "followers_url": "https://api.github.com/users/defunkt/followers",
    "following_url": "https://api.github.com/users/defunkt/following{/other_user}",
    "gists_url": "https://api.github.com/users/defunkt/gists{/gist_id}",
    "starred_url": "https://api.github.com/users/defunkt/starred{/owner}{/repo}",
    "subscriptions_url": "https://api.github.com/users/defunkt/subscriptions",
    "organizations_url": "https://api.github.com/users/defunkt/orgs",
    "repos_url": "https://api.github.com/users/defunkt/repos",
    "events_url": "https://api.github.com/users/defunkt/events{/privacy}",
    "received_events_url": "https://api.github.com/users/defunkt/received_events",
    "type": "User",
    "site_admin": false
  }
]

```

For this app, we only need to store the username of each user, or the 'login' element of each JSON object in the list.

Therefore, we can create an extremely short model class called [UserModel.java](https://github.com/dangpham3040/Dubaothoitiet/blob/main/app/src/main/java/com/example/dubaothoitiet/UserModel.java) (or anything you want) that looks like this:

```java
package com.example.dubaothoitiet;

/**
 * Retrofit model class to store information about GitHub users.
 */
public class UserModel {
    String login;
    String id;
    String avatar_url;
    String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "login='" + login + '\'' +
                ", id='" + id + '\'' +
                ", avatar_url='" + avatar_url + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
```

Since we only need to store the username, it's an extremely simple file. If you want to store more fields, such as 'avatar\_url', you would have to create corresponding 'avatar\_url' fields in your model class.
### Using GitHub's API
Now that we have our model class set up, we are now ready to prepare to connect to GitHub's API.  To accomplish this, we need to create an interface that Retrofit uses to make calls to GitHub's API. Let's name this [GitHubApi.java](https://github.com/dangpham3040/Dubaothoitiet/blob/main/app/src/main/java/com/example/dubaothoitiet/GitHubApi.java). It will look similar to this:

```java
package com.example.dubaothoitiet;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Class that details the request(s) that we will call
 */

public interface GitHubApi {
    @GET("users/{user}/followers")
    Call<List<UserModel>> loadFollowers(
            @Path("user") String user
    );
    @GET("users")
    Call<List<UserModel>> loadUser(

    );

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

}

```

The first part of this interfaces defines the GET call that we want to use. It specifies both the endpoint (notice that it allows for an argument) as well as the return type. We can set this to whatever we want, provided that it makes sense. However, ```@GET("users/{user}/followers")``` returns a list of JSON objects, so while the model class can be whatever we specify (the ```UserModel``` class), we must return a ```List```.

The second part sets up the Retrofit object that we can use in our app. We specify the base URL as well as the converter factory to convert the JSON response into our model class. ```GsonConverterFactory``` is the standard factory to use.

Now, we are ready to actually use this API call in our app.

### Making the API call

In our app, we will be making the API call in [```MainActivity```](https://github.com/dangpham3040/Dubaothoitiet/blob/main/app/src/main/java/com/example/dubaothoitiet/MainActivity.java).

The first thing to do is to actually take advantage of the interface we have created. If you know what an interface is (don't worry if you don't, just make a quick Google search during or even after this tutorial), you know that normally we have to implement our interface in a class to actually do anything. However, the beauty of Retrofit is that it does this for us at runtime. All we have to do is the following:

```final GitHubApi gitHubApi = GitHubApi.retrofit.create(GitHubApi.class);```

After this, we can use the newly created ```gitHubApi``` object to make API calls.

You can put the above line in your ```onCreate``` method as we did, or you can put it anywhere else in your app, so long as it gets called before you make any API calls.

After this is done, we can make our API call as shown below

```java
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
```

The first line sets up the API call, but doesn't actually run it. The next line runs it, but not on the main thread. On Android, it is not advised to make network calls on the main thread, as that can result in the UI waiting for a network call and not being able to update (lag). Because of this, we use ```call.enqueue()```, which makes our network call on a different thread.

Depending on your app, if you are already off of the main thread and want to make a network call on that thread, you can use ```call.execute().body()```, which will return the response on the same thread.

Once we get the list of followers in ```onResponse()```, we choose a random follower and display the follwer's username in our TextView. All of the above code is in an ```OnClickListener``` object that we assign to our button. The result of this is that we assign a random follower username to the TextView every time the button is clicked.

![Screenshot](josacky.png)

