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

import java.util.ArrayList;

public class CreatePun extends AppCompatActivity {

    Toolbar toolbarCreatePun;
    ArrayList<String> createdPunsList;
    EditText editSetup;
    EditText editPunchline;
    Button btnCreatePun;
    TinyDB tinydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pun);



        toolbarCreatePun = findViewById(R.id.createPunToolbar);
        setSupportActionBar(toolbarCreatePun);
        editSetup = findViewById(R.id.editSetup);
        editPunchline = findViewById(R.id.editPunchline);
        btnCreatePun = findViewById(R.id.btnCreatePun);

        tinydb = new TinyDB(CreatePun.this);
        createdPunsList = tinydb.getListString("createdPunsList");



        btnCreatePun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String setup = editSetup.getText().toString();
                String punchline = editPunchline.getText().toString();
                String createdPun = setup + "\n\n" + punchline;
                boolean isCreated = false;
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

                    if (isCreated == false){
                        createdPunsList.add(createdPun);
                        editSetup.setText("");
                        editPunchline.setText("");
                        Toast.makeText(CreatePun.this, "Pun Created!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(CreatePun.this, "Pun Already Created!", Toast.LENGTH_SHORT).show();
                    }
                    tinydb.putListString("createdPunsList", createdPunsList);
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

}//end of class