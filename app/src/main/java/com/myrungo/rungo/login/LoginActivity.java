package com.myrungo.rungo.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.myrungo.rungo.MainActivity;
import com.myrungo.rungo.R;
import com.myrungo.rungo.base.BaseActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LoginActivity
        extends BaseActivity
        implements View.OnClickListener, LoginContract.View {

    @Nullable
    private LoginContract.Presenter<LoginContract.View> presenter;

    @Override
    final protected void onStart() {
        super.onStart();
        getPresenter().onStart();
    }

    @Override
    final public void onClick(@NonNull final View v) {
        final int i = v.getId();

        @Nullable final Editable emailFieldText = getEmailField().getText();

        if (emailFieldText == null) {
            throw new RuntimeException("emailFieldText == null");
        }

        @Nullable final Editable passwordFieldText = getPasswordField().getText();

        if (passwordFieldText == null) {
            throw new RuntimeException("passwordFieldText == null");
        }

        @Nullable final Editable phoneNumberFieldText = getPhoneNumberField().getText();

        if (phoneNumberFieldText == null) {
            throw new RuntimeException("phoneNumberFieldText == null");
        }

        @NonNull final String email = emailFieldText.toString().trim();
        @NonNull final String password = passwordFieldText.toString().trim();
        @NonNull final String phoneNumber = phoneNumberFieldText.toString().trim();

        hideKeyboard();

        if (i == R.id.signInWithEmailButton) {
            getPresenter().signInWithEmailAndPassword(email, password);
        } else if (i == R.id.signInWithPhoneNumberButton) {
            getPresenter().signInWithPhoneNumber(phoneNumber);
        } else if (i == R.id.signUpWithEmailButton) {
            getPresenter().signUpWithEmail(email, password);
        } else if (i == R.id.signUpWithPhoneNumberButton) {
            getPresenter().signUpWithPhoneNumber(phoneNumber);
        }
    }

    @Override
    public void disableSignInWithEmailButton() {
        getSignInWithEmailButton().setEnabled(false);
    }

    @Override
    public void enableSignInWithEmailButton() {
        getSignInWithEmailButton().setEnabled(true);
    }

    @Override
    public void disableSignInWithPhoneNumberButton() {
        getSignInWithPhoneNumberButton().setEnabled(false);
    }

    @Override
    public void enableSignInWithPhoneNumberButton() {
        getSignInWithPhoneNumberButton().setEnabled(true);
    }

    @NonNull
    @Override
    final protected CoordinatorLayout getCoordinatorLayout() {
        @Nullable final CoordinatorLayout activitySignUpCL = findViewById(R.id.activitySignUpCL);

        if (activitySignUpCL == null) {
            throw new RuntimeException("activitySignUpCL == null");
        }

        return activitySignUpCL;
    }

    @NonNull
    @Override
    final protected LoginContract.Presenter getPresenter() {
        if (presenter == null) {
            throw new RuntimeException("presenter == null");
        }

        return presenter;
    }

    @Override
    final protected void setupContentView() {
        setContentView(R.layout.activity_login);
    }

    @Override
    final protected void setPresenter() {
        presenter = new LoginPresenter<>();

        presenter.onBindView(this);
    }

    @Override
    final public boolean isEmailAndPasswordValid() {
        @Nullable final Editable emailFieldText = getEmailField().getText();

        if (emailFieldText == null) {
            getEmailInputLayout().setError(getString(R.string.you_have_not_entered_email));
            return false;
        }

        @Nullable final Editable passwordFieldText = getPasswordField().getText();

        if (passwordFieldText == null) {
            getPasswordInputLayout().setError(getString(R.string.you_have_not_entered_password));
            return false;
        }

        @NonNull final String email = emailFieldText.toString();

        @NonNull final String password = passwordFieldText.toString();

        final boolean emailIsEmpty = TextUtils.getTrimmedLength(email) == 0;

        if (emailIsEmpty) {
            getEmailField().setError(getString(R.string.you_have_not_entered_email));
        }

        int passwordLength = TextUtils.getTrimmedLength(password);

        final boolean passwordIsEmpty = passwordLength == 0;

        if (passwordIsEmpty) {
            getPasswordField().setError(getString(R.string.you_have_not_entered_password));
        }

        final boolean passwordIsWeak = passwordLength < 6;

        if (passwordIsWeak) {
            getPasswordField().setError(getString(R.string.password_must_be_at_least_6_characters));
        }

        final boolean isEmailCorrect = isEmailCorrect(email);

        if (!isEmailCorrect) {
            getEmailField().setError(getString(R.string.the_email_address_is_badly_formatted));
        }

        return !emailIsEmpty && !passwordIsEmpty && !passwordIsWeak && isEmailCorrect;
    }

    @Override
    final public boolean isPhoneNumberValid() {
        @Nullable final Editable phoneNumberField = getPhoneNumberField().getText();

        @NonNull final String notFilled = getString(R.string.you_have_not_entered_phone_number);

        if (phoneNumberField == null) {
            getPhoneNumberInputLayout().setError(notFilled);
            return false;
        }

        @NonNull final String phoneNumber = phoneNumberField.toString().trim();

        boolean phoneNumberCorrect = isPhoneNumberCorrect(phoneNumber);

        if (!phoneNumberCorrect) {
            getPhoneNumberInputLayout().setError(notFilled);
        }

        return phoneNumberCorrect;
    }

    @Override
    final protected void setupClickListeners() {
        getSignInWithEmailButton().setOnClickListener(this);
        getSignInWithPhoneNumberButton().setOnClickListener(this);

        getSignUpWithEmailButton().setOnClickListener(this);
        getSignUpWithPhoneNumberButton().setOnClickListener(this);

        getEmailField().addTextChangedListener(new TextWatcher() {

            @Override
            final public void beforeTextChanged(
                    @NonNull final CharSequence s,
                    final int start,
                    final int count,
                    final int after
            ) {
            }

            @Override
            final public void onTextChanged(
                    @NonNull final CharSequence s,
                    final int start,
                    final int before,
                    final int count
            ) {
            }

            @Override
            final public void afterTextChanged(@NonNull final Editable s) {
                if (s.length() == 0)
                    getEmailInputLayout().setError(getString(R.string.you_have_not_entered_email));
                else {
                    getEmailInputLayout().setError(null);
                }

                enableSignInWithEmailButton();
            }

        });

        getPasswordField().addTextChangedListener(new TextWatcher() {

            @Override
            final public void beforeTextChanged(
                    @NonNull final CharSequence s,
                    final int start,
                    final int count,
                    final int after
            ) {
            }

            @Override
            final public void onTextChanged(
                    @NonNull final CharSequence s,
                    final int start,
                    final int before,
                    final int count
            ) {
            }

            @Override
            final public void afterTextChanged(@NonNull final Editable s) {
                if (s.length() == 0)
                    getPasswordInputLayout().setError(getString(R.string.you_have_not_entered_password));
                else {
                    getPasswordInputLayout().setError(null);
                }

                enableSignInWithEmailButton();
            }

        });

        getPhoneNumberField().addTextChangedListener(new TextWatcher() {

            @Override
            final public void beforeTextChanged(
                    @NonNull final CharSequence s,
                    final int start,
                    final int count,
                    final int after
            ) {
            }

            @Override
            final public void onTextChanged(
                    @NonNull final CharSequence s,
                    final int start,
                    final int before,
                    final int count
            ) {
            }

            @Override
            final public void afterTextChanged(@NonNull final Editable s) {
                if (s.length() == 0)
                    getPhoneNumberInputLayout().setError(getString(R.string.you_have_not_entered_phone_number));
                else {
                    getPhoneNumberInputLayout().setError(null);
                }

                enableSignInWithPhoneNumberButton();
            }

        });
    }

    @Override
    final public void goToMain() {
        @NonNull final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @NonNull
    @Override
    final protected ViewGroup getProgressBarLayout() {
        @Nullable final ViewGroup layoutWithProgressBar = findViewById(R.id.layoutWithProgressBar);

        if (layoutWithProgressBar == null) {
            throw new RuntimeException("layoutWithProgressBar == null");
        }

        return layoutWithProgressBar;
    }

    @Override
    public void showPhoneNumberError(final @NonNull String message) {
        getPhoneNumberInputLayout().setError(message);
    }

    private boolean isEmailCorrect(@NonNull final String email) {
        //stupid check

        if (!email.contains("@")) {
            return false;
        }

        final int indexOfAt = email.indexOf('@');

        @NonNull final String name = email.substring(0, indexOfAt);

        if (name.isEmpty()) {
            return false;
        }

        @NonNull final String emailProvider = email.substring(indexOfAt + 1, email.length());

        if (emailProvider.isEmpty()) {
            return false;
        }

        if (emailProvider.contains("@")) {
            return false;
        }

        return emailProvider.contains(".");
    }

    private boolean isPhoneNumberCorrect(@NonNull final String phoneNumber) {
        String russianPhoneRegExp = "^((\\+7|8)+([0-9]){10})$";

        Pattern pattern = Pattern.compile(russianPhoneRegExp);
        Matcher matcher = pattern.matcher(phoneNumber);

        return matcher.matches();
    }

    @NonNull
    private Button getSignUpWithEmailButton() {
        @Nullable final Button signUpWithEmailButton = findViewById(R.id.signUpWithEmailButton);

        if (signUpWithEmailButton == null) {
            throw new RuntimeException("signUpWithEmailButton == null");
        }

        return signUpWithEmailButton;
    }

    @NonNull
    private Button getSignUpWithPhoneNumberButton() {
        @Nullable final Button signUpWithPhoneNumberButton = findViewById(R.id.signUpWithPhoneNumberButton);

        if (signUpWithPhoneNumberButton == null) {
            throw new RuntimeException("signUpWithPhoneNumberButton == null");
        }

        return signUpWithPhoneNumberButton;
    }

    @NonNull
    private Button getSignInWithPhoneNumberButton() {
        @Nullable final Button signInWithPhoneNumberButton = findViewById(R.id.signInWithPhoneNumberButton);

        if (signInWithPhoneNumberButton == null) {
            throw new RuntimeException("signInWithPhoneNumberButton == null");
        }

        return signInWithPhoneNumberButton;
    }

    @NonNull
    private Button getSignInWithEmailButton() {
        @Nullable final Button signInWithEmailButton = findViewById(R.id.signInWithEmailButton);

        if (signInWithEmailButton == null) {
            throw new RuntimeException("signInWithEmailButton == null");
        }

        return signInWithEmailButton;
    }

    @NonNull
    private EditText getEmailField() {
        @Nullable final EditText emailField = findViewById(R.id.emailField);

        if (emailField == null) {
            throw new RuntimeException("emailField == null");
        }

        return emailField;
    }

    @NonNull
    private EditText getPasswordField() {
        @Nullable final EditText passwordField = findViewById(R.id.passwordField);

        if (passwordField == null) {
            throw new RuntimeException("passwordField == null");
        }

        return passwordField;
    }

    @NonNull
    private EditText getPhoneNumberField() {
        @Nullable final EditText phoneNumberField = findViewById(R.id.phoneNumberField);

        if (phoneNumberField == null) {
            throw new RuntimeException("phoneNumberField == null");
        }

        return phoneNumberField;
    }

    @NonNull
    private TextInputLayout getEmailInputLayout() {
        @Nullable final TextInputLayout emailInputLayout = findViewById(R.id.emailInputLayout);

        if (emailInputLayout == null) {
            throw new RuntimeException("emailInputLayout == null");
        }

        return emailInputLayout;
    }

    @NonNull
    private TextInputLayout getPasswordInputLayout() {
        @Nullable final TextInputLayout passwordInputLayout = findViewById(R.id.passwordInputLayout);

        if (passwordInputLayout == null) {
            throw new RuntimeException("passwordInputLayout == null");
        }

        return passwordInputLayout;
    }

    @NonNull
    private TextInputLayout getPhoneNumberInputLayout() {
        @Nullable final TextInputLayout phoneNumberInputLayout = findViewById(R.id.phoneNumberInputLayout);

        if (phoneNumberInputLayout == null) {
            throw new RuntimeException("phoneNumberInputLayout == null");
        }

        return phoneNumberInputLayout;
    }

}
