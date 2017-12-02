package tipkuu.padi.com.tipkuu.client;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Executor;

import okhttp3.OkHttpClient;
import tipkuu.padi.com.tipkuu.models.Tipee;
import tipkuu.padi.com.tipkuu.models.TipeeCallback;
import tipkuu.padi.com.tipkuu.models.Tipper;

import static android.content.ContentValues.TAG;

public class BackendClient {
    public static final String API_ENDPOINT = "http://tipkuu.herokuapp.com";
    private final Context context;
    private final OkHttpClient client;

    public BackendClient(Context context) {
        this.context = context;
        this.client = new OkHttpClient();
    }

    public String heartbeat() throws IOException {
        String url = API_ENDPOINT + "/api/heartbeat";
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .build();
        okhttp3.Response response = client.newCall(request).execute();
        try {
            JSONObject jsonBody = new JSONObject(response.body().string());
            return jsonBody.getString("timestamp");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Tipper getTipperSync(String email) {
        final String url = API_ENDPOINT + "/tippers/find?email=" + email;

        Log.d(TAG, " >>>>>>>>>>>>>>>>>>>> " + url);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .build();
        try {
            okhttp3.Response response = client.newCall(request).execute();
            String bodyStr = response.body().string();
            Log.d(TAG, "response = " + bodyStr);
            if (response.isSuccessful()) {
                return Tipper.fromString(bodyStr);
            } else {
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, " >>>>>>>>>>>>>>>>>>>> " +e.getMessage());
            e.printStackTrace();
        }
        return null;
    };

    public Tipee getTipeeSync(String code) {
        final String url = API_ENDPOINT + "/tipees/find?qr_code=" + code;

        Log.d(TAG, " >>>>>>>>>>>>>>>>>>>> " + url);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .build();
        try {
            okhttp3.Response response = client.newCall(request).execute();
            String bodyStr = response.body().string();
            Log.d(TAG, "response = " + bodyStr);
            if (response.isSuccessful()) {
                return Tipee.fromString(bodyStr);
            } else {
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, " >>>>>>>>>>>>>>>>>>>> " +e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void getTipee(final String code, final TipeeCallback tipeeCallback) {
        AsyncTask<Void, Void, Tipee> task = new AsyncTask<Void,Void,Tipee>() {
            @Override
            protected Tipee doInBackground(Void... voids) {
               return getTipeeSync(code);
            }

            @Override
            protected void onPostExecute(Tipee o) {
                super.onPostExecute(o);
                tipeeCallback.success(o);
            }
        };

        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void getTipper(final String email, final TipperCallback tipperCallback) {
        AsyncTask<Void, Void, Tipper> task = new AsyncTask<Void,Void,Tipper>() {
            @Override
            protected Tipper doInBackground(Void... voids) {
                return getTipperSync(email);
            }

            @Override
            protected void onPostExecute(Tipper o) {
                super.onPostExecute(o);
                tipperCallback.success(o);
            }
        };

        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
