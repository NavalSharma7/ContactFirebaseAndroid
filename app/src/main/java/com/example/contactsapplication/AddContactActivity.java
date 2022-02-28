package com.example.contactsapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.contactsapplication.model.ContactInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class AddContactActivity extends AppCompatActivity {

    ImageView imgEditContactImage;
    EditText edtEditName;
    EditText edtEditEmail;
    EditText edtEditPhone;

    String UID ;
    String name;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    String uri;
    final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);



        imgEditContactImage = (ImageView) findViewById(R.id.imgContactImage);
        edtEditName = (EditText)findViewById(R.id.edtContactName);
        edtEditEmail = (EditText)findViewById(R.id.edtContactEmail);
        edtEditPhone = (EditText)findViewById(R.id.edtContactPhone);

        if(getIntent().getExtras()!=null){
            UID = getIntent().getExtras().getString("UID");
            name = getIntent().getExtras().getString("NAME");
        }

        DocumentReference rootRef = FirebaseFirestore.getInstance().collection("Contacts").document();
       // final DatabaseReference userRf = rootRef.(UID);

        final Bitmap bitmap1 = ((BitmapDrawable)imgEditContactImage.getDrawable()).getBitmap();

        findViewById(R.id.btnContactAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bitmap bitmap2 = ((BitmapDrawable)imgEditContactImage.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap2.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);

                if(edtEditName.getText().toString().equals("") || edtEditEmail.getText().toString().equals("")|| edtEditPhone.equals("")){
                    Toast.makeText(AddContactActivity.this, "All inputs are mandatory", Toast.LENGTH_SHORT).show();
                }
                else if(!edtEditEmail.getText().toString().matches(EMAIL_PATTERN))
                {
                    Toast.makeText(AddContactActivity.this, "Email should be in correct format", Toast.LENGTH_SHORT).show();
                }
                else if (edtEditPhone.length() < 10)
                {
                    Toast.makeText(AddContactActivity.this, "Phone should be 10 character", Toast.LENGTH_SHORT).show();
                }
                else {


                    String uuid = UUID.randomUUID() + "";


                    imgEditContactImage.setDrawingCacheEnabled(true);
                    imgEditContactImage.buildDrawingCache();
                    Bitmap bitmap = imgEditContactImage.getDrawingCache();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    imgEditContactImage.setDrawingCacheEnabled(false);
                    byte[] data = baos.toByteArray();

                    final String path = "users/" + UID + "/" + "contacts/" + uuid + ".png";
                    StorageReference firememesRef = storage.getReference(path);


                    UploadTask uploadTask = firememesRef.putBytes(data);
                    uploadTask.addOnSuccessListener(AddContactActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                            uri = downloadUrl.toString();
                            ContactInfo contact = new ContactInfo(edtEditName.getText().toString(), edtEditEmail.getText().toString(), edtEditPhone.getText().toString(), uri, path);

                            rootRef.set(contact).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(Task<Void> task) {
                                    Toast.makeText(AddContactActivity.this, "Contact added", Toast.LENGTH_SHORT).show();

                                }
                            });

                            Intent intent = new Intent(AddContactActivity.this, MainActivity.class);
                            intent.putExtra("UID", UID);
                            intent.putExtra("NAME", name);
                            finish();
                            startActivity(intent);
                        }
                    });
                }


            }
        });

        findViewById(R.id.btnContactCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(AddContactActivity.this, MainActivity.class);
                intent.putExtra("UID", UID);
                intent.putExtra("NAME",name);
                startActivity(intent);
            }
        });

        imgEditContactImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence[] items = {"Camara","Gallery"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(AddContactActivity.this);
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 0:
                if(resultCode == AddContactActivity.RESULT_OK){
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    imgEditContactImage.setImageBitmap(Bitmap.createScaledBitmap(photo, 600, 600, false));
                }

                break;
            case 1:
                if(resultCode == AddContactActivity.RESULT_OK){
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    imgEditContactImage.setImageBitmap(Bitmap.createScaledBitmap(photo, 600, 600, false));
                }
                break;
        }
    }
}