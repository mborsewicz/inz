package pl.brsk.brsk.aplikacjakursyprojektinz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.HashMap;
import java.util.List;


public class FragmentComments extends Fragment {

    private SQLiteHandler db;
    private SessionManager session;
    String name = new String();
    String email = new String();
    String user_id = new String();
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Komentarz> listaKomentarze;
    private RequestQueue requestQueue;
    private RecyclerView.Adapter adapter;
    private String kurs_id;

    public FragmentComments() {
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
        View view=inflater.inflate(R.layout.fragment_comments, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        //Initializing our superheroes list

        listaKomentarze = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        Bundle b = getActivity().getIntent().getExtras();
        kurs_id = b.getString("id");

        //Calling method to get data to fetch data
        getData(kurs_id);

        //initializing our adapter
        adapter = new CardAdapterComments(listaKomentarze, getActivity().getApplicationContext());

        //Adding adapter to recyclerview
        //recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext()));
        recyclerView.setAdapter(adapter);

        return view; }


    private void getData(String kurs_id) {
        //Adding the method to the queue by calling the method getDataFromServer
        requestQueue.add(getDataFromServer(kurs_id));
        //Incrementing the request counter
    }

    private JsonArrayRequest getDataFromServer(String kurs_id) {


        //JsonArrayRequest of volley
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(AppConfig.DATA_KOMENTARZE + kurs_id,
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
            Komentarz komentarz = new Komentarz();
            JSONObject json = null;
            try {
                //Getting json
                json = array.getJSONObject(i);

                //Adding data to the superhero object
                komentarz.setId(json.getString("id"));

                Log.d("JSON: ", "wartosc ID " + json.getString("id"));
                komentarz.setName(json.getString("name"));
                komentarz.setCreated(json.getString("created"));
                Log.d("JSON: ", "wartosc CREATED " + json.getString("id"));
                komentarz.setText(json.getString("text"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Adding the superhero object to the list
            listaKomentarze.add(komentarz);
        }

        //Notifying the adapter that data has been added or changed
        adapter.notifyDataSetChanged();
    }
}