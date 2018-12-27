package order.food.com.foodorder;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    public static final String USER_ADDED_SUCCESSFULLY = "User added successfully!!";
    public static final String MOBILE_NUMBER_ALREADY_EXIST = "Mobile Number Already Exist!!";
    public static final String PLEASE_ENTER_CORRECT_DETAILS = "Please enter correct details!!";
    UsersDatabaseAdapter userAdapter;
    TextInputEditText name, mobNo, mail, password, confirmPassword;
    TextInputLayout passwordLayout, confirmPasswordLayout;
    TextView password_error, cnf_password_error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        name = findViewById(R.id.name_txt);
        mobNo = findViewById(R.id.mob_no_txt);
        mail = findViewById(R.id.mail_txt);
        password = findViewById(R.id.password_txt);
        confirmPassword = findViewById(R.id.cnf_password_txt);

        passwordLayout = findViewById(R.id.signup_password);
        confirmPasswordLayout = findViewById(R.id.signup_confirm_password);

        password_error = findViewById(R.id.password_error);
        cnf_password_error = findViewById(R.id.cnf_password_error);

        userAdapter = new UsersDatabaseAdapter(this);
        initializeEventListners();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userAdapter.close();
    }

    private void initializeEventListners() {
        mobNo.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) validateMobileNumber(v);
        });
        mail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) validateMailAddress(v);
        });
        password.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) validatePassword(v);
        });
        name.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) validateUserName(v);
        });
        confirmPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) validateConfirmPassword(v);
        });

    }

    public boolean validateUserName(View view) {
        String username = name.getText().toString();
        if (username == null || username.isEmpty()) {
            name.setError(getResources().getString(R.string.app_error_empty_user_name));
            return false;
        }
        return true;
    }


    public boolean validateMobileNumber(View view) {
        String mobileNumber = mobNo.getText().toString();
        if (mobileNumber == null || mobileNumber.isEmpty()) {
            mobNo.setError(getResources().getString(R.string.app_error_empty_mob_no));
            return false;
        } else if (!PatternValidator.validateMobileNo(mobileNumber)) {
            mobNo.setError(getResources().getString(R.string.app_error_invalid_mob_no));
            return false;
        }
        return true;
    }

    public boolean validateMailAddress(View view) {
        String mailAddress = mail.getText().toString();
        if (mailAddress == null || mailAddress.isEmpty()) {
            mail.setError(getResources().getString(R.string.app_error_empty_mail));
            return false;
        } else if (!PatternValidator.validateEmail(mailAddress)) {
            mail.setError(getResources().getString(R.string.app_error_invalid_mail));
            return false;
        }
        return true;
    }

    public boolean validatePassword(View view) {
        String password_str = password.getText().toString();
        if (password_str == null || password_str.isEmpty()) {
            passwordLayout.setErrorEnabled(true);
            password_error.setText(getResources().getString(R.string.app_error_empty_password));
            password_error.setVisibility(View.VISIBLE);
            return false;
        } else if (!PatternValidator.validatePassword(password_str)) {
            passwordLayout.setErrorEnabled(true);
            password_error.setText(getResources().getString(R.string.app_error_invalid_password));
            password_error.setVisibility(View.VISIBLE);
            return false;
        } else {
            passwordLayout.setErrorEnabled(false);
            password_error.setVisibility(View.GONE);
        }
        return true;
    }

    public boolean validateConfirmPassword(View view) {
        String password_str = password.getText().toString();
        String cnf_password_str = confirmPassword.getText().toString();
        if (cnf_password_str == null || cnf_password_str.isEmpty()) {
            confirmPasswordLayout.setErrorEnabled(true);
            cnf_password_error.setText(getResources().getString(R.string.app_error_empty_confirm_password));
            cnf_password_error.setVisibility(View.VISIBLE);
            return false;
        } else if (!password_str.equals(cnf_password_str)) {
            confirmPasswordLayout.setErrorEnabled(true);
            cnf_password_error.setText(getResources().getString(R.string.app_error_invalid_password_mismatch));
            cnf_password_error.setVisibility(View.VISIBLE);
            return false;
        } else {
            confirmPasswordLayout.setErrorEnabled(false);
            cnf_password_error.setVisibility(View.GONE);
        }
        return true;
    }

    public void signUp(View v) {
        if (!(validateUserName(v) && validateMobileNumber(v) && validateMailAddress(v) && validatePassword(v) && validateConfirmPassword(v))) {
            Toast.makeText(this, PLEASE_ENTER_CORRECT_DETAILS, Toast.LENGTH_LONG).show();
            return;
        }
        if (!validateMobNumberAlreadyExist()) {
            return;
        }
        userAdapter.createUser(getContentValues());
        addMobNoToSharedPreferences();
        Toast.makeText(this, USER_ADDED_SUCCESSFULLY, Toast.LENGTH_LONG).show();
        startLoginActivity();

    }

    private void addMobNoToSharedPreferences() {
        getSharedPreferences(getString(R.string.food_order), MODE_PRIVATE).edit().putString(UsersDatabaseAdapter.MOB_FIELD, mobNo.getText().toString()).commit();
    }

    private boolean validateMobNumberAlreadyExist() {
        Cursor c = userAdapter.getUserByMobNo(mobNo.getText().toString());
        if (c.getCount() != 0) {
            mobNo.setError(MOBILE_NUMBER_ALREADY_EXIST);
            Toast.makeText(this, MOBILE_NUMBER_ALREADY_EXIST, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void startLoginActivity() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(UsersDatabaseAdapter.NAME_FIELD, name.getText().toString());
        contentValues.put(UsersDatabaseAdapter.MOB_FIELD, mobNo.getText().toString());
        contentValues.put(UsersDatabaseAdapter.MAIL_FIELD, mail.getText().toString());
        contentValues.put(UsersDatabaseAdapter.PASSWORD_FIELD, password.getText().toString());
        return contentValues;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
