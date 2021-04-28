package sg.edu.rp.id18044455.punny;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Favourites extends AppCompatActivity {

    Toolbar toolbarFav;
    ArrayList<String> favouritesList;
    ViewPager2 viewPagerFavs;
    Providers provider;
    FavouritePunsAdapter fpAdapter;
    int lastFPosition;
    TinyDB tinydb;
    Menu optionsMenu;
    String userID;


    FirebaseAuth fAuth;
    DatabaseReference root;
    FirebaseUser currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        fAuth = FirebaseAuth.getInstance();
        currUser = fAuth.getCurrentUser();
        root = FirebaseDatabase.getInstance().getReference();

        tinydb = new TinyDB(Favourites.this);

        updateUI();

        this.setTitle("Favourites");
        toolbarFav = findViewById(R.id.toolbarFav);
        setSupportActionBar(toolbarFav);
        provider = new Providers();
        viewPagerFavs = findViewById(R.id.viewPagerFavs);


        BottomNavigationView bottomNavBar = findViewById(R.id.bottom_nav);

        bottomNavBar.setSelectedItemId(R.id.Favourites);

        bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                tinydb.putInt(userID + "lastFPosition", viewPagerFavs.getCurrentItem());
                if (item.getItemId() == R.id.Home){
                    startActivity(new Intent(Favourites.this, MainActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                }
                else if (item.getItemId() == R.id.Creations){
                    startActivity(new Intent(Favourites.this, Create.class));
                    overridePendingTransition(0,0);
                    return true;
                }
                else if (item.getItemId() == R.id.CreatePun){
                    startActivity(new Intent(Favourites.this, CreatePun.class));
                    overridePendingTransition(0,0);
                    return true;
                }
                else if (item.getItemId() == R.id.Settings){
                    startActivity(new Intent(Favourites.this, Settings.class));
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
        getMenuInflater().inflate(R.menu.toolbar_favmenu, menu);

        if (favouritesList.get(0).equals("You have not saved any pun(s).")){
            menu.findItem(R.id.deleteFav).setVisible(false);
            menu.findItem( R.id.share).setVisible(false);
        }
        optionsMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if (id == R.id.deleteFav) {
            int currentItem = viewPagerFavs.getCurrentItem();
            AlertDialog.Builder myBuilder = new AlertDialog.Builder(Favourites.this);
            myBuilder.setTitle("Remove Pun From Favourites?");
            myBuilder.setMessage("This pun would be removed from your favourites.");
            myBuilder.setCancelable(false);
            myBuilder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (favouritesList.size() == 1){
                        favouritesList.remove(currentItem);
                        Toast.makeText(Favourites.this, "Pun Removed From Favourites!", Toast.LENGTH_SHORT).show();
                        favouritesList.add("You have not saved any pun(s).");
                        fpAdapter.notifyDataSetChanged();
                        optionsMenu.findItem(R.id.deleteFav).setVisible(false);
                        optionsMenu.findItem(R.id.share).setVisible(false);
                        tinydb.putListString(userID +"favouritesList", favouritesList);
                    }
                    else{
                        favouritesList.remove(currentItem);
                        Toast.makeText(Favourites.this, "Pun Removed From Favourites!", Toast.LENGTH_SHORT).show();
                        fpAdapter.notifyDataSetChanged();
                        tinydb.putListString(userID +"favouritesList", favouritesList);
                    }
                }
            });

            myBuilder.setNegativeButton("Cancel", null);
            AlertDialog myDialog = myBuilder.create();
            myDialog.show();
            return true;
        }//end of DeletePun

        else if (id == R.id.share) {
            int currentItem = viewPagerFavs.getCurrentItem();
            String currentItemStr = favouritesList.get(currentItem);
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Pun", currentItemStr);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(Favourites.this, "Pun Copied!", Toast.LENGTH_SHORT).show();
            return true;
        }//end of SharePun

        else if (id == R.id.help) {
            AlertDialog.Builder myBuilder = new AlertDialog.Builder(Favourites.this);
            myBuilder.setTitle(getString(R.string.helpDialogTitle));
            myBuilder.setMessage(getString(R.string.helpMessageFav));
            myBuilder.setCancelable(false);
            myBuilder.setPositiveButton("Dismiss", null);

            AlertDialog myDialog = myBuilder.create();
            myDialog.show();

            return true;
        }//end of SharePun

        return super.onOptionsItemSelected(item);
    }//end of onOptionsItemSelected


    public void updateUI(){
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userID = snapshot.child("Users").child(currUser.getUid()).getValue().toString();
                lastFPosition  = tinydb.getInt(userID + "lastFPosition");
                favouritesList = tinydb.getListString(userID + "favouritesList");

                if (favouritesList.size() == 0){
                    favouritesList.add("You have not saved any pun(s).");
                }
                else if (favouritesList.size() >= 2){
                    boolean isE = false;
                    int position = 0;
                    for (int i = 0; i < favouritesList.size(); i++){
                        if (favouritesList.get(i).equals("You have not saved any pun(s).")){
                            isE = true;
                            position = i;
                        }//end of if
                    }//end of for loop

                    if (isE == true){
                        favouritesList.remove(position);
                    }
                }

                fpAdapter = new FavouritePunsAdapter(favouritesList, provider.getColours());
                viewPagerFavs.setAdapter(fpAdapter);
                viewPagerFavs.setCurrentItem(lastFPosition,false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }//end of updateUI

}//end of class