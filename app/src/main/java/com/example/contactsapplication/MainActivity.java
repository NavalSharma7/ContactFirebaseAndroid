package com.example.contactsapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contactsapplication.databinding.ActivityMainBinding;
import com.example.contactsapplication.model.ContactInfo;
import com.example.contactsapplication.model.ContactListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;


    String uid = "GaekeiN61HZHZNvndEcGBDNmEbN2";

    DatabaseReference contactsRef;

    RecyclerView recyclerView;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        refreshDataFromCloud();

        //setContactDatatoCloud();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        findViewById(R.id.btnCreateContact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // go to add contact screen

                Intent intent = new Intent(MainActivity.this,AddContactActivity.class);
                intent.putExtra("UID",uid);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean onSupportNavigateUp() {
////        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
////        return NavigationUI.navigateUp(navController, appBarConfiguration)
////                || super.onSupportNavigateUp();
//    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDataFromCloud();
    }


    // private methods


    private  void  refreshDataFromCloud(){
        ArrayList<ContactInfo> arrayList = new ArrayList<ContactInfo>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference contacts = db.collection("Contacts");
        contacts.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                for(QueryDocumentSnapshot snapshot: task.getResult()){
                    ContactInfo contactInfo = snapshot.toObject(ContactInfo.class);
                    arrayList.add(contactInfo);

                }
                    recyclerView = (RecyclerView)findViewById(R.id.recyclerThread);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
                    recyclerView.setLayoutManager(mLayoutManager);
                    ContactListAdapter showContactAdapter = new ContactListAdapter(arrayList,uid,name,MainActivity.this);
                    recyclerView.setAdapter(showContactAdapter);
                }else{
                   // Toast.makeText(MainActivity.this,"faiiure in getting contacts",Toast.LENGTH_LONG);
                }
                }

        });
    }
    @Override
    protected void onStart() {
        super.onStart();



       // callEventListener();

    }

    private  void  callEventListener(){
        contactsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<ContactInfo> arrayList = new ArrayList<ContactInfo>();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    ContactInfo contact = dataSnapshot1.getValue(ContactInfo.class);
                    arrayList.add(contact);
                }
//                Log.d("seize",arrayList.get(0).getImgString());
                recyclerView = (RecyclerView)findViewById(R.id.recyclerThread);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
                recyclerView.setLayoutManager(mLayoutManager);
                ContactListAdapter showContactAdapter = new ContactListAdapter(arrayList,uid,name,MainActivity.this);
                recyclerView.setAdapter(showContactAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}