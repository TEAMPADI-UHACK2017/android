package tipkuu.padi.com.tipkuu;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import tipkuu.padi.com.tipkuu.adapter.TipperEventAdapter;
import tipkuu.padi.com.tipkuu.models.Event;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventFragment extends Fragment {
    private static final String ARG_USERID = "user_id";
    private String userId;
    private RecyclerView myRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private TipperEventAdapter myAdapter;


    public EventFragment() {
        // Required empty public constructor
    }


    public static EventFragment newInstance(String userId) {
        EventFragment fragment = new EventFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.userId = getArguments().getString(ARG_USERID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_event, container, false);
        myRecyclerView = (RecyclerView) layout.findViewById(R.id.tipperEvents);
        mLayoutManager = new LinearLayoutManager(getContext());
        myRecyclerView.setLayoutManager(mLayoutManager);

        ArrayList<Event> eventsArrayList = new ArrayList<Event>();
        eventsArrayList.add(new Event());
        myAdapter = new TipperEventAdapter(eventsArrayList);
        myRecyclerView.setAdapter(myAdapter);
        return layout;
    }

}
