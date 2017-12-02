package tipkuu.padi.com.tipkuu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import tipkuu.padi.com.tipkuu.models.Tipper;
import tipkuu.padi.com.tipkuu.utils.Utils;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccountFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_USERID = "user_id";
    private OnFragmentInteractionListener mListener;
    private String mUserId;
    private ImageView profileImage;
    private TextView profileName;
    private ImageButton logoutButton;
    private TextView totalGivenText;

    public AccountFragment() {
        // Required empty public constructor
    }

    public static AccountFragment newInstance(String userId) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getString(ARG_USERID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_account, container, false);
        profileImage = (ImageView)layout.findViewById(R.id.profileImage);
        logoutButton = (ImageButton)layout.findViewById(R.id.logoutButton);
        totalGivenText = (TextView)layout.findViewById(R.id.totalGiven);
        logoutButton.setOnClickListener(this);
        profileName = (TextView)layout.findViewById(R.id.name);
        Tipper loginInfo = Utils.getLoginInfo(getContext());
        profileName.setText(loginInfo.getName());
        totalGivenText.setText(Integer.toString(loginInfo.getTipsCount()));
        Picasso.with(getContext()).load(loginInfo.getPhotoUrl()).into(profileImage);
        return layout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        if (view == this.logoutButton) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Confirm")
                    .setMessage("Do you really want to Log out?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            Utils.logOut(getContext());
                            getActivity().finish();
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            startActivity(intent);
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
