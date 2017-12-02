package tipkuu.padi.com.tipkuu.models;

public interface ScanStateCallback {
    void onComplete(Tipee parseObjects);

    void onMissing();
}
