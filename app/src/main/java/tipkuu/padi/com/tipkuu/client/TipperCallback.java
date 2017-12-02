package tipkuu.padi.com.tipkuu.client;

import tipkuu.padi.com.tipkuu.models.Tipper;

/**
 * Created by jedld on 12/2/17.
 */

public interface TipperCallback {
    void success(Tipper o);
    void failure(int code, String message);
}
