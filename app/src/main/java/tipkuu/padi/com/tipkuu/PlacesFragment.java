package tipkuu.padi.com.tipkuu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link PlacesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlacesFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_USERID = "user_id";
    private View place1;
    private View place2;

    public PlacesFragment() {
        // Required empty public constructor
    }

    public static PlacesFragment newInstance(String userId) {
        PlacesFragment fragment = new PlacesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_places, container, false);
        this.place1 = layout.findViewById(R.id.place1);
        place1.setOnClickListener(this);
        this.place2 = layout.findViewById(R.id.place2);
        place2.setOnClickListener(this);
        return layout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        if (view == place1) {
            Intent intent = new Intent(getActivity(), EstablishmentActivity.class);
            startActivity(intent);
        } else if (view == place2) {
            Intent intent = new Intent(getActivity(), EstablishmentActivity.class);
            startActivity(intent);
        }
    }
}
