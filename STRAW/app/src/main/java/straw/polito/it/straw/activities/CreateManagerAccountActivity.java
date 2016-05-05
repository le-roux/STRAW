package straw.polito.it.straw.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import straw.polito.it.straw.R;

import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.data.Review;
import straw.polito.it.straw.utils.DatabaseUtils;
import straw.polito.it.straw.utils.ImageManager;
import straw.polito.it.straw.utils.Logger;
import straw.polito.it.straw.utils.SharedPreferencesHandler;

public class CreateManagerAccountActivity extends AppCompatActivity {

    ImageView photo;
    EditText c_pwd;
    EditText cc_pwd;
    EditText tel;
    EditText email;
    EditText r_n;
    Spinner r_t;
    EditText addr;
    EditText seats;
    Button c_acc_button;
    PopupWindow popUp;

    Bitmap bitmap;

    String imageString;

    List<String> types;
    private String TAG = "CreateManagerAccountActivity";
    private SharedPreferencesHandler sharedPreferencesHandler;
    private static final int IMAGE_REQ = 1;
    private static final int CAMERA_REQ = 2;
    public static final String NUMBER_OF_ELEMENTS = "ElementsNb";

    ArrayList<Manager> arrayManager;
    Manager man;
    boolean sw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        sharedPreferencesHandler = ((StrawApplication)getApplication()).getSharedPreferencesHandler();
        initialize();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        types=new ArrayList<>();
        types.add(getString(R.string.bar));
        types.add(getString(R.string.restaurant));
        types.add(getString(R.string.canteen));
        types.add(getString(R.string.ta));
        setListeners();
        if(getIntent().hasExtra("manager")){
            Log.v(TAG,getIntent().getExtras().getString("manager"));
            man=new Manager(getIntent().getExtras().getString("manager"));
            loadPrevInfo(man);
        }else{
            man=new Manager();
            setPhoto();
        }

