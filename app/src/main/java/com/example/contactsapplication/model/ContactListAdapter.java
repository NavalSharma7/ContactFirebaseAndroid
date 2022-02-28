package com.example.contactsapplication.model;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contactsapplication.EditContactActivity;
import com.example.contactsapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder>{
    ArrayList<ContactInfo> mData;
    String uid;
    Activity activity;
    String name;

    public ContactListAdapter(ArrayList<ContactInfo> mData,String uid,String name,Activity activity) {
        this.mData = mData;
        this.uid = uid;
        this.activity = activity;
        this.name = name;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_contact_item, parent, false);
        ContactListAdapter.ViewHolder viewHolder = new ContactListAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ContactInfo contact = (ContactInfo) mData.get(position);
        holder.txtName.setText(contact.getName());
        holder.txtEmail.setText(contact.getEmail());
        holder.txtPhone.setText(contact.getPhone());
//set image
//        Picasso.with(activity)
//                .load(contact.getImgString())
//                .into(holder.imgViewPersonalAvatar);
        holder.imgDeleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String docId = String.format("%s",mData.get(position).getId());
                DocumentReference contactRef = db.collection("Contacts").document(docId);

                contactRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if(task.isSuccessful()){
                            //value deleted
                            mData.remove(position);
                            notifyDataSetChanged();
                        }else{


                        }
                    }
                });
            }
        });

        holder.imgEditContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
                Intent intent = new Intent(activity, EditContactActivity.class);
                intent.putExtra("UID", uid);
                intent.putExtra("NAME",name);
                intent.putExtra("CONTACT", contact);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        TextView txtPhone;
        TextView txtEmail;
        ImageView imgViewPersonalAvatar;
        ImageView imgEditContact,imgDeleteContact;

        public ViewHolder(final View itemView) {
            super(itemView);

            imgEditContact = (ImageView)itemView.findViewById(R.id.imgEditContact);
            imgDeleteContact = (ImageView)itemView.findViewById(R.id.imgDeleteContact);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtPhone = (TextView) itemView.findViewById(R.id.txtPhone);
            txtEmail = (TextView) itemView.findViewById(R.id.txtEmail);
            imgViewPersonalAvatar = (ImageView) itemView.findViewById(R.id.imgViewPersonalAvatar);
        }
    }

}
