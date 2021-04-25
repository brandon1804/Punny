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
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    ArrayList<String> favouritesList;
    Toolbar toolbar;
    ViewPager2 viewPager;
    Providers provider;
    ProvidedPunsAdapter ppAdapter;
    int lastMAPosition;
    TinyDB tinydb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        provider = new Providers();


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tinydb = new TinyDB(MainActivity.this);
        favouritesList = tinydb.getListString("favouritesList");
        lastMAPosition  = tinydb.getInt("lastMAPosition");

        viewPager = findViewById(R.id.viewPagerMA);
        ppAdapter = new ProvidedPunsAdapter(provider.getProvidedPuns(), provider.getColours());
        viewPager.setAdapter(ppAdapter);
        viewPager.setCurrentItem(lastMAPosition,false);


        BottomNavigationView bottomNavBar = findViewById(R.id.bottom_nav);

        bottomNavBar.setSelectedItemId(R.id.Home);

        bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                tinydb.putInt("lastMAPosition", viewPager.getCurrentItem());
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
            tinydb.putListString("favouritesList", favouritesList);
            return true;
        }//end of FavouritePun

        else if (id == R.id.share) {
            int currentItem = viewPager.getCurrentItem();
            String currentItemStr = provider.getProvidedPuns()[currentItem];
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
        }//end of SharePun
        return super.onOptionsItemSelected(item);
    }//end of onOptionsItemSelected


}//end of class

