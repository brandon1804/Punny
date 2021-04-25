package sg.edu.rp.id18044455.punny;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class Create extends AppCompatActivity {

    Toolbar toolbarCreate;
    ArrayList<String> favouritesList;
    ArrayList<String> createdPunsList;
    ViewPager2 viewPagerCreatedPuns;
    Providers provider;
    CreatedPunsAdapter cpAdapter;
    int lastCPosition;
    TinyDB tinydb;
    Menu optionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        tinydb = new TinyDB(Create.this);
        toolbarCreate = findViewById(R.id.toolbarCreation);
        setSupportActionBar(toolbarCreate);
        provider = new Providers();
        lastCPosition  = tinydb.getInt("lastCPosition");
        favouritesList = tinydb.getListString("favouritesList");
        createdPunsList = tinydb.getListString("createdPunsList");
        viewPagerCreatedPuns = findViewById(R.id.viewPagerCreations);

        if (createdPunsList.size() == 0){
            createdPunsList.add("You have not created any pun(s).");
        }
        else if (createdPunsList.size() >= 2){
            boolean isE = false;
            int position = 0;
            for (int i = 0; i < createdPunsList.size(); i++){
                if (createdPunsList.get(i).equals("You have not created any pun(s).")){
                    isE = true;
                    position = i;
                }//end of if
            }//end of for loop

            if (isE == true){
                createdPunsList.remove(position);
            }
        }
        cpAdapter = new CreatedPunsAdapter(createdPunsList, provider.getColours());
        viewPagerCreatedPuns.setAdapter(cpAdapter);
        viewPagerCreatedPuns.setCurrentItem(lastCPosition,false);


        BottomNavigationView bottomNavBar = findViewById(R.id.bottom_nav);

        bottomNavBar.setSelectedItemId(R.id.Creations);

        bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                tinydb.putInt("lastCPosition", viewPagerCreatedPuns.getCurrentItem());
                if (item.getItemId() == R.id.Home){
                    startActivity(new Intent(Create.this, MainActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                }
                else if (item.getItemId() == R.id.Favourites){
                    startActivity(new Intent(Create.this, Favourites.class));
                    overridePendingTransition(0,0);
                    return true;
                }
                else if (item.getItemId() == R.id.CreatePun){
                    startActivity(new Intent(Create.this, CreatePun.class));
                    overridePendingTransition(0,0);

                    return true;
                }
                else if (item.getItemId() == R.id.Settings){
                    startActivity(new Intent(Create.this, Settings.class));
                    overridePendingTransition(0,0);
                    return true;
                }
                return false;
            }
        });
    }//end of onCreate
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_createmenu, menu);
        if (createdPunsList.get(0).equals("You have not created any pun(s).")){
            menu.findItem(R.id.favCreation).setVisible(false);
            menu.findItem( R.id.editCreation).setVisible(false);
            menu.findItem( R.id.deleteCreation).setVisible(false);
            menu.findItem( R.id.share).setVisible(false);
        }
        optionsMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if (id == R.id.favCreation) {
            int currentItem = viewPagerCreatedPuns.getCurrentItem();
            String currentItemStr = createdPunsList.get(currentItem);
            boolean isAdded = false;

            for (int i = 0; i < favouritesList.size(); i++){
                if (currentItemStr.equals(favouritesList.get(i))){
                    isAdded = true;
                }//end of if
            }//end of for loop

            if (isAdded == false){
                favouritesList.add(currentItemStr);
                Toast.makeText(Create.this, "Created Pun Saved!", Toast.LENGTH_SHORT).show();
            }
            else if (isAdded == true){
                Toast.makeText(Create.this, "Created Pun Already Saved!", Toast.LENGTH_SHORT).show();
            }
            tinydb.putListString("favouritesList", favouritesList);
            return true;
        }//end of FavouritePun

        else if (id == R.id.editCreation) {
            int currentItem = viewPagerCreatedPuns.getCurrentItem();

            LayoutInflater inflater  = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View viewDialog = inflater.inflate(R.layout.edit_pun, null);
            final EditText etSetup = viewDialog.findViewById(R.id.editCPSetup);
            final EditText etPunchline = viewDialog.findViewById(R.id.editCPPunchline);

            AlertDialog.Builder myBuilder = new AlertDialog.Builder(Create.this);
            myBuilder.setView(viewDialog);
            myBuilder.setTitle("Edit Created Pun");
            myBuilder.setCancelable(false);


            myBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String setup = etSetup.getText().toString();
                    String punchline = etPunchline.getText().toString();
                    String updatedPun = setup + "\n\n" + punchline;

                    if (setup.length() == 0  && punchline.length() == 0){
                        Toast.makeText(Create.this, "Please ensure no text fields are empty!", Toast.LENGTH_SHORT).show();
                    }
                    else if (setup.length() == 0){
                        Toast.makeText(Create.this, "Please enter the setup!", Toast.LENGTH_SHORT).show();
                    }
                    else if (punchline.length() == 0){
                        Toast.makeText(Create.this, "Please enter the punchline!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        createdPunsList.set(currentItem, updatedPun);
                        Toast.makeText(Create.this, "Pun Updated!", Toast.LENGTH_SHORT).show();
                        cpAdapter.notifyDataSetChanged();
                        tinydb.putListString("createdPunsList", createdPunsList);
                    }//end of validation
                }
            });

            myBuilder.setNeutralButton("Cancel", null);
            AlertDialog myDialog = myBuilder.create();
            myDialog.show();
            return true;
        }//end of editPun

        else if (id == R.id.deleteCreation) {
            int currentItem = viewPagerCreatedPuns.getCurrentItem();
            AlertDialog.Builder myBuilder = new AlertDialog.Builder(Create.this);
            myBuilder.setTitle("Delete Created Pun?");
            myBuilder.setMessage("This would remove your created pun from our database.");
            myBuilder.setCancelable(false);

            myBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (createdPunsList.size() == 1){
                        createdPunsList.remove(currentItem);
                        Toast.makeText(Create.this, "Pun Removed From Creations!", Toast.LENGTH_SHORT).show();
                        createdPunsList.add("You have not created any pun(s).");
                        cpAdapter.notifyDataSetChanged();
                        optionsMenu.findItem(R.id.deleteCreation).setVisible(false);
                        optionsMenu.findItem(R.id.editCreation).setVisible(false);
                        optionsMenu.findItem(R.id.share).setVisible(false);
                        optionsMenu.findItem(R.id.favCreation).setVisible(false);
                        tinydb.putListString("createdPunsList", createdPunsList);
                    }
                    else{
                        createdPunsList.remove(currentItem);
                        Toast.makeText(Create.this, "Pun Removed From Creations!", Toast.LENGTH_SHORT).show();
                        cpAdapter.notifyDataSetChanged();
                        tinydb.putListString("createdPunsList", createdPunsList);
                    }
                }
            });

            myBuilder.setNeutralButton("Cancel", null);
            AlertDialog myDialog = myBuilder.create();
            myDialog.show();
            return true;
        }//end of deletePun

        else if (id == R.id.share) {
            int currentItem = viewPagerCreatedPuns.getCurrentItem();
            String currentItemStr = createdPunsList.get(currentItem);
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Pun", currentItemStr);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(Create.this, "Pun Copied!", Toast.LENGTH_SHORT).show();
            return true;
        }//end of SharePun

        else if (id == R.id.help) {
            AlertDialog.Builder myBuilder = new AlertDialog.Builder(Create.this);
            myBuilder.setTitle("Help");
            myBuilder.setMessage(getString(R.string.helpMessageCreate));
            myBuilder.setCancelable(false);
            myBuilder.setPositiveButton("Dismiss", null);

            AlertDialog myDialog = myBuilder.create();
            myDialog.show();
            return true;
        }//end of SharePun

        return super.onOptionsItemSelected(item);
    }//end of onOptionsItemSelected


}//end of class