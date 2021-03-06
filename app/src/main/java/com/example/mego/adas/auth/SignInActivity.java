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

package com.example.mego.adas.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mego.adas.main.MainActivity;
import com.example.mego.adas.R;
import com.example.mego.adas.utils.Constants;
import com.example.mego.adas.utils.NetworkUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.rxbinding2.widget.RxTextView;


import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subscribers.DisposableSubscriber;
import timber.log.Timber;

/**
 * Activity used for signing in
 */
public class SignInActivity extends AppCompatActivity {

    @BindView(R.id.password_editText_sign_in_activity)
    EditText passwordEditText;

    @BindView(R.id.email_editText_sign_in_activity)
    EditText emailEditText;

    @BindView(R.id.password_wrapper_sign_in_activity)
    TextInputLayout passwordWrapper;

    @BindView(R.id.email_wrapper_sign_in_activity)
    TextInputLayout emailWrapper;

    @BindView(R.id.forget_password_textView)
    TextView forgetPasswordTextView;

    @BindView(R.id.sign_in_Button_sign_in_activity)
    Button signInButton;

    private ProgressDialog mProgressDialog;

    /**
     * Firebase Authentication
     */
    private FirebaseAuth mFirebaseAuth;
    private boolean isPhoneVerified = false;

    /**
     * Firebase objects
     * to specific part of the database
     */
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference isPhoneAuthDatabaseReference;
    private ValueEventListener isPhoneAuthValueEventListener;

    private DisposableSubscriber<Boolean> disposableObserver = null;
    private Flowable<CharSequence> emailChangeObservable;
    private Flowable<CharSequence> passwordChangeObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_sign_in);

        ButterKnife.bind(this);

        //initialize the Firebase auth object
        mFirebaseAuth = FirebaseAuth.getInstance();

        //set up the firebase
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        emailChangeObservable = RxTextView.textChanges(emailEditText)
                .skip(1).toFlowable(BackpressureStrategy.LATEST);

        passwordChangeObservable = RxTextView.textChanges(passwordEditText)
                .skip(1).toFlowable(BackpressureStrategy.LATEST);

        validateFormFields();

        signInButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            AuthenticationUtilities.hideKeyboard(SignInActivity.this);
            if (NetworkUtil.isAvailableInternetConnection(getApplicationContext())) {
                signIn(email, password);
            } else {
                Toast.makeText(SignInActivity.this, R.string.error_message_failed_sign_in_no_network,
                        Toast.LENGTH_SHORT).show();
            }
        });

        forgetPasswordTextView.setOnClickListener(v -> {
            Intent resetPasswordIntent = new Intent(SignInActivity.this, ResetPasswordActivity.class);
            startActivity(resetPasswordIntent);
        });
    }

    /**
     * Helper method to validate the data from the edit text
     */
    private void validateFormFields() {
        disposableObserver = new DisposableSubscriber<Boolean>() {
            @Override
            public void onNext(Boolean formValid) {
                if (formValid) {
                    signInButton.setEnabled(true);
                } else {
                    signInButton.setEnabled(false);
                }
            }

            @Override
            public void onError(Throwable t) {
                Timber.e("error sign in validate");
            }

            @Override
            public void onComplete() {
                Timber.e("complete sign in validate");
            }
        };

        Flowable.combineLatest(
                emailChangeObservable,
                passwordChangeObservable,
                (email, password) -> {

                    //Check email
                    boolean emailNotEmpty = !TextUtils.isEmpty(email);
                    boolean emailValid = AuthenticationUtilities.isEmailValid(email);
                    if (!emailNotEmpty) {
                        emailWrapper.setError(getString(R.string.error_message_required));
                    } else if (!emailValid) {
                        emailWrapper.setError(getString(R.string.error_message_valid_email));
                    } else {
                        emailWrapper.setError(null);
                    }

                    //Check password
                    boolean passwordNotEmpty = !TextUtils.isEmpty(password);
                    if (!passwordNotEmpty) {
                        passwordWrapper.setError(getString(R.string.error_message_required));
                    } else {
                        passwordWrapper.setError(null);
                    }

                    return emailNotEmpty && emailValid && passwordNotEmpty;
                }
        ).subscribe(disposableObserver);
    }

    /**
     * To preform the sign in operation
     */
    private void signIn(String email, String password) {
        showProgressDialog();

        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        isPhoneAuthDatabaseReference = mFirebaseDatabase.getReference()
                                .child(Constants.FIREBASE_USERS)
                                .child(task.getResult().getUser().getUid())
                                .child(Constants.FIREBASE_IS_VERIFIED_PHONE);

                        getPhoneVerificationState();
                    }
                }).addOnFailureListener(e -> {
            hideProgressDialog();
            showErrorDialog(e.getLocalizedMessage());
        });
    }


    /**
     * Helper method to show progress dialog
     */
    public void showProgressDialog() {

        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.sign_in_loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    /**
     * Helper method to hide progress dialog
     */
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    /**
     * show a dialog that till that an error
     */
    private void showErrorDialog(String error) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
        builder.setMessage(error);
        builder.setTitle(R.string.error);

        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            if (dialog != null) {
                dialog.dismiss();
            }
        });

        //create and show the alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Method to get the current phone verification state
     */
    private void getPhoneVerificationState() {
        isPhoneAuthValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    isPhoneVerified = dataSnapshot.getValue(Boolean.class);
                    if (!isPhoneVerified) {
                        hideProgressDialog();
                        Intent mainIntent = new Intent(SignInActivity.this, VerifyPhoneNumberActivity.class);
                        //clear the application stack (clear all  former the activities)
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                        finish();
                    } else {
                        hideProgressDialog();
                        Intent mainIntent = new Intent(SignInActivity.this, MainActivity.class);
                        //clear the application stack (clear all  former the activities)
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                        finish();
                    }
                } else {
                    hideProgressDialog();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        isPhoneAuthDatabaseReference.addValueEventListener(isPhoneAuthValueEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposableObserver.dispose();
    }
}
