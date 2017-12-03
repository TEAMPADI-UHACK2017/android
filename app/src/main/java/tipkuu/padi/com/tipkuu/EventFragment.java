package tipkuu.padi.com.tipkuu;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import tipkuu.padi.com.tipkuu.adapter.TipperEventAdapter;
import tipkuu.padi.com.tipkuu.client.Client;
import tipkuu.padi.com.tipkuu.client.OnTransactionsCompleteCallback;
import tipkuu.padi.com.tipkuu.models.Event;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventFragment extends Fragment {
    private static final String ARG_USERID = "user_id";
    private int userId;
    private RecyclerView myRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private TipperEventAdapter myAdapter;

    public void setClient(Client client) {
        this.client = client;
    }

    private Client client;

    public EventFragment() {
        // Required empty public constructor
    }


    public static EventFragment newInstance(int userId, Context context) {
        EventFragment fragment = new EventFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_USERID, userId);
        fragment.setClient(new Client(context));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.userId = getArguments().getInt(ARG_USERID);
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
        client.transactionsAsync(userId, new OnTransactionsCompleteCallback() {
            @Override
            public void onDone(ArrayList<Event> events) {
                myAdapter = new TipperEventAdapter(events);
                myRecyclerView.setAdapter(myAdapter);
            }
        });

        return layout;
    }

}
