package com.example.ayush.myrelief;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableOperation;
import com.microsoft.azure.storage.table.TableQuery;

//import com.microsoft.azure.storage.*;
//import com.microsoft.azure.storage.table.*;
//import com.microsoft.azure.storage.table.TableQuery.*;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.example.ayush.myrelief.MainActivity.USER_EMAIL;
import static com.example.ayush.myrelief.MainActivity.USER_IMAGE;
import static com.example.ayush.myrelief.MainActivity.USER_NAME;
import static com.example.ayush.myrelief.MainActivity.fab_remove_request;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    public static final String storageConnectionString =
            "DefaultEndpointsProtocol=https;AccountName=b2749e58-0ee0-4-231-b9ee;AccountKey=RyV0fKvindMLr7RFMYdiCsNDHz3YdkXjNfykfu2cvpMISmra1QRMerBjTpTxXjchQCxnTBmVohXoaWeqcpM8xA==;TableEndpoint=https://b2749e58-0ee0-4-231-b9ee.table.cosmosdb.azure.com:443/;" ;

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    static ArrayList<ItemMarker> markerArrayList;
    public static double USER_LATITUDE, USER_LONGITUDE;
    public static String USER_DESCRIPTION;
    public static boolean USER_STATUS;
    public  static  UserEntity CURRENT_USER;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);






//        try
//        {
//            // Retrieve storage account from connection-string.
//            CloudStorageAccount storageAccount =
//                    CloudStorageAccount.parse(storageConnectionString);
//
//            // Create the table client.
//            CloudTableClient tableClient = storageAccount.createCloudTableClient();
//
//            // Create the table if it doesn't exist.
//            String tableName = "people";
//            CloudTable cloudTable = tableClient.getTableReference(tableName);
//            cloudTable.createIfNotExists();
//                    }
//        catch (Exception e)
//        {
//            // Output the stack trace.
//            Log.d("ababab", e.toString());
//        }

//        try
//        {
//            // Retrieve storage account from connection-string.
//            CloudStorageAccount storageAccount =
//                    CloudStorageAccount.parse(storageConnectionString);
//
//            // Create the table client.
//            CloudTableClient tableClient = storageAccount.createCloudTableClient();
//
//            // Loop through the collection of table names.
//            for (String table : tableClient.listTables())
//            {
//                // Output each table name.
//                Toast.makeText(getContext(), table, Toast.LENGTH_LONG).show();
//            }
//        }
//        catch (Exception e)
//        {
//            // Output the stack trace.
////            e.printStackTrace();
//            Toast.makeText(getContext(),e.toString(), Toast.LENGTH_LONG).show();
//            Log.d("ababab", e.toString());
//        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }



