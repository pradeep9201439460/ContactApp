package com.example.contactapp;

import com.example.contactapp.model.Contact;

import java.util.List;

public interface MainContactView {
    void showLoading();

    void hideLoading();

    void setContact(List<Contact> contactList);
}
