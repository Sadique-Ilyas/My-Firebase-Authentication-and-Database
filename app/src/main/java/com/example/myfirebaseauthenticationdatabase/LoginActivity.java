package com.example.myfirebaseauthenticationdatabase;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText loginEmail, loginPassword;
    Button loginBtn;
    TextView signUpTxt, forgotPasswwordTxt;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser firebaseUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginBtn);
        forgotPasswwordTxt = findViewById(R.id.forgotPassword);
        signUpTxt = findViewById(R.id.signUpTxt);

        /*mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if (mFirebaseUser != null && mFirebaseUser.isEmailVerified())
                {
                    Toast.makeText(LoginActivity.this,"You are logged in !!!(Auth Listener)",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };*/

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();

                if (email.isEmpty() && password.isEmpty())
                {
                    loginEmail.setError("Please enter email id");
                    loginPassword.setError("Please enter password");
                    loginEmail.requestFocus();
                }
                else if (email.isEmpty())
                {
                    loginEmail.setError("Please enter email id");
                    loginEmail.requestFocus();
                }
                else if (password.isEmpty())
                {
                    loginPassword.setError("Please enter password");
                    loginPassword.requestFocus();
                }
                else {
                    mFirebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                if (mFirebaseAuth.getCurrentUser().isEmailVerified())
                                {
                                    startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                                    finish();
                                }
                                else
                                {
                                    Snackbar.make(findViewById(R.id.loginActivity),"Please verify your Email Address", Snackbar.LENGTH_LONG)
                                            .setAction("Resend Email", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if(firebaseUser!=null){
                                                        firebaseUser.reload();
                                                        if(!firebaseUser.isEmailVerified()) {
                                                            firebaseUser.sendEmailVerification();
                                                            Toast.makeText(LoginActivity.this, "Email Sent !", Toast.LENGTH_LONG).show();
                                                        }else {
                                                            Toast.makeText(LoginActivity.this, "Your email has been verified ! You can login now.", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                }
                                            }).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        String signUpText= "Don't have an Account ? Sign Up here";
        SpannableString ss1 = new SpannableString(signUpText);
        StyleSpan bold_italic = new StyleSpan(Typeface.BOLD_ITALIC);
        UnderlineSpan underline = new UnderlineSpan();
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
                finish();
            }
        };

        ss1.setSpan(clickableSpan1,24,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss1.setSpan(bold_italic,24,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss1.setSpan(underline,24,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);;
        signUpTxt.setText(ss1);
        signUpTxt.setMovementMethod(LinkMovementMethod.getInstance());

        String forgotText= "Forgot Password ???";
        SpannableString ss = new SpannableString(forgotText);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
            }
        };

        ss.setSpan(clickableSpan,0,19, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        forgotPasswwordTxt.setText(ss);
        forgotPasswwordTxt.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /*public void forgotPassword(View view)
    {
        startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseUser != null && firebaseUser.isEmailVerified()) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }
    }
}