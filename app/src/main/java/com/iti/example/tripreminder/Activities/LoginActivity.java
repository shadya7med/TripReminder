package com.iti.example.tripreminder.Activities;
/* ******************** *
 * Author: We'am  Kamal *
 * Date : 19th Mar 2021 *
 * ******************** */
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.iti.example.tripreminder.R;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "SignIn";
    private static final int GOOGLE_SIGN_IN = 123;
    EditText email, password;
    Button login, gmail, signupbtn;
    TextView signup;
    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;
    FirebaseAuth.AuthStateListener authListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.edtTxt_email_login);
        password = findViewById(R.id.edtTxt_password_login);
        login = findViewById(R.id.btn_LogIn_login);
        gmail = findViewById(R.id.btn_google_login);
        signup = findViewById(R.id.txt_signup_login);

        /*Button OnClick Actions*/
        login.setOnClickListener(v -> LoginWithEmailAndPassword());
        gmail.setOnClickListener(v -> LoginWithGoogle());
        signup.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));
        /*Check if the Firebase user is not/null even to login or goto home directly*/
        authListener = firebaseAuth -> {
                FirebaseUser user1 = firebaseAuth.getCurrentUser();
                if (user1 != null) {
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                }
            };
    }
    public void LoginWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        finish();
    }

    public void LoginWithEmailAndPassword() {
        String enteredEmail = email.getText().toString().trim();
        String enteredPassword = password.getText().toString().trim();

        if (TextUtils.isEmpty(enteredEmail)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(enteredPassword)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }
        auth.signInWithEmailAndPassword(enteredEmail, enteredPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
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
                Toast.makeText(getApplicationContext(), "Sign in with gmail succeed", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(LoginActivity.this, "Authentication failed." + task.getException(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}
