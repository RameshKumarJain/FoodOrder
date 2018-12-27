package order.food.com.foodorder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternValidator {

    public static Pattern VALID_MOBILE_NUMBER_REGEX = Pattern.compile("^[0-9]{10}$");

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static final Pattern VALID_PASSWORD_REGEX =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{6,10}$");

    public static boolean validateMobileNo(String number){
        return VALID_MOBILE_NUMBER_REGEX.matcher(number).find();
    }

    public static boolean validateEmail(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(email);
        return matcher.find();
    }

    public static boolean validatePassword(String password) {
        Matcher matcher = VALID_PASSWORD_REGEX .matcher(password);
        return matcher.find();
    }


}
