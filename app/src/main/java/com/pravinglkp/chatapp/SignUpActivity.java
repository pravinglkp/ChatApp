package com.pravinglkp.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {


    private CircleImageView imageView;
    private TextInputEditText editTextEmailSignUp,editTextPasswordSignup,editTextUsernameSignup;
    private Button buttonRegister;


    FirebaseAuth auth;

    FirebaseDatabase database;
    DatabaseReference reference;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    Uri imageUri;
    boolean imageControl=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        imageView=findViewById(R.id.circleImageView);
        editTextEmailSignUp=findViewById(R.id.editTextEmailSignup);
        editTextPasswordSignup=findViewById(R.id.editTextPasswordSignup);
        editTextUsernameSignup=findViewById(R.id.editTextUsernameSignup);

        buttonRegister =findViewById(R.id.buttonRegister);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        reference=database.getReference();

        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email,password,username;
                email=editTextEmailSignUp.getText().toString();
                password=editTextPasswordSignup.getText().toString();
                username=editTextUsernameSignup.getText().toString();

                if(!email.equals("") && !password.equals("") && !username.equals("")){
                    signUp(email,password,username);
                }

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
            Picasso.get().load(imageUri).into(imageView);
            imageControl=true;
        }
        else{
            imageControl=false;
        }
    }

    public void signUp(String email,String password,String username){
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d("m","SignUp Success");
                    reference.child("Users").child(auth.getUid()).child("userName").setValue(username);
                    if(imageControl){
                        Log.d("m","Image Avaialble");
                        UUID randomId=UUID.randomUUID();
                        final String imageName="images/"+randomId+".jpg";
                        storageReference.child(imageName).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.d("m","Image Uploaded to Storage");
                                StorageReference myStorageRef=firebaseStorage.getReference(imageName);
                                myStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Log.d("m","URI Fetched");
                                        Toast.makeText(SignUpActivity.this,"Write to storage is successfull",Toast.LENGTH_SHORT).show();
                                        String filePath=uri.toString();
                                        reference.child("Users").child(auth.getUid()).child("image")
                                                .setValue(filePath).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d("m","URI Connected to DB");
                                                Toast.makeText(SignUpActivity.this,"Write to database is successfull",Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                           // Log.d("m","URI not Connected to DB");
                                            @Override
                                            public void onFailure(@NonNull @NotNull Exception e) {
                                                Toast.makeText(SignUpActivity.this,"Write to database is not successfull",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    //Log.d("m","URI not Fetched");
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        Toast.makeText(SignUpActivity.this,"Write storage is not successfull",Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        });
                    }
                    else{
                        Log.d("m","Image Not Avaialble");
                        reference.child("Users").child(auth.getUid()).child("image").setValue("null");
                    }

                    Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(SignUpActivity.this,"There is A Problem",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}









