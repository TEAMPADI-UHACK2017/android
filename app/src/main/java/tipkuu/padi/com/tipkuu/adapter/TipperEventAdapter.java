package tipkuu.padi.com.tipkuu.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Array;
import java.util.ArrayList;

import tipkuu.padi.com.tipkuu.R;
import tipkuu.padi.com.tipkuu.models.Event;

/**
 * Created by jedld on 12/2/17.
 */

public class TipperEventAdapter extends RecyclerView.Adapter<EventViewHolder> {

    ArrayList<Event> events = new ArrayList<Event>();

    public TipperEventAdapter(ArrayList<Event> events) {
        this.events = events;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View layout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item, parent, false);
        EventViewHolder vh = new EventViewHolder(layout, parent.getContext());
        return vh;
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.setEvent(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
