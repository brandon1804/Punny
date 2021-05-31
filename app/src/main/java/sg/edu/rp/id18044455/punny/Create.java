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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Create extends AppCompatActivity {

    Toolbar toolbarCreate;
    ArrayList<String> punsList, favouritesList, createdPunsList;
    ViewPager2 viewPagerCreatedPuns;
    Providers provider;
    CreatedPunsAdapter cpAdapter;
    int lastCPosition;
    TinyDB tinydb;
    Menu optionsMenu;
    String userID;


    FirebaseAuth fAuth;
    DatabaseReference root;
    FirebaseUser currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        fAuth = FirebaseAuth.getInstance();
        currUser = fAuth.getCurrentUser();
        root = FirebaseDatabase.getInstance().getReference();

        tinydb = new TinyDB(Create.this);
        punsList = new ArrayList<String>();

        updateUI();

        toolbarCreate = findViewById(R.id.toolbarCreation);
        setSupportActionBar(toolbarCreate);
        provider = new Providers();
        viewPagerCreatedPuns = findViewById(R.id.viewPagerCreations);

        BottomNavigationView bottomNavBar = findViewById(R.id.bottom_nav);

        bottomNavBar.setSelectedItemId(R.id.Creations);

        bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                tinydb.putInt(userID + "lastCPosition", viewPagerCreatedPuns.getCurrentItem());
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
    public void onBackPressed(){

        moveTaskToBack(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_createmenu, menu);
        if (createdPunsList.get(0).equals("You have not created any pun(s).")){
            menu.findItem(R.id.publishCreation).setVisible(false);
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

        if (id == R.id.publishCreation) {
            AlertDialog.Builder myBuilder = new AlertDialog.Builder(Create.this);
            myBuilder.setTitle("Publish Pun?");
            myBuilder.setMessage("Your pun would be visible to other users.");
            myBuilder.setCancelable(false);

            int currentItem = viewPagerCreatedPuns.getCurrentItem();
            String currentItemStr = createdPunsList.get(currentItem);

            myBuilder.setPositiveButton("Publish", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    boolean isPublished = false;

                    for (int i = 0; i < punsList.size(); i++){
                        if (currentItemStr.equals(punsList.get(i))){
                            isPublished = true;
                        }//end of if
                    }//end of for loop

                    if (isPublished == false){
                        punsList.add(currentItemStr);
                        root.child("Puns").setValue(punsList);
                        tinydb.putString("punIdentifier" + currentItemStr, currUser.getUid());
                        Toast.makeText(Create.this, "Pun Published!", Toast.LENGTH_SHORT).show();
                    }
                    else if (isPublished == true){
                        Toast.makeText(Create.this, "Pun Already Published!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            myBuilder.setNegativeButton("Cancel", null);
            AlertDialog myDialog = myBuilder.create();
            myDialog.show();
            return true;
        }//end of publish

        else if (id == R.id.editCreation) {
            int currentItem = viewPagerCreatedPuns.getCurrentItem();
            String currentItemStr = createdPunsList.get(currentItem);
            String punIdentifier = tinydb.getString("punIdentifier" + currentItemStr);
            LayoutInflater inflater  = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View viewDialog = inflater.inflate(R.layout.edit_pun, null);
            final EditText etSetup = viewDialog.findViewById(R.id.editCPSetup);
            final EditText etPunchline = viewDialog.findViewById(R.id.editCPPunchline);

            AlertDialog.Builder myBuilder = new AlertDialog.Builder(Create.this);
            myBuilder.setView(viewDialog);
            myBuilder.setTitle("Edit Pun?");
            myBuilder.setCancelable(false);

            myBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
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
                        boolean isFav = false;
                        int favIndex = 0;
                        boolean isCreated = false;
                        boolean isAdded = false;
                        boolean isPublished = false;
                        int punIndex = 0;

                        for (int i = 0; i < punsList.size(); i++){
                            if (currentItemStr.equals(punsList.get(i))){
                                isPublished = true;
                                punIndex = i;
                            }//end of if
                        }//end of for loop

                        for (int i = 0; i < createdPunsList.size(); i++){
                            if (updatedPun.equals(createdPunsList.get(i))){
                                isCreated = true;
                            }//end of if
                        }//end of for loop

                        for (int i = 0; i < favouritesList.size(); i++){
                            if (createdPunsList.get(currentItem).equals(favouritesList.get(i))){
                                isFav = true;
                                favIndex = i;
                            }//end of if
                        }//end of for loop

                        for (int i = 0; i < punsList.size(); i++){
                            if (updatedPun.equals(punsList.get(i))){
                                isAdded = true;
                            }//end of if
                        }//end of for loop





                        if(isAdded == false){
                            if(isPublished == true && isCreated == false && currUser.getUid().equals(punIdentifier)){
                                punsList.set(punIndex, updatedPun);
                                if(isFav == true){
                                    favouritesList.set(favIndex, updatedPun);
                                }
                                createdPunsList.set(currentItem, updatedPun);
                                root.child("Puns").setValue(punsList);
                                tinydb.putString("punIdentifier" + updatedPun, currUser.getUid());
                                Toast.makeText(Create.this, "Pun Updated!", Toast.LENGTH_SHORT).show();
                                cpAdapter.notifyDataSetChanged();
                                tinydb.putListString(userID + "createdPunsList", createdPunsList);
                                tinydb.putListString(userID + "favouritesList", favouritesList);
                            }

                            else if(isCreated == false){
                                if(isFav == true){
                                    favouritesList.set(favIndex, updatedPun);
                                }
                                createdPunsList.set(currentItem, updatedPun);
                                Toast.makeText(Create.this, "Pun Updated!", Toast.LENGTH_SHORT).show();
                                cpAdapter.notifyDataSetChanged();
                                tinydb.putListString(userID + "createdPunsList", createdPunsList);
                                tinydb.putListString(userID + "favouritesList", favouritesList);
                            }
                            else{
                                Toast.makeText(Create.this, "Pun Already Created!", Toast.LENGTH_SHORT).show();
                            }
                        }//end of isAdded
                        else{
                            Toast.makeText(Create.this, "Pun Already Exists!", Toast.LENGTH_SHORT).show();
                        }

                    }//end of validation
                }
            });

            myBuilder.setNegativeButton("Cancel", null);
            AlertDialog myDialog = myBuilder.create();
            myDialog.show();
            return true;
        }//end of editPun

        else if (id == R.id.deleteCreation) {
            AlertDialog.Builder myBuilder = new AlertDialog.Builder(Create.this);
            myBuilder.setTitle("Delete Created Pun?");
            myBuilder.setMessage("This would remove your pun from our database.");
            myBuilder.setCancelable(false);

            int currentItem = viewPagerCreatedPuns.getCurrentItem();
            String currentItemStr = createdPunsList.get(currentItem);
            String punIdentifier = tinydb.getString("punIdentifier" + currentItemStr);


            myBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    boolean isFav = false;
                    int favIndex = 0;
                    boolean isPublished = false;
                    int punIndex = 0;

                    for (int i = 0; i < punsList.size(); i++){
                        if (currentItemStr.equals(punsList.get(i))){
                            isPublished = true;
                            punIndex = i;
                        }//end of if
                    }//end of for loop

                    for (int i = 0; i < favouritesList.size(); i++){
                        if (createdPunsList.get(currentItem).equals(favouritesList.get(i))){
                            isFav = true;
                            favIndex = i;
                        }//end of if
                    }//end of for loop



                    if (createdPunsList.size() == 1 && isPublished == true && currUser.getUid().equals(punIdentifier)){
                        punsList.remove(punIndex);
                        createdPunsList.remove(currentItem);
                        if(isFav == true){
                            favouritesList.remove(favIndex);
                        }
                        root.child("Puns").setValue(punsList);
                        Toast.makeText(Create.this, "Pun Removed From Creations!", Toast.LENGTH_SHORT).show();
                        createdPunsList.add("You have not created any pun(s).");
                        cpAdapter.notifyDataSetChanged();
                        optionsMenu.findItem(R.id.publishCreation).setVisible(false);
                        optionsMenu.findItem(R.id.deleteCreation).setVisible(false);
                        optionsMenu.findItem(R.id.editCreation).setVisible(false);
                        optionsMenu.findItem(R.id.share).setVisible(false);
                        tinydb.putListString(userID + "createdPunsList", createdPunsList);
                        tinydb.putListString(userID + "favouritesList", favouritesList);
                    }
                    else if (createdPunsList.size() >= 2 && isPublished == true && currUser.getUid().equals(punIdentifier)){
                        punsList.remove(punIndex);
                        createdPunsList.remove(currentItem);
                        if(isFav == true){
                            favouritesList.remove(favIndex);
                        }
                        root.child("Puns").setValue(punsList);
                        Toast.makeText(Create.this, "Pun Removed From Creations!", Toast.LENGTH_SHORT).show();
                        cpAdapter.notifyDataSetChanged();
                        tinydb.putListString(userID + "createdPunsList", createdPunsList);
                        tinydb.putListString(userID + "favouritesList", favouritesList);
                    }
                    else if (createdPunsList.size() == 1){
                        createdPunsList.remove(currentItem);
                        if(isFav == true){
                            favouritesList.remove(favIndex);
                        }
                        Toast.makeText(Create.this, "Pun Removed From Creations!", Toast.LENGTH_SHORT).show();
                        createdPunsList.add("You have not created any pun(s).");
                        cpAdapter.notifyDataSetChanged();
                        optionsMenu.findItem(R.id.publishCreation).setVisible(false);
                        optionsMenu.findItem(R.id.deleteCreation).setVisible(false);
                        optionsMenu.findItem(R.id.editCreation).setVisible(false);
                        optionsMenu.findItem(R.id.share).setVisible(false);
                        tinydb.putListString(userID + "createdPunsList", createdPunsList);
                        tinydb.putListString(userID + "favouritesList", favouritesList);
                    }
                    else if (createdPunsList.size() >= 2){
                        createdPunsList.remove(currentItem);
                        if(isFav == true){
                            favouritesList.remove(favIndex);
                        }
                        Toast.makeText(Create.this, "Pun Removed From Creations!", Toast.LENGTH_SHORT).show();
                        cpAdapter.notifyDataSetChanged();
                        tinydb.putListString(userID + "createdPunsList", createdPunsList);
                        tinydb.putListString(userID + "favouritesList", favouritesList);
                    }

                }
            });

            myBuilder.setNegativeButton("Cancel", null);
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
        }//end of help

        return super.onOptionsItemSelected(item);
    }//end of onOptionsItemSelected


    public void updateUI(){

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (punsList.size() == 0) {
                    for (DataSnapshot child : snapshot.child("Puns").getChildren()) {
                        punsList.add(child.getValue().toString());
                    }//end of for puns loop
                }

                userID = snapshot.child("Users").child(currUser.getUid()).getValue().toString();
                lastCPosition  = tinydb.getInt(userID + "lastCPosition");
                favouritesList = tinydb.getListString(userID +"favouritesList");
                createdPunsList = tinydb.getListString(userID +"createdPunsList");

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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }//end of updateUI






}//end of class