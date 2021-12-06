package com.pravinglkp.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView imageViewCircleProfile;
    private TextInputEditText editTextUserNameProfile;
    private Button buttonUpdate;
    String image;


    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    Uri imageUri;
    boolean imageControl=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        imageViewCircleProfile=findViewById(R.id.imageViewCircleProfile);
        editTextUserNameProfile=findViewById(R.id.editTextUsernameProfile);
        buttonUpdate=findViewById(R.id.buttonUpdate);

        database = FirebaseDatabase.getInstance();
        reference=database.getReference();
        auth=FirebaseAuth.getInstance();
        firebaseUser=auth.getCurrentUser();

        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();
        getUserInfo();

        imageViewCircleProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });


    }

    void updateProfile(){
        String userName=editTextUserNameProfile.getText().toString();
        reference.child("Users").child(firebaseUser.getUid()).child("userName").setValue(userName);
        if(imageControl){
            UUID randomId=UUID.randomUUID();
            String imageName="images/"+randomId+".jpg";
            storageReference.child("imageName").putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    StorageReference myStorageRef=firebaseStorage.getReference(imageName);
                    myStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String filePath=uri.toString();
                            reference.child("Users").child(auth.getUid()).child("image").setValue(filePath).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(ProfileActivity.this,"Write to database is successfull",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    Toast.makeText(ProfileActivity.this,"Write to database is not successfull",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            });
        }
        else{
            reference.child("Users").child(auth.getUid()).child("image").setValue(image);
        }

        Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
        intent.putExtra("userName",userName);
        startActivity(intent);
        finish();
    }
    void getUserInfo(){
        reference.child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String name=snapshot.child("userName").getValue().toString();
                image=snapshot.child("image").getValue().toString();
                editTextUserNameProfile.setText(name);
                if(image.equals("null")){
                    imageViewCircleProfile.setImageResource(R.drawable.account);
                }
                else{
                    Picasso.get().load(image).into(imageViewCircleProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    public void imageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null){

            imageUri=data.getData();
            Picasso.get().load(imageUri).into(imageViewCircleProfile);
            imageControl=true;
        }
        else{
            imageControl=false;
        }
    }
}