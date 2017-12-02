package tipkuu.padi.com.tipkuu.models;

import com.google.gson.Gson;

public class Event {
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTipee_id() {
        return tipee_id;
    }

    public void setTipee_id(int tipee_id) {
        this.tipee_id = tipee_id;
    }

    public int getTipper_id() {
        return tipper_id;
    }

    public void setTipper_id(int tipper_id) {
        this.tipper_id = tipper_id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    int tipee_id;
    int tipper_id;
    String amount;
    int rating;

    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Event fromString(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, Event.class);
    }
}
