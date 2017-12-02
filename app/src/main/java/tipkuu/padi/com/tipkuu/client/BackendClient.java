package tipkuu.padi.com.tipkuu.client;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;

public class BackendClient {
    public static final String API_ENDPOINT = "http://10.0.2.2:8080";
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

}
