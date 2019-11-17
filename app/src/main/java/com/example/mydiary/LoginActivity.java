package com.example.mydiary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mydiary.Model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private EditText mEditEmail, mEditPassword;
    private SignInButton btnSigninGG;
    private Button btnSignin,btnRegister;
    private FirebaseAuth auth;
    private ProgressDialog myProgress;
    private DatabaseReference dbReference;
    private String userId;

    public static GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 123;
    public static final int LOGIN_METHOD_GGCredential = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnSignin = findViewById(R.id.btnSignIn);
        btnSigninGG = findViewById(R.id.btnSignInGG);
        btnRegister = findViewById(R.id.btnRegister);
        mEditEmail = (EditText)findViewById(R.id.mEdit_Email);
        mEditPassword = (EditText)findViewById(R.id.mEdit_Password);
        auth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        setEvent();
        dbReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null) {
            firebaseAuthWithGoogle(account);
        }
    }

    public void setEvent() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mEditEmail.getText().toString().equals("") && !mEditPassword.getText().toString().equals("")) {
                    myProgress = new ProgressDialog(LoginActivity.this);
                    myProgress.setTitle("Authenticatiing");
                    myProgress.setMessage("Please wait...");
                    myProgress.setCancelable(true);
                    myProgress.show();
                    signInWithFirebaseViaAccount(view);
                } else {
                    Toast.makeText(LoginActivity.this,"Please fill in all the fields",Toast.LENGTH_LONG).show();
                }

            }
        });
        btnSigninGG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myProgress = new ProgressDialog(LoginActivity.this);
                myProgress.setTitle("Authenticatiing");
                myProgress.setMessage("Please wait...");
                myProgress.setCancelable(true);
                myProgress.show();
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }
    public void signInWithFirebaseViaAccount(View v) {
        auth.signInWithEmailAndPassword(mEditEmail.getText().toString(), mEditPassword.getText().toString())
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        myProgress.dismiss();
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Please double check your email and password", Toast.LENGTH_LONG).show();
                        } else {

                            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("userId", userId);
                            startActivity(intent);
                        }
                    }
                });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        myProgress.dismiss();
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            intent.putExtra("userId",userId);
                            intent.putExtra("loginMethod",LOGIN_METHOD_GGCredential);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                        }

                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });
    }
    public void function1() {
        FirebaseDatabase.getInstance().getReference().child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
//                            dbReference.child(userId).setValue(1)
//                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if(task.isSuccessful()) {
//
//                                            }
//                                        }
//                                    });
                    User user = new User();
                    user.setUsername("User " + userId);
                    FirebaseDatabase.getInstance().getReference().child(userId).child("Information").setValue(user);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
