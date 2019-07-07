package com.example.contactapp.controller;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.provider.ContactsContract;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.bluelinelabs.conductor.Controller;
import com.example.contactapp.R;
import com.example.contactapp.database.DatabaseHandler;
import com.example.contactapp.databinding.ControllerAddContactBinding;
import com.example.contactapp.model.Contact;

import static com.example.contactapp.Constant.REQUEST_READ_CONTACTS;

public class ContactAddController extends Controller {

    private static final String TAG = "ContactAddController";
    private DatabaseHandler databaseHandler;
    private ControllerAddContactBinding binding;

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.controller_add_contact, container, false);
        databaseHandler = new DatabaseHandler(getActivity());
        requestPermission();
        binding.imgClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRouter().popToRoot();
            }
        });
        binding.tvSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.etName.getText().toString();
                String phone = binding.etNumber.getText().toString();
                String email = binding.etEmail.getText().toString();
                if (name != null && phone != null && !phone.equals("") && !name.equals("")) {

                    Log.d(TAG, "onClick: Name:" + name + "\tPhone:" + phone + "\t Email:" + email);
                    try {
                        databaseHandler.addContact(new Contact(name, phone, email));
                        getRouter().popToRoot();
                    } catch (Exception e) {
                        Log.e(TAG, "onClick: " + e);
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), "Name or Phone number is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return binding.getRoot();
    }
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_CONTACTS)) {

        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }
}

