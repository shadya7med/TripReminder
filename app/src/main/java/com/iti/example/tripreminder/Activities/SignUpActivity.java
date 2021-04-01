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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.iti.example.tripreminder.R;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "GoogleSignIn";
    private static final int GOOGLE_SIGN_IN = 123;
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
                else if (s.length() < password.getCounterMaxLength()-2)   password.setError("Min character length is " + (password.getCounterMaxLength()-2));
                else password.setError(null);
            }
        });
        /* *********************************************************************************** */
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
    }

    void SignUpWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
        startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
        finish();
    }

    void SignUpWithEmailAndPassword() {
        String enteredEmail = emailEditText.toString().trim();
        String enteredPassword = passwordEditTest.toString().trim();
        String enteredRePassword = rePasswordEditText.toString().trim();

        // Check for entering data
       /* if (TextUtils.isEmpty(enteredEmail)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(enteredPassword)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(enteredRePassword)) {
            Toast.makeText(getApplicationContext(), "Re-enter password!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(enteredMobile)) {
            Toast.makeText(getApplicationContext(), "Enter Mobile!", Toast.LENGTH_SHORT).show();
            return;
        }*/

        //create user
        if (enteredPassword.equalsIgnoreCase(enteredRePassword)) {
            auth.createUserWithEmailAndPassword(enteredEmail, enteredPassword)
                    .addOnCompleteListener(SignUpActivity.this, task -> {
                        Toast.makeText(SignUpActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Wrong email or password." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                            finish();
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "The passwords are not matched!", Toast.LENGTH_SHORT).show();
        }
    }
}