package com.example.contactsapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.contactsapplication.model.ContactInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class EditContactActivity extends AppCompatActivity {
    ImageView imgEditContactImage;
    EditText edtEditName;
    EditText edtEditEmail;
    EditText edtEditPhone;

    String uid,name;
    final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ContactInfo contact;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        if(getIntent().getExtras()!=null){
            uid = getIntent().getExtras().getString("UID");
            name = getIntent().getExtras().getString("NAME");
            contact = (ContactInfo) getIntent().getExtras().getSerializable("CONTACT");
        }


        imgEditContactImage = (ImageView) findViewById(R.id.imgEditContactImage);
        edtEditName = (EditText)findViewById(R.id.edtEditContactName);
        edtEditEmail = (EditText)findViewById(R.id.edtEditContactEmail);
        edtEditPhone = (EditText)findViewById(R.id.edtEditContactPhone);


        edtEditName.setText(contact.getName());
        edtEditPhone.setText(contact.getPhone());
        edtEditEmail.setText(contact.getEmail());

//        Picasso.with(this)
//                .load(contact.getImgString())
//                .into(imgEditContactImage);

        findViewById(R.id.btnEditContactCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditContactActivity.this,MainActivity.class);
                intent.putExtra("UID",uid);
                intent.putExtra("NAME",name);
                finish();
                startActivity(intent);
            }
        });


        findViewById(R.id.imgEditContactImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence[] items = {"Camara","Gallery"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(EditContactActivity.this);
                dialog.setTitle("Choose Option").setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(takePicture, 0);
                        }
                        else{
                            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            getIntent.setType("image/*");

                            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            pickIntent.setType("image/*");

                            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                            startActivityForResult(chooserIntent, 1);
                        }
                    }
                });

                dialog.show();
            }
        });


        findViewById(R.id.btnEditContactUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edtEditName.getText().toString().equals("") || edtEditEmail.getText().toString().equals("")|| edtEditPhone.equals("")){
                    Toast.makeText(EditContactActivity.this, "All inputs are mandatory", Toast.LENGTH_SHORT).show();
                }
                else if(!edtEditEmail.getText().toString().matches(EMAIL_PATTERN))
                {
                    Toast.makeText(EditContactActivity.this, "Email should be in correct format", Toast.LENGTH_SHORT).show();
                }
                else if (edtEditPhone.length() < 10)
                {
                    Toast.makeText(EditContactActivity.this, "Phone should be 10 character", Toast.LENGTH_SHORT).show();
                }
                else {
                    imgEditContactImage.setDrawingCacheEnabled(true);
                    imgEditContactImage.buildDrawingCache();
                    Bitmap bitmap = imgEditContactImage.getDrawingCache();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    imgEditContactImage.setDrawingCacheEnabled(false);
                    byte[] data = baos.toByteArray();


                    final String path = contact.getPath();
                    StorageReference firememesRef = storage.getReference(path);

                    UploadTask uploadTask = firememesRef.putBytes(data);

                    uploadTask.addOnSuccessListener(EditContactActivity.this,new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                            String uri = downloadUrl.toString();

                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                            DatabaseReference userRf = rootRef.child(uid);
                            DatabaseReference contactsRef = userRf.child("Contacts");
                            DatabaseReference contactRef = contactsRef.child(contact.getName());
                            contactRef.removeValue();
                            DatabaseReference contactRef1 = contactsRef.child(edtEditName.getText().toString());
                            ContactInfo contactUpdate = new ContactInfo(edtEditName.getText().toString(),edtEditEmail.getText().toString(),edtEditPhone.getText().toString(),uri,contact.getPath());
                            contactRef1.setValue(contactUpdate);

                            Intent intent = new Intent(EditContactActivity.this,MainActivity.class);
                            intent.putExtra("UID",uid);
                            intent.putExtra("NAME",name);
                            finish();
                            startActivity(intent);
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        switch(requestCode) {
//            case 0:
//                if(resultCode == SignUp.RESULT_OK){
//                    Bitmap photo = (Bitmap) data.getExtras().get("data");
//                    ((ImageView)findViewById(R.id.imgEditContactImage)).setImageBitmap(Bitmap.createScaledBitmap(photo, 600, 600, false));
//                }
//
//                break;
//            case 1:
//                if(resultCode == SignUp.RESULT_OK){
//                    Bitmap photo = (Bitmap) data.getExtras().get("data");
//                    ((ImageView)findViewById(R.id.imgEditContactImage)).setImageBitmap(Bitmap.createScaledBitmap(photo, 600, 600, false));
//                }
//                break;
//        }
    }

}
