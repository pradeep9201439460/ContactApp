package com.example.contactapp.bundleBuilder;

import android.os.Bundle;

public class BundleBuilder {
    private final Bundle bundle;

    public BundleBuilder(Bundle bundle) {

        this.bundle = bundle;
    }



    public BundleBuilder putString(String key, String value) {
        bundle.putString(key, value);
        return this;
    }

    public BundleBuilder putInt(String keyId, int contactId) {
        bundle.putInt(keyId, contactId);
        return this;
    }
    public Bundle build() {
        return bundle;
    }
}
