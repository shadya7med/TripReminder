package com.iti.example.tripreminder;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "SignIn";
    EditText email, password;
    Button login, gmail, signupbtn;
    TextView signup;
    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;

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
    }

    public void LoginWithGoogle() {

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
}
 /*   @Override
    public void onStart()
    {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        updateUI(currentUser);
        startActivity(new Intent(LoginActivity.this,HomeActivity.class));
    }
    private void updateUI(FirebaseUser currentUser) { }
}*/
