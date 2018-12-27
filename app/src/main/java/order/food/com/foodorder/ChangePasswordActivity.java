package order.food.com.foodorder;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ChangePasswordActivity extends AppCompatActivity {

    public static final String PASSWORD_UPDATED_SUCCESSFULLY = "Password updated successfully!!";
    public static final String INVALID_CURRENT_PASSWORD = "Invalid Current Password!!";

    TextInputEditText currentPassword, newPassword, newConfirmPassword;
    TextInputLayout currentPasswordLayout, newPasswordLayout, newConfirmPasswordLayout;
    TextView currentPasswordError, newPasswordError, newConfirmPasswordError;

    UsersDatabaseAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        userAdapter = new UsersDatabaseAdapter(this);

        initializeToolBar();
        initializeViews();
        initializeEventListeners();
    }

    private void initializeToolBar() {
        Toolbar toolbar = findViewById(R.id.password_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_arrow_back_black_24dp));

        toolbar.setNavigationOnClickListener((v) -> onBackPressed());
    }

    private void initializeViews() {

        currentPassword = findViewById(R.id.current_password_txt);
        newPassword = findViewById(R.id.new_password_txt);
        newConfirmPassword = findViewById(R.id.new_cnf_password_txt);

        currentPasswordLayout = findViewById(R.id.current_password);
        newPasswordLayout = findViewById(R.id.new_password);
        newConfirmPasswordLayout = findViewById(R.id.new_confirm_password);

        currentPasswordError = findViewById(R.id.current_password_error);
        newPasswordError = findViewById(R.id.new_password_error);
        newConfirmPasswordError = findViewById(R.id.new_cnf_password_error);
    }

    private void initializeEventListeners() {
        currentPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                validatePassword(currentPassword, currentPasswordLayout, currentPasswordError);
        });

        newPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) validatePassword(newPassword, newPasswordLayout, newPasswordError);
        });

        newConfirmPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) validateConfirmPassword();
        });
    }

    public boolean validatePassword(TextInputEditText password, TextInputLayout passwordLayout, TextView password_error) {
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
        } else if (password == newPassword && password_str.equals(currentPassword.getText().toString())) {
            passwordLayout.setErrorEnabled(true);
            password_error.setText(R.string.new_password_same_as_current_error);
            password_error.setVisibility(View.VISIBLE);

        } else {
            passwordLayout.setErrorEnabled(false);
            password_error.setVisibility(View.GONE);
        }
        return true;
    }

    public boolean validateConfirmPassword() {
        String password_str = newPassword.getText().toString();
        String cnf_password_str = newConfirmPassword.getText().toString();
        if (cnf_password_str == null || cnf_password_str.isEmpty()) {
            newConfirmPasswordLayout.setErrorEnabled(true);
            newConfirmPasswordError.setText(getResources().getString(R.string.app_error_empty_confirm_password));
            newConfirmPasswordError.setVisibility(View.VISIBLE);
            return false;
        } else if (!password_str.equals(cnf_password_str)) {
            newConfirmPasswordLayout.setErrorEnabled(true);
            newConfirmPasswordError.setText(getResources().getString(R.string.app_error_invalid_password_mismatch));
            newConfirmPasswordError.setVisibility(View.VISIBLE);
            return false;
        } else {
            newConfirmPasswordLayout.setErrorEnabled(false);
            newConfirmPasswordError.setVisibility(View.GONE);
        }
        return true;
    }

    public void changePassword(View v) {
        if (validatePassword(currentPassword, currentPasswordLayout, currentPasswordError) &&
                validatePassword(newPassword, newPasswordLayout, newPasswordError) && validateConfirmPassword()) {

            ContentValues contentValues = new ContentValues();
            contentValues.put(UsersDatabaseAdapter.PASSWORD_FIELD, newConfirmPassword.getText().toString());

            String mobNo = getSharedPreferences(getString(R.string.food_order), MODE_PRIVATE).getString(UsersDatabaseAdapter.MOB_FIELD, "");

            Cursor c = userAdapter.getUser(mobNo, currentPassword.getText().toString());

            if (c.getCount() == 1) {
                userAdapter.updateUser(contentValues, DatabaseConstant.ID + "=?", new String[]{String.valueOf(HomeActivity.userId)});
                Toast.makeText(this, PASSWORD_UPDATED_SUCCESSFULLY, Toast.LENGTH_LONG).show();
                onBackPressed();
            } else {
                Toast.makeText(this, INVALID_CURRENT_PASSWORD, Toast.LENGTH_LONG).show();
                currentPasswordError.setText(INVALID_CURRENT_PASSWORD);
                currentPasswordError.setVisibility(View.VISIBLE);
            }
        } else {
            Snackbar.make(v, SignUpActivity.PLEASE_ENTER_CORRECT_DETAILS, 1000).show();
        }
    }

    @Override
    protected void onDestroy() {
        RestaurantBasketDetails.saveRestaurantDetailsToSharedPreferences(getSharedPreferences(getString(R.string.food_order), MODE_PRIVATE));
        userAdapter.close();
        super.onDestroy();
    }

}
