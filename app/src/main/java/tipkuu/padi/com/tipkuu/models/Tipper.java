package tipkuu.padi.com.tipkuu.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class Tipper {
    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @SerializedName("account_num")
    String accountNum;

    @SerializedName("email")
    String email;

    int id;
    String name;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @SerializedName("url")
    String url;

    @SerializedName("photo_url")
    String photoUrl;

    public int getTipsCount() {
        return tipsCount;
    }

    public void setTipsCount(int tipsCount) {
        this.tipsCount = tipsCount;
    }

    @SerializedName("tips_count")
    int tipsCount;

    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Tipper fromString(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, Tipper.class);
    }
}
