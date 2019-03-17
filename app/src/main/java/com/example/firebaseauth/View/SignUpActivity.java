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

import com.example.firebaseauth.Model.UserInformation;
import com.example.firebaseauth.R;
import com.example.firebaseauth.ViewModel.InternetConnectionChecker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private InternetConnectionChecker connectionChecker;
    private FirebaseAuth mAuth;

    private ProgressBar progressBar;

    private TextInputLayout editTextName, editTextEmail, editTextPassword;
    private Button buttonSignUp;
    private TextView textViewSignIn;
    private String nameText, emailText, passwordText;
    private boolean nameFlag = false, emailFlag = false, passwordFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        connectionChecker = new InternetConnectionChecker(this);
        mAuth = FirebaseAuth.getInstance();

        progressBar = (ProgressBar) findViewById(R.id.progressBar_circular);

        editTextName = findViewById(R.id.editText_Name);
        editTextEmail = findViewById(R.id.editText_Email);
        editTextPassword = findViewById(R.id.editText_Password);
        buttonSignUp = (Button) findViewById(R.id.button_SignUp);
        textViewSignIn = (TextView) findViewById(R.id.textView_SignIn);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameText = editTextName.getEditText().getText().toString();
                emailText = editTextEmail.getEditText().getText().toString();
                passwordText = editTextPassword.getEditText().getText().toString();

                if (connectionChecker.isConnected()) {
                    //Check if any input left blank
                    nameInputChecker();
                    emailInputChecker();
                    passwordInputChecker();
                    if (nameFlag == true && emailFlag == true && passwordFlag == true) {
                        progressBar.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    editTextName.getEditText().setText("");
                                    editTextEmail.getEditText().setText("");
                                    editTextPassword.getEditText().setText("");
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "User Registration Successful!", Toast.LENGTH_SHORT).show();

                                    UserInformation userInformation = new UserInformation(nameText, emailText);
                                    FirebaseDatabase.getInstance().getReference("User info")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(userInformation)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getApplicationContext(), "User information's saved to online database!", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "User information's save failure!!!", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                } else {
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        Toast.makeText(getApplicationContext(), "This Email is already registered!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Internet Connection Required!", Toast.LENGTH_LONG).show();
                }
            }
        });

        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            }
        });
    }

    public void nameInputChecker() {
        if (nameText.isEmpty()) {
            editTextName.setError("Name must be entered!");
        } else if (nameText.length() < 3) {
            editTextName.setError("Name length should be at least 3 characters!");
        } else {
            editTextName.setError(null);
            nameFlag = true;
        }
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