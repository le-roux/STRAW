package straw.polito.it.straw.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import straw.polito.it.straw.R;
import straw.polito.it.straw.data.Manager;

public class ProfileActivity extends AppCompatActivity {

    ImageView photo;
    TextView user_n;
    TextView tel;
    TextView r_n;
    TextView addr;
    TextView seats;
    TextView menu_link;
    TextView reservations_link;
    TextView offerts_link;
    Button edit_button;

    Bitmap bitmap;

    private String TAG = "ProfileActivity";
    private SharedPreferences mShared;
    Manager man;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mShared = PreferenceManager.getDefaultSharedPreferences(this);

        initialize();

        String ss = mShared.getString("Manager", "Error");
        man=new Manager(ss);
        loadPrevInfo(man);

        setListeners();

    }

    @Override
    protected void onResume() {
        super.onResume();

        man=new Manager(mShared.getString("Manager","Error"));
        loadPrevInfo(man);
    }

    private void setListeners() {
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), CreateAccountActivity.class);
                Log.v(TAG,man.toJSONObject());
                i.putExtra("manager", man.toJSONObject());
                startActivity(i);
            }
        });

        this.menu_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateMenuActivity.class);
                startActivity(intent);
            }
        });

        this.reservations_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TO DO switch to the reservation page
            }
        });
        offerts_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getBaseContext(),CreateOffertActivity.class);
                startActivity(i);
            }
        });
    }

    private void initialize() {
        photo=(ImageView)findViewById(R.id.photo_imageView);
        user_n=(TextView)findViewById(R.id.user_n_editText);
        tel=(TextView)findViewById(R.id.tel_editText);
        r_n=(TextView)findViewById(R.id.r_n_editText);
        addr=(TextView)findViewById(R.id.addr_editText);
        seats=(TextView)findViewById(R.id.seats_editText);
        edit_button=(Button)findViewById(R.id.edit_button);
        this.menu_link = (TextView)findViewById(R.id.menu_link);
        this.reservations_link = (TextView)findViewById(R.id.reservations_link);
        offerts_link=(TextView)findViewById(R.id.offers_link_textView);
    }
    private void loadPrevInfo(Manager man) {

            File imgFile = new  File(getRealPathFromURI(getBaseContext(),Uri.parse(man.getImage())));

            if(imgFile.exists()){

                //Log.v(TAG,"Image exists! "+getRealPathFromURI(getBaseContext(),Uri.parse(man.getImage())));
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                if(myBitmap!=null) {
                    photo.setImageBitmap(myBitmap);
                }else{
                    bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.no_image);
                    photo.setImageBitmap(bitmap);
                }

            }
        user_n.setText(getString(R.string.user_n) + ": " + man.getName());
        tel.setText(getString(R.string.tel)+": "+man.getTelephone());
        r_n.setText(getString(R.string.r_name)+": "+man.getRes_name());
        addr.setText(getString(R.string.addr)+": "+man.getAddress());
        seats.setText(getString(R.string.seats)+": "+man.getSeats());
    }

    private void showAlert(String message,String title, final boolean ex){
        AlertDialog alertDialog = new AlertDialog.Builder(ProfileActivity.this).create();
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
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
