package com.iti.example.tripreminder;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "GoogleSignIn";
    private static final int GOOGLE_SIGN_IN = 123;
    EditText email,password,rePassword,mobile;
    Button Signup,Gmail;
    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this,gso);
        auth = FirebaseAuth.getInstance();
        /*Views Declaration*/
        email = findViewById(R.id.edtTxt_email_signup);
        password = findViewById(R.id.edtTxt_password_signup);
        rePassword = findViewById(R.id.edtTxt_rePassword_signup);
        mobile = findViewById(R.id.edtTxt_mobile_signup);
        Signup = findViewById(R.id.btn_SignUp_signup);
        Gmail = findViewById(R.id.btn_google_signup);
        /*On Buttons Click Actions*/
        Signup.setOnClickListener(v -> SignUpWithEmailAndPassword());
        Gmail.setOnClickListener(v -> SignUpWithGoogle());
    }

    void SignUpWithGoogle()
    {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
        startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
        finish();
    }

    void SignUpWithEmailAndPassword()
    {
        String enteredEmail = email.getText().toString().trim();
        String enteredPassword = password.getText().toString().trim();
        String enteredRePassword = rePassword.getText().toString().trim();
        String enteredMobile = mobile.getText().toString().trim();
       // Check for entering data
        if (TextUtils.isEmpty(enteredEmail))
        {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(enteredPassword))
        {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(enteredRePassword))
        {
            Toast.makeText(getApplicationContext(), "Re-enter password!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(enteredMobile))
        {
            Toast.makeText(getApplicationContext(), "Enter Mobile!", Toast.LENGTH_SHORT).show();
            return;
        }

        //create user
        if(enteredPassword.equalsIgnoreCase(enteredRePassword))
        {
            auth.createUserWithEmailAndPassword(enteredEmail, enteredPassword)
                    .addOnCompleteListener(SignUpActivity.this, task -> {
                        Toast.makeText(SignUpActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                            finish();
                        }
                    });
        }
        else{
            Toast.makeText(getApplicationContext(), "The passwords are not matched!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
    //Google Authentication
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = auth.getCurrentUser();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

}