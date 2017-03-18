package pl.brsk.brsk.aplikacjakursyprojektinz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by borse on 12.03.2017.
 */

public class DodajKursActivity extends AppCompatActivity {

    private SQLiteHandler db;
    private SessionManager session;
    private TextView txtName;
    private TextView txtEmail;
    private TextView txtUser_id;
    private Spinner spinnerFood;
    private ArrayList<Category> categoriesList;
    ProgressDialog pDialog;
    private RequestQueue requestQueue;
    private String URL_CATEGORIES = "http://192.168.0.38/android_login_api/getCategory.php";
    private static Context mCtx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_course);

        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);
        txtUser_id = (TextView) findViewById(R.id.user_id);
        spinnerFood = (Spinner) findViewById(R.id.spinFood);


        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }
        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");
        String user_id = user.get("user_id");

        txtName.setText(name);
        txtEmail.setText(email);
        txtUser_id.setText(user_id);

        categoriesList = new ArrayList<Category>();

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(getDataFromServer());
        populateSpinner();

        Log.d("dodaj kurs activity: ","zaczyna getCategories ");
        //new GetCategories().execute();
        spinnerFood.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                Category kategoria = new Category();
                kategoria = categoriesList.get(position);

                Toast.makeText(getApplicationContext(), kategoria.getTitle() + kategoria.getId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    /*private static View.OnKeyListener Spinner_OnKey = new View.OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                Toast.makeText(getApplicationContext(), "klik na spiner", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        }
    };*/

    //Toast.makeText(DodajKursActivity.this, "klik na spiner", Toast.LENGTH_SHORT).show()

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(DodajKursActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private JsonArrayRequest getDataFromServer() {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL_CATEGORIES,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d("bierzemy id: ","response.lenght: " + response.length());
                        for (int i = 0; i < response.length(); i++) {

                            Category kategoria = new Category();
                            JSONObject json = null;
                            try {
                                //Getting json
                                json = response.getJSONObject(i);

                                kategoria.setId(json.getString("id"));
                                kategoria.setTitle(json.getString("title"));
                                Log.d("bierzemy id: ","bierzemy jsona: " + json.getString("id"));
                                Log.d("bierzemy title: ","bierzemy jsona: " + json.getString("title"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            categoriesList.add(kategoria);
                        }

                        populateSpinner();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        //Returning the request
        return jsonArrayRequest;
    }

    /**
     * Adding spinner data
     * */
    private void populateSpinner() {
        List<String> lables = new ArrayList<String>();

        for (int i = 0; i < categoriesList.size(); i++) {
            Log.d("populateSpinner() ","json: " + categoriesList.get(i).getTitle());
            lables.add(categoriesList.get(i).getTitle());
        }

        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerFood.setAdapter(spinnerAdapter);
    }
}


