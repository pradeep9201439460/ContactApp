package com.example.contactapp;

import android.content.Context;

import com.example.contactapp.database.DatabaseHandler;
import com.example.contactapp.model.Contact;

public class DetailContactPresenter  {
    public DetailContactView view;
    private DatabaseHandler databaseHandler;
    private Context context;

    public DetailContactPresenter(DetailContactView view,Context context) {
        this.view = view;
        this.context = context;
    }

    public void getSingleContact(int id){
        databaseHandler=new DatabaseHandler(context);
        view.setSingleContact(databaseHandler.getContact(id));
    }
}
