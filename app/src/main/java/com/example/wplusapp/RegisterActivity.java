package com.example.wplusapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    //views
    EditText mUsernameET, mPasswordET, mPhonenumber, mage, mAddress;
    TextView mAlreadyregistered, mRegtitle;
    Button mRegbtn;


    //Loading progress while logging in
    ProgressDialog progressDialog;

    //Declare an instance of FireBaseAuth
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Actionbar and its view
        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.wplus);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setTitle("Register");

        mUsernameET = findViewById(R.id.username);
        mPasswordET = findViewById(R.id.password);
        mPhonenumber = findViewById(R.id.phonenumber);
        mage = findViewById(R.id.Age);
        mAddress = findViewById(R.id.Address);
        mAlreadyregistered = findViewById(R.id.LoginFromRegister);
        mRegbtn = findViewById(R.id.btnReg);
        mRegtitle = findViewById(R.id.Registertittle);


        //In the onCreate() method, initialize the FireBaseAuth instance.
        // Initialize the fireBase Auth
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering user....please wait");

        //For handling the login button;
        mRegbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //input username and password
                String username = mUsernameET.getText().toString().trim();
                String password = mPasswordET.getText().toString().trim();

                //validate the persons details
                if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                    //set error
                    mUsernameET.setError("invalid username");
                    mUsernameET.setFocusable(true);
                } else if (password.length() < 7) {
                    mPasswordET.setError("incorrect password");
                    mPasswordET.setFocusable(true);
                } else {
                    RegisterUser(username, password); //sign in the user
                }
            }
        });
        mAlreadyregistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
            }
        });
    }

    private void RegisterUser(String username , final String password){
        //Email and password pattern is valid, show progress dialog for registering
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            //get user-id and email from auth
                            String email = user.getEmail();
                            String uid = user.getUid();
                            //if user is registered store information in dataBase using
                            // HashMap
                            HashMap<Object , String> hashMap = new HashMap<>();
                            //get value to store in HashMap
                            hashMap.put("email", email);
                            hashMap.put("uid", uid);
                            hashMap.put("name", ""); // we will add this Later in edit profile
                            hashMap.put("phone", "");
                            hashMap.put("image","");

                            //FireBaseDatabase database instance
                            FirebaseDatabase Database = FirebaseDatabase.getInstance();
                            //PAth to store user data named
                            DatabaseReference reference = Database.getReference("Users");
                            //put data using hashMap
                            reference.child(uid).setValue(hashMap);

                            Toast.makeText(RegisterActivity.this, "Registered user...\n."+user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            progressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Show error message
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "Couldn't register"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}