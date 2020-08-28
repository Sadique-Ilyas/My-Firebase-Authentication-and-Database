package com.example.myfirebaseauthenticationdatabase;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SignUpActivity extends AppCompatActivity {
    private EditText myFullName, myPhoneNumber, myEmail, myPassword;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView myProfilePic;
    private FloatingActionButton floatingActionButton;
    private Button createAccountBtn;
    private Uri mProfilePicUri;
    private TextView signInTxt;
    private ProgressBar progressBar;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        myProfilePic = findViewById(R.id.myProfilePic);
        floatingActionButton = findViewById(R.id.fab);
        myFullName = findViewById(R.id.myFullName);
        myPhoneNumber = findViewById(R.id.myPhoneNumber);
        myEmail = findViewById(R.id.myEmail);
        myPassword = findViewById(R.id.myPassword);
        createAccountBtn = findViewById(R.id.createAccountBtn);
        signInTxt = findViewById(R.id.signInTxt);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("Users");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fullName = myFullName.getText().toString().trim();
                final String phoneNumber = myPhoneNumber.getText().toString().trim();
                final String email = myEmail.getText().toString().trim();
                String password = myPassword.getText().toString().trim();

                if (fullName.isEmpty() && phoneNumber.isEmpty() && email.isEmpty() && password.isEmpty()) {
                    myFullName.setError("Enter Full Name");
                    myPhoneNumber.setError("Enter Phone Number");
                    myEmail.setError("Enter Email");
                    myPassword.setError("Enter Password");
                    myFullName.requestFocus();
                } else if (fullName.isEmpty()) {
                    myFullName.setError("Enter Full Name");
                    myFullName.requestFocus();
                } else if (phoneNumber.isEmpty()) {
                    myPhoneNumber.setError("Enter Phone Number");
                    myPhoneNumber.requestFocus();
                } else if (email.isEmpty()) {
                    myEmail.setError("Enter Email");
                    myEmail.requestFocus();
                } else if (password.isEmpty()) {
                    myPassword.setError("Enter Password");
                    myPassword.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(SignUpActivity.this, "Account Created !!!" +
                                        "\nPlease Check your Email for Verification", Toast.LENGTH_LONG).show();
                                emailVerification();
                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                                if (mProfilePicUri != null) {
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd _ HH:mm:ss");
                                    String currentDateTime = simpleDateFormat.format(new Date());
                                    final StorageReference fileReference = storageReference.child(mAuth.getCurrentUser().getUid()).child("IMG_" + currentDateTime + "." + getFileExtension(mProfilePicUri));
                                    fileReference.putFile(mProfilePicUri)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    Toast.makeText(SignUpActivity.this, "Profile Pic Saved", Toast.LENGTH_SHORT).show();

                                                    Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                                                    while (!uri.isComplete()) ;
                                                    Uri url = uri.getResult();
                                                    User user = new User(fullName, phoneNumber, email, url.toString());
                                                    databaseReference
                                                            .child(mAuth.getCurrentUser().getUid())
                                                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(SignUpActivity.this, "Profile info Saved !!!", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                progressBar.setVisibility(View.GONE);
                                                                Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.getMessage();
                        }
                    });
                }
            }
        });
        String signInText = "Already have an Account ? Sign In here";
        SpannableString ss1 = new SpannableString(signInText);
        StyleSpan bold_italic = new StyleSpan(Typeface.BOLD_ITALIC);
        UnderlineSpan underline = new UnderlineSpan();
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        };

        ss1.setSpan(clickableSpan1, 26, 38, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss1.setSpan(bold_italic, 26, 38, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss1.setSpan(underline, 26, 38, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ;
        signInTxt.setText(ss1);
        signInTxt.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mProfilePicUri = data.getData();
            Picasso.get().load(mProfilePicUri).into(myProfilePic);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void emailVerification() {
        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Verification Email Sent !!!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}