package com.example.conav;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ContactActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    Context activityContext;
    String savedUserId;
    ListView lv;
    Button go_to_add_contact;
    NavigationView navView;
    public static final String USER_NAME_KEY = "user_name";

    public static final String USER_ID_KEY = "uer_id";
    private static final String TAG = "Read";
    FirebaseListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);



        mDrawerLayout= findViewById(R.id.activity_contact);
        mToggle= new ActionBarDrawerToggle(this, mDrawerLayout,R.string.Open,R.string.Close );
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        activityContext = this;
        savedUserId = AppSettings.getSetting(activityContext, USER_ID_KEY);
        String userName= AppSettings.getSetting(activityContext, USER_NAME_KEY);
        Log.d(TAG, "onCreate: " + savedUserId);
        Log.d(TAG, "onCreate: " + userName);
        lv = findViewById(R.id.list_view);
        Query query = FirebaseDatabase.getInstance().getReference().child("contacts").child(savedUserId);
        final FirebaseListOptions<UserContact> contact = new FirebaseListOptions.Builder<UserContact>()
                .setLayout(R.layout.list_layout).setQuery(query, UserContact.class).build();
        adapter = new FirebaseListAdapter(contact) {
            @Override
            protected void populateView(View v, Object model, int position) {
                TextView cName = v.findViewById(R.id.contact_name);
                TextView cPhone = v.findViewById(R.id.contact_phone);

                UserContact contact1 = (UserContact) model;
                Log.d(TAG, "populateView: " + contact1.getPhoneNumber() + "name: "+ contact1.getName());
                cName.setText("Name: " + contact1.getName());
                cPhone.setText("Phone: "+ contact1.getPhoneNumber());
            }
        };
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent updateDelete= new Intent(ContactActivity.this, updateDelete.class);
                UserContact c= (UserContact)adapterView.getItemAtPosition(position);
                Log.d(TAG, "onItemClick: " + c.getPhoneNumber());
                updateDelete.putExtra("name",c.getName());
                updateDelete.putExtra("phone",c.getPhoneNumber());
                updateDelete.putExtra("address",c.getAddress());
                startActivity(updateDelete);
             }
        });

        go_to_add_contact =(Button)findViewById(R.id.goto_add_contact);
        go_to_add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ContactActivity.this,AddContact.class));
            }
        });
        mDrawerLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                checkViewAndUpdate();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

    public void showMap(){
        Intent openMap= new Intent(this, MapsActivity.class);
        startActivity(openMap);
    }
    public void showContact(){
        Intent openContact= new Intent(this, ContactActivity.class);
        startActivity(openContact);
    }
    public void showLogout(){

        Intent logOut= new Intent(this, MainActivity.class);
        logOut.putExtra("action","logout");
        startActivity(logOut);
        Log.d("test_log","abc 123");
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fake, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return false;
    }
    public void checkViewAndUpdate(){
        if((findViewById(R.id.user_name)!=null) &&(findViewById(R.id.user_name).getVisibility() == View.VISIBLE)){
            TextView userName = (TextView)findViewById(R.id.user_name);
            String user=AppSettings.getSetting(activityContext, USER_NAME_KEY);
            userName.setText(user);
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_logout:
                showLogout();
                break;
            case R.id.nav_maps:
                showMap();
                break;
            case R.id.nav_contacts:
                showContact();

        }
        return false;
    }
}


