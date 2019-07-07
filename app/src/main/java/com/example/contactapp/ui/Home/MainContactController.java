package com.example.contactapp.controller;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.example.contactapp.MainContactPresenter;
import com.example.contactapp.MainContactView;
import com.example.contactapp.R;
import com.example.contactapp.adapter.ContactsAdapter;
import com.example.contactapp.database.DatabaseHandler;
import com.example.contactapp.databinding.ControllerAllContactBinding;
import com.example.contactapp.model.Contact;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

import static com.example.contactapp.Constant.MY_PERMISSIONS_REQUEST_READ_CONTACTS;

public class MainContactController extends Controller implements MainContactView {
    private static final String TAG = "MainContactController";
    private String name, number, email;
    private DatabaseHandler databaseHandler;
    private Activity mActivity;
    private ControllerAllContactBinding binding;
    CompositeDisposable lifecycle;
    MainContactPresenter presenter;

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.controller_all_contact, container, false);
        mActivity = getActivity();
        presenter=new MainContactPresenter(this,mActivity);
        lifecycle = new CompositeDisposable();
        databaseHandler = new DatabaseHandler(mActivity);
        Log.d(TAG, "----------------onCreateView: ---------------------------------");
        contactPermission();
        addNewContact();
        return binding.getRoot();
    }

    private void addNewContact() {
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRouter().pushController(RouterTransaction.with(
                        new ContactAddController())
                        .pushChangeHandler(new FadeChangeHandler())
                        .popChangeHandler(new FadeChangeHandler()));
            }
        });
    }

    private void contactPermission() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            presenter.getContactList();

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (permissions[0].equals(Manifest.permission.READ_CONTACTS)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getAllContacts();
                    presenter.getContactList();
                }
            }
        }
    }
    private static final String[] PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
            ContactsContract.CommonDataKinds.Email.DATA,
    };

    private void getAllContacts() {
        ContentResolver cr = getApplicationContext().getContentResolver();
        final Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, null);
        if (cursor != null) {
            try {
                final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                while (cursor.moveToNext()) {
                    name = cursor.getString(nameIndex);
                    number = cursor.getString(numberIndex);
                    email = getNameEmailDetails(number);
                    databaseHandler.addContact(new Contact(name, number, email));
                }
            } finally {
                cursor.close();
            }
        }
    }

    private String getNameEmailDetails(String number) {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        String contactId = null;
        String email = null;
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
            cursor.close();
        }
        ContentResolver cr = getApplicationContext().getContentResolver();
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
        if (cur != null) {
            while (cur.moveToNext()) {
                email = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            }
            cur.close();
        }
        return email;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void setContact(final List<Contact> contactList) {
        ContactsAdapter contactsAdapter = new ContactsAdapter(getActivity(), contactList);
        binding.rvContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvContacts.setAdapter(contactsAdapter);
        contactsAdapter.notifyDataSetChanged();
        contactsAdapter.setOnItemClickListener(new ContactsAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                getRouter().pushController(RouterTransaction
                        .with(new ContactDetailController(contactList.get(position).getContactId()))
                        .pushChangeHandler(new FadeChangeHandler())
                        .popChangeHandler(new FadeChangeHandler()));
            }
        });
    }
}
