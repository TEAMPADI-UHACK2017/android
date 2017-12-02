package tipkuu.padi.com.tipkuu;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import tipkuu.padi.com.tipkuu.client.BackendClient;
import tipkuu.padi.com.tipkuu.client.TipperCallback;
import tipkuu.padi.com.tipkuu.models.Tipee;
import tipkuu.padi.com.tipkuu.models.TipeeCallback;
import tipkuu.padi.com.tipkuu.models.Tipper;

public class MainActivity extends AppCompatActivity {
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
        });

        BackendClient client = new BackendClient(this);
        client.getTipee("c60ae785-4a38-483a-9ed7-7796ea25ea90", new TipeeCallback() {

            @Override
            public void success(Tipee tipee) {
                if (tipee != null) {
                    Log.d(TAG, ">>>>>>>>>>> found !!!! " + tipee.toString());
                } else {
                    Log.e(TAG, ">>>>>>>>>>> not found !!!!");
                }
            }

            @Override
            public void failure(int code, String message) {
                Log.e(TAG, ">>>>>>>>>> " + code + "  message: " + message);
            }
        });

        client.getTipper("hello@katpadi.ph", new TipperCallback() {

            @Override
            public void success(Tipper tipee) {
                if (tipee != null) {
                    Log.d(TAG, ">>>>>>>>>>> found !!!! " + tipee.toString());
                } else {
                    Log.e(TAG, ">>>>>>>>>>> not found !!!!");
                }
            }

            @Override
            public void failure(int code, String message) {
                Log.e(TAG, ">>>>>>>>>> " + code + "  message: " + message);
            }
        });
    }

}
