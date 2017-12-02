package tipkuu.padi.com.tipkuu.models;

import com.google.gson.Gson;

public class LoginInfo {
    String userId;
    private String lastName;
    private String firstName;
    private String MiddleName;

    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static LoginInfo fromString(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, LoginInfo.class);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return MiddleName;
    }

    public void setMiddleName(String middleName) {
        MiddleName = middleName;
    }
}
