package straw.polito.it.straw.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.utils.Logger;

public class CreateUserAccountActivity extends AppCompatActivity {

    ImageView photo;
    EditText c_pwd;
    EditText cc_pwd;
    EditText tel;
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
    Manager man;
    boolean sw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        mShared= PreferenceManager.getDefaultSharedPreferences(this);
        initialize();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    private void initialize() {
        photo=(ImageView)findViewById(R.id.photo_imageView);
        c_pwd=(EditText)findViewById(R.id.c_pwd_editText);
        cc_pwd=(EditText)findViewById(R.id.cc_pwd_editText);
        tel=(EditText)findViewById(R.id.tel_editText);
        uni=(EditText)findViewById(R.id.uni_editText);
        u_t=(Spinner)findViewById(R.id.u_t_spinner);
        u_d=(Spinner)findViewById(R.id.u_d_spinner);
        p_t=(Spinner)findViewById(R.id.p_t_spinner);
        email=(EditText)findViewById(R.id.email_editText);
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
