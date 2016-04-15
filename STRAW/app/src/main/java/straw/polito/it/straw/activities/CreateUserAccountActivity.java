package straw.polito.it.straw.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import straw.polito.it.straw.R;
import straw.polito.it.straw.data.User;
import straw.polito.it.straw.utils.Logger;

public class CreateUserAccountActivity extends AppCompatActivity {

    ImageView photo;
    EditText c_pwd;
    EditText cc_pwd;
    EditText email;
    EditText uni;
    Spinner u_d;
    Spinner u_t;
    Spinner p_t;
    Button c_acc_button;
    PopupWindow popUp;

    Bitmap bitmap;

    Uri photo_uri;

    List<String> u_t_list;
    List<String> u_d_list;
    List<String> p_t_list;
    private String TAG = "CreateManagerAccountActivity";
    private SharedPreferences mShared;
    private static final int IMAGE_REQ = 1;
    private static final int CAMERA_REQ = 2;
    User user;
    boolean sw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        mShared= PreferenceManager.getDefaultSharedPreferences(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initialize();
        setListeners();
        if(getIntent().hasExtra("user")){
            Log.v(TAG,getIntent().getExtras().getString("user"));
            user=new User(getIntent().getExtras().getString("user"));
            loadPrevInfo(user);
        }else{
            user=new User();
            setPhoto();
        }

    }

    private void loadPrevInfo(User user) {
        try {
            photo_uri=Uri.parse(user.getImage());
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(user.getImage()));
            photo.setImageBitmap(bitmap);
        } catch (IOException e) {
            Log.v(TAG, "Error on loading the photo! " + e.getMessage());
        }
        c_pwd.setText(user.getPwd());
        email.setText(user.getEmail());
        uni.setText(user.getUniversity());
        u_d.setSelection(u_d_list.indexOf(user.getDiet()));
        u_t.setSelection(u_t_list.indexOf(user.getDiet()));
        p_t.setSelection(p_t_list.indexOf(user.getDiet()));
        c_acc_button.setText(getString(R.string.save));
    }

    private void initialize() {
        photo=(ImageView)findViewById(R.id.photo_imageView);
        c_pwd=(EditText)findViewById(R.id.c_pwd_editText);
        cc_pwd=(EditText)findViewById(R.id.cc_pwd_editText);
        uni=(EditText)findViewById(R.id.uni_editText);
        u_t=(Spinner)findViewById(R.id.u_t_spinner);
        u_d=(Spinner)findViewById(R.id.u_d_spinner);
        p_t=(Spinner)findViewById(R.id.p_t_spinner);
        email=(EditText)findViewById(R.id.email_textView);
        c_acc_button=(Button)findViewById(R.id.create_button);
        setUpPopUpWindow();

        u_t_list=new ArrayList<>();
        u_d_list=new ArrayList<>();
        p_t_list=new ArrayList<>();

        u_t_list.add(getString(R.string.student));
        u_t_list.add(getString(R.string.professor));
        u_t_list.add(getString(R.string.worker));

        u_d_list.add(getString(R.string.Vegan));
        u_d_list.add(getString(R.string.Gluten_free));

        for(int i=11;i<5;i++){
                p_t_list.add(i+":00");
                p_t_list.add(i+":30");
        }

        u_t.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, (List<String>) u_t));
        u_d.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, (List<String>) u_d));
        p_t.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, (List<String>) p_t));

    }
    private void setListeners() {

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUp.showAsDropDown(v, 0, 0);
            }
        });

        c_acc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean sw = false;
                if (!email.getText().toString().equals("")) {
                    user.setEmail(email.getText().toString());
                } else {
                    showAlert(getString(R.string.m_email), getString(R.string.error), false);
                    sw = true;
                }
                if (!c_pwd.getText().toString().equals("") && c_pwd.getText().toString().equals(cc_pwd.getText().toString())) {
                    user.setPwd(c_pwd.getText().toString());
                } else {
                    showAlert(getString(R.string.m_pwd), getString(R.string.error), false);
                    sw = true;
                }
                if (!uni.getText().toString().equals("")) {
                    user.setUniversity(uni.getText().toString());
                } else {
                    showAlert(getString(R.string.uni), getString(R.string.error), false);
                    sw = true;
                }
                user.setDiet(u_d_list.get(u_d.getSelectedItemPosition()));
                user.setType(u_t_list.get(u_t.getSelectedItemPosition()));
                user.setPref_time(p_t_list.get(p_t.getSelectedItemPosition()));
                user.setImage( photo_uri.toString());

                if (!sw) {
                    String oj = user.toString();
                    mShared.edit().putString("User", oj).commit();
                    if(getIntent().hasExtra("user")) {
                        showAlert(getString(R.string.m_save), getString(R.string.m_succ), true);
                    }else{
                        showAlert(getString(R.string.m_c), getString(R.string.m_succ), true);
                    }
                    Intent intent = new Intent(getApplicationContext(), ProfileManagerActivity.class);
                    startActivity(intent);
                } else {
                    return;
                }
            }
        });


    }

    private void showAlert(String message,String title, final boolean ex){
        AlertDialog alertDialog = new AlertDialog.Builder(CreateUserAccountActivity.this).create();
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
        bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.no_image);
        photo.setImageBitmap(bitmap);
        photo_uri=Uri.parse("android.resource://straw.polito.it.straw/drawable/no_image");
    }
    private void setUpPopUpWindow() {
        popUp = new PopupWindow(getBaseContext());
        final List<String> listOpt=new ArrayList<>();
        listOpt.add(getString(R.string.Choose_photo));
        listOpt.add(getString(R.string.Take_photo));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateUserAccountActivity.this, android.R.layout.simple_list_item_1, listOpt);
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
                    photo_uri=uriSavedImage;
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
            Uri image =data.getData();
            photo_uri=data.getData();
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),image);
                bitmap=Bitmap.createScaledBitmap(bitmap,photo.getHeight(),photo.getWidth(),true);
                photo.setImageBitmap(bitmap);
            }catch(IOException e){
                Log.v(TAG,"Error on Activity result! "+e.getMessage());
            }
        }
        if( requestCode== CAMERA_REQ){
            sw=true;
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),photo_uri);
                bitmap=Bitmap.createScaledBitmap(bitmap, photo.getHeight(), photo.getWidth(), true);
                photo.setImageBitmap(bitmap);
            }catch(Exception e){
                Logger.d("Error on Activity result! " + e.getMessage());
            }
        }
    }
}
