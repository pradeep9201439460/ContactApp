package com.example.contactapp.controller;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.example.contactapp.DetailContactPresenter;
import com.example.contactapp.DetailContactView;
import com.example.contactapp.MainContactPresenter;
import com.example.contactapp.bundleBuilder.BundleBuilder;
import com.example.contactapp.R;
import com.example.contactapp.database.DatabaseHandler;
import com.example.contactapp.databinding.ControllerDetailContactBinding;
import com.example.contactapp.model.Contact;
import com.google.android.material.snackbar.Snackbar;


import static com.example.contactapp.Constant.KEY_ID;
import static com.example.contactapp.Constant.REQUEST_READ_CONTACTS;

public class ContactDetailController extends Controller implements DetailContactView {
    private static final String TAG = "ContactDetailController";
    private int contactId;
    String displayName;
    String phoneNumber;
    String emailId;
    private Activity mActivity;
    private DatabaseHandler databaseHandler;
    private ControllerDetailContactBinding binding;
    DetailContactPresenter presenter;

    public ContactDetailController(int contactId) {
        this(new BundleBuilder(new Bundle())
                .putInt(KEY_ID, contactId)
                .build());
    }

    public ContactDetailController(Bundle args) {
        super(args);
        contactId = getArgs().getInt(KEY_ID);
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.controller_detail_contact, container, false);
        mActivity=getActivity();
        databaseHandler = new DatabaseHandler(mActivity);
        presenter=new DetailContactPresenter(this,mActivity);
        presenter.getSingleContact(contactId);
       // Contact contact = databaseHandler.getContact(contactId);
//        displayName = contact.getDisplayName();
//        phoneNumber = contact.getPhoneNumber();
//        emailId = contact.getEmailId();
        binding.imgContactDetailPicture.setImageDrawable(getDrawable());
        binding.tvContactDetailName.setText(displayName);
        binding.tvContactDetailNumber.setText(phoneNumber);
        init();
        return binding.getRoot();
    }

    private void init() {
        requestPermission();
        shareContact();
        getEmailId(emailId);
        setCalling();
        deleteContactToRoot();
        updateContactTORoot();
        binding.imgBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRouter().popToRoot();
            }
        });
    }

    private void updateContactTORoot() {
        binding.editFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRouter().pushController(RouterTransaction.with(
                        new ContactUpdateController(contactId, displayName, phoneNumber, emailId))
                        .pushChangeHandler(new FadeChangeHandler())
                        .popChangeHandler(new FadeChangeHandler()));
            }
        });
    }

    private void deleteContactToRoot() {
        binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseHandler.deleteContact(contactId);
                getRouter().popToRoot();
            }
        });

    }

    private void setCalling() {
        binding.layoutCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(callIntent);
            }
        });
    }

    private void getEmailId(String emailId) {
        Log.d(TAG, "getEmailId: " + emailId);
        if (emailId != null && !emailId.equals("")) {
            binding.emailLayout.setVisibility(View.VISIBLE);
            binding.contactDetailEmail.setText(emailId);
            binding.emailLayout.setVisibility(View.VISIBLE);
        } else {
            binding.emailLayout.setVisibility(View.INVISIBLE);
            binding.viewEmailBottomLine.setVisibility(View.INVISIBLE);
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CALL_PHONE)) {

        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CALL_PHONE},
                    REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull
            String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                binding.layoutCallButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(v, "Call Function wont work as Permissions were denied", Snackbar.LENGTH_LONG);
                        requestPermission();
                    }
                });
            }
        }
    }

    private TextDrawable getDrawable() {
        ColorGenerator generator = ColorGenerator.MATERIAL;
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(displayName.substring(0, 1), generator.getRandomColor());
        return drawable;
    }

    private void shareContact() {
        binding.imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cur = getApplicationContext().getContentResolver().
                        query(ContactsContract.Contacts.CONTENT_URI,
                                new String[]{ContactsContract.Contacts.LOOKUP_KEY},
                                ContactsContract.Contacts._ID, null, null);
                if (cur.moveToFirst()) {
                    String lookupKey = cur.getString(0);
                    Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType(ContactsContract.Contacts.CONTENT_VCARD_TYPE);
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.putExtra(Intent.EXTRA_SUBJECT, displayName);
                    startActivity(intent);
                }
            }
        });
    }
    @Override
    public void setSingleContact(Contact contact) {
        displayName = contact.getDisplayName();
        phoneNumber = contact.getPhoneNumber();
        emailId = contact.getEmailId();
    }
}