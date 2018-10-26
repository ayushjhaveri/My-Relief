package com.example.ayush.myrelief;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableOperation;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.example.ayush.myrelief.MainActivity.USER_EMAIL;
import static com.example.ayush.myrelief.MainActivity.USER_NAME;
import static com.example.ayush.myrelief.MainActivity.fab_remove_request;
import static com.example.ayush.myrelief.MainActivity.loadFrag;
import static com.example.ayush.myrelief.MapsFragment.CURRENT_USER;
import static com.example.ayush.myrelief.MapsFragment.USER_DESCRIPTION;
import static com.example.ayush.myrelief.MapsFragment.USER_STATUS;
import static com.example.ayush.myrelief.MapsFragment.storageConnectionString;


public class RequestFragment extends Fragment {

    Button btn_submit;
    EditText edit_desc;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_request, container, false);

        btn_submit=(Button)view.findViewById(R.id.btn_submit);
        edit_desc=(EditText)view.findViewById(R.id.edit_desc);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edit_desc.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Request Failed! Please eneter a description!", Toast.LENGTH_LONG).show();
                }

                else{


                USER_STATUS = true;
                USER_DESCRIPTION = edit_desc.getText().toString();


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
                            specificEntity.setStatus(true);
                            specificEntity.setDescription(USER_DESCRIPTION);


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
//                if(progressBar.getVisibility()==View.VISIBLE){
//                    rl_filled.setVisibility(View.GONE);
//                    rl_empty.setVisibility(View.VISIBLE);
//                }
//                progressBar.setVisibility(View.GONE);
                        edit_desc.setText("");
                        Toast.makeText(getContext(), "Requested Successfully", Toast.LENGTH_SHORT).show();

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

                    }
                };
                asyncTask.execute();


            }
        }});


        return view;
    }

}