        /*
        int elementsNb = mShared.getInt(NUMBER_OF_ELEMENTS, 0);

        for (int i = 0; i < elementsNb; i++) {
            arrayManager.add(new Manager(mShared.getString(String.valueOf(i), "")));
        }

        if(mShared.contains("Manager")){
            try {
                JSONArray jarr=new JSONArray(mShared.getString("Manager","Error"));
                for (int i=0;i<jarr.length();i++){
                    JSONObject jo= new JSONObject(jarr.get(i).toString());
                    arrayManager.add(new Manager(jo.toString()));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        */

    }

    private void loadPrevInfo(Manager man) {
        imageString = man.getImage();
        ImageManager.setImage(getApplicationContext(), photo, imageString);
        tel.setText(String.valueOf(man.getTelephone()));
        email.setText(man.getEmail());
        r_n.setText(man.getRes_name());
        r_t.setSelection(types.indexOf(man.getRes_type()));
        addr.setText(man.getAddress());
        seats.setText(String.valueOf(man.getSeats()));
        c_acc_button.setText(getString(R.string.save));
    }

    private void setListeners() {

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUp.showAsDropDown(v, 0, 0);
            }
        });

        r_t.setAdapter(new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, types));

        c_acc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean sw = false;
                if (!email.getText().toString().equals("")) {
                    man.setEmail(email.getText().toString());
                } else {
                    showAlert(getString(R.string.m_email), getString(R.string.error), false);
                    sw = true;
                }
                if (c_pwd.getText().toString().equals("") || !c_pwd.getText().toString().equals(cc_pwd.getText().toString())) {
                    showAlert(getString(R.string.m_pwd), getString(R.string.error), false);
                    sw = true;
                }
                if (tel.getText().toString().length() > 6) {
                    man.setTelephone(tel.getText().toString());
                } else {
                    showAlert(getString(R.string.m_tel), getString(R.string.error), false);
                    sw = true;
                }
                if (!r_n.getText().toString().equals("")) {
                    man.setRes_name(r_n.getText().toString());
                } else {
                    showAlert(getString(R.string.m_r_n), getString(R.string.error), false);
                    sw = true;
                }
                man.setRes_type(types.get(r_t.getSelectedItemPosition()));
                if (!addr.getText().toString().equals("")) {
                    man.setAddress(addr.getText().toString());
                } else {
                    showAlert(getString(R.string.m_addr), getString(R.string.error), false);
                    sw = true;
                }
                if (!seats.getText().toString().equals("") && Integer.parseInt(seats.getText().toString()) > 0) {
                    man.setSeats(Integer.parseInt(seats.getText().toString()));
                } else {
                    showAlert(getString(R.string.m_seats), getString(R.string.error), false);
                    sw = true;
                }
                man.setImage(imageString);
                if (!sw) {
                    man.setReviews(new ArrayList<Review>());
                    /**
                     * Set the new profile as the current manager.
                     */
                    sharedPreferencesHandler.storeCurrentManager(man.toJSONObject());
                    /**
                     * Save the profile in the database, log the manager and launch the profile activity.
                     */
                    ProgressDialog dialog= new ProgressDialog(CreateManagerAccountActivity.this, ProgressDialog.STYLE_SPINNER);
                    dialog.setIndeterminate(true);
                    dialog.setMessage(getResources().getString(R.string.AccountCreation));
                    dialog.show();
                    DatabaseUtils databaseUtils = ((StrawApplication)getApplication()).getDatabaseUtils();
                    String password = c_pwd.getText().toString();
                    if (getIntent().hasExtra(ProfileManagerActivity.MANAGER)) {
                        /**
                         * Update the existing profile
                         */
                        databaseUtils.saveManagerProfile(man);
                    } else {
                        /**
                         * Create a new user and save the profile in the database
                         */
                        databaseUtils.createUser(man.getEmail(), password, SharedPreferencesHandler.MANAGER, dialog);
                    }
                } else {
                    return;
                }
            }
        });


    }

    private void showAlert(String message,String title, final boolean ex){
        AlertDialog alertDialog = new AlertDialog.Builder(CreateManagerAccountActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (ex) {

                            finish();
                        } else {
                            dialog.dismiss();
                        }
                    }
                });
        alertDialog.show();
    }

    private void setPhoto() {
        bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.no_image);
        photo.setImageBitmap(bitmap);
        Uri uri = Uri.parse("android.resource://straw.polito.it.straw/drawable/no_image");
        imageString = ImageManager.getImageFromUri(getApplicationContext(), uri);
    }


    private void initialize() {
        photo=(ImageView)findViewById(R.id.photo_imageView);
        c_pwd=(EditText)findViewById(R.id.c_pwd_editText);

        cc_pwd=(EditText)findViewById(R.id.cc_pwd_editText);
        tel=(EditText)findViewById(R.id.tel_editText);
        r_n=(EditText)findViewById(R.id.diet_editText);
        r_t=(Spinner)findViewById(R.id.r_t_spinner);
        addr=(EditText)findViewById(R.id.pref_t_textView);
        seats=(EditText)findViewById(R.id.seats_editText);
        email=(EditText)findViewById(R.id.email_editText);
        c_acc_button=(Button)findViewById(R.id.create_button);
        setUpPopUpWindow();
        sw=false;
    }

    private void setUpPopUpWindow() {
        popUp = new PopupWindow(getBaseContext());
        final List<String> listOpt=new ArrayList<>();
        listOpt.add(getString(R.string.Choose_photo));
        listOpt.add(getString(R.string.Take_photo));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateManagerAccountActivity.this, android.R.layout.simple_list_item_1, listOpt);
        final ListView opt_listView = new ListView(getBaseContext());
        opt_listView.setAdapter(adapter);
        opt_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    Intent gallery = new Intent();
                    gallery.setType("image/*");
                    gallery.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(gallery, getString(R.string.Choose_photo)), IMAGE_REQ);
                }
                if (position == 1){
                    //camera stuff
                    Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());


                    File imagesFolder = new File(Environment.getExternalStorageDirectory(), "MyImages");
                    imagesFolder.mkdirs();

                    File image = new File(imagesFolder, "QR_" + timeStamp + ".png");
                    Uri uriSavedImage = Uri.fromFile(image);
                    imageString = ImageManager.getImageFromUri(getApplicationContext(), uriSavedImage);
                    imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                    startActivityForResult(imageIntent, CAMERA_REQ);

                }
                popUp.dismiss();
            }
        });
        popUp.setFocusable(true);
        popUp.setWidth(500);
        popUp.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popUp.setContentView(opt_listView);
        popUp.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "Photo selected! " + requestCode);
        if( data!=null && resultCode==RESULT_OK && requestCode== IMAGE_REQ){
            sw=false;
            Uri uri = data.getData();
            imageString = ImageManager.getImageFromUri(getApplicationContext(), uri);
            Logger.d("onActivityResult : " + imageString);
            ImageManager.setImage(getApplicationContext(), photo, imageString);
        }
        if( requestCode== CAMERA_REQ){
            sw=true;
            ImageManager.setImage(getApplicationContext(), photo, imageString);
        }
    }
}
