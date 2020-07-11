package com.example.myfirebaseauthenticationdatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private EditText myFullName, myPhoneNumber, myEmail, myPassword;
    private Button createAccountBtn;
    private TextView signInTxt;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        myFullName = findViewById(R.id.myFullName);
        myPhoneNumber = findViewById(R.id.myPhoneNumber);
        myEmail = findViewById(R.id.myEmail);
        myPassword = findViewById(R.id.myPassword);
        createAccountBtn = findViewById(R.id.createAccountBtn);
        signInTxt = findViewById(R.id.signInTxt);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fullName = myFullName.getText().toString().trim();
                final String phoneNumber = myPhoneNumber.getText().toString().trim();
                final String email = myEmail.getText().toString().trim();
                String password = myPassword.getText().toString().trim();

                if (fullName.isEmpty() && phoneNumber.isEmpty() && email.isEmpty() && password.isEmpty())
                {
                    myFullName.setError("Enter Full Name");
                    myPhoneNumber.setError("Enter Phone Number");
                    myEmail.setError("Enter Email");
                    myPassword.setError("Enter Password");
                    myFullName.requestFocus();
                }
                else if (fullName.isEmpty())
                {
                    myFullName.setError("Enter Full Name");
                    myFullName.requestFocus();
                }
                else if (phoneNumber.isEmpty())
                {
                    myPhoneNumber.setError("Enter Phone Number");
                    myPhoneNumber.requestFocus();
                }
                else if (email.isEmpty())
                {
                    myEmail.setError("Enter Email");
                    myEmail.requestFocus();
                }
                else if (password.isEmpty())
                {
                    myPassword.setError("Enter Password");
                    myPassword.requestFocus();
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful())
                                {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(SignUpActivity.this, "Account Created !!!" +
                                            "\nPlease Check your Email for Verification", Toast.LENGTH_LONG).show();
                                    emailVerification();
                                    Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                    User user = new User(fullName,phoneNumber,email);
                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                //If Database  saved then Do Something
                                            }
                                            else
                                            {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                else
                                {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                        }
                    });
                }
            }
        });
        signInTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                finish();
            }
        });
    }

    private void emailVerification()
    {
        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(SignUpActivity.this, "Verification Email Sent !!!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}