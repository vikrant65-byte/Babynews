package com.example.wplusapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;
    //views
    EditText mUsernameET2, mPasswordET2;
    TextView mNewuser, mLogtitle, mForgotPassword;
    Button mLoginbtn;
    SignInButton mGoogleLoginBtn;


    //Loading progress while logging in
    ProgressDialog progressDialog;

    //Declare an instance of FireBaseAuth
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Actionbar and its view
        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.wplus);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setTitle("Login");

        mUsernameET2 = findViewById(R.id.username2);
        mPasswordET2 = findViewById(R.id.Password2);
        mNewuser = findViewById(R.id.RegisterFromLogin);
        mForgotPassword = findViewById(R.id.ForgotPassword);
        mLoginbtn = findViewById(R.id.btnLogin);
        mLogtitle = findViewById(R.id.LoginTittle);
        mGoogleLoginBtn = findViewById(R.id.GoogleLoginBtn);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);


        //In the onCreate() method, initialize the FireBaseAuth instance.
        // Initialize the fireBase Auth
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in user....please wait");

        //For handling the login button;
        mLoginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //input username and password
                String username = mUsernameET2.getText().toString().trim();
                String password = mPasswordET2.getText().toString().trim();

                //validate the persons details
                if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                    //set error
                    mUsernameET2.setError("invalid username");
                    mUsernameET2.setFocusable(true);
                } else if (password.length() < 7) {
                    mPasswordET2.setError("incorrect password");
                    mPasswordET2.setFocusable(true);
                } else {
                    LoginUser(username, password); //sign in the user
                }
            }
        });
        mNewuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                finish();
            }
        });
        //Recover password click
        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }
        });
        mGoogleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }

            }
        });
        progressDialog = new ProgressDialog(this);
    }

    private void showRecoverPasswordDialog() {
        //Alert Dialog
        AlertDialog .Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        //Set linear layout
        LinearLayout linearLayout= new LinearLayout(this);
        //views to set in this linear layout
        final EditText mEmailEt = new EditText(this);
        mEmailEt.setHint("Email");
        mEmailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        //Set min width of EditView to fit to fit a text of n 'M' letters regardless of the actual text size
        //and extension
        mEmailEt.setMinEms(10);

        linearLayout.addView(mEmailEt);
        linearLayout.setPadding(10,10,10,10);

        builder.setView(linearLayout);

        //button
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Input email
                String Email = mEmailEt.getText().toString().trim();
                beginRecovery(Email);

            }
        });
        //Button cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Dismiss Dialog
                dialog.dismiss();

            }
        });
        //show dialog
        builder.create().show();
    }

    private void beginRecovery(String Email) {
        //Email and password pattern is valid, show progress dialog for registering
        progressDialog.setMessage("Sending email...");
        progressDialog.show();

        mAuth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, " Reset password Email sent ", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(LoginActivity.this, " Failed..... ", Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                //Get and show error message
                Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void LoginUser(String username , final String password){
        //Email and password pattern is valid, show progress dialog for registering
        progressDialog.setMessage("Logging in....");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            //User is logged in ,so start profile activity
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Show error message
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Couldn't register" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
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


                            //Show user email toast
                            Toast.makeText(LoginActivity.this,""+user.getEmail(),Toast.LENGTH_SHORT).show();
                            //start profile activity
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this,"Login failed",Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Get and show error message
                Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }
}