package straw.polito.it.straw.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import straw.polito.it.straw.R;
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.utils.ImageManager;

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
                Intent intent = new Intent(getApplicationContext(), DisplayReservationsActivity.class);
                startActivity(intent);
            }
        });
        offerts_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getBaseContext(),OfferActivity.class);
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
        String path=getRealPathFromURI(getBaseContext(),Uri.parse(man.getImage()));
        ImageManager.setImage(this, photo, Uri.parse(man.getImage()));

        user_n.setText(getString(R.string.email) + ": " + man.getEmail());
        tel.setText(getString(R.string.tel) + ": " + man.getTelephone());
        r_n.setText(getString(R.string.r_name) + ": " + man.getRes_name());
        addr.setText(getString(R.string.addr) + ": " + man.getAddress());
        seats.setText(getString(R.string.seats) + ": " + man.getSeats());
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

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }catch(Exception e){
            return null;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
