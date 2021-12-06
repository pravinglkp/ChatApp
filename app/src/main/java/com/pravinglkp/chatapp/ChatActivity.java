package com.pravinglkp.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private ImageView imageViewBack;
    private TextView textViewChat;
    private EditText editTextMessage;
    private FloatingActionButton fab;
    private RecyclerView rvChat;

    String userName,otherName;

    FirebaseDatabase database;
    DatabaseReference reference;

    MessageAdapter adapter;
    List<ModelClass> list;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        imageViewBack =findViewById(R.id.imageViewBack);
        textViewChat=findViewById(R.id.textViewChat);
        editTextMessage=findViewById(R.id.editTextMessage);
        fab=findViewById(R.id.fab);

        list=new ArrayList<>();
        rvChat=findViewById(R.id.rvChat);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        //adapter = new MessageAdapter(list,userName);
        //rvChat.setAdapter(adapter);

        userName=getIntent().getStringExtra("userName");
        otherName=getIntent().getStringExtra("otherName");

        textViewChat.setText(otherName);

        database=FirebaseDatabase.getInstance();
        reference=database.getReference();



        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(ChatActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message=editTextMessage.getText().toString();
                if(!message.equals("")){
                   sendMessage(message);
                    editTextMessage.setText("");
                }
            }
        });

        getMessage();
    }
    void sendMessage(String message){
        final String key=reference.child("Messages").child(userName).child(otherName).push().getKey();
        final Map<String,Object> messageMap = new HashMap<>();
        messageMap.put("message",message);
        messageMap.put("from",userName);

        reference.child("Messages").child(userName).child(otherName).child(key).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NotNull Task<Void> task) {
                if(task.isSuccessful()){
                    reference.child("Messages").child(otherName).child(userName)
                            .child(key).setValue(messageMap);
                }
            }
        });
    }

    public void getMessage(){

        reference.child("Messages").child(userName).child(otherName)
                .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                ModelClass modelClass = snapshot.getValue(ModelClass.class);
                list.add(modelClass);
                adapter.notifyDataSetChanged();
                rvChat.scrollToPosition(list.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        adapter = new MessageAdapter(list,userName);
        rvChat.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }
}