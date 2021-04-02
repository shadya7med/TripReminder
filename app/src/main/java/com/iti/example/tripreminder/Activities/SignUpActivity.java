package com.iti.example.tripreminder.Activities;
/* ******************** *
 * Author: We'am Kamal  *
 * Date : 19th Mar 2021 *
 * ******************** */

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.iti.example.tripreminder.R;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "GoogleSignIn";
    private static final int RC_SIGN_IN = 9001;

    TextInputLayout email, password, rePassword, mobile;
    TextInputEditText emailEditText,passwordEditTest,rePasswordEditText,mobileEditText;
    Button Signup, Gmail;
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
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        auth = FirebaseAuth.getInstance();
        /*Views Declaration*/
        email = findViewById(R.id.Txt_email_signUp);
        password = findViewById(R.id.Txt_password_signUp);
        rePassword = findViewById(R.id.Txt_rePassword_signUp);
        mobile = findViewById(R.id.Txt_mobile_signUp);
        Signup = findViewById(R.id.btn_SignUp_signUp);
        Gmail = findViewById(R.id.btn_google_singUp);
        emailEditText = findViewById(R.id.edt_email_signUp);
        passwordEditTest = findViewById(R.id.edt_password_signUp);
        rePasswordEditText = findViewById(R.id.edt_rePassword_signUp);
        mobileEditText = findViewById(R.id.edt_mobile_signUp);
        /*On Buttons Click Actions*/
        Signup.setOnClickListener(v -> SignUpWithEmailAndPassword());
        Gmail.setOnClickListener(v -> SignUpWithGoogle());

        /*  ***************************************************************************
         *  ********************* Email & PW Verification *****************************
         *  *************************************************************************** */
        emailEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.length() == 0)   email.setError("Email address Required");
                else email.setError(null);
            }
        });
        /* ************************************************************************************** */
        passwordEditTest.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.length() > password.getCounterMaxLength())   password.setError("Max character length is " + password.getCounterMaxLength());
                else if (s.length() < password.getCounterMaxLength()-4)   password.setError("Min character length is " + (password.getCounterMaxLength()-4));
                else password.setError(null);
            }
        });
        /* *********************************************************************************** */
        rePasswordEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.length() > rePassword.getCounterMaxLength())   rePassword.setError("Max character length is " + password.getCounterMaxLength());
                else if (s.length() < rePassword.getCounterMaxLength()-2)   rePassword.setError("Min character length is " + (password.getCounterMaxLength()-2));
                else rePassword.setError(null);
            }
        });
    }

    void SignUpWithGoogle()
    {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    void SignUpWithEmailAndPassword()
    {
        String enteredEmail = emailEditText.getText().toString().trim();
        String enteredPassword = passwordEditTest.getText().toString().trim();
        String reEnteredPassword = rePasswordEditText.getText().toString().trim();
        String enteredMobile = mobileEditText.getText().toString().trim();

        // Check for entering data
        if (TextUtils.isEmpty(enteredEmail)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(enteredPassword)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(reEnteredPassword)) {
            Toast.makeText(getApplicationContext(), "Re-enter password!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(enteredMobile)) {
            Toast.makeText(getApplicationContext(), "Enter Mobile!", Toast.LENGTH_SHORT).show();
            return;
        }

        //create user
        if (enteredPassword.equals(reEnteredPassword))
        {
            auth.createUserWithEmailAndPassword(enteredEmail,enteredPassword)
                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this, "User Signed up please Login", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = auth.getCurrentUser();
                                //Intent openLoginIntent =  new Intent(SignUpActivity.this,LoginActivity.class);
                                //startActivity(openLoginIntent);
                                finish();

                            }else{
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                emailEditText.setText("");
                                passwordEditTest.setText("");
                                rePasswordEditText.setText("");

                            }
                        }
                    });


        }
        else
        {
            Toast.makeText(getApplicationContext(), "The passwords are not matched!", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try   // Google Sign In was successful, authenticate with Firebase
            {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                Toast.makeText(getApplicationContext(), "Sign in with gmail succeed", Toast.LENGTH_SHORT).show();
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);  // Google Sign In failed, update UI appropriately
            }

        }

    }
    //Google Authentication
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential).addOnCompleteListener(this, task ->
        {
            if (task.isSuccessful())   // Sign in success, update UI with the signed-in user's information
            {
                Log.d(TAG, "signInWithCredential:success");
                FirebaseUser user = auth.getCurrentUser();
                finish();
            } else     // If sign in fails, display a message to the user.
            {
                Log.w(TAG, "signInWithCredential:failure", task.getException());
                Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}