package tipkuu.padi.com.tipkuu;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import tipkuu.padi.com.tipkuu.client.Client;
import tipkuu.padi.com.tipkuu.models.ScanStateCallback;
import tipkuu.padi.com.tipkuu.models.Tipee;
import tipkuu.padi.com.tipkuu.models.TipeeCallback;
import tipkuu.padi.com.tipkuu.views.PreviewOverlay;


public class ScanCode2Activity extends Activity implements SurfaceHolder.Callback {

    private static final String TAG = ScanCode2Activity.class.getName();
    private static final int ADD_ITEM = 1;
    private Camera mCamera;

    ImageScanner scanner;

    private boolean previewing = true;

    static {
        System.loadLibrary("iconv");
    }

    private PreviewOverlay previewOverlay;
    private TextView statusView = null;
    private boolean inScanMode = false;
    private String mode = "barcode";
    private SurfaceView previewView;
    private boolean surfaceAvailable = false;
    private Handler autoFocusHandler;

    Hashtable<String, ScanState> scanStateHashtable = new Hashtable<>();

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_scan_code);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        previewView = (SurfaceView) findViewById(R.id.preview_view);
        statusView = (TextView) findViewById(R.id.status_view);
        previewOverlay = (PreviewOverlay) findViewById(R.id.viewfinder_view);
        autoFocusHandler = new Handler();
        Intent intent = getIntent();
        if (intent != null) {
            String passedMode = intent.getStringExtra("mode");
            if (passedMode != null) {
                mode = passedMode;
            }
        }

        scanner = new ImageScanner();
        scanner.setConfig(0, net.sourceforge.zbar.Config.X_DENSITY, 3);
        scanner.setConfig(0, net.sourceforge.zbar.Config.Y_DENSITY, 3);


        Log.d(TAG, "mode = " + mode);
        Log.v(TAG, "onCreate()");
        inScanMode = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()");
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dismissPanel();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onResume() {
        super.onResume();
        SurfaceHolder surfaceHolder = previewView.getHolder();

        if (surfaceAvailable) {
            newOpenCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
        }

    }


    public void queryBarCodeInformation(final String barcode) {
    }

    protected void showScanner() {
        inScanMode = true;
        statusView.setText(getString(R.string.scan_qr_code));
        statusView.setVisibility(View.VISIBLE);
        previewOverlay.setVisibility(View.VISIBLE);
    }

    protected void showResults() {
        inScanMode = false;
        statusView.setVisibility(View.GONE);
        previewOverlay.setVisibility(View.GONE);
    }

    public void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            previewing = false;
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };


    float[] convertIntarray(int input[]) {
        float[] result = new float[input.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = input[i];
        }
        return result;
    }

    int[] convertFloatArray(float input[]) {
        int[] result = new int[input.length];
        for (int i = 0; i < input.length; i++) {
            result[i] = (int) input[i];
        }
        return result;
    }

    private static class ScanState {

        public static final int IN_PROGRESS = 0;
        public static final int COMPLETE = 1;
        public static final int MISSING = 2;

        public Tipee getItemCodeObjects() {
            return parseObjects;
        }

        Tipee parseObjects;

        private ScanStateCallback callbackListener;

        public int getState() {
            return state;
        }

        int state = IN_PROGRESS;

        public synchronized void addListener(ScanStateCallback callbackListener) {
            this.callbackListener = callbackListener;
            if (state == COMPLETE) {
                callbackListener.onComplete(parseObjects);
            } else if (state == MISSING) {
                callbackListener.onMissing();
            }
        }

        public synchronized void updateState(int state, Tipee parseObject) {
            this.state = state;
            if (state == COMPLETE) {
                if (callbackListener != null) {
                    callbackListener.onComplete(parseObjects);
                }
                this.parseObjects = parseObjects;
            } else if (state == MISSING) {
                if (callbackListener != null) {
                    callbackListener.onMissing();
                }
            }
        }


    }

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);


            if (result != 0) {
                SymbolSet syms = scanner.getResults();

                ArrayList<Pair<String, CodeInfo>> scanList = new ArrayList<>();
                for (Symbol sym : syms) {
                    String barCodeString = sym.getData();
                    int[] bounds = sym.getBounds();
                    transformBounds(bounds, size.width, size.height, previewOverlay.getWidth(), previewOverlay.getHeight());

                    CodeInfo codeInfo = new CodeInfo();
                    codeInfo.setBounds(bounds);
                    codeInfo.setStatus(CodeInfo.UNKNOWN);

                    ScanState info = scanStateHashtable.get(barCodeString);
                    if (info == null) {
                        ScanState scanState = new ScanState();
                        scanStateHashtable.put(barCodeString, scanState);
                        lookupItemInBackground(barCodeString, scanState);
                    } else if (info.getState() == ScanState.COMPLETE) {
                        Tipee po = info.getItemCodeObjects();
                        if (po != null) {
                            codeInfo.setStatus(CodeInfo.FOUND);
                            codeInfo.setLabel(po.getName());
                        }
                    }
                    scanList.add(new Pair(barCodeString, codeInfo));
                }

                final String candidateBarcode = previewOverlay.clearAndAddBounds(scanList);

                if (candidateBarcode != null) {
                    previewing = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            decodeBarcode(candidateBarcode);
                        }
                    });

                }
            } else {
                previewOverlay.clearBounds();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    previewOverlay.invalidate();
                }
            });
        }
    };

    protected void lookupItemInBackground(final String currentBarcode, final ScanState scanState) {
        AsyncTask<Void, Void, Boolean> itemLookupTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                Tipee tipee = new Client(ScanCode2Activity.this).getTipeeSync(currentBarcode);
                if (tipee!=null) {
                    Log.d(TAG," >>>>>>>>>>>>>>>> loading " + tipee.getName());
                    scanState.updateState(ScanState.COMPLETE, tipee);
                } else {
                    scanState.updateState(ScanState.MISSING, null);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Boolean isMissing) {

            }
        };
        itemLookupTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void transformBounds(int[] bounds, int width, int height, int targetWidth, int targetHeight) {
        int x = bounds[0];
        int y = height - bounds[1];
        int b_width = bounds[2];
        int b_height = bounds[3];
        float rWidth = (float) targetHeight / (float) width;
        float rHeight = (float) targetWidth / (float) height;
        bounds[0] = (int) ((float) (y - b_height) * rHeight);
        bounds[1] = (int) ((float) x * rWidth);
        bounds[2] = (int) ((float) b_height * rHeight);
        bounds[3] = (int) ((float) b_width * rWidth);
    }

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    // Put up our own UI for how to handle the decodBarcodeFormated contents.
    private void decodeBarcode(final String barcodeString) {
        onPause();
        showResults();
        Client client = new Client(this);
        client.getTipee(barcodeString, new TipeeCallback() {

            @Override
            public void success(Tipee tipee) {
                if (tipee != null) {
                    finish();
                    Intent tipeeIntent = new Intent(ScanCode2Activity.this, TipeeProfileActivity.class);
                    tipeeIntent.putExtra("tipee", tipee.toString());
                    startActivity(tipeeIntent);
                } else {
                    statusView.setText(getString(R.string.code_not_found));
                    onResume();
                }
            }

            @Override
            public void failure(int code, String message) {
                statusView.setText(getString(R.string.code_not_found));
                onResume();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ADD_ITEM) {
            }
        }
    }


    public void dismissPanel() {
        FragmentManager fManager = getFragmentManager();
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null)
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        surfaceAvailable = true;
        newOpenCamera(holder);
    }


    private CameraHandlerThread mThread = null;

    private class CameraHandlerThread extends HandlerThread {
        Handler mHandler = null;

        CameraHandlerThread() {
            super("CameraHandlerThread");
            start();
            mHandler = new Handler(getLooper());
        }

        synchronized void notifyCameraOpened() {
            notify();
        }

        void openCamera(final SurfaceHolder holder) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    startCamera(holder);
                    notifyCameraOpened();
                }
            });
            try {
                wait();
            } catch (InterruptedException e) {
                Log.w(TAG, "wait was interrupted");
            }
        }
    }

    private void newOpenCamera(SurfaceHolder holder) {
        if (mThread == null) {
            mThread = new CameraHandlerThread();
        }

        synchronized (mThread) {
            mThread.openCamera(holder);
        }
    }


    private void startCamera(SurfaceHolder holder) {
        mCamera = getCameraInstance();

        setupAutoFocusMode();


        mCamera.setPreviewCallback(previewCb);
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
            previewing = true;
            mCamera.autoFocus(autoFocusCB);
        } catch (IOException e) {
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showScanner();
            }
        });

    }

    private void setupAutoFocusMode() {
        Log.d(TAG, ">>>>>>>>>>>> setting up autofocusmode");
        Camera.Parameters parameters = mCamera.getParameters();
        for (String f : parameters.getSupportedFocusModes()) {
            if (f == Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                autoFocusCB = null;
                break;
            }
        }
        mCamera.setParameters(parameters);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, ">>>>>>>>> surface changed");
          /*
         * If your preview can change or rotate, take care of those events here.
         * Make sure to stop the preview before resizing or reformatting it.
         */
        if (holder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        try {
            // Hard code camera surface rotation 90 degs to match Activity view in portrait
            mCamera.setDisplayOrientation(90);

            mCamera.setPreviewDisplay(holder);
            mCamera.setPreviewCallback(previewCb);
            mCamera.startPreview();
            previewing = true;
            mCamera.autoFocus(autoFocusCB);
        } catch (Exception e) {
            Log.d("DBG", "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceAvailable = false;
    }


    public static class CodeInfo {
        public static final int UNKNOWN = 0;
        public static final int FOUND = 1;
        private int[] bounds;
        private int status;
        private String label;

        public void setBounds(int[] bounds) {
            this.bounds = bounds;
        }

        public int[] getBounds() {
            return bounds;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }
}
