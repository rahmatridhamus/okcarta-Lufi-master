package com.buahbatu.okarta;

import android.content.Context;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.buahbatu.okarta.databinding.ActivityMainBinding;
import com.buahbatu.okarta.model.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;

    public ObservableBoolean loadingVisibility;
    public ObservableField<String> fullName;
    public ObservableField<String> carType;
    public ObservableField<String> carPlat;
    public ObservableField<String> sensorId;
    public ObservableField<String> imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingVisibility = new ObservableBoolean(true);
        fullName = new ObservableField<>();
        carType = new ObservableField<>();
        carPlat = new ObservableField<>();
        sensorId = new ObservableField<>();
        imageUrl = new ObservableField<>();

        String email = getIntent().getStringExtra("email");

        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainBinding.setMain(this);

        loadUserData(email);
    }

    private void loadUserData(String email){
        String username = email.split("@")[0]; // get username from email

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users/"+username);

        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User value = dataSnapshot.getValue(User.class);
                fullName.set(value.fullName.toUpperCase());
                carType.set(value.carType);
                carPlat.set(value.carPlat);
                sensorId.set(value.sensorId);
                loadingVisibility.set(false);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        FirebaseStorage.getInstance().getReference("images/"+username+".jpg").getDownloadUrl()
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        imageUrl.set(task.getResult().toString());
                    }
                });
    }

    public void moveToDetailActivity(String attribute){
        Intent moving = new Intent(MainActivity.this, DetailActivity.class);
        moving.putExtra("sensor_id", sensorId.get());
        moving.putExtra("attribute", attribute);
        startActivity(moving);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout_button:
                FirebaseAuth.getInstance().signOut();
                // return to login
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @BindingAdapter("imageUrl")
    public static void setImageUrl(ImageView imageView, String url) {
        Context context = imageView.getContext();
        Glide.with(context).load(url).into(imageView);
    }
}
