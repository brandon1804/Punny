package sg.edu.rp.id18044455.punny;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    TextView tvNoAcc, tvForgotPassword;
    EditText etEmail, etPassword;
    CheckBox rCheckbox;
    Button btnSignIn;

    FirebaseAuth fAuth;
    FirebaseUser currUser;
    DatabaseReference root;

    TinyDB tinyDB;
    boolean rememberUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        tvNoAcc = findViewById(R.id.tvNoAcc);
        tvForgotPassword = findViewById(R.id.tvForgotPW);
        etEmail = findViewById(R.id.editEmail);
        etPassword = findViewById(R.id.editPW);
        rCheckbox = findViewById(R.id.rCheckbox);
        btnSignIn = findViewById(R.id.btnSignIn);

        fAuth = FirebaseAuth.getInstance();
        currUser = fAuth.getCurrentUser();

        tinyDB = new TinyDB(Login.this);
        rememberUser = tinyDB.getBoolean("rememberUser");



        if (rememberUser == true && currUser != null) {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        }


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if(email.isEmpty() && password.isEmpty()){
                    etEmail.setError("Email is required");
                    etPassword.setError("Password is required");
                }
                else if(email.isEmpty()){
                    etEmail.setError("Email is required");
                }

                else if(password.isEmpty()){
                    etPassword.setError("Password is required");
                }

                else{
                    fAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(Login.this, "Login Success", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        }); //end of login


        rCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    tinyDB.putBoolean("rememberUser", true);
                }//end of if
                else{
                    tinyDB.putBoolean("rememberUser", false);
                }
            }
        });//end of remember me



        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View viewDialog = inflater.inflate(R.layout.reset_password_input, null);

                final EditText etRPWEmail = viewDialog.findViewById(R.id.etRPWEmail);

                AlertDialog.Builder myBuilder = new AlertDialog.Builder(Login.this);
                myBuilder.setView(viewDialog);
                myBuilder.setTitle(R.string.tvRPW);
                myBuilder.setMessage(getString(R.string.tvRPWInfo));
                myBuilder.setCancelable(false);

                myBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = etRPWEmail.getText().toString();
                        if (email.isEmpty() == true){
                            etRPWEmail.setError("Email is required");
                        }//end of if
                        else{
                            fAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Login.this, "Reset Password Email Sent", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }//end of else
                    }
                });

                myBuilder.setNeutralButton("Cancel", null);
                AlertDialog myDialog = myBuilder.create();
                myDialog.show();
            }
        });//end of forgot password



        tvNoAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });//end of register



    }//end of onCreate





}//end of class