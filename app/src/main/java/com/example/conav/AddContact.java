package com.example.conav;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddContact extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private EditText name,phone,address;
    private Button submit;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    DatabaseReference dbRef;
    String savedUserId;
    Context activityContext;
    public static final String USER_ID_KEY = "uer_id";
    NavigationView navView;
    public static final String USER_NAME_KEY = "user_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);


        mDrawerLayout= findViewById(R.id.activity_add_contact);
        mToggle= new ActionBarDrawerToggle(this, mDrawerLayout,R.string.Open,R.string.Close );

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbRef= database.getReference();
        activityContext = this;
        //dbRef = FirebaseDatabase.getInstance().getReference();

        name=findViewById(R.id.new_contact_name);
        phone=findViewById(R.id.new_phone);
        address=findViewById(R.id.new_address);
        submit=findViewById(R.id.add_contact_submit);

        savedUserId = AppSettings.getSetting(activityContext, USER_ID_KEY);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact();
            }
        });

        mDrawerLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                checkViewAndUpdate();
            }
        });

    }

    public void addContact(){
        String contactName= name.getText().toString();
        String contactPhone= phone.getText().toString();
        String contactAddress= address.getText().toString();

        if(!TextUtils.isEmpty((contactPhone)) && !TextUtils.isEmpty(contactName)){
            String id= contactPhone;
            UserContact contact= new UserContact(contactName,contactPhone,contactAddress);
            dbRef.child("contacts").child(savedUserId).child(contact.phoneNumber).setValue(contact);
            Intent intent = new Intent(AddContact.this, ContactActivity.class);
            startActivity(intent);
        }
        else{
        Toast.makeText(AddContact.this, "Please fill contact's Name & Phone", Toast.LENGTH_LONG).show();
        }

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
        startActivity(logOut);
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
    public void checkViewAndUpdate(){
        if((findViewById(R.id.user_name)!=null) &&(findViewById(R.id.user_name).getVisibility() == View.VISIBLE)){
            TextView userName = (TextView)findViewById(R.id.user_name);
            String user=AppSettings.getSetting(activityContext, USER_NAME_KEY);
            userName.setText(user);
        }
    }
}
