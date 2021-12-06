package com.pravinglkp.chatapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    List<String> userList;
    String userName;
    Context mContext;

    FirebaseDatabase database;
    DatabaseReference reference;

    public UserAdapter(List<String> userList, String userName, Context mContext) {
        this.userList = userList;
        this.userName = userName;
        this.mContext = mContext;

        database=FirebaseDatabase.getInstance();
        reference=database.getReference();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card,parent,false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserAdapter.ViewHolder holder, int position) {
        reference.child("Users").child(userList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String otherName=snapshot.child("userName").getValue().toString();
                String imageUrl=snapshot.child("image").getValue().toString();

                holder.textViewUsers.setText(otherName);
                if(imageUrl.equals("null")){
                    holder.imageViewUsers.setImageResource(R.drawable.account);
                }
                else{
                    Picasso.get().load(imageUrl).into(holder.imageViewUsers);
                }

                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext,ChatActivity.class);
                        intent.putExtra("userName",userName);
                        intent.putExtra("otherName",otherName);
                        mContext.startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewUsers;
        private CircleImageView imageViewUsers;
        private CardView cardView;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            textViewUsers =itemView.findViewById(R.id.textViewUsers);
            imageViewUsers =itemView.findViewById(R.id.imageViewUsers);
            cardView =itemView.findViewById(R.id.cardView);
        }
    }
}
