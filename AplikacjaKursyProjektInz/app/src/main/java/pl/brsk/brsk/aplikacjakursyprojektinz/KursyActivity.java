package pl.brsk.brsk.aplikacjakursyprojektinz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;

public class KursyActivity extends AppCompatActivity implements RecyclerView.OnScrollChangeListener {

    private SQLiteHandler db;
    private SessionManager session;

    private TextView txtName;
    private TextView txtEmail;
    private TextView txtUser_id;
    Spinner spinnerFood;
    private ArrayList<Category> categoriesList;
    private Category kategoria;
    private String wybranaKategoria;
    private String URL_CATEGORIES = "http://192.168.0.38/android_login_api/getCategory.php";



    //Creating a List of superheroes
    private List<Kurs> listaKursy;

    //Creating Views
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    //Volley Request Queue
    private RequestQueue requestQueue;

    //The request counter to send ?page=1, ?page=2  requests
    private int requestCount = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kursy);

        txtName = (TextView) findViewById(R.id.name);
        spinnerFood = (Spinner)findViewById(R.id.spinFood);


        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        categoriesList = new ArrayList<Category>();

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(getDataFromServer());



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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("KursoMANIAK");
        getSupportActionBar().setLogo(R.drawable.kursomaniak);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KursyActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");
        String user_id = user.get("user_id");

        txtName.setText(name);

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

                            Intent i = new Intent(getApplicationContext(), ShowCourseInfoActivity.class);

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
        getData();

        //Adding an scroll change listener to recyclerview
        recyclerView.setOnScrollChangeListener(this);

        //initializing our adapter
        adapter = new CardAdapter(listaKursy, this);

        //Adding adapter to recyclerview
        //recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext()));
        recyclerView.setAdapter(adapter);


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

    private void populateSpinner() {

        List<String> lables = new ArrayList<String>();

        for (int i = 0; i < categoriesList.size(); i++) {
            Log.d("populateSpinner() ","json: " + categoriesList.get(i).getTitle());
            lables.add(categoriesList.get(i).getTitle());
            Log.d("category ","bierzemy jsona: " + categoriesList.get(i).getTitle());
        }

        // Creating adapter for spinner

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item,lables);

        Log.d("category ","spinnerAdapter: " + spinnerAdapter);

        // Drop down layout style - list view with radio button
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // attaching data adapter to spinner
        spinnerFood.setAdapter(spinnerAdapter);
    }

    //Request to get json from server we are passing an integer here
    //This integer will used to specify the page number for the request ?page = requestcount
    //This method would return a JsonArrayRequest that will be added to the request queue
    private JsonArrayRequest getDataFromServer(int requestCount) {
        //Initializing ProgressBar
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);

        //Displaying Progressbar
        progressBar.setVisibility(View.VISIBLE);
        setProgressBarIndeterminateVisibility(true);

        //JsonArrayRequest of volley
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(AppConfig.DATA_URL + String.valueOf(requestCount),
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
                        Toast.makeText(KursyActivity.this, "Nie ma więcej kursów", Toast.LENGTH_SHORT).show();
                    }
                });

        //Returning the request
        return jsonArrayRequest;
    }

    //This method will get data from the web api
    private void getData() {
        //Adding the method to the queue by calling the method getDataFromServer
        requestQueue.add(getDataFromServer(requestCount));
        //Incrementing the request counter
        requestCount++;
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

    //This method would check that the recyclerview scroll has reached the bottom or not
    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(KursyActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    //Overriden method to detect scrolling
    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        //Ifscrolled at last then
        if (isLastItemDisplaying(recyclerView)) {
            //Calling the method getdata again
            getData();
        }
    }
}
