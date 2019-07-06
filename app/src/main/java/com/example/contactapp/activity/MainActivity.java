package com.example.contactapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.example.contactapp.R;
import com.example.contactapp.controller.MainContactController;
import com.example.contactapp.database.DatabaseHandler;
import com.example.contactapp.model.Contact;

import static com.example.contactapp.Constant.MY_PERMISSIONS_REQUEST_READ_CONTACTS;

public class MainActivity extends AppCompatActivity {
    private Router router;
    private String name, number, email;
    private DatabaseHandler databaseHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHandler = new DatabaseHandler(this);
        setContentView(R.layout.activity_main);

        ViewGroup container = (ViewGroup) findViewById(R.id.controller_container);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean statusLocked = prefs.edit().putBoolean("locked", showContacts()).commit();
        if(statusLocked)
        { router = Conductor.attachRouter(this, container, savedInstanceState);
            if (!router.hasRootController()) {
                router.setRoot(RouterTransaction.with(new MainContactController()));
            }
        } else {
            Toast.makeText(this, "Contacts Permission not granted ", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean showContacts() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        } else {
            return true;
        }
        return false;
    }



    @Override
    public void onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed();
        }
    }
}