//        markerArrayList = new ArrayList<>();
//        markerArrayList.add(new ItemMarker("Ayush Jhaveri", "I need help, Please come as soon as possible! I need help, Please come as soon as possible! I need help, Please come as soon as possible! I need help, Please come as soon as possible! I need help, Please come as soon as possible! I need help, Please come as soon as possible!", 28.363821, 75.587029));
//        markerArrayList.add(new ItemMarker("Akshit Khanna", "I'm stuck! Help required urgently.", 28.363854, 75.585854));
//
//        for (ItemMarker itemMarker : markerArrayList) {
//            LatLng latLng = new LatLng(itemMarker.getLatitude(), itemMarker.getLongitide());
//            MarkerOptions markerOptions = new MarkerOptions();
//            markerOptions.position(latLng);
//            markerOptions.title(itemMarker.getTitle());
//            markerOptions.snippet(itemMarker.getSnippet());
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//            googleMap.addMarker(markerOptions);
//        }

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });


        final ArrayList<UserEntity> userEntityArrayList = new ArrayList<>();

        @SuppressLint("StaticFieldLeak") final AsyncTask asyncTask2 = new AsyncTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                progressBar.setVisibility(View.VISIBLE);
                Log.d("name1234", "onPreExexute");

            }

            @Override
            protected Object doInBackground(Object[] objects) {

                try
                {
                    // Define constants for filters.
                    final String PARTITION_KEY = "PartitionKey";
//                            final String ROW_KEY = "RowKey";
//                            final String TIMESTAMP = "Timestamp";

                    // Retrieve storage account from connection-string.
                    CloudStorageAccount storageAccount =
                            CloudStorageAccount.parse(storageConnectionString);

                    // Create the table client.
                    CloudTableClient tableClient = storageAccount.createCloudTableClient();

                    // Create a cloud table object for the table.
                    CloudTable cloudTable = tableClient.getTableReference("users");

                    // Create a filter condition where the partition key is "Smith".
                    String partitionFilter = TableQuery.generateFilterCondition(
                            PARTITION_KEY,
                            TableQuery.QueryComparisons.EQUAL,
                            "user");

                    // Specify a partition query, using "Smith" as the partition key filter.
                    TableQuery<UserEntity> partitionQuery =
                            TableQuery.from(UserEntity.class).where(partitionFilter);
                    Log.d("name1234", "It is happenening bitches.");


                    // Loop through the results, displaying information about the entity.
                    for (UserEntity entity : cloudTable.execute(partitionQuery)) {

                        Log.d("name1234",entity.getName() );

                        if(!entity.getEmail().equals(USER_EMAIL)){
                            userEntityArrayList.add(entity);}

//                        double lat = Double.parseDouble(entity.getLatitude());
//                        double longt =  Double.parseDouble(entity.getLongitude());
//                        Log.d("name1234", "lat:"+lat + "end  long:"+longt+ "end");
//                        LatLng latLng = new LatLng(lat, longt);
//                        MarkerOptions markerOptions = new MarkerOptions();
//                        markerOptions.position(latLng);
//                        markerOptions.title(entity.getName());
//                        markerOptions.snippet(entity.getDescription());
//                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//                        googleMap.addMarker(markerOptions);
                    }
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
//                if(progressBar.getVisibility()==View.VISIBLE){
//                    rl_filled.setVisibility(View.GONE);
//                    rl_empty.setVisibility(View.VISIBLE);
//                }
//                progressBar.setVisibility(View.GONE);
                for (UserEntity entity:userEntityArrayList ){
                    double lat = Double.parseDouble(entity.getLatitude());
                    double longt =  Double.parseDouble(entity.getLongitude());
                    Log.d("name1234", "lat:"+lat + "end  long:"+longt+ "end");
                    LatLng latLng = new LatLng(lat, longt);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(entity.getName());
                    markerOptions.snippet(entity.getDescription());
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    googleMap.addMarker(markerOptions);
                }

                if(MapsFragment.USER_STATUS) {
                    NotificationManager nMN = (NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE);
                    Notification n = new Notification.Builder(getContext())
                            .setContentTitle("Request Active")
                            .setContentText(MapsFragment.USER_DESCRIPTION)
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setOngoing(true)
                            .build();
                    nMN.notify(1, n);
                    fab_remove_request.setVisibility(View.VISIBLE);
                }

                Log.d("q1w2e3r4", String.valueOf(USER_STATUS) + " "+ USER_DESCRIPTION);

//                iv_user.setImageURI(USER_IMAGE);
//                tv_name.setText(USER_NAME);
//                tv_email.setText(USER_EMAIL);


            }
        };


        @SuppressLint("StaticFieldLeak") final AsyncTask asyncTask = new AsyncTask() {
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

                    // Retrieve the entity with partition key of "Smith" and row key of "Jeff"
                    TableOperation retrieveSmithJeff =
                            TableOperation.retrieve("user", USER_EMAIL, UserEntity.class);

                    // Submit the operation to the table service and get the specific entity.
                    UserEntity specificEntity =
                            cloudTable.execute(retrieveSmithJeff).getResultAsType();

                    // Output the entity.
                    if (specificEntity != null)
                    {
                        USER_DESCRIPTION = specificEntity.getDescription();
                        USER_STATUS=specificEntity.getStatus();
                    }
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
//                if(progressBar.getVisibility()==View.VISIBLE){
//                    rl_filled.setVisibility(View.GONE);
//                    rl_empty.setVisibility(View.VISIBLE);
//                }
//                progressBar.setVisibility(View.GONE)
                asyncTask2.execute();


            }
        };
        asyncTask.execute();





    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                final Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsFragment", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //Place current location marker
                final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                USER_LATITUDE = location.getLatitude();
                USER_LONGITUDE = location.getLongitude();

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(USER_NAME);
                markerOptions.snippet(USER_DESCRIPTION);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

                mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                    @Override
                    public View getInfoWindow(Marker arg0) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {

                        LinearLayout info = new LinearLayout(getContext());
                        info.setOrientation(LinearLayout.VERTICAL);

                        TextView title = new TextView(getContext());
                        title.setTextColor(Color.BLACK);
                        title.setGravity(Gravity.CENTER);
                        title.setTypeface(null, Typeface.BOLD);
                        title.setText(marker.getTitle());

                        TextView snippet = new TextView(getContext());
                        snippet.setTextColor(Color.GRAY);
                        snippet.setText(marker.getSnippet());

                        info.addView(title);
                        info.addView(snippet);

                        return info;
                    }
                });

                //move map camera
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
//                        Toast.makeText(getContext(), "Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude(), Toast.LENGTH_LONG).show();

                        return false;
                    }
                });
            }

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

                        // Create a new customer entity.
                        CURRENT_USER = new UserEntity("user", USER_EMAIL, USER_EMAIL, USER_NAME, USER_DESCRIPTION, USER_STATUS, String.valueOf(USER_LATITUDE), String.valueOf(USER_LONGITUDE));



                        // Create an operation to add the new customer to the people table.
                        TableOperation insertUser = TableOperation.insertOrReplace(CURRENT_USER);

                        // Submit the operation to the table service.
                        cloudTable.execute(insertUser);
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
//                if(progressBar.getVisibility()==View.VISIBLE){
//                    rl_filled.setVisibility(View.GONE);
//                    rl_empty.setVisibility(View.VISIBLE);
//                }
//                progressBar.setVisibility(View.GONE);
                }
            };
            asyncTask.execute();
//            Toast.makeText(getContext(), "Requested Succesfully!", Toast.LENGTH_LONG).show();
        }
    };

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getContext())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getContext(), "permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
