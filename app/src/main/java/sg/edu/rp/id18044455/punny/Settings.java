package sg.edu.rp.id18044455.punny;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;


public class Settings extends AppCompatActivity {

    Switch nSwitch;
    TextView etTOD, tvChangePW, tvChangeEmail;
    Button btnLogout;
    BottomNavigationView bottomNavBar;

    TinyDB tinyDB;

    FirebaseAuth fAuth;
    DatabaseReference root;
    FirebaseUser currUser;


    String nTime, isNChecked;
    boolean nBoolean;
    int hod, mins;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        nSwitch = findViewById(R.id.nSwitch);
        etTOD = findViewById(R.id.etTOD);
        tvChangePW = findViewById(R.id.tvChangePW);
        tvChangeEmail = findViewById(R.id.tvChangeEmail);
        btnLogout = findViewById(R.id.btnLogout);

        bottomNavBar = findViewById(R.id.bottom_nav);
        bottomNavBar.setSelectedItemId(R.id.Settings);

        tinyDB = new TinyDB(Settings.this);


        fAuth = FirebaseAuth.getInstance();
        currUser = fAuth.getCurrentUser();
        root = FirebaseDatabase.getInstance().getReference().child("Users").child(currUser.getUid());

        updateSettings();


        tvChangePW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View viewDialog = inflater.inflate(R.layout.change_password, null);

                final EditText etCPW = viewDialog.findViewById(R.id.etCPW);

                AlertDialog.Builder myBuilder = new AlertDialog.Builder(Settings.this);
                myBuilder.setTitle("Change your Password?");
                myBuilder.setView(viewDialog);
                myBuilder.setCancelable(false);

                myBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newPassword = etCPW.getText().toString();
                        if (newPassword.isEmpty() == true){
                            etCPW.setError("Password is required");
                        }//end of if
                        else{
                            currUser.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Settings.this, "Password Changed Successfully", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Settings.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }//end of else
                    }
                });

                myBuilder.setNegativeButton("Cancel", null);
                AlertDialog myDialog = myBuilder.create();
                myDialog.show();

            }
        });//end of change password




        tvChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View viewDialog = inflater.inflate(R.layout.change_email, null);

                final EditText etSEmail = viewDialog.findViewById(R.id.etSEmail);
                etSEmail.setText(currUser.getEmail());

                AlertDialog.Builder myBuilder = new AlertDialog.Builder(Settings.this);
                myBuilder.setTitle("Change your Email?");
                myBuilder.setView(viewDialog);
                myBuilder.setCancelable(false);

                myBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newEmail = etSEmail.getText().toString();
                        if (newEmail.isEmpty() == true){
                            etSEmail.setError("Email is required");
                        }//end of if
                        else{
                            currUser.updateEmail(newEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Settings.this, "Email Changed Successfully", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Settings.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }//end of else
                    }
                });

                myBuilder.setNegativeButton("Cancel", null);
                AlertDialog myDialog = myBuilder.create();
                myDialog.show();

            }
        });//end of change email




        nSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    etTOD.setVisibility(View.VISIBLE);
                    root.child("NotificationsEnabled").setValue("true");
                }//end of if
                else{
                    etTOD.setVisibility(View.INVISIBLE);
                    root.child("NotificationsEnabled").setValue("false");
                }
            }
        });


        etTOD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        int hour = hourOfDay;
                        int minutes = minute;

                        String amPM = "";

                        if (hour > 12) {
                            hour -= 12;
                            amPM = "PM";
                        } else if (hour == 0) {
                            hour += 12;
                            amPM = "AM";
                        } else if (hour == 12){
                            amPM = "PM";
                        }else{
                            amPM = "AM";
                        }

                        String min = "";
                        if (minutes < 10)
                            min = "0" + minutes;
                        else{
                            min = String.valueOf(minutes);
                        }


                        etTOD.setText(hour + ":" + min + " " + amPM);

                        root.child("NotificationTiming").setValue(etTOD.getText().toString());
                        root.child("hourOfDay").setValue(hourOfDay);
                        root.child("minute").setValue(minute);

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);

                        if (calendar.getTime().compareTo(new Date()) < 0){
                            calendar.add(Calendar.DAY_OF_MONTH, 1);
                        }

                        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                        if (alarmManager != null) {
                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                        }

                        Toast.makeText(Settings.this, "Time Set", Toast.LENGTH_SHORT).show();
                    }//end of onTimeSet
                };
                Calendar calendar = Calendar.getInstance();


                if(hod != 0 && mins != 0){
                    TimePickerDialog myTimeDialog;
                    myTimeDialog = new TimePickerDialog(Settings.this, myTimeListener, hod, mins, false);
                    myTimeDialog.show();
                }
                else{
                    TimePickerDialog myTimeDialog;
                    myTimeDialog = new TimePickerDialog(Settings.this, myTimeListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                    myTimeDialog.show();
                }

            }
        });


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                builder.setTitle("Log out?");
                builder.setMessage("This would log you out of Punny.");
                builder.setCancelable(false);

                builder.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fAuth.signOut();
                        Intent intent = new Intent(Settings.this, Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                });//end of positive

                builder.setNegativeButton("Cancel", null);
                AlertDialog myDialog = builder.create();
                myDialog.show();
            }
        });



        bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.Home){
                    startActivity(new Intent(Settings.this, MainActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                }
                else if (item.getItemId() == R.id.Favourites){
                    startActivity(new Intent(Settings.this, Favourites.class));
                    overridePendingTransition(0,0);
                    return true;
                }
                else if (item.getItemId() == R.id.Creations){
                    startActivity(new Intent(Settings.this, Create.class));
                    overridePendingTransition(0,0);
                    return true;
                }
                else if (item.getItemId() == R.id.CreatePun){
                    startActivity(new Intent(Settings.this, CreatePun.class));
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


    public void updateSettings(){
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("NotificationTiming") && snapshot.hasChild("NotificationsEnabled") &&  snapshot.hasChild("hourOfDay") && snapshot.hasChild("minute")){
                    nTime = snapshot.child("NotificationTiming").getValue().toString();
                    isNChecked = snapshot.child("NotificationsEnabled").getValue().toString();
                    hod = Integer.parseInt(snapshot.child("hourOfDay").getValue().toString());
                    mins = Integer.parseInt(snapshot.child("minute").getValue().toString());

                    nBoolean = false;

                    if(isNChecked.equals("true")){
                        nBoolean = true;
                    }

                    nSwitch.setChecked(nBoolean);

                    if (!nTime.isEmpty()){
                        etTOD.setText(nTime);
                    }

                }//end of validation

                if (nSwitch.isChecked() == false){
                    etTOD.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }//end of updateSettings()





}//end of class