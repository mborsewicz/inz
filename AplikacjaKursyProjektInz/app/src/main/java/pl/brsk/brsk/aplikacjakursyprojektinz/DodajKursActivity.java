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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by borse on 12.03.2017.
 */


/* !!!!!!!!!!!!!!NIE UZYWANE ACTIVITY - ZASTPIONE ADDCOURSEACTIVITY I FRAGMENTAMI!!!!!!!!!!*/
public class DodajKursActivity extends AppCompatActivity {

    private SQLiteHandler db;
    private SessionManager session;
    private TextView txtName;
    private TextView txtEmail;
    private TextView txtUser_id;
    private Spinner spinnerFood;
    private ArrayList<Category> categoriesList;
    private RequestQueue requestQueue;
    private String URL_CATEGORIES = "http://192.168.0.38/android_login_api/getCategory.php";
    private static Context mCtx;
    private EditText editTextDlugiOpis;
    private EditText editTextNazwaKursu;
    private EditText editTextKrotkiOpis;
    private EditText editTextCena;
    private Button btnDodajKurs;
    private Category kategoria;
    private String wybranaKategoria;
    private ProgressDialog pDialog;
    private List<Kurs> listaKursy;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private int requestCount = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_course);

        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);
        txtUser_id = (TextView) findViewById(R.id.user_id);
        spinnerFood = (Spinner) findViewById(R.id.spinFood);
        editTextDlugiOpis = (EditText) findViewById(R.id.dlugi_opis);
        editTextKrotkiOpis = (EditText) findViewById(R.id.krotki_opis);
        editTextNazwaKursu = (EditText) findViewById(R.id.nazwa_kursu);
        editTextCena= (EditText) findViewById(R.id.cena);
        btnDodajKurs = (Button) findViewById(R.id.btnDodajKurs);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }
        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        final String name = user.get("name");
        final String email = user.get("email");
        final String user_id = user.get("user_id");

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

                kategoria = categoriesList.get(position);
                wybranaKategoria = kategoria.getId();

                Toast.makeText(getApplicationContext(), kategoria.getTitle() + kategoria.getId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        btnDodajKurs.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                String title = editTextNazwaKursu.getText().toString().trim();
                String description = editTextDlugiOpis.getText().toString().trim();
                String shortDescription = editTextKrotkiOpis.getText().toString().trim();
                String price = editTextCena.getText().toString().trim();

                insertCourse(user_id, wybranaKategoria, title, description, shortDescription, price);

            }

        });

        //Initializing Views
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Kurs kurs = new Kurs();
                        kurs =  listaKursy.get(position);

                        Intent i = new Intent(getApplicationContext(), InfoKursActivity.class);

                        i.putExtra("id",kurs.getId());
                        startActivity(i);

                           /* Intent intent = new Intent(KursyActivity.this, InfoKursActivity.class);
                            startActivity(intent);
                            finish();*/
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        //Initializing our superheroes list
        listaKursy = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        //Calling method to get data to fetch data
        getData(user_id);

        //initializing our adapter
        adapter = new CardAdapterMyCourses(listaKursy, this);

        //Adding adapter to recyclerview
        //recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext()));
        recyclerView.setAdapter(adapter);
    }

    private void getData(String user_id) {
        //Adding the method to the queue by calling the method getDataFromServer
        requestQueue.add(getDataFromServer(user_id));
        //Incrementing the request counter
    }
    private JsonArrayRequest getDataFromServer(String user_id) {
        //Initializing ProgressBar
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);

        //Displaying Progressbar
        progressBar.setVisibility(View.VISIBLE);
        setProgressBarIndeterminateVisibility(true);

        //JsonArrayRequest of volley
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(AppConfig.DATA_STWORZONE + user_id,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Calling method parseData to parse the json response
                        parseData(response);
                        //Hiding the progressbar
                        progressBar.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        //If an error occurs that means end of the list has reached
                        Toast.makeText(DodajKursActivity.this, "Nie ma więcej kursów", Toast.LENGTH_SHORT).show();
                    }
                });

        //Returning the request
        return jsonArrayRequest;
    }

    //This method will parse json data
    private void parseData(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            //Creating the superhero object
            Kurs kurs = new Kurs();
            JSONObject json = null;
            try {
                //Getting json
                json = array.getJSONObject(i);

                //Adding data to the superhero object
                kurs.setId(json.getString(AppConfig.TAG_ID));
                kurs.setImage(json.getString(AppConfig.TAG_IMAGE_URL));
                kurs.setTitle(json.getString(AppConfig.TAG_NAME));
                kurs.setPrice(json.getString(AppConfig.TAG_PUBLISHER));
                kurs.setShortDescription(json.getString(AppConfig.TAG_SHORT_DESC));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Adding the superhero object to the list
            listaKursy.add(kurs);
        }

        //Notifying the adapter that data has been added or changed
        adapter.notifyDataSetChanged();
    }

    private void insertCourse(final String user_id, final String wybranaKategoria, final String title, final String description, final String shortDescription, final String price) {
        //Adding the method to the queue by calling the method getDataFromServer
        //requestQueue.add(insertData(user_id, wybranaKategoria, title, description, shortDescription, price));
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.DATA_INSERT_COURSE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    String id = jObj.getString("id_kursu");
                    String id_sekcji = jObj.getString("id_sekcji");


                    Toast.makeText(getApplicationContext(), "Dodano kurs " +id + " o sekcji id = " + id_sekcji , Toast.LENGTH_LONG).show();

                    Intent i = new Intent(getApplicationContext(), InfoKursActivity.class);

                    i.putExtra("id",id);
                    startActivity(i);
                    finish();

                }catch (JSONException e){}
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Błąd", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
                params.put("category_id", wybranaKategoria);
                params.put("title", title);
                params.put("price", price);
                params.put("short_description", shortDescription);
                params.put("description", description);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void insertSection(final String user_id, final String wybranaKategoria, final String title, final String description, final String shortDescription, final String price) {
        //Adding the method to the queue by calling the method getDataFromServer
        //requestQueue.add(insertData(user_id, wybranaKategoria, title, description, shortDescription, price));
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.DATA_INSERT_COURSE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Dodano kurs", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Błąd", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
                params.put("category_id", wybranaKategoria);
                params.put("title", title);
                params.put("price", price);
                params.put("short_description", shortDescription);
                params.put("description", description);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

 /*   private JsonArrayRequest insertData(String user_id, String wybranaKategoria, String title, String description, String shortDescription, String price){

        //http://localhost/android_login_api/addCourse.php?user_id=3&category_id=1&title=KursTestowy2&price=30&short_description=krotkiopis&description=okokokokokoko

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(AppConfig.DATA_INSERT_COURSE + String.valueOf(user_id) + "&category_id=" + wybranaKategoria + "&title=" + title + "&price=" + price + "&short_description=" + shortDescription.trim() + "&description=" + description.trim(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Toast.makeText(DodajKursActivity.this, "Dodano kurs!", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DodajKursActivity.this, "Wystąpił błąd", Toast.LENGTH_SHORT).show();
                    }
                });
        return jsonArrayRequest;
    }*/

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


