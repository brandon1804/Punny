package sg.edu.rp.id18044455.punny;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class CreatePun extends AppCompatActivity {

    Toolbar toolbarCreatePun;
    ArrayList<String> punsList, favouritesList, createdPunsList;
    EditText editSetup;
    EditText editPunchline;
    Button btnCreatePun;
    TinyDB tinydb;
    String userID;

    FirebaseAuth fAuth;
    DatabaseReference root;
    FirebaseUser currUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pun);

        fAuth = FirebaseAuth.getInstance();
        currUser = fAuth.getCurrentUser();
        root = FirebaseDatabase.getInstance().getReference();

        tinydb = new TinyDB(CreatePun.this);
        punsList = new ArrayList<String>();
        updateUI();

        toolbarCreatePun = findViewById(R.id.createPunToolbar);
        setSupportActionBar(toolbarCreatePun);
        editSetup = findViewById(R.id.editSetup);
        editPunchline = findViewById(R.id.editPunchline);
        btnCreatePun = findViewById(R.id.btnCreatePun);



        btnCreatePun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String setup = editSetup.getText().toString();
                String punchline = editPunchline.getText().toString();
                String createdPun = setup + "\n\n" + punchline;
                boolean isCreated = false;
                boolean isFav = false;
                if (setup.length() == 0  && punchline.length() == 0){
                    editSetup.setError("Please enter the setup");
                    editPunchline.setError("Please enter the punchline");
                }
                else if (setup.length() == 0){
                    editSetup.setError("Please enter the setup");
                }
                else if (punchline.length() == 0){
                    editPunchline.setError("Please enter the punchline");
                }
                else{
                    for (int i = 0; i < createdPunsList.size(); i++){
                        if (createdPun.equals(createdPunsList.get(i))){
                            isCreated = true;
                        }//end of if
                    }//end of for loop

                    for (int i = 0; i < favouritesList.size(); i++){
                        if (createdPun.equals(favouritesList.get(i))){
                            isFav = true;
                        }//end of if
                    }//end of for loop

                    if (isCreated == false && isFav == false){
                        createdPunsList.add(createdPun);
                        favouritesList.add(createdPun);
                        editSetup.setText("");
                        editPunchline.setText("");
                        Toast.makeText(CreatePun.this, "Pun Created!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(CreatePun.this, "Pun Already Exists!", Toast.LENGTH_SHORT).show();
                    }
                    tinydb.putListString(userID + "createdPunsList", createdPunsList);
                    tinydb.putListString(userID + "favouritesList", favouritesList);
                }//end of validation
            }
        });



        BottomNavigationView bottomNavBar = findViewById(R.id.bottom_nav);

        bottomNavBar.setSelectedItemId(R.id.CreatePun);

        bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.Home){
                    startActivity(new Intent(CreatePun.this, MainActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                }
                else if (item.getItemId() == R.id.Favourites){
                    startActivity(new Intent(CreatePun.this, Favourites.class));
                    overridePendingTransition(0,0);
                    return true;
                }
                else if (item.getItemId() == R.id.Creations){
                    startActivity(new Intent(CreatePun.this, Create.class));
                    overridePendingTransition(0,0);
                    return true;
                }
                else if (item.getItemId() == R.id.Settings){
                    startActivity(new Intent(CreatePun.this, Settings.class));
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
        getMenuInflater().inflate(R.menu.toolbar_createpunmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();


        if (id == R.id.help) {
            AlertDialog.Builder myBuilder = new AlertDialog.Builder(CreatePun.this);
            myBuilder.setTitle("Help");
            myBuilder.setMessage(getString(R.string.helpMessageCreatePun));
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

                if (punsList.size() == 0){
                    for (DataSnapshot child : snapshot.child("Puns").getChildren()) {
                        punsList.add(child.getValue().toString());
                    }//end of for puns loop
                }

                userID = snapshot.child("Users").child(currUser.getUid()).getValue().toString();
                createdPunsList = tinydb.getListString(userID + "createdPunsList");
                favouritesList = tinydb.getListString(userID + "favouritesList");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }//end of updateUI




}//end of class