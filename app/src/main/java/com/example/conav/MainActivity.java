package com.example.conav;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    public CallbackManager mCallbackManager;

    DatabaseReference mRootReference= FirebaseDatabase.getInstance().getReference();

    public static final String TAG = "FACELOG";
    public static final String USER_ID_KEY = "uer_id";
    public static final String USER_NAME_KEY = "user_name";

    String savedUserId;
    FirebaseAuth mAuth;
    Context activityContext;
    protected void handleIntent(Intent intent){
        Log.d("test_log","yes");
        if (intent!=null && intent.hasExtra("action")){
        Log.d("test_log","yes2");
            if (intent.getStringExtra("action").equals("logout")){
        Log.d("test_log","yes3");
                AppSettings.setSetting(this,MainActivity.USER_ID_KEY,"");
                AppSettings.setSetting(this,MainActivity.USER_NAME_KEY,"");
                LoginManager.getInstance().logOut();
            }
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
       handleIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        Intent incomingIntent = getIntent();
        handleIntent(incomingIntent);

        mAuth = FirebaseAuth.getInstance();
        activityContext = this;

        savedUserId = AppSettings.getSetting(activityContext, USER_ID_KEY);
        if (savedUserId.equals("")) {
            Log.d(TAG, "new user" + savedUserId);
            promtLogin();
        }
        else{
            Log.d(TAG,"not a new user" + savedUserId);
            Intent intent = new Intent(MainActivity.this, ContactActivity.class);
                //intent.putExtra("YOUR_DATA_KEY", data);
            startActivity(intent);
        }
// Initialize Facebook Login button

    }

    private void promtLogin() {
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");

        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {

                                    String user_id = object.getString("id");
                                    AppSettings.setSetting(activityContext, USER_ID_KEY, user_id);
                                    AppSettings.setSetting(activityContext, USER_NAME_KEY, object.getString("name"));
                                    Log.d(TAG, "user" + AppSettings.getSetting(activityContext, USER_ID_KEY));
                                    Intent intent2 = new Intent(MainActivity.this, ContactActivity.class);
                                    startActivity(intent2);
                                } catch (Exception ex) {
                                    Log.e(TAG, ex.getMessage());
                                }
                            }
                        });

                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });
    }

    private void proceedLoggedIn() {
        Intent intent = new Intent(activityContext, MapsActivity.class);
        //intent.putExtra("YOUR_DATA_KEY", data);
        startActivity(intent);
    }

    // ...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}

