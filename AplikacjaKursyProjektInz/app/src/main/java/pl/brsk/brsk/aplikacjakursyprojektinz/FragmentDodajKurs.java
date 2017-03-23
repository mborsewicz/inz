package pl.brsk.brsk.aplikacjakursyprojektinz;

        import android.app.ProgressDialog;
        import android.content.Context;
        import android.content.Intent;
        import android.os.Bundle;
        import android.support.annotation.Nullable;
        import android.support.v4.app.Fragment;
        import android.support.v7.widget.RecyclerView;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ProgressBar;
        import android.widget.Spinner;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.android.volley.Request;
        import com.android.volley.RequestQueue;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.JsonArrayRequest;
        import com.android.volley.toolbox.StringRequest;
        import com.android.volley.toolbox.Volley;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;


public class FragmentDodajKurs extends Fragment{


    private SQLiteHandler db;
    private SessionManager session;
    private TextView txtName;
    private TextView txtEmail;
    private TextView txtUser_id;
    Spinner spinnerFood;
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
    Context contextt;

    String user_id = new String();

    public FragmentDodajKurs() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        spinnerFood = (Spinner)getView().findViewById(R.id.spinFood);

        categoriesList = new ArrayList<Category>();

        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(getDataFromServer());

        spinnerFood.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                kategoria = categoriesList.get(position);
                wybranaKategoria = kategoria.getId();

                Toast.makeText(getActivity().getApplicationContext(), kategoria.getTitle() + kategoria.getId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new SQLiteHandler(getActivity().getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        user_id = user.get("user_id");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_dodaj_kurs, container, false);
        //spinnerFood = (Spinner)getView().findViewById(R.id.spinFood);
        editTextDlugiOpis = (EditText) view.findViewById(R.id.dlugi_opis);
        editTextKrotkiOpis = (EditText) view.findViewById(R.id.krotki_opis);
        editTextNazwaKursu = (EditText) view.findViewById(R.id.nazwa_kursu);
        editTextCena= (EditText) view.findViewById(R.id.cena);
        btnDodajKurs = (Button) view.findViewById(R.id.btnDodajKurs);

        btnDodajKurs.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                String title = editTextNazwaKursu.getText().toString().trim();
                String description = editTextDlugiOpis.getText().toString().trim();
                String shortDescription = editTextKrotkiOpis.getText().toString().trim();
                String price = editTextCena.getText().toString().trim();

                insertCourse(user_id, wybranaKategoria, title, description, shortDescription, price);

            }

        });

        // Inflate the layout for this fragment
        return view;
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


                    Toast.makeText(getActivity().getApplicationContext(), "Dodano kurs " +id + " o sekcji id = " + id_sekcji , Toast.LENGTH_LONG).show();

                    Intent i = new Intent(getActivity().getApplicationContext(), InfoKursActivity.class);

                    i.putExtra("id",id);
                    startActivity(i);
                    getActivity().finish();

                }catch (JSONException e){}
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), "Błąd", Toast.LENGTH_LONG).show();
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

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.spinner_item,lables);

        Log.d("category ","spinnerAdapter: " + spinnerAdapter);

        // Drop down layout style - list view with radio button
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // attaching data adapter to spinner
        spinnerFood.setAdapter(spinnerAdapter);
    }

}