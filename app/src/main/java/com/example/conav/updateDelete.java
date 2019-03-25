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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class updateDelete extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    EditText eName, eAddress, ePhone;
    DatabaseReference ref,dbRef;
    Context activityContext;
    String savedUserId;
    public static final String USER_ID_KEY = "uer_id";
    public static final String USER_NAME_KEY = "user_name";
    private static final String TAG = "Update";
    NavigationView navView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delete);
        activityContext=this;

        //Log.d(TAG, "onCreate: " + user);
        mDrawerLayout= findViewById(R.id.activity_update_delete);
        mToggle= new ActionBarDrawerToggle(this, mDrawerLayout,R.string.Open,R.string.Close );

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        eName = findViewById(R.id.new_contact_name);
        eAddress = findViewById(R.id.new_address);
        ePhone = findViewById(R.id.new_phone);

        savedUserId = AppSettings.getSetting(activityContext, USER_ID_KEY);
        String id= getIntent().getStringExtra("phone");
        dbRef =  FirebaseDatabase.getInstance().getReference().child("contacts").child(savedUserId);
        ref =dbRef.child(id);
        eName.setText(getIntent().getStringExtra("name"));
        Log.d(TAG, "onCreate: " + getIntent().getStringExtra("phone"));
        ePhone.setText(getIntent().getStringExtra("phone"));
        eAddress.setText(getIntent().getStringExtra("address"));

        mDrawerLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                checkViewAndUpdate();
            }
        });

    }

    public void checkViewAndUpdate(){
        if((findViewById(R.id.user_name)!=null) &&(findViewById(R.id.user_name).getVisibility() == View.VISIBLE)){
            TextView userName = (TextView)findViewById(R.id.user_name);
            String user=AppSettings.getSetting(activityContext, USER_NAME_KEY);
            userName.setText(user);
        }
    }
    public void btnUpdate_Click(View view) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               //String checkName= eName.getText().toString();
                UserContact userContact  =new UserContact(eName.getText().toString(),ePhone.getText().toString(),eAddress.getText().toString());
                if(TextUtils.isEmpty(userContact.getPhoneNumber())){
                    Toast.makeText(updateDelete.this, "Please fill out the Phone", Toast.LENGTH_LONG).show();
                    return;
                }
                ref.removeValue();

                dbRef.child(userContact.getPhoneNumber()).setValue(userContact);
                Toast.makeText(updateDelete.this, "Data Updated Successfully!", Toast.LENGTH_SHORT).show();
                Intent back= new Intent(updateDelete.this,ContactActivity.class);
                startActivity(back);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(updateDelete.this, "No change recorded!", Toast.LENGTH_SHORT).show();
                Intent back= new Intent(updateDelete.this,ContactActivity.class);
                startActivity(back);
            }
        });
    }

    public void btnDelete_Click(View view) {
        ref.setValue(null).addOnCompleteListener(updateDelete.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(updateDelete.this, "Contact Deleted Successfully", Toast.LENGTH_LONG).show();
                    updateDelete.this.finish();

                } else {
                    Toast.makeText(updateDelete.this, "Something went wrong :/ Contact not Deleted ", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fake, menu);
        return true;
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
}

