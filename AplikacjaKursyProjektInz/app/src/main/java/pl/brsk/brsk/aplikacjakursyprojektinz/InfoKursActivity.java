package pl.brsk.brsk.aplikacjakursyprojektinz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.toolbox.NetworkImageView;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by brsk on 2016-12-11.
 */
public class InfoKursActivity extends AppCompatActivity {


    private TextView txtCourseTitle;
    private TextView txtCourseDescription;
    private TextView txtCoursePrice;
    private TextView txtCourseCategory;
    private TextView txtIdUzytkownika;
    private TextView txtIdKursu;
    private NetworkImageView mNetworkImageView;
    private ImageLoader mImageLoader;
    private RecyclerView recyclerView;
    private SQLiteHandler db;
    private SessionManager session;
    private String kurs_id;
    private RecyclerView.LayoutManager layoutManager;
    private List<Lekcja> listaLekcje;
    private RecyclerView.Adapter adapter;
    private Button btnJoin;
    private Button btnZapisany;
    private RequestQueue requestQueue;
    String czyPokazacButton;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_kursy);

        //txtName = (TextView) findViewById(R.id.name);
        //txtEmail = (TextView) findViewById(R.id.email);
        //txtUser_id = (TextView) findViewById(R.id.user_id);
        txtCourseTitle = (TextView) findViewById(R.id.nazwa_kursu);
        txtCourseDescription = (TextView) findViewById(R.id.opisKursu);
        txtCoursePrice = (TextView) findViewById(R.id.cenaKursu);
        txtCourseCategory = (TextView) findViewById(R.id.kategoriaKursu);
        mNetworkImageView = (NetworkImageView) findViewById(R.id.networkImageView);
        btnJoin = (Button) findViewById(R.id.join);
        btnZapisany = (Button) findViewById(R.id.zapisany);


        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            kurs_id = extras.getString("id");
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        final String id = user.get("user_id");
        String name = user.get("name");
        final String email = user.get("email");
        final String user_id = kurs_id;

        requestQueue = Volley.newRequestQueue(this);
        getData(user_id, id);

        //txtName.setText(name);
        //txtEmail.setText(email);
       // txtUser_id.setText(user_id);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("KursoMANIAK");
        getSupportActionBar().setLogo(R.drawable.kursomaniak);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewLekcje);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Initializing our superheroes list
        listaLekcje = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        //initializing our adapter
        adapter = new LekcjaCardAdapter(listaLekcje, this);

        //Adding adapter to recyclerview
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext()));
        recyclerView.setAdapter(adapter);

        btnJoin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                insertCourseMember(user_id, id);
                finish();
                overridePendingTransition( 0, 0);
                startActivity(getIntent());
                overridePendingTransition( 0, 0);

            }

        });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Lekcja lekcja = new Lekcja();
                        lekcja = listaLekcje.get(position);

                        Intent i = new Intent(getApplicationContext(), InfoKursActivity.class);

                        //i.putExtra("id", lekcja.getId());

                        String id_lekcji  = lekcja.getId();

                        if (Integer.parseInt(czyPokazacButton) == 0){
                            Toast.makeText(InfoKursActivity.this, "Zapisz sie na kurs", Toast.LENGTH_SHORT).show();


                        }
                        else {

                            //Toast.makeText(InfoKursActivity.this, lekcja.getVideo(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MovieActivity.class);

                            intent.putExtra("film_url", lekcja.getVideo());
                            intent.putExtra("kurs_id", user_id);
                            startActivity(intent);
                        }

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

    }

    private JsonArrayRequest insertdata(String user_id, String id){

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(AppConfig.DATA_INSERT_COURSE_MEMBER + String.valueOf(user_id) + "&user_id=" + String.valueOf(id),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Toast.makeText(InfoKursActivity.this, "Zapisano", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(InfoKursActivity.this, "błąd zapisu", Toast.LENGTH_SHORT).show();
                    }
                });
            return jsonArrayRequest;
    }

    private JsonArrayRequest getButtonInfo(String user_id, String id){

        Log.d("DebugTag", "Value user_id:" + user_id);
        //JsonArrayRequest of volley
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(AppConfig.DATA_GET_BUTTON_INFO + String.valueOf(user_id) + "&user_id=" + String.valueOf(id),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Calling method parseData to parse the json response
                        parseDataButton(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(InfoKursActivity.this, "Błąd buttona", Toast.LENGTH_SHORT).show();
                    }
                });

        //Returning the request
        return jsonArrayRequest;

    }

    private JsonArrayRequest getDataFromServer(int user_id) {

        //Initializing ProgressBar
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);

        //Displaying Progressbar
        progressBar.setVisibility(View.VISIBLE);
        setProgressBarIndeterminateVisibility(true);

        Log.d("DebugTag", "Value user_id:" + user_id);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(AppConfig.DATA_LESSONS_URL + String.valueOf(user_id),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        parseData(response);
                        //Hiding the progressbar
                        progressBar.setVisibility(View.GONE);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(InfoKursActivity.this, "Wszystkie lekcje", Toast.LENGTH_SHORT).show();
                        //Hiding the progressbar
                        progressBar.setVisibility(View.GONE);
                    }
                });

        //Returning the request
        return jsonArrayRequest;
    }

    private void getData(String user_id, String id) {
        //Adding the method to the queue by calling the method getDataFromServer
        requestQueue.add(getDataFromServer(Integer.parseInt(user_id)));
        requestQueue.add(getButtonInfo(user_id, id));
        //Incrementing the request counter
    }

    private void parseData(JSONArray array) {
        String course_title = null;
        String course_description = null;
        String course_price = null;
        String course_category = null;


        Log.d("DebugTag", "Value array.length() w parseData:" + array.length());
        for (int i = 0; i < array.length(); i++) {
            //Creating the superhero object
            Lekcja lekcja = new Lekcja();

            JSONObject json = null;
            try {

                //Getting json
                json = array.getJSONObject(i);

                //Adding data to the superhero object
                lekcja.setId(json.getString(AppConfig.TAG_LESSON_ID));
                lekcja.setDescription(json.getString(AppConfig.TAG_LESSON_lesson_description));
                lekcja.setTitle(json.getString(AppConfig.TAG_LESSON_lesson_title));
                lekcja.setVideo(json.getString(AppConfig.TAG_LESSON_video));
                lekcja.setFree(json.getString(AppConfig.TAG_LESSON_free));
                lekcja.setIs_enabled(json.getString(AppConfig.TAG_LESSON_is_enabled));
                lekcja.setBigImage(json.getString(AppConfig.TAG_LESSON_bigimage));

                course_title = json.getString(AppConfig.TAG_LESSON_course_title);
                course_price = json.getString(AppConfig.TAG_LESSON_course_price);
                course_description = json.getString(AppConfig.TAG_LESSON_course_description);
                course_category = json.getString(AppConfig.TAG_LESSON_category);

                mImageLoader = CustomVolleyRequest.getInstance(this.getApplicationContext()).getImageLoader();
                mImageLoader.get(AppConfig.URL_IMAGE + lekcja.getBigImage(), ImageLoader.getImageListener(mNetworkImageView, 0, 0));
                mNetworkImageView.setImageUrl(AppConfig.URL_IMAGE + lekcja.getBigImage(), mImageLoader);
                mNetworkImageView.setAdjustViewBounds(true);
                mNetworkImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);



            } catch (JSONException e) {
                e.printStackTrace();
            }
            listaLekcje.add(lekcja);

        }
        Log.d("DebugCourse_title", "Value course_title przed dodaniem:" + course_title);
        //daneKursu.add(course_title);
        txtCourseTitle.setText(course_title);
        txtCourseDescription.setText(course_description);
        txtCoursePrice.setText(course_price);
        txtCourseCategory.setText(course_category);
        //Notifying the adapter that data has been added or changed
        //adapter.notifyDataSetChanged();
    }

    private String parseDataButton(JSONArray array) {



            JSONObject json = null;
            try {
                //Getting json
                json = array.getJSONObject(0);
                czyPokazacButton = json.getString("show");


            } catch (JSONException e) {
                e.printStackTrace();
            }

        if (Integer.parseInt(czyPokazacButton) == 0){
            btnJoin.setVisibility(View.VISIBLE);
            btnZapisany.setVisibility(View.GONE);
        }
        else {
            btnJoin.setVisibility(View.GONE);
            btnZapisany.setVisibility(View.VISIBLE);
        }

        return czyPokazacButton;

    }

    private void insertCourseMember(String user_id, String id) {
        //Adding the method to the queue by calling the method getDataFromServer
        requestQueue.add(insertdata(user_id, id));
    }
}




