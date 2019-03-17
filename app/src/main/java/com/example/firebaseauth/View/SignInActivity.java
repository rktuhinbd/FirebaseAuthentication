package com.example.firebaseauth.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebaseauth.R;
import com.example.firebaseauth.ViewModel.InternetConnectionChecker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    private InternetConnectionChecker connectionChecker;
    private FirebaseAuth mAuth;

    private ProgressBar progressBar;

    private TextInputLayout editTextEmail, editTextPassword;
    private Button buttonSignIn;
    private TextView textViewSignUp;
    private String emailText, passwordText;
    private boolean emailFlag = false, passwordFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        connectionChecker = new InternetConnectionChecker(this);
        mAuth = FirebaseAuth.getInstance();

        progressBar = (ProgressBar) findViewById(R.id.progressBar_circular);

        editTextEmail = findViewById(R.id.editText_Email);
        editTextPassword = findViewById(R.id.editText_Password);
        buttonSignIn = (Button) findViewById(R.id.button_SignIn);
        textViewSignUp = (TextView) findViewById(R.id.textView_SignUp);

        //Set login button Action
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                emailText = editTextEmail.getEditText().getText().toString();
                passwordText = editTextPassword.getEditText().getText().toString();

                if (connectionChecker.isConnected()) {
                    //Check if any input left blank
                    emailInputChecker();
                    passwordInputChecker();
                    if (emailFlag == true && passwordFlag == true) {
                        progressBar.setVisibility(View.VISIBLE);
                        mAuth.signInWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    editTextEmail.getEditText().setText("");
                                    editTextPassword.getEditText().setText("");

                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Sign in successful!", Toast.LENGTH_SHORT).show();
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Internet Connection Required!", Toast.LENGTH_LONG).show();
                }
            }
        });

        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            }
        });
    }

    public void emailInputChecker() {
        if (emailText.isEmpty()) {
            editTextEmail.setError("Email must be entered!");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            editTextEmail.setError("Invalid email address!");
        } else {
            editTextEmail.setError(null);
            emailFlag = true;
        }
    }

    public void passwordInputChecker() {
        if (passwordText.isEmpty()) {
            editTextPassword.setError("Password must be entered!");
        } else if (passwordText.length() < 6) {
            editTextPassword.setError("Password length should be at least 6 characters!");
        } else {
            editTextPassword.setError(null);
            passwordFlag = true;
        }
    }
}
