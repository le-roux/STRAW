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
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.R;

public class CreateAccountActivity extends AppCompatActivity {

    ImageView photo;
    EditText user_n;
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

    Uri photo_uri;

    List<String> types;
    private String TAG = "CreateAccountActivity";
    private SharedPreferences mShared;
    private static final int IMAGE_REQ = 1;
    private static final int CAMERA_REQ = 2;
    Manager man;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        mShared= PreferenceManager.getDefaultSharedPreferences(this);
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

    }

    private void loadPrevInfo(Manager man) {
        try {
            photo.setImageBitmap( MediaStore.Images.Media.getBitmap(getContentResolver(),Uri.parse(man.getImage())));
            photo_uri=Uri.parse(man.getImage());
        } catch (IOException e) {
            Log.v(TAG, "Error on loading the photo! " + e.getMessage());
        }
        user_n.setText(man.getName());
        c_pwd.setText(man.getPwd());
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
                if (!user_n.getText().toString().equals("")) {
                    man.setName(user_n.getText().toString());
                } else {
                    showAlert(getString(R.string.m_user_n), getString(R.string.error), false);
                    sw = true;
                }
                if (!email.getText().toString().equals("")) {
                    man.setEmail(email.getText().toString());
                } else {
                    showAlert(getString(R.string.m_email), getString(R.string.error), false);
                    sw = true;
                }
                if (!c_pwd.getText().toString().equals("") && c_pwd.getText().toString().equals(cc_pwd.getText().toString())) {
                    man.setPwd(c_pwd.getText().toString());
                } else {
                    showAlert(getString(R.string.m_pwd), getString(R.string.error), false);
                    sw = true;
                }
                if (tel.getText().toString().length() > 6) {
                    man.setTelephone(Integer.parseInt(tel.getText().toString()));
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
                man.setImage( photo_uri.toString());
                if (!sw) {
                    String oj = man.toJSONObject();
                    mShared.edit().putString("Manager", oj).commit();
                    if(getIntent().hasExtra("manager")) {
                        showAlert(getString(R.string.m_save), getString(R.string.m_succ), true);
                    }else{
                        showAlert(getString(R.string.m_c), getString(R.string.m_succ), true);
                    }
                    Intent intent = new Intent(getApplicationContext(), CreateMenuActivity.class);
                    startActivity(intent);
                } else {
                    return;
                }
            }
        });


    }

    private void showAlert(String message,String title, final boolean ex){
        AlertDialog alertDialog = new AlertDialog.Builder(CreateAccountActivity.this).create();
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
        photo_uri=Uri.parse("android.resource://straw.polito.it.straw/drawable/no_image");
    }


    private void initialize() {
        photo=(ImageView)findViewById(R.id.photo_imageView);
        user_n=(EditText)findViewById(R.id.user_n_editText);
        c_pwd=(EditText)findViewById(R.id.c_pwd_editText);
        cc_pwd=(EditText)findViewById(R.id.cc_pwd_editText);
        tel=(EditText)findViewById(R.id.tel_editText);
        r_n=(EditText)findViewById(R.id.r_n_editText);
        r_t=(Spinner)findViewById(R.id.r_t_spinner);
        addr=(EditText)findViewById(R.id.addr_editText);
        seats=(EditText)findViewById(R.id.seats_editText);
        email=(EditText)findViewById(R.id.email_editText);
        c_acc_button=(Button)findViewById(R.id.create_button);
        setUpPopUpWindow();
    }

    private void setUpPopUpWindow() {
        popUp = new PopupWindow(getBaseContext());
        final List<String> listOpt=new ArrayList<>();
        listOpt.add(getString(R.string.select_photo));
        listOpt.add(getString(R.string.take_photo));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateAccountActivity.this, android.R.layout.simple_list_item_1, listOpt);
        final ListView opt_listView = new ListView(getBaseContext());
        opt_listView.setAdapter(adapter);
        opt_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    Intent gallery = new Intent();
                    gallery.setType("image/*");
                    gallery.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(gallery, getString(R.string.select_photo)), IMAGE_REQ);
                }
                if (position == 1){
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQ);
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
        if( data!=null && resultCode==RESULT_OK && requestCode== IMAGE_REQ){
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
    }
}
