package com.example.contactapp;

import android.content.Context;

import com.example.contactapp.database.DatabaseHandler;

public class MainContactPresenter {

    public MainContactView view;
    private DatabaseHandler databaseHandler;
    private  Context context;

    public MainContactPresenter(MainContactView view,Context context) {
        this.view = view;
        this.context=context;
    }

        public void getContactList(){
        databaseHandler=new DatabaseHandler(context);
        view.setContact(databaseHandler.getAllContacts());
        }
}
