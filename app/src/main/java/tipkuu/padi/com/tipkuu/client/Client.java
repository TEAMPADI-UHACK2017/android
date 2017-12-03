package tipkuu.padi.com.tipkuu.client;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executor;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import tipkuu.padi.com.tipkuu.models.Event;
import tipkuu.padi.com.tipkuu.models.Tipee;
import tipkuu.padi.com.tipkuu.models.TipeeCallback;
import tipkuu.padi.com.tipkuu.models.Tipper;

import static android.content.ContentValues.TAG;

public class Client {
    public static final String API_ENDPOINT = "http://tipkuu.herokuapp.com";
    private final Context context;
    private final OkHttpClient client;

    public Client(Context context) {
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

    public ArrayList<Event> transactions(int userId) {
        ArrayList<Event> eventArrayList = new ArrayList<Event>();
        final String url = API_ENDPOINT + "/transactions";
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .build();

        try {
            okhttp3.Response response = client.newCall(request).execute();
            JSONArray jsonBody = new JSONArray(response.body().string());
            for(int i =0; i < jsonBody.length(); i++) {
                JSONObject eventObj = jsonBody.getJSONObject(i);
                Event event = Event.fromString(eventObj.toString());
                Tipper tipper = getTipperSync(event.getTipper_id());
                event.setTipper(tipper);
                eventArrayList.add(event);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return eventArrayList;
    }

    public void transactionsAsync(final int userId, final OnTransactionsCompleteCallback completeCallback) {
        AsyncTask<Void, Void, ArrayList<Event>> asyncTask = new AsyncTask<Void, Void, ArrayList<Event>>() {

            @Override
            protected ArrayList<Event> doInBackground(Void... voids) {
                return transactions(userId);
            }

            @Override
            protected void onPostExecute(ArrayList<Event> events) {
                super.onPostExecute(events);
                completeCallback.onDone(events);
            }
        };
        asyncTask.execute();
    };

    public String transfer(String code, String amount, int userId, int tpid) {
        final String url = API_ENDPOINT + "/tippers/" + userId + "/transfer";
        FormBody formBody = new FormBody.Builder()
                .add("tipee_id", Integer.toString(tpid))
                .add("amount", amount)
                .add("code", code)
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(formBody)
                .build();


        okhttp3.Response response = null;
        try {
            response = client.newCall(request).execute();
            JSONObject jsonBody = new JSONObject(response.body().string());
            if (jsonBody.getString("state").equals("SUCCESS")) {
                return jsonBody.getString("transferId");
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    };

    public void transferAsync(final String code, final String amount, final int userId, final int tpid, final OnTransferCallback callback) {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                return transfer(code, amount, userId, tpid);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s!= null) {
                    callback.onSuccess(s);
                } else {
                    callback.onFailure();
                }

            }
        };
        task.execute();
    }

    public String getRedirectUrl(int userId) {
        final String url = API_ENDPOINT + "/tippers/" + userId +"/get_token";
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
               JSONObject responseObj = new JSONObject(bodyStr);
               return responseObj.getString("url");
            } else {
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, " >>>>>>>>>>>>>>>>>>>> " +e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getRedirectUrlAsync(final int userId, final OnUrlCallback callback) {
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                return getRedirectUrl(userId);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                callback.onDone(s);
            }
        };
        asyncTask.execute();
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

    public Tipper getTipperSync(int id) {
        final String url = API_ENDPOINT + "/tippers/" + id + ".json";

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
                try {
                    return getTipeeSync(code);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
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
