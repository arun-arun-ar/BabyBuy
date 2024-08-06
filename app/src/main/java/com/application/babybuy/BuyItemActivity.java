package com.application.babybuy;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class BuyItemActivity extends AppCompatActivity implements LocationListener {
    private ImageButton arrow_back_signup, imgLocation;
    private TextView img_label, title_label;
    private ImageView imageView, browse_img;
    private Button save_btn;
    private AppCompatTextView txtLocation;
    private EditText title_input, description_input, price_input;

    private CheckBox purchased_checkbox;

    private Location location;
    private LocationManager locationManager;

    Uri filepath = null;
    Bitmap bitmap;

    private String userId;
    private FirebaseUser user;
    private DatabaseReference reference;

    final String[] thisuri = new String[1];

    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    private AppCompatTextView date_input;
    private androidx.appcompat.widget.AppCompatImageButton add_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.babybuy_add_product_activity);

//      Firebase initialization (Database)
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userId = user.getUid();


//      View variable mapping

        imageView = findViewById(R.id.imageView);
        img_label = findViewById(R.id.img_label);
        title_label = findViewById(R.id.title_label);
        purchased_checkbox = findViewById(R.id.purchased_checkbox);
        price_input = findViewById(R.id.price_input);
        txtLocation = findViewById(R.id.location);
        imgLocation = findViewById(R.id.addLocation);
        title_input = findViewById(R.id.title_input);
        description_input = findViewById(R.id.description_input);
        save_btn = findViewById(R.id.save_btn);



//      to browse pictures
        imageView.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, imageView);
            popupMenu.inflate(R.menu.menue_camera);

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.camera:
                        checkAndRequestPermissions();
                        break;

                    case R.id.gallery:
                        browseImage();
                        break;
                }
                return false;
            });
            popupMenu.show();
        });


//      image location button
        imgLocation.setOnClickListener(v -> {
            System.out.println("clicked");
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            }
            accessLocationService();
            if (location != null) {
                txtLocation.setText("lat: " + location.getLatitude() + ",\nlng: " + location.getLongitude());
            }
        });


          // Receiving product details and display
        BuyItem edit_item = (BuyItem) getIntent().getSerializableExtra("EDIT");

        if (edit_item != null) {
            title_label.setVisibility(View.GONE);
            title_input.setText(edit_item.getTitle());
            purchased_checkbox.setChecked(edit_item.isIs_purchased());
            description_input.setText(edit_item.getDescription());
            price_input.setText(edit_item.getPrice());
            txtLocation.setText("lat: " + edit_item.getLat() + ",\nlng: " + edit_item.getLng());



            if (edit_item.getPimage() != null) {
                Glide.with(this).load(edit_item.getPimage()).into(imageView);
            }
        }

        // save button for products
        save_btn.setOnClickListener(v -> {
            if (filepath != null) {
                fileUpload();
            } else {
                addBuyItem();
            }
        });


//      to input data
        addDate();

    }


    // Helper functions

    private void addDate(){
        date_input = findViewById(R.id.date_input);
        date_input.setText(formatter.format(calendar.getTime()));


        // calender
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hr = calendar.get(Calendar.HOUR);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);

        DatePickerDialog dialog = new DatePickerDialog(BuyItemActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int yr, int monthOfYear, int day) {
                int mm = monthOfYear + 1;
                date_input.setText(day + "-" + mm + "-" + yr + " " + hr + ":" + min + ":" + sec);
            }
        }, year, month, day);

        add_date = findViewById(R.id.add_date);
        add_date.setOnClickListener(v -> {
            dialog.show();
        });
    }


    private void addBuyItem() {

        BuyItemDAO dao = new BuyItemDAO();
        BuyItem edit_item = (BuyItem) getIntent().getSerializableExtra("EDIT");

        final String[] templocation = new String[2];

        if (location != null) {
            templocation[0] = String.valueOf(location.getLatitude());
            templocation[1] = String.valueOf(location.getLongitude());
        }

        if (title_input.getText().toString().isEmpty()) {
            title_input.setError("Title is required !");
            title_input.requestFocus();
            return;
        }


        BuyItem obj = new BuyItem(
                userId,
                title_input.getText().toString(),
                description_input.getText().toString(),
                price_input.getText().toString(),
                purchased_checkbox.isChecked(),
                templocation[0],
                templocation[1],
                thisuri[0],
                date_input.getText().toString(),
                ""
            );



        if (edit_item == null) {
            dao.add(obj).addOnSuccessListener(suc -> {
                Toast.makeText(BuyItemActivity.this, "Inserted ", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(er -> {
                Toast.makeText(BuyItemActivity.this, "" + er.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }

        if (edit_item != null) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("title", title_input.getText().toString());
            hashMap.put("description", description_input.getText().toString());

            hashMap.put("price", price_input.getText().toString());
            hashMap.put("is_purchased", purchased_checkbox.isChecked());

            if (templocation[0] != null) {
                hashMap.put("lat", templocation[0]);
                hashMap.put("lng", templocation[1]);
            }
            if (thisuri[0] != null) {
                hashMap.put("pimage", thisuri[0]);
            }
            hashMap.put("updated_at", formatter.format(calendar.getTime()));

            dao.update(edit_item.getKey(), hashMap).addOnSuccessListener(suc -> {
                finish();
            }).addOnFailureListener(er -> {
                Toast.makeText(this, "" + er.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }



    // permissions and location
    private void accessLocationService() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }



        locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 0, 0, this);
        locationManager.getLastKnownLocation(LocationManager.FUSED_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show();
            accessLocationService();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        this.location = location;
        System.out.println("current Location" + location.getLatitude() + ", " + location.getLongitude());
        txtLocation.setText("lat: " + location.getLatitude() + ",\nlng: " + location.getLongitude());
    }


    // image upload
    private Boolean fileUpload() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("File uploader");
        dialog.show();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference uploader = storage.getReference().child("image" + formatter.format(calendar.getTime()));

        uploader.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                dialog.dismiss();
                Toast.makeText(BuyItemActivity.this, "Product uploaded successfully", Toast.LENGTH_SHORT).show();

                uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        thisuri[0] = uri.toString();
                        addBuyItem();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                float percentage = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                dialog.setMessage("uploaded: " + (int) percentage + "%");
            }
        });
        return true;
    }


    private void browseImage() {
        Dexter.withActivity(BuyItemActivity.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "please select image"), 1);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            filepath = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(filepath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
                img_label.setVisibility(View.GONE);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (requestCode == 101 && resultCode == Activity.RESULT_OK) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);

            // method to get the url fro bitmap
            Uri tempUri = getImageUri(getApplicationContext(), photo);
            filepath  = tempUri;

        }
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "", null);
        return Uri.parse(path);
    }


//   setting permission codes
    int code = 101;
    private void checkAndRequestPermissions() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
        } else if (code == 101) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, 101);
        }

    }

}
