package com.fovbe.githubapi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fovbe.githubapi.api.ApiClient;
import com.fovbe.githubapi.api.GitInterface;
import com.fovbe.githubapi.model.Item;
import com.fovbe.githubapi.utils.FabAnimator;
import com.singh.daman.proprogressviews.FadeCircleProgress;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

public class UserDetails extends AppCompatActivity {
    LinearLayout details;
    FadeCircleProgress cirlePro;
    TextView fullName, bio,following, followers, repos, loginName, userUrl;
    ImageView userImage;
    public Item result;
    //ProgressDialog pdItem;
    public String userLogin, url, avatarUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        details = (LinearLayout) findViewById(R.id.gitDetails);
        details.setVisibility(View.INVISIBLE);
        cirlePro = (FadeCircleProgress) findViewById(R.id.cirleprog);
        fullName = (TextView) findViewById(R.id.uname);
        bio = (TextView) findViewById(R.id.bio);
        following = (TextView) findViewById(R.id.following);
        repos = (TextView) findViewById(R.id.repos);
        followers = (TextView) findViewById(R.id.follower);
        userImage = (ImageView) findViewById(R.id.userImg);
        userLogin = getIntent().getExtras().getString("username");
        loginName = (TextView) findViewById(R.id.uLogin);
        loginName.setText("@"+userLogin.toString());
        url = getIntent().getExtras().getString("url");
        userUrl = (TextView) findViewById(R.id.userUrl);
        userUrl.setText(url.toString());
        avatarUrl = getIntent().getExtras().getString("avatar");

        //Display the user image
        showUserImage();

        //Visiting the the activity for the first time?
        if(savedInstanceState == null || !savedInstanceState.containsKey("userInfo")){

            getGithubUser(userLogin);
        } else {
            //If not use the data saved and passed using Parcelable
            result = savedInstanceState.getParcelable("userInfo");

            displayInfo();
        }

    }

    private void showUserImage(){
        //Get User Image
        Picasso.with(getApplicationContext()).load(avatarUrl)
                .transform(new CropCircleTransformation())
                .placeholder(R.drawable.pholder)
                .into(userImage);
    }

    private void getGithubUser(String loginName){

        GitInterface apiService = ApiClient.getClient().create(GitInterface.class);

        Call<Item> call = apiService.getUsersInfo(loginName);

        call.enqueue(new Callback<Item>() {
            @Override
            public void onResponse(Call<Item> call, Response<Item> response) {
                result = response.body();
                displayInfo();
            }

            @Override
            public void onFailure(Call<Item> call, Throwable t) {
                Log.e("Error", t.getMessage());
                Snackbar.make(findViewById(R.id.rlUsersDetails), "An Error Occurred. Make sure you're online", Snackbar.LENGTH_LONG).show();
                cirlePro.setVisibility(GONE);
            }
        });
    }

    /*
        Back button to end the User Detail activity
     */
    public void btnBack(View v){
       finish();
    }

    /**
     * Share Intent to share the user login name and
     * user URL on other apps e.g WhatsApp, Facebook or the likes
     * @param v
     */
    public void shareInfo(View v){
        CharSequence shareContent = "Check out this awesome developer @"+ userLogin+ ", " + url+".";
        Intent shareBtn = new Intent(Intent.ACTION_SEND);
        shareBtn.setType("text/plain");
        shareBtn.putExtra(Intent.EXTRA_TEXT, shareContent );
        if (shareBtn.resolveActivity(getPackageManager()) != null) {
            startActivity(shareBtn);
        }
    }

    /**
     * Webview Intent to view the user on its Github page
     * @param v
     */
    public void viewPage(View v){
        Uri gitHubLink = Uri.parse(url.toString());
        Intent visitLink = new Intent(Intent.ACTION_VIEW, gitHubLink);
        if (visitLink.resolveActivity(getPackageManager()) != null) {
            startActivity(visitLink);
        }
    }


    /*
        DIsplay user information on the userDetail UI
     */
    public void displayInfo(){
        Log.e("DisplayError", "Error trying to display user info." );
        fullName.setText(result.getName().toString());
        bio.setText(result.getBio().toString());
        following.setText(result.getFollowing().toString());
        repos.setText(result.getPublicRepos().toString());
        followers.setText(result.getFollowers().toString());

        cirlePro.setVisibility(GONE);
        FabAnimator.slide_down(getApplicationContext(),details);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Being able to handle orientation change
        //Save info using Parcelable
        outState.putParcelable("userInfo", result);
        super.onSaveInstanceState(outState);
    }
}
