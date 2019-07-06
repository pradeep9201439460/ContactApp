package com.example.contactapp.controller;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.example.contactapp.R;
import com.example.contactapp.bundleBuilder.BundleBuilder;
import com.example.contactapp.database.DatabaseHandler;
import com.example.contactapp.databinding.ControllerUpdateContactBinding;
import com.example.contactapp.model.Contact;

import static com.example.contactapp.Constant.KEY_EMAIL;
import static com.example.contactapp.Constant.KEY_ID;
import static com.example.contactapp.Constant.KEY_NAME;
import static com.example.contactapp.Constant.KEY_PH_NO;

public class ContactUpdateController extends Controller {
    private static final String TAG = "ContactUpdateController";
    int contactId;
    String name;
    String phone;
    String email;
    private DatabaseHandler databaseHandler;
    private ControllerUpdateContactBinding binding;

    public ContactUpdateController(int contactId, String name, String phone, String email) {
        this(new BundleBuilder(new Bundle())
                .putInt(KEY_ID, contactId)
                .putString(KEY_NAME, name)
                .putString(KEY_PH_NO, phone)
                .putString(KEY_EMAIL, email)
                .build());
    }

    public ContactUpdateController(Bundle args) {
        super(args);
        contactId = getArgs().getInt(KEY_ID);
        name = getArgs().getString(KEY_NAME);
        phone = getArgs().getString(KEY_PH_NO);
        email = getArgs().getString(email);
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        binding= DataBindingUtil.inflate(inflater,R.layout.controller_update_contact, container, false);
        Log.d(TAG, "onCreateView: " + contactId + name + phone + email);

        databaseHandler = new DatabaseHandler(getActivity());
        Log.d(TAG, "onCreateView: " + contactId);
        binding.etNameUpdate.setText(name);
        binding.etNumberUpdate.setText(phone);
        binding.etEmailUpdate.setText(email);
        binding.imgProfileUpdate.setImageDrawable(getDrawable());

        binding.imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRouter().pushController(RouterTransaction
                        .with(new ContactDetailController(contactId))
                        .pushChangeHandler(new FadeChangeHandler())
                        .popChangeHandler(new FadeChangeHandler()));
            }
        });

        binding.imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = binding.etNameUpdate.getText().toString();
                String phone = binding.etNumberUpdate.getText().toString();
                String email = binding.etEmailUpdate.getText().toString();
                if (name != null && phone != null && !phone.equals("") && !name.equals("")) {
                    Log.d(TAG, "onClick: Name:" + name + "\tPhone:" + phone + "\t Email:" + email);
                    try {
                        Log.d(TAG, "onClick: ContactId=" + contactId);
                        databaseHandler.updateContact(new Contact(contactId, name, phone, email));
                        getRouter().pushController(RouterTransaction
                                .with(new ContactDetailController(contactId))
                                .pushChangeHandler(new FadeChangeHandler())
                                .popChangeHandler(new FadeChangeHandler()));
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

    private TextDrawable getDrawable() {
        ColorGenerator generator = ColorGenerator.MATERIAL;
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(name.substring(0, 1), generator.getRandomColor());
        return drawable;
    }

}
