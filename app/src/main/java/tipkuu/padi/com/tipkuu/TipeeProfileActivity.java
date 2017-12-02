package tipkuu.padi.com.tipkuu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import tipkuu.padi.com.tipkuu.models.Tipee;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class TipeeProfileActivity extends AppCompatActivity {
    Tipee tipee;
    private TextView profileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipee_profile);
        this.profileName = (TextView)findViewById(R.id.textProfileName);
        Intent intent = getIntent();
        this.tipee = Tipee.fromString(intent.getStringExtra("tipee"));
        profileName.setText(tipee.getName());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
}
