package com.buahbatu.okarta;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.buahbatu.okarta.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    public ObservableInt progressBarVisibility;

    private ActivityLoginBinding loginBinding;
    private final int REGISTER_CODE = 101;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressBarVisibility = new ObservableInt(View.GONE);
        loginBinding = DataBindingUtil
                .setContentView(this, R.layout.activity_login);
        loginBinding.setLogin(this);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            moveToMain(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }
    }

    public void onRegisterClick() {
        System.out.println("clicked");
        Intent moveToRegister = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivityForResult(moveToRegister, REGISTER_CODE);
    }

    public void onLoginClick() {
        final String username = loginBinding.textUserName.getEditText() == null ?
                "" : loginBinding.textUserName.getEditText().getText().toString();

        String password = loginBinding.textUserPassword.getEditText() == null ?
                "" : loginBinding.textUserPassword.getEditText().getText().toString();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(String.format(getString(R.string.email_format), username), password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            moveToMain(String.format(getString(R.string.email_format), username));
                        }
                    }
                });
    }

    private void moveToMain(String email) {
        Intent moveToMain = new Intent(LoginActivity.this, MainActivity.class);
        moveToMain.putExtra("email", email);
        startActivity(moveToMain);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REGISTER_CODE && resultCode == RESULT_OK) {
            String username = data.getStringExtra("username");
            moveToMain(String.format(getString(R.string.email_format), username));
        }
    }
}
