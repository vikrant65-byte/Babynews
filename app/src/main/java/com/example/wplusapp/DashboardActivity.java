package com.example.wplusapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    //FireBase auth
    FirebaseAuth firebaseAuth;

    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Actionbar and its view
        actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");

        //init
        firebaseAuth = FirebaseAuth.getInstance();

        //Bottom navigation view
        BottomNavigationView navigationView = findViewById(R.id.menu_nav);
        navigationView.setOnNavigationItemSelectedListener(selectedListerner);

        //home fragment transaction (default, on start)
        actionBar.setTitle("Home");
        HomeFragment fragment1 = new HomeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content, fragment1 ,"");
        ft1.commit();

    }
    private BottomNavigationView.OnNavigationItemSelectedListener  selectedListerner =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    //Handle item clicks
                    switch (menuItem.getItemId()){
                        case R.id.Homenav:
                        //home fragment transaction
                            actionBar.setTitle("Home");
                            HomeFragment fragment1 = new HomeFragment();
                            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.content, fragment1 ,"");
                            ft1.commit();
                          return true;
                        case R.id.ProfileBack:
                            //Profile fragment transaction
                            actionBar.setTitle("Profile");
                            ProfileFragment fragment2 = new ProfileFragment();
                            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.content, fragment2 ,"");
                            ft2.commit();
                            return true;
                        case R.id.usersBack:
                            //user fragment transaction
                            actionBar.setTitle("Profile");
                            UsersFragment fragment3 = new UsersFragment();
                            FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                            ft3.replace(R.id.content, fragment3 ,"");
                            ft3.commit();
                            return true;
                    }
                    return false;
                }
            };
    private void checkUserStatus(){
        FirebaseUser user  = firebaseAuth.getCurrentUser();

        if (user != null){
            //user signed-in stay here
            //Set email of logged-in user
            //mprofiletv.setText(user.getEmail());

        }
        else {
            //user signed-in stay here
            startActivity(new Intent(DashboardActivity.this,MainActivity.class));
            finish();
        }

    }
    @Override
    protected void onStart(){
        //Check on start of app
        checkUserStatus();
        super.onStart();
    };
    /*Inflate options menu*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflating menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    };

    /*Hande menu options*/
     @Override
    public boolean onOptionsItemSelected(MenuItem item){
         //get item-id
         int id = item.getItemId();
         if (id == R.id.logoutbtn){
             firebaseAuth.signOut();
             checkUserStatus();
         }
         return super.onOptionsItemSelected(item);


     }
}
