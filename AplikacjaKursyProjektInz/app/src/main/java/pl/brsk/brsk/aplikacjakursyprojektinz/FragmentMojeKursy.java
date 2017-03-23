package pl.brsk.brsk.aplikacjakursyprojektinz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class FragmentMojeKursy extends Fragment {

    private SQLiteHandler db;
    private SessionManager session;
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

    String name = new String();
    String email = new String();
    String user_id = new String();

    public FragmentMojeKursy() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // SqLite database handler
        db = new SQLiteHandler(getActivity().getApplicationContext());

        // session manager
        session = new SessionManager(getActivity().getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();
        name = user.get("name");
        email = user.get("email");
        user_id = user.get("user_id");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_moje_kursy, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Kurs kurs = new Kurs();
                        kurs =  listaKursy.get(position);

                        Intent i = new Intent(getActivity().getApplicationContext(), InfoKursActivity.class);

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
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        //Calling method to get data to fetch data
        getData(user_id);

        //initializing our adapter
        adapter = new CardAdapterMyCourses(listaKursy, getActivity().getApplicationContext());

        //Adding adapter to recyclerview
        //recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext()));
        recyclerView.setAdapter(adapter);


        return view; }

    private void getData(String user_id) {
        //Adding the method to the queue by calling the method getDataFromServer
        requestQueue.add(getDataFromServer(user_id));
        //Incrementing the request counter
    }

    private JsonArrayRequest getDataFromServer(String user_id) {


        //JsonArrayRequest of volley
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(AppConfig.DATA_STWORZONE + user_id,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Calling method parseData to parse the json response
                        parseData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getActivity().getApplicationContext(), "Nie ma więcej kursów", Toast.LENGTH_SHORT).show();
                    }
                });

        //Returning the request
        return jsonArrayRequest;
    }

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


}