package com.example.ayush.myrelief;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableOperation;
import com.squareup.picasso.Picasso;

import java.net.URI;

import static com.example.ayush.myrelief.MapsFragment.USER_DESCRIPTION;
import static com.example.ayush.myrelief.MapsFragment.USER_STATUS;
import static com.example.ayush.myrelief.MapsFragment.storageConnectionString;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 0;
    SignInButton signInButton;
    public static String USER_EMAIL, USER_NAME;
    public static Uri USER_IMAGE;

    public static FrameLayout fab_remove_request;
    ImageView iv_user;
    TextView tv_name, tv_email;
    NavigationView navigationView;


    @Override
    protected void onResume() {
        super.onResume();

        Log.d("statusCheck", String.valueOf(MapsFragment.USER_STATUS));

        if(MapsFragment.USER_STATUS) {
            NotificationManager nMN = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Notification n = new Notification.Builder(this)
                    .setContentTitle("Request Active")
                    .setContentText(MapsFragment.USER_DESCRIPTION)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setOngoing(true)
                    .build();
            nMN.notify(1, n);
            fab_remove_request.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        navigationView=(NavigationView)findViewById(R.id.nav_view);

        iv_user = (ImageView)navigationView.getHeaderView(0).findViewById(R.id.iv_user);
        tv_email=(TextView)navigationView.getHeaderView(0).findViewById(R.id.tv_email);
        tv_name=(TextView)navigationView.getHeaderView(0).findViewById(R.id.tv_name);


        fab_remove_request = (FrameLayout)findViewById(R.id.fab_remove_request);
        fab_remove_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setTitle("Remove Request");
                builder1.setMessage("Are you sure you want to remove your request?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                                mNotificationManager.cancel(1);



                                @SuppressLint("StaticFieldLeak") AsyncTask asyncTask = new AsyncTask() {
                                    @Override
                                    protected void onPreExecute() {
                                        super.onPreExecute();
//                progressBar.setVisibility(View.VISIBLE);
                                        Log.d("atask", "onPreExexute");

                                    }

                                    @Override
                                    protected Object doInBackground(Object[] objects) {

                                        try
                                        {
                                            // Retrieve storage account from connection-string.
                                            CloudStorageAccount storageAccount =
                                                    CloudStorageAccount.parse(storageConnectionString);

                                            // Create the table client.
                                            CloudTableClient tableClient = storageAccount.createCloudTableClient();

                                            // Create a cloud table object for the table.
                                            CloudTable cloudTable = tableClient.getTableReference("users");

                                            // Retrieve the entity with partition key of "Smith" and row key of "Jeff".
                                            TableOperation retrieveUser =
                                                    TableOperation.retrieve("user", USER_EMAIL, UserEntity.class);

                                            // Submit the operation to the table service and get the specific entity.
                                            UserEntity specificEntity =
                                                    cloudTable.execute(retrieveUser).getResultAsType();

                                            // Specify a new phone number.
                                            specificEntity.setStatus(false);
                                            specificEntity.setDescription("");


                                            // Create an operation to replace the entity.
                                            TableOperation replaceEntity = TableOperation.replace(specificEntity);

                                            // Submit the operation to the table service.
                                            cloudTable.execute(replaceEntity);
                                        }
                                        catch (Exception e)
                                        {
                                            // Output the stack trace.
                                            e.printStackTrace();
                                        }

                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Object o) {
                                        super.onPostExecute(o);
                                        USER_STATUS=false;
                                        USER_DESCRIPTION="";

                                    }
                                };
                                asyncTask.execute();

                                fab_remove_request.setVisibility(View.GONE);
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

//                AlertDialog.Builder builder;
//
//                builder = new AlertDialog.Builder(getApplicationContext());
//
//                builder.setTitle("Remove Request")
//                        .setMessage("Are you sure you want to remove your request?")
//                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                // continue with delete
//
//                                NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//                                mNotificationManager.cancel(1);
//
//                                fab_remove_request.setVisibility(View.GONE);
//                            }
//                        })
//                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                // do nothing
//                            }
//                        })
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .show();








            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

//        signInButton = findViewById(R.id.sign_in_button);
//        signInButton.setSize(SignInButton.SIZE_STANDARD);

//        loadFrag(new MapsFragment(), "Maps", getSupportFragmentManager());
        signIn();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("ababababa", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    void updateUI(GoogleSignInAccount account){

        if(account!=null) {
            loadFrag(new MapsFragment(), "Maps", getSupportFragmentManager());
            USER_NAME = account.getDisplayName();
            USER_EMAIL = account.getEmail();
            USER_IMAGE = account.getPhotoUrl();


            iv_user.setImageURI(account.getPhotoUrl());
            Picasso.with(this).load(account.getPhotoUrl().toString()).into(iv_user);
            tv_email.setText(USER_EMAIL);
            tv_name.setText(USER_NAME);
        }
        else{
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            // Build a GoogleSignInClient with the options specified by gso.
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            signIn();
        }

    }



    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.signout) {
            signOut();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.map) {
            loadFrag(new MapsFragment(), "maps", getSupportFragmentManager());
            // Handle the camera action
        }
        else if(id == R.id.request_help){
            loadFrag(new RequestFragment(), "request", getSupportFragmentManager());
        }
        else if(id == R.id.earthquake){
            loadFrag(new EarthquakeFragment(), "earthquakes", getSupportFragmentManager());
        }
        else if (id ==R.id.flood){
            loadFrag(new FloodFragment(), "floods", getSupportFragmentManager());
        }
        else if (id ==R.id.hurricane){
            loadFrag(new HurricaneFragment(), "hurricanes", getSupportFragmentManager());
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        USER_EMAIL="";
                        USER_NAME="";
                        MapsFragment.USER_DESCRIPTION="";
                        MapsFragment.USER_LATITUDE = 28.363821;
                        MapsFragment.USER_LONGITUDE = 0.0;
                        MapsFragment.USER_STATUS=false;

                        Toast.makeText(getApplicationContext(), "Signed out Successfully!", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });

    }

    public static void loadFrag(Fragment f1, String name, FragmentManager fm) {
//        selectedFragment = name;
        FragmentTransaction ft = fm.beginTransaction()
                .addToBackStack(null);
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        ft.setCustomAnimations(R.anim.fade_out, R.anim.fade_in);
        ft.replace(R.id.fl_view, f1, name);
        ft.commit();
    }
}
