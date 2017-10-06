package com.buahbatu.okarta;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableInt;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import com.buahbatu.okarta.databinding.ActivityRegisterBinding;
import com.buahbatu.okarta.model.User;
import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class RegisterActivity extends AppCompatActivity {
    private final int REQUEST_CODE_PICKER = 100;

    private ActivityRegisterBinding registerBinding;
    private Image selectedImage = null;
    public ObservableInt progressBarVisibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressBarVisibility = new ObservableInt(View.GONE);

        registerBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        registerBinding.setRegister(this);
    }

    public void onImageSelectClick(){
        ImagePicker.create(this)
                .returnAfterFirst(true) // set whether pick or camera action should return immediate result or not. For pick selectedImage only work on single mode
                .folderMode(true) // folder mode (false by default)
//                .folderTitle("Folder") // folder selection title
                .imageTitle("Tap to select") // selectedImage selection title
                .single() // single mode
//                .multi() // multi mode (default mode)
//                .limit(10) // max images can be selected (99 by default)
                .showCamera(true) // show camera or not (true by default)
                .imageDirectory("Camera") // directory name for captured selectedImage  ("Camera" folder by default)
                .enableLog(false) // disabling log
                .start(REQUEST_CODE_PICKER); // start selectedImage picker activity with request code
    }

    public void onRegisterClick(){
        TextInputLayout[] inputLayout = new TextInputLayout[]{
                registerBinding.textUserName,
                registerBinding.textUserPassword,
                registerBinding.textFullName,
                registerBinding.textType,
                registerBinding.textPlat,
                registerBinding.textSensorId
        };

        // clear error
        for (TextInputLayout input : inputLayout){
            input.setError(null);
        }

        boolean anyEmptyInput = false;
        for (TextInputLayout input : inputLayout){
            if (input.getEditText() != null && TextUtils.isEmpty(input.getEditText().getText())){
                input.setError("Please Fill");
                anyEmptyInput = true;
                break;
            }
        }

        if (!anyEmptyInput && selectedImage == null){
            Toast.makeText(this, "Please Select Image", Toast.LENGTH_SHORT).show();
            anyEmptyInput = true;
        }

        if (!anyEmptyInput){
            String username = registerBinding.textUserName.getEditText() == null ?
                    "" : registerBinding.textUserName.getEditText().getText().toString();

            String password = registerBinding.textUserPassword.getEditText() == null ?
                    "" : registerBinding.textUserPassword.getEditText().getText().toString();

            progressBarVisibility.set(View.VISIBLE);
            registerData(username, password);
        }
    }

    private void registerData(final String username, String password){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(
                String.format(getString(R.string.email_format), username), password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            uploadData(username);
                            uploadImage(username);

                            // registration complete
                            // finish();
                        }else {
//                            System.out.println(task.getResult().toString());
                            Toast.makeText(RegisterActivity.this,
                                    R.string.auth_failed, Toast.LENGTH_SHORT).show();
                            progressBarVisibility.set(View.GONE);
                        }
                    }
                });
    }

    private void uploadData(String username){
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users/"+username);

        String fullName = registerBinding.textFullName.getEditText() == null ?
                "" : registerBinding.textFullName.getEditText().getText().toString();
        String carType = registerBinding.textType.getEditText() == null ?
                "" : registerBinding.textType.getEditText().getText().toString();
        String carPlat = registerBinding.textPlat.getEditText() == null ?
                "" : registerBinding.textPlat.getEditText().getText().toString();
        String sensorId = registerBinding.textSensorId.getEditText() == null ?
                "" : registerBinding.textSensorId.getEditText().getText().toString();

        myRef.setValue(new User(username, fullName, carType, carPlat, sensorId));
    }

    private void uploadImage(final String username){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        Uri file = Uri.fromFile(new File(selectedImage.getPath()));
        StorageReference riversRef = storageRef.child(String.format("images/%s.jpg", username));

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // registration complete
                        progressBarVisibility.set(View.GONE);
                        Intent data = new Intent();
                        data.putExtra("username", username);
                        setResult(RESULT_OK, data);
                        finish();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICKER && resultCode == RESULT_OK && data != null) {
            selectedImage = ImagePicker.getImages(data).get(0);
            Glide.with(this).load(new File(selectedImage.getPath())).into(registerBinding.buttonSelectImage);
        }
    }
}
