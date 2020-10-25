package com.lynhill.firebasechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wang.avi.AVLoadingIndicatorView;

public class MainActivity extends AppCompatActivity {
    private CardView cardView;
    private EditText userName;
    private TextView alreadyUser ;
    private  FirebaseDatabase database ;
    private  DatabaseReference myRef;
    private   String userNameS;
    private AVLoadingIndicatorView indicatorView;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance();
         myRef = database.getReference("users");
         mAuth = FirebaseAuth.getInstance();
         indicatorView = findViewById(R.id.avi);
         userName = findViewById(R.id.name);
        cardView= findViewById(R.id.sign_up);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 userNameS = userName.getText().toString();
                if (userNameS !=  null){
                    indicatorView.setVisibility(View.VISIBLE);
                    indicatorView.show();
                    signIn();
                }

            }
        });
    }

    private void signIn() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            myRef.child(user.getUid().toString()).setValue(userNameS);
                           indicatorView.hide();
                            startActivity(new Intent(MainActivity.this,ChatActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInAnonymously:failure", task.getException());
                            indicatorView.hide();
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser !=null){
            startActivity(new Intent(MainActivity.this,ChatActivity.class));
            finish();
        }

    }
}