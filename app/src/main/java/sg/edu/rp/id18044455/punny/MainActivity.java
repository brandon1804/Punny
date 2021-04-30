package sg.edu.rp.id18044455.punny;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    ArrayList<String> favouritesList;
    ArrayList<String> punsList;
    Toolbar toolbar;
    ViewPager2 viewPager;
    Providers provider;
    ProvidedPunsAdapter ppAdapter;
    int lastMAPosition;
    TinyDB tinydb;
    String userID;


    FirebaseAuth fAuth;
    DatabaseReference root;
    FirebaseUser currUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        provider = new Providers();

        fAuth = FirebaseAuth.getInstance();
        currUser = fAuth.getCurrentUser();
        root = FirebaseDatabase.getInstance().getReference();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewPager = findViewById(R.id.viewPagerMA);

        tinydb = new TinyDB(MainActivity.this);
        punsList = new ArrayList<String>();


        updateUI();


        BottomNavigationView bottomNavBar = findViewById(R.id.bottom_nav);

        bottomNavBar.setSelectedItemId(R.id.Home);

        bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                tinydb.putInt(userID + "lastMAPosition", viewPager.getCurrentItem());
                if (item.getItemId() == R.id.Favourites){
                    Intent intent = new Intent(MainActivity.this, Favourites.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    return true;
                }
                else if (item.getItemId() == R.id.Creations){
                    startActivity(new Intent(MainActivity.this, Create.class));
                    overridePendingTransition(0,0);
                    return true;
                }
                else if (item.getItemId() == R.id.CreatePun){
                    startActivity(new Intent(MainActivity.this, CreatePun.class));
                    overridePendingTransition(0,0);
                    return true;
                }
                else if (item.getItemId() == R.id.Settings){
                    startActivity(new Intent(MainActivity.this, Settings.class));
                    overridePendingTransition(0,0);
                    return true;
                }
                return false;
            }
        });

    }//end of onCreate

    @Override
    public void onBackPressed(){

        moveTaskToBack(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.fav) {
            int currentItem = viewPager.getCurrentItem();
            String currentItemStr = provider.getProvidedPuns()[currentItem];
            boolean isAdded = false;

            for (int i = 0; i < favouritesList.size(); i++){
                if (currentItemStr.equals(favouritesList.get(i))){
                    isAdded = true;
                }//end of if
            }//end of for loop

            if (isAdded == false){
                favouritesList.add(currentItemStr);
                Toast.makeText(MainActivity.this, "Pun Saved!", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(MainActivity.this, "Pun Already Saved!", Toast.LENGTH_SHORT).show();
            }
            tinydb.putListString(userID + "favouritesList", favouritesList);
            return true;
        }//end of FavouritePun

        else if (id == R.id.share) {
            int currentItem = viewPager.getCurrentItem();
            String currentItemStr = punsList.get(currentItem);
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Pun", currentItemStr);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(MainActivity.this, "Pun Copied!", Toast.LENGTH_SHORT).show();
            return true;
        }//end of SharePun

        else if (id == R.id.help) {
            AlertDialog.Builder myBuilder = new AlertDialog.Builder(MainActivity.this);
            myBuilder.setTitle(getString(R.string.helpDialogTitle));
            myBuilder.setMessage(getString(R.string.helpMessage));
            myBuilder.setCancelable(false);
            myBuilder.setPositiveButton("Dismiss", null);

            AlertDialog myDialog = myBuilder.create();
            myDialog.show();

            return true;
        }//end of help
        return super.onOptionsItemSelected(item);
    }//end of onOptionsItemSelected


    public void updateUI(){
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.child("Puns").getChildren()) {
                    punsList.add(child.getValue().toString());
                }//end of for loop

                userID = snapshot.child("Users").child(currUser.getUid()).getValue().toString();
                favouritesList = tinydb.getListString(userID + "favouritesList");
                lastMAPosition  = tinydb.getInt(userID +"lastMAPosition");

                ppAdapter = new ProvidedPunsAdapter(punsList, provider.getColours());
                viewPager.setAdapter(ppAdapter);
                viewPager.setCurrentItem(lastMAPosition,false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }//end of updateUI



}//end of class

