package tipkuu.padi.com.tipkuu;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import tipkuu.padi.com.tipkuu.client.Client;
import tipkuu.padi.com.tipkuu.client.OnTransferCallback;
import tipkuu.padi.com.tipkuu.client.OnUrlCallback;
import tipkuu.padi.com.tipkuu.models.Tipee;
import tipkuu.padi.com.tipkuu.models.Tipper;
import tipkuu.padi.com.tipkuu.utils.Utils;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class TipeeProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int UNIONBANK_LOGIN = 1;
    private static final String TAG = TipeeProfileActivity.class.getName();
    Tipee tipee;
    private TextView profileName;
    private ActionBar actionBar;
    private EditText tipAmount;
    private Button actionTip;
    private ImageView profileImage;
    private Client client;
    private Tipper tipper;
    private View formContainer;
    private View thankYouContainer;
    private Button actionClose;
    private ImageView profileImage2;
    private RatingBar ratingsBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipee_profile);
        this.tipAmount = (EditText)findViewById(R.id.tip_amount);
        this.actionTip = (Button)findViewById(R.id.actionTipButton);
        this.profileImage = (ImageView)findViewById(R.id.profileImage);
        this.profileImage2 = (ImageView)findViewById(R.id.profileImage2);
        this.actionClose = (Button)findViewById(R.id.actionClose);
        this.ratingsBar = (RatingBar)findViewById(R.id.ratings_bar);
//        this.profileName = (TextView)findViewById(R.id.textProfileName);
        Intent intent = getIntent();
        this.formContainer =  findViewById(R.id.tipFormContainer);
        this.thankYouContainer = findViewById(R.id.thankYouFormContainer);
        this.tipee = Tipee.fromString(intent.getStringExtra("tipee"));
        Picasso.with(this).load(tipee.getProfileUrl()).placeholder(R.drawable.ic_account_white_24dp).into(profileImage);
        Picasso.with(this).load(tipee.getProfileUrl()).placeholder(R.drawable.ic_account_white_24dp).into(profileImage2);
        actionBar = getSupportActionBar();
        actionBar.setTitle(tipee.getName());
        this.actionClose.setOnClickListener(this);
        this.actionTip.setOnClickListener(this);
        this.client = new Client(this);
        this.tipper = Utils.getLoginInfo(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        if (view == actionTip) {
            float  amount = Float.parseFloat(tipAmount.getText().toString());
            if (ratingsBar.getRating() > 1 && amount == 0) {
                tipAmount.requestFocus();
                Toast.makeText(this, "Tip amount cannot be zero if rating is above 1 stars", Toast.LENGTH_LONG).show();
            }
            this.client.getRedirectUrlAsync(this.tipper.getId(), new OnUrlCallback() {
                @Override
                public void onDone(String s) {
                    Intent intent = new Intent(TipeeProfileActivity.this, UnionBankLoginActivity.class);
                    intent.putExtra("url", s);
                    startActivityForResult(intent, UNIONBANK_LOGIN);
                }
            });

        } else if (view == actionClose) {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UNIONBANK_LOGIN) {
            if (resultCode == RESULT_OK) {
                String code = data.getStringExtra("code");
                int rating = Math.round(ratingsBar.getRating());
                final ProgressDialog pd = new ProgressDialog(this);
                pd.setMessage("Processing.. Please wait");
                pd.show();

                client.transferAsync(code, tipAmount.getText().toString(), tipper.getId(), tipee.getId(), rating, new OnTransferCallback() {
                    @Override
                    public void onSuccess(String s) {
                        pd.cancel();
                        formContainer.setVisibility(View.GONE);
                        thankYouContainer.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure() {
                        pd.cancel();
                        Toast.makeText(TipeeProfileActivity.this, "Error while performing transfer", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
}
