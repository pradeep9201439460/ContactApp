package com.example.contactapp.model;

public class Contact {
    private int contactId;
    private String displayName;
    private String phoneNumber;
    private String emailId;
    public Contact(){   }
    public Contact(String displayName, String phoneNumber, String emailId) {
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
        this.emailId = emailId;
    }

    public Contact(int contactId, String displayName, String phoneNumber, String emailId) {
        this.contactId = contactId;
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
        this.emailId = emailId;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

}
