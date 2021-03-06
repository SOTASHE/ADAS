/*
 * Copyright (c) 2017 Ahmed-Abdelmeged
 *
 * github: https://github.com/Ahmed-Abdelmeged
 * email: ahmed.abdelmeged.vm@gamil.com
 * Facebook: https://www.facebook.com/ven.rto
 * Twitter: https://twitter.com/A_K_Abd_Elmeged
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mego.adas.user_info;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mego.adas.R;
import com.example.mego.adas.auth.AuthenticationUtilities;
import com.example.mego.adas.auth.User;
import com.example.mego.adas.utils.Constants;
import com.example.mego.adas.utils.NetworkUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Activity to edit user name
 */

public class EditUserNameActivity extends AppCompatActivity {

    /**
     * UI Element
     */
    private EditText nameEditText;
    private TextInputLayout nameWrapper;
    private Button saveNameButton;
    private Toast toast;

    /**
     * Firebase objects
     * to specific part of the database
     */
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersNameDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.EditInfoThemeNoActionBar);
        setContentView(R.layout.activity_edit_user_name);

        initializeScreen();

        //set up the firebase
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        User currentUser = AuthenticationUtilities.getCurrentUser(EditUserNameActivity.this);
        mUsersNameDatabaseReference = mFirebaseDatabase.getReference()
                .child(Constants.FIREBASE_USERS).child(currentUser.getUserUid())
                .child(Constants.FIREBASE_USER_NAME);

        saveNameButton.setOnClickListener(v -> {
            if (validateName()) {
                if (NetworkUtil.isAvailableInternetConnection(EditUserNameActivity.this)) {
                    String name = nameEditText.getText().toString();
                    mUsersNameDatabaseReference.setValue(name);
                    AuthenticationUtilities.setCurrentUserName(
                            EditUserNameActivity.this, name);
                    finish();
                } else {
                    showToast(getString(R.string.no_internet_connection));
                }
            }
        });
    }


    /**
     * Link the layout element from XML to Java
     */
    private void initializeScreen() {
        //to show white up button in the activity
        Toolbar toolbar = findViewById(R.id.edit_user_name_toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable drawable = toolbar.getNavigationIcon();
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(drawable);
        getSupportActionBar().setTitle("");

        nameEditText = findViewById(R.id.name_editText_edit_name_activity);

        nameWrapper = findViewById(R.id.name_wrapper_edit_name_activity);

        saveNameButton = findViewById(R.id.save_new_name_button);
    }

    /**
     * Helper method to validate the data from the edit text
     *
     * @return boolean to indicate name validation
     */
    private boolean validateName() {
        String name = nameEditText.getText().toString();
        if (TextUtils.isEmpty(name) ) {
            nameWrapper.setError(getString(R.string.error_message_required));
            return false;
        } else {
            nameWrapper.setError(null);
        }
        return true;
    }

    /**
     * Fast way to call Toast
     */
    private void showToast(String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(EditUserNameActivity.this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (toast != null) {
            toast.cancel();
        }
    }
}