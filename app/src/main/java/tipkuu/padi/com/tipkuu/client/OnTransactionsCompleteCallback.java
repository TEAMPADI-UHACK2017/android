package tipkuu.padi.com.tipkuu.client;

import java.util.ArrayList;

import tipkuu.padi.com.tipkuu.models.Event;

/**
 * Created by jedld on 12/3/17.
 */

public interface OnTransactionsCompleteCallback {
    void onDone(ArrayList<Event> events);
}
