package tipkuu.padi.com.tipkuu.models;

public interface TipeeCallback {
    public void success(Tipee tipee);
    public void failure(int code, String message);
}
