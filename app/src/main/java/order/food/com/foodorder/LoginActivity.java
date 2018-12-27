package order.food.com.foodorder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {


    public static final String INVALID_CREDENTIALS = "Invalid Credentials";
    public static final String PLEASE_ENTER_VALID_CREDENTIALS = "Please enter valid credentials!!";
    public static final String LOGIN_SUCCESSFUL = "Login successful!!";

    UsersDatabaseAdapter userAdapter;
    TextInputEditText mobNo, password;
    TextInputLayout passwordLayout;
    TextView password_error;
    TextView signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        initializeDBAsync();

        moveToHomePageIfUserIdPresent();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userAdapter = new UsersDatabaseAdapter(this);

        initializeViews();

        initializeEventListeners();

        fillMobNoIfSignedUp();
    }

    private void fillMobNoIfSignedUp() {
        String mobNo = getSharedPreferences(getString(R.string.food_order), MODE_PRIVATE).getString(UsersDatabaseAdapter.MOB_FIELD, "");
        if (!mobNo.isEmpty()) {
            this.mobNo.setText(mobNo);
        }
    }

    private void initializeDBAsync() {
        DatabaseDataInitializer dataInitialize = new DatabaseDataInitializer(this);
        Thread t1 = new Thread(dataInitialize, "t1");
        t1.start();
    }

    private void initializeViews() {
        mobNo = findViewById(R.id.mob_txt_input);
        password = findViewById(R.id.password_txt_input);
        signUp = findViewById(R.id.signup_txt);

        passwordLayout = findViewById(R.id.password_txt_input_layout);

        password_error = findViewById(R.id.password_error);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userAdapter.close();
    }

    private void initializeEventListeners() {
        mobNo.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) validateMobileNumber(v);
        });

        password.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) validatePassword(v);
        });

        signUp.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });
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

    public boolean validatePassword(View view) {
        String password_txt = password.getText().toString();
        if (password_txt == null || password_txt.isEmpty()) {
            passwordLayout.setErrorEnabled(true);
            password_error.setVisibility(View.VISIBLE);
            password_error.setText(getResources().getString(R.string.app_error_empty_password));
            return false;
        } else {
            passwordLayout.setErrorEnabled(false);
            password_error.setVisibility(View.GONE);
        }
        return true;
    }

    public void login(View view) {
        if (validateMobileNumber(view) && validatePassword(view)) {
            Cursor c = userAdapter.getUser(mobNo.getText().toString(), password.getText().toString());
            if (c.getCount() == 0) {
                Toast.makeText(this, INVALID_CREDENTIALS, Toast.LENGTH_LONG).show();
            } else {
                c.moveToFirst();
                startRestaurantActivity(c.getInt(c.getColumnIndexOrThrow(DatabaseConstant.ID)), c.getString(c.getColumnIndexOrThrow(UsersDatabaseAdapter.NAME_FIELD)), mobNo.getText().toString());
            }

        } else {
            Toast.makeText(this, PLEASE_ENTER_VALID_CREDENTIALS, Toast.LENGTH_LONG).show();
        }
    }

    private void startRestaurantActivity(int id, String name, String mob) {
        initializeSharedPreferences(id, name, mob);
        Intent restaurantIntent = new Intent(this, HomeActivity.class);
        restaurantIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Toast.makeText(this, LOGIN_SUCCESSFUL, Toast.LENGTH_LONG).show();
        startActivity(restaurantIntent);
    }

    private void initializeSharedPreferences(int id, String name, String mob) {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.food_order), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(FrequentRestaurantDatabaseAdapter.USERID, id);
        editor.putString(getString(R.string.user_name), name);
        editor.putString(UsersDatabaseAdapter.MOB_FIELD, mob);
        editor.commit();
        RestaurantBasketDetails.initializeRestaurantDetailsFromSharedPreferences(getSharedPreferences(getString(R.string.food_order), MODE_PRIVATE), id);
    }

    private void moveToHomePageIfUserIdPresent() {
        if (getSharedPreferences(getString(R.string.food_order), MODE_PRIVATE).contains(FrequentRestaurantDatabaseAdapter.USERID)) {
            int userId = getSharedPreferences(getString(R.string.food_order), MODE_PRIVATE).getInt(FrequentRestaurantDatabaseAdapter.USERID, 0);
            RestaurantBasketDetails.initializeRestaurantDetailsFromSharedPreferences(getSharedPreferences(getString(R.string.food_order), MODE_PRIVATE), userId);
            startActivity(new Intent(this, HomeActivity.class));
        }
    }
}
