package pl.brsk.brsk.aplikacjakursyprojektinz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView txtName;
    private TextView txtEmail;
    private TextView txtUser_id;
    private TextView top5_najnowsze;
    private TextView top5_ocena;
    private TextView top5_wyswietlenia;

    private SQLiteHandler db;
    private SessionManager session;

    private RecyclerView recyclerView1;
    private RecyclerView recyclerView2;


    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter1;
    private RecyclerView.Adapter adapter2;

    private List<Kurs> listaKursy1;
    private List<Kurs> listaKursy2;
    private RequestQueue requestQueue;
    private RequestQueue requestQueue2;

    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private String mActivityTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    ProgressDialog pDialog;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("KursoMANIAK");
        getSupportActionBar().setLogo(R.drawable.kursomaniak);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);
        txtUser_id = (TextView) findViewById(R.id.user_id);
        top5_ocena = (TextView) findViewById(R.id.top5_ocena);
        top5_najnowsze = (TextView) findViewById(R.id.top5_najnowsze);
        top5_wyswietlenia = (TextView) findViewById(R.id.top5_wyswietlenia);

        Typeface elegant_lux = Typeface.createFromAsset(getAssets(), "fonts/ElegantLux-Mager.otf");
        top5_najnowsze.setTypeface(elegant_lux);
        top5_ocena.setTypeface(elegant_lux);
        top5_wyswietlenia.setTypeface(elegant_lux);

        // ******************************* NIE AKTUALNE **************************************
        //dodanie drawera
/*        mDrawerList = (ListView)findViewById(R.id.navList);
        mActivityTitle = getTitle().toString();
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList.bringToFront();
        mDrawerLayout.requestLayout();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        addDrawerItems();
        setupDrawer();*/

        //ActionBar bar = getActionBar();
        // bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#b4daff")));
        // ******************************* NIE AKTUALNE **************************************

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
        email = user.get("email");
        String user_id = user.get("user_id");

        // Displaying the user details on the screen
        txtName.setText(name);
        txtEmail.setText(email);
        txtUser_id.setText(user_id);


        initNavigationDrawer(email);
        //Initializing Views
        recyclerView1 = (RecyclerView) findViewById(R.id.recyclerViewNajwyzszaOcena);
        recyclerView2 = (RecyclerView) findViewById(R.id.recyclerViewTopSprzedaz);


        recyclerView1.setHasFixedSize(true);
        //layoutManager = new KursyHorizontalLayout(this, 120, 120);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        //------------2
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        recyclerView1.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recyclerView1, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Kurs kurs = new Kurs();
                        kurs = listaKursy1.get(position);

                        Intent i = new Intent(getApplicationContext(), InfoKursActivity.class);

                        i.putExtra("id", kurs.getId());
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
        recyclerView2.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recyclerView2, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Kurs kurs = new Kurs();
                        kurs = listaKursy2.get(position);

                        Intent i = new Intent(getApplicationContext(), InfoKursActivity.class);

                        i.putExtra("id", kurs.getId());
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
        listaKursy1 = new ArrayList<>();
        listaKursy2 = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);
        requestQueue2 = Volley.newRequestQueue(this);

        //Calling method to get data to fetch data
        getData();

        //initializing our adapter
        adapter1 = new CardAdapterHorizontal(listaKursy1, this);
        adapter2 = new CardAdapterHorizontal(listaKursy2, this);
        //Adding adapter to recyclerview
        //recyclerView1.addItemDecoration(new DividerItemDecoration(getApplicationContext()));
        recyclerView1.setAdapter(adapter1);
        recyclerView2.addItemDecoration(new DividerItemDecoration(getApplicationContext()));
        recyclerView2.setAdapter(adapter2);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private JsonArrayRequest getDataFromServer1(int requestCount) {

        //Initializing ProgressBar
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);

        //Displaying Progressbar
        progressBar.setVisibility(View.VISIBLE);
        setProgressBarIndeterminateVisibility(true);

        //JsonArrayRequest of volley
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(AppConfig.DATA_URL_TOP5,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Calling method parseData to parse the json response
                        parseData1(response);
                        //Hiding the progressbar
                        progressBar.setVisibility(View.GONE);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Hiding the progressbar
                        progressBar.setVisibility(View.GONE);
                    }
                });

        //Returning the request
        return jsonArrayRequest;
    }

    private JsonArrayRequest getDataFromServer2(int requestCount) {

        //Initializing ProgressBar
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);

        //Displaying Progressbar
        progressBar.setVisibility(View.VISIBLE);
        setProgressBarIndeterminateVisibility(true);

        //JsonArrayRequest of volley
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(AppConfig.DATA_URL_TOP5,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Calling method parseData to parse the json response
                        parseData2(response);
                        //Hiding the progressbar
                        progressBar.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Hiding the progressbar
                        progressBar.setVisibility(View.GONE);
                    }
                });

        //Returning the request
        return jsonArrayRequest;
    }

    //This method will get data from the web api
    private void getData() {
        //Adding the method to the queue by calling the method getDataFromServer
        requestQueue.add(getDataFromServer1(1));
        requestQueue2.add(getDataFromServer2(1));
    }

    //This method will parse json data
    private void parseData1(JSONArray array) {
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
            listaKursy1.add(kurs);
        }
        //Notifying the adapter that data has been added or changed
        adapter1.notifyDataSetChanged();
    }

    private void parseData2(JSONArray array) {
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
            listaKursy2.add(kurs);
        }

        //Notifying the adapter that data has been added or changed
        adapter2.notifyDataSetChanged();
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    public void initNavigationDrawer(String email) {

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.przegladaj_kursy:
                        Intent intent = new Intent(MainActivity.this, KursyActivity.class);
                        startActivity(intent);

                        break;
                    case R.id.dodaj_kurs:
                        Intent intent2 = new Intent(MainActivity.this, DodajKursActivity.class);
                        startActivity(intent2);

                        break;
                    case R.id.moje_konto:
                        Toast.makeText(getApplicationContext(), "Trash", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.wyloguj:
                        logoutUser();

                }
                return true;
            }
        });
        View header = navigationView.getHeaderView(0);
        TextView tv_email = (TextView) header.findViewById(R.id.tv_email);
        tv_email.setText(email);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
    /*----------------------------------------------------------------------------------------
     -----------------------OLD DRAWER------------------------------------------------------------
     -----------------------------------------------------------------------------------------*/
/*
    private void addDrawerItems(){
        String[] osArray = { "Moje kursy", "Przeglądaj kursy", "Dodaj kurs", "Mój profil", "Wyloguj" };
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    Intent intent = new Intent(MainActivity.this, KursyActivity.class);
                    startActivity(intent);
                    finish();
                }
                if(position == 4){
                    logoutUser();
                }
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /*//* Called when a drawer has settled in a completely open state.
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Menu");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /*//* Called when a drawer has settled in a completely closed state.
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
    /* ------------------------OLD DRAWER------------------------------------------------------- */
}
