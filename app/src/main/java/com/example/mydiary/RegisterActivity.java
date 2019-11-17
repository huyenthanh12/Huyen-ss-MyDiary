package com.example.mydiary;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {
    private Button mOKButton, mCancelButton;
    private EditText mUsername, mPassword, mValidation;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ProgressDialog myProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mOKButton = findViewById(R.id.OK_button);
        mCancelButton = findViewById(R.id.Cancel_Button);
        mUsername = findViewById(R.id.username);
        mPassword = findViewById(R.id.password);
        mValidation = findViewById(R.id.password_validation);
    }

    public boolean checkLength(String s) {
        return (s.length() >= 8) ? true : false;
    }

    public void setListener() {
        mOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = mUsername.getText().toString();
                final String password = mPassword.getText().toString();
                myProgress = new ProgressDialog(RegisterActivity.this);
                myProgress.setTitle("Authenticatiing");
                myProgress.setMessage("Please wait ...");
                myProgress.setCancelable(true);
                myProgress.show();
                String validation = mValidation.getText().toString();
                if (checkLength(username) && checkLength(password) && checkLength(validation)) {
                    if (password.equals(validation)) {
                        auth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    createUserNode(auth.getCurrentUser().getUid(), password);
                                    auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                myProgress.dismiss();
                                                String status = "Register Successfully. Please check your email for verification!";
                                                Toast.makeText(RegisterActivity.this, status, Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        Toast.makeText(RegisterActivity.this, "Validation must match password", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Username or Password does not match requirement length ( >= 8)", Toast.LENGTH_LONG).show();

                }
            }
        });
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void createUserNode(final String userId, final String nickname) {
        final DatabaseReference mDbRoot = FirebaseDatabase.getInstance().getReference();
        mDbRoot.child(userId)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    mDbRoot.child(userId).child("userInfo").child("username").setValue(nickname)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                    } else {
                                                        Toast.makeText(RegisterActivity.this, "Wrong", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        }
                );
    }
}
