package pl.brsk.brsk.aplikacjakursyprojektinz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FragmentInfoCourse extends Fragment {

    private SQLiteHandler db;
    private SessionManager session;
    private TextView txtCourseTitle;
    private TextView txtCourseDescription;
    private TextView txtCoursePrice;
    private TextView txtCourseCategory;
    private TextView txtIdUzytkownika;
    private TextView txtIdKursu;
    private NetworkImageView mNetworkImageView;
    private ImageLoader mImageLoader;
    private RecyclerView recyclerView;
    private String kurs_id;
    private RecyclerView.LayoutManager layoutManager;
    private List<Lekcja> listaLekcje;
    private RecyclerView.Adapter adapter;
    private Button btnJoin;
    private Button btnZapisany;
    private RequestQueue requestQueue;
    private String czyPokazacButton;
    private ProgressDialog pDialog;
    private RatingBar ratingBar;
    private Button submitRateButton;
    private TextView rateDisplay;
    private TextView textOcen;
    private TextView txtPrzeslane;
    String file;

    public FragmentInfoCourse() {
    }



    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // SqLite database handler
        db = new SQLiteHandler(getActivity().getApplicationContext());

        // session manager
        session = new SessionManager(getActivity().getApplicationContext());

        Bundle args = this.getArguments();
        //kurs_id = args.getString("id", "0");

        //kurs_id = getArguments().getString("id", "0");
        Log.d("Fragment onCreate: ", "wartosc kurs_id " + kurs_id);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_info_course, container, false);

        //txtName = (TextView) findViewById(R.id.name);
        //txtEmail = (TextView) findViewById(R.id.email);
        //txtUser_id = (TextView) findViewById(R.id.user_id);
        txtCourseTitle = (TextView) view.findViewById(R.id.nazwa_kursu);
        txtCourseDescription = (TextView) view.findViewById(R.id.opisKursu);
        txtCoursePrice = (TextView) view.findViewById(R.id.cenaKursu);
        txtCourseCategory = (TextView) view.findViewById(R.id.kategoriaKursu);
        mNetworkImageView = (NetworkImageView)view.findViewById(R.id.networkImageView);
        btnJoin = (Button) view.findViewById(R.id.join);
        btnZapisany = (Button) view.findViewById(R.id.zapisany);

        /*kurs_id=getArguments().getString("id", "1");
        Log.d("Fragment: ","wartosc kurs_id " + kurs_id);*/
        Bundle b = getActivity().getIntent().getExtras();
        kurs_id = b.getString("id");

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        final String id = user.get("user_id");
        String name = user.get("name");
        final String email = user.get("email");
        final String user_id = kurs_id;

        Log.d("pobieramy dane: ","id " + id);
        Log.d("pobieramy dane: ","user_id  " +user_id );

        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        getData(user_id, id);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewLekcje);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        //Initializing our superheroes list
        listaLekcje = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        //initializing our adapter
        adapter = new LekcjaCardAdapter(listaLekcje, getActivity().getApplicationContext());

        //Adding adapter to recyclerview
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext()));
        recyclerView.setAdapter(adapter);

        btnJoin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                insertCourseMember(user_id, id);
                getActivity().finish();
                startActivity(getActivity().getIntent());

            }
        });

        //-----------RatingBar---------------------------
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.rgb(63, 81, 181), PorterDuff.Mode.SRC_ATOP);
        ratingBar.setStepSize(1);
        textOcen = (TextView) view.findViewById(R.id.textViewOcen);
        rateDisplay = (TextView) view.findViewById(R.id.ratingDisplay);
        rateDisplay.setText("Rate:");

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

                if(rating<1.0f){
                    ratingBar.setRating(1.0f);
                    rating = 1;
                }
                //obsluga nacisniecia gwiazdki TODO dodac
                rateDisplay.setText(String.valueOf(rating));

            }
        });

        //-----------RatingBar---------------------------

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Lekcja lekcja = new Lekcja();
                        lekcja = listaLekcje.get(position);

                        Intent i = new Intent(getActivity().getApplicationContext(), InfoKursActivity.class);

                        //i.putExtra("id", lekcja.getId());

                        String id_lekcji  = lekcja.getId();

                        if (Integer.parseInt(czyPokazacButton) == 0){
                            Toast.makeText(getActivity().getApplicationContext(), "Zapisz sie na kurs", Toast.LENGTH_SHORT).show();


                        }
                        else {

                            //Toast.makeText(InfoKursActivity.this, lekcja.getVideo(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity().getApplicationContext(), MovieActivity.class);

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


        return view;
    }

    public void rateSubmit(View view) {
        String ratingValue = String.valueOf(ratingBar.getRating());
        rateDisplay.setText("Rate: " + ratingValue);
        Toast.makeText(getActivity().getApplicationContext(), "Rate: " + ratingValue, Toast.LENGTH_LONG).show();
    }

    private JsonArrayRequest insertdata(String user_id, String id){

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(AppConfig.DATA_INSERT_COURSE_MEMBER + String.valueOf(user_id) + "&user_id=" + String.valueOf(id),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Toast.makeText(getActivity().getApplicationContext(), "Zapisano", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity().getApplicationContext(), "błąd zapisu", Toast.LENGTH_SHORT).show();
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

                        Toast.makeText(getActivity().getApplicationContext(), "Błąd buttona", Toast.LENGTH_SHORT).show();
                    }
                });

        //Returning the request
        return jsonArrayRequest;

    }

    private JsonArrayRequest getDataFromServer(final int user_id) {


        Log.d("DebugTag", "Value user_id:" + user_id);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(AppConfig.DATA_LESSONS_URL + String.valueOf(user_id),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        parseData(response, String.valueOf(user_id));
                        //parseLessons(response, String.valueOf(user_id));

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity().getApplicationContext(), "Wszystkie lekcje", Toast.LENGTH_SHORT).show();
                        //Hiding the progressbar
                    }
                });

        //Returning the request
        return jsonArrayRequest;
    }

    private JsonArrayRequest getLessonData(final int user_id) {

        Log.d("DebugTag", "Value user_id:" + user_id);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(AppConfig.DATA_LESSON + String.valueOf(user_id),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        parseLessons(response, String.valueOf(user_id));

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity().getApplicationContext(), "Wszystkie lekcje", Toast.LENGTH_SHORT).show();
                    }
                });

        //Returning the request
        return jsonArrayRequest;
    }

    private void getData(String user_id, String id) {
        //Adding the method to the queue by calling the method getDataFromServer
        requestQueue.add(getDataFromServer(Integer.parseInt(user_id)));
        requestQueue.add(getLessonData(Integer.parseInt(user_id)));
        requestQueue.add(getButtonInfo(user_id, id));
        //Incrementing the request counter
    }

    private void parseLessons(JSONArray array, String user_id) {
        String big_image = null;


        Log.d("DebugTag", "Value array.length() w parseData:" + array.length());
        for (int i = 0; i < array.length(); i++) {

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

            } catch (JSONException e) {
                e.printStackTrace();
            }
            listaLekcje.add(lekcja);

        }
    }

    private void parseData(JSONArray array, String user_id) {
        String big_image = null;
        String course_title = null;
        String course_description = null;
        String course_price = null;
        String course_category = null;


        Log.d("DebugTag", "Value array.length() w parseData:" + array.length());
        for (int i = 0; i < array.length(); i++) {
            //Creating the superhero object
            //Lekcja lekcja = new Lekcja();

            JSONObject json = null;
            try {

                //Getting json
                json = array.getJSONObject(i);

                //Adding data to the superhero object
                /*lekcja.setId(json.getString(AppConfig.TAG_LESSON_ID));
                lekcja.setDescription(json.getString(AppConfig.TAG_LESSON_lesson_description));
                lekcja.setTitle(json.getString(AppConfig.TAG_LESSON_lesson_title));
                lekcja.setVideo(json.getString(AppConfig.TAG_LESSON_video));
                lekcja.setFree(json.getString(AppConfig.TAG_LESSON_free));
                lekcja.setIs_enabled(json.getString(AppConfig.TAG_LESSON_is_enabled));*/

                big_image = json.getString(AppConfig.TAG_LESSON_bigimage);
                course_title = json.getString(AppConfig.TAG_LESSON_course_title);
                course_price = json.getString(AppConfig.TAG_LESSON_course_price);
                course_description = json.getString(AppConfig.TAG_LESSON_course_description);
                course_category = json.getString(AppConfig.TAG_LESSON_category);

                mImageLoader = CustomVolleyRequest.getInstance(getActivity().getApplicationContext()).getImageLoader();
                mImageLoader.get(AppConfig.URL_IMAGE + "kursy/" + String.valueOf(user_id) + "/" + big_image, ImageLoader.getImageListener(mNetworkImageView, 0, 0));
                mNetworkImageView.setImageUrl(AppConfig.URL_IMAGE + "kursy/" + String.valueOf(user_id) + "/" + big_image, mImageLoader);
                mNetworkImageView.setAdjustViewBounds(true);
                mNetworkImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);



            } catch (JSONException e) {
                e.printStackTrace();
            }
            //listaLekcje.add(lekcja);

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
            ratingBar.setVisibility(View.GONE);
            rateDisplay.setVisibility(View.GONE);
            textOcen.setVisibility(View.GONE);
        }
        else {
            btnJoin.setVisibility(View.GONE);
            btnZapisany.setVisibility(View.GONE);
            ratingBar.setVisibility(View.VISIBLE);
            rateDisplay.setVisibility(View.VISIBLE);
            textOcen.setVisibility(View.VISIBLE);
        }

        return czyPokazacButton;

    }

    private void insertCourseMember(String user_id, String id) {
        //Adding the method to the queue by calling the method getDataFromServer
        requestQueue.add(insertdata(user_id, id));
    }


}