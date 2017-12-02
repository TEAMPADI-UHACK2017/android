package tipkuu.padi.com.tipkuu.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class Tipee {
    @SerializedName("qr_code")
    String qrCode;
    String name;

    @SerializedName("merchant_id")
    int merchantId;

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    String profileUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @SerializedName("account_num")
    String accountNumber;

    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Tipee fromString(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, Tipee.class);
    }
}
