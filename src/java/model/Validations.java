package model;

public class Validations {

    public static boolean isPasswordValid(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$");
    }

    public static boolean isMobileNumber(String mobile) {
        return mobile.matches("^07[01245678]{1}[0-9]{7}$");
    }
}
