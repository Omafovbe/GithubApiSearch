package com.fovbe.githubapi;

import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fovbe.githubapi.api.ApiClient;
import com.fovbe.githubapi.api.GitInterface;
import com.fovbe.githubapi.model.Qresult;
import com.fovbe.githubapi.model.UserSearchAdapter;
import com.fovbe.githubapi.model.Users;
import com.fovbe.githubapi.utils.EndlessRecyclerOnScrollListener;
import com.fovbe.githubapi.utils.FabAnimator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String perPage = "50";
    private String q = "language:java location:lagos";
    private int page = 1;
    private UserSearchAdapter userSearchAdapter;
    TextView totalPresons;
    public int userTotal = 0;
    private List<Users> qryUsers = new ArrayList<>();
    ProgressDialog pd;
    ProgressBar progBarState;
    RecyclerView rvGitHub;

    private EndlessRecyclerOnScrollListener scrollListener;
    private boolean connected;
    private Boolean isFabOpen = false;
    public FloatingActionButton fabM, fabS, fabR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fabM = (FloatingActionButton) findViewById(R.id.fabMenu);
        fabS = (FloatingActionButton) findViewById(R.id.fabSearch);
        fabR = (FloatingActionButton) findViewById(R.id.fabRefresh);
        progBarState = (ProgressBar) findViewById(R.id.progBar22) ;
        rvGitHub = (RecyclerView) findViewById(R.id.rvUsers);

        initialViews();
        LinearLayoutManager rvLinearLayManager = new LinearLayoutManager(getApplicationContext());
        rvLinearLayManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvGitHub.setLayoutManager(rvLinearLayManager);

        rvGitHub.addOnScrollListener( new EndlessRecyclerOnScrollListener(rvLinearLayManager){
            @Override
            public void onLoadMore(int current_page) {
                progBarState.setVisibility(View.VISIBLE);

//                new Handler().postDelayed(() -> {
//                        progBarState.setVisibility(View.GONE);
//
//                }, 3000);

                connected();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem userNum = menu.findItem(R.id.menuUsers);
        userNum.setTitle(String.valueOf(userTotal));
        return super.onPrepareOptionsMenu(menu);
    }

    private void initialViews() {

        userSearchAdapter = new UserSearchAdapter(getApplicationContext(), qryUsers);
        rvGitHub.setAdapter(userSearchAdapter);
        rvGitHub.setHasFixedSize(true);
        progress_dialog();
        connected();

    }

    //Displays the Progress Dialog
    public void progress_dialog() {
        pd = new ProgressDialog(this);
        pd.setMessage("Searching Github...");
        pd.setCancelable(false);
        pd.show();
    }

    /**
     * Searches Github using Retrofit library by passing the query param
     *
     * @param gitQry
     */
    private void getGithubUsers(String gitQry) {
        Map<String, String> qryString = new HashMap<>();
        qryString.put("q", gitQry);
        qryString.put("per_page", perPage);

        if (page>1){
            qryString.put("page",String.valueOf(page));
        }

        GitInterface apiService = ApiClient.getClient().create(GitInterface.class);

        Call<Qresult> call = apiService.getAllUsers(qryString);

        call.enqueue(new Callback<Qresult>() {
            @Override
            public void onResponse(Call<Qresult> call, Response<Qresult> response) {
                if (response.isSuccessful()) {
                    Qresult result = response.body();

                    //Get Number of GitHub users found from search on the toolbar
                    userTotal = result.getTotalCount();

                    //Display the total number on the ToolBar
                    invalidateOptionsMenu();

                    //Upon reaching the end of the list do nothing or add more users and increase
                    //the page count.
                    if (result.getItems() != null || !(result.getItems().isEmpty())){
                        qryUsers.addAll(result.getItems());
                        userSearchAdapter.addNew();
                        pd.hide();
                        page++;
                    }


                    if (progBarState.getVisibility() == View.VISIBLE){
                        progBarState.setVisibility(View.GONE);
                    }

                } else {
                    Log.e("Error message", response.code() + ": " + response.message());
                    pd.hide();
                    progBarState.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Qresult> call, Throwable t) {
                Log.e("Error", t.getMessage());
                pd.hide();
                progBarState.setVisibility(View.GONE);
                Snackbar.make(findViewById(R.id.rlmain), "An Error Occurred. Make sure you're online", Snackbar.LENGTH_LONG).show();
            }
        });
    }


    /*
        Check the network if we're connected online
     */
    public boolean checkNetwork(){
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                                    .getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = (activeNetwork !=null && activeNetwork.isConnectedOrConnecting());

        return isConnected;
    }

    /*

     */

    private void connected(){
        connected = checkNetwork();
        if (!connected){
            Snackbar.make(findViewById(R.id.rlmain), "You're offline", 3000)
                    .setAction("RETRY", new retryListener())
                    .show();
            return;
        }
        else {
            getGithubUsers(q);
        }
    }

    /**
     * The user should have turned ON network
     */
    public class retryListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            getGithubUsers(q);
            // Code to undo the user's last action
        }
    }

    /**
     * Animate the Floating Action Button when clicked
     * @param view
     */
    public void fabMenu(View view){
        if(!isFabOpen) {
            FabAnimator.fabRotateForward(getApplicationContext(), fabM);
            FabAnimator.fabOpen(getApplicationContext(), fabS);
            FabAnimator.fabOpen(getApplicationContext(), fabR);
            fabS.setClickable(true);

            fabR.setClickable(true);
            isFabOpen = true;
            Log.d("Fab Menu", "Opened");
        } else {
            FabAnimator.fabRotateBack(getApplicationContext(), fabM);
            FabAnimator.fabClose(getApplicationContext(), fabS);
            FabAnimator.fabClose(getApplicationContext(), fabR);
            fabS.setClickable(false);
            fabR.setClickable(false);
            isFabOpen = false;
            Log.d("Fab Menu", "Closed");
        }
    }

    /**
     * FAB Refesh button
     * @param v
     */
    public void refreshList(View v){
        page=1;
        userSearchAdapter.clear();
        progress_dialog();
        connected();

    }

    public void searchQry(View v){
        fabMenu(v);
        //Toast.makeText(this,"Search icon Clicked",Toast.LENGTH_SHORT).show();
        final AlertDialog.Builder searchDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View inflator = inflater.inflate(R.layout.search_users, null);
        searchDialog.setView(inflator);
        searchDialog.setTitle("Search Criteria");

        final EditText searchLang = (EditText) inflator.findViewById(R.id.langQry);
        final EditText searchLocate =(EditText) inflator.findViewById(R.id.locateQry);

        searchDialog.setPositiveButton("Search", (dialog, which)-> {
                q = "language:" + searchLang.getText().toString();
                q += " location:" + searchLocate.getText().toString();
                progress_dialog();
                userSearchAdapter.clear();
                page=1;
                getGithubUsers(q);

        });

        searchDialog.setNegativeButton("Cancel", (dialog, which) -> {  dialog.cancel();  });

        AlertDialog showSearchDialog = searchDialog.create();
        showSearchDialog.show();

    }


}
