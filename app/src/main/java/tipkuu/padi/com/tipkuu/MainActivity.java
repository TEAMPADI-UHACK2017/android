package tipkuu.padi.com.tipkuu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import tipkuu.padi.com.tipkuu.adapter.TipperEventAdapter;
import tipkuu.padi.com.tipkuu.client.Client;
import tipkuu.padi.com.tipkuu.models.Tipee;
import tipkuu.padi.com.tipkuu.models.TipeeCallback;
import tipkuu.padi.com.tipkuu.models.Tipper;
import tipkuu.padi.com.tipkuu.utils.Utils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AccountFragment.OnFragmentInteractionListener {

    int currentTabSelected = 0;

    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final String TAG = MainActivity.class.getName();
    private Tipper user;
    private RecyclerView myRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private TipperEventAdapter myAdapter;
    private Client client;
    private ImageView tabTrophy;
    private ImageView tabFriends;
    private ImageView tabPlaces;
    private ImageView tabAccount;
    private EventFragment eventFragment;
    private BadgeFragment badgeFragment;
    private AccountFragment accountFragment;
    private FriendsFragment friendsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.actionbar_main, null);
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);

        actionBar.setCustomView(v, layout);
        this.client = new Client(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        tabTrophy = (ImageView)findViewById(R.id.tab_trophy);
        tabFriends = (ImageView)findViewById(R.id.tab_friends);
        tabPlaces = (ImageView)findViewById(R.id.tab_places);
        tabAccount = (ImageView)findViewById(R.id.tab_account);
        tabTrophy.setImageResource(R.drawable.ic_trophy_outline_black_36dp);


        tabTrophy.setOnClickListener(this);
        tabFriends.setOnClickListener(this);
        tabPlaces.setOnClickListener(this);
        tabAccount.setOnClickListener(this);

        ImageView imageView = (ImageView) findViewById(R.id.profileImage);

        user = Utils.getLoginInfo(this);
        Picasso.with(this).load(user.getPhotoUrl()).placeholder(R.drawable.ic_account_white_24dp).into(imageView);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                client.getTipee("c60ae785-4a38-483a-9ed7-7796ea25ea90", new TipeeCallback() {
//                    @Override
//                    public void success(Tipee tipee) {
//                        Intent tipeeIntent = new Intent(MainActivity.this, TipeeProfileActivity.class);
//                        tipeeIntent.putExtra("tipee", tipee.toString());
//                        startActivity(tipeeIntent);
//                    }
//
//                    @Override
//                    public void failure(int code, String message) {
//                        Log.e(TAG, ">>>>>>>>>> " + code + "  message: " + message);
//                    }
//                });


                showQRCodeActivity();
            }
        });



//
//        client.getTipper("hello@katpadi.ph", new TipperCallback() {
//
//            @Override
//            public void success(Tipper tipee) {
//                if (tipee != null) {
//                    Log.d(TAG, ">>>>>>>>>>> found !!!! " + tipee.toString());
//                } else {
//                    Log.e(TAG, ">>>>>>>>>>> not found !!!!");
//                }
//            }
//
//            @Override
//            public void failure(int code, String message) {
//                Log.e(TAG, ">>>>>>>>>> " + code + "  message: " + message);
//            }
//        });

        // Create new fragment and transaction
        this.eventFragment = new EventFragment();
        this.badgeFragment = new BadgeFragment();
        this.accountFragment = AccountFragment.newInstance(Utils.getLoginInfo(this).getAccountNum());
        this.friendsFragment = FriendsFragment.newInstance(Utils.getLoginInfo(this).getAccountNum());

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, badgeFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void updateCurrentTabState() {

    }

    private void showQRCodeActivity() {
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Intent showQrCodeCapture = new Intent(MainActivity.this, ScanCode2Activity.class);
            startActivity(showQrCodeCapture);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CAMERA)) {

            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tab_trophy:
                if (currentTabSelected != 0 ) {
                    currentTabSelected = 0;
                    switchTabs(currentTabSelected);
                }
                break;
            case R.id.tab_friends:
                if (currentTabSelected != 1) {
                    currentTabSelected = 1;
                    switchTabs(currentTabSelected);
                }
                break;
            case R.id.tab_places:
                if (currentTabSelected != 2) {
                    currentTabSelected = 2;
                    switchTabs(currentTabSelected);
                }
                break;
            case R.id.tab_account:
                if (currentTabSelected != 3) {
                    currentTabSelected = 3;
                    switchTabs(currentTabSelected);
                }
                break;
        }
    }

    private void switchTabs(int currentTabSelected) {
        tabTrophy.setImageResource(R.drawable.ic_trophy_outline_white_36dp);
        tabFriends.setImageResource(R.drawable.ic_account_multiple_white_36dp);
        tabPlaces.setImageResource(R.drawable.ic_map_marker_white_36dp);
        tabAccount.setImageResource(R.drawable.ic_account_card_details_white_36dp);
        Fragment fragment = null;
        switch (currentTabSelected) {
            case 0:
                tabTrophy.setImageResource(R.drawable.ic_trophy_outline_black_36dp);
                fragment = badgeFragment;
                break;
            case 1:
                tabFriends.setImageResource(R.drawable.ic_account_multiple_black_36dp);
                fragment = friendsFragment;
                break;
            case 2:
                tabPlaces.setImageResource(R.drawable.ic_map_marker_black_36dp);
                fragment = eventFragment;
                break;
            case 3:
                tabAccount.setImageResource(R.drawable.ic_account_card_details_black_36dp);
                fragment = accountFragment;
                break;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
