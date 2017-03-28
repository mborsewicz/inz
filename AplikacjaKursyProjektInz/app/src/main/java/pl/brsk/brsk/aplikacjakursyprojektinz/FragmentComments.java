package pl.brsk.brsk.aplikacjakursyprojektinz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    private EditText editTextComment;
    private Button btnDodajKomentarz;
    private AppCompatCheckBox checkBoxRegulamin;
    private String czyPokazacButton;

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
        editTextComment = (EditText) view.findViewById(R.id.edit_text_comment);
        btnDodajKomentarz = (Button) view.findViewById(R.id.btnDodajKomentarz);
        checkBoxRegulamin = (AppCompatCheckBox)view.findViewById(R.id.checkBoxRegulamin);

        //Initializing our superheroes list

        listaKomentarze = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        Bundle b = getActivity().getIntent().getExtras();
        kurs_id = b.getString("id");

        if (b != null) {
            for (String key : b.keySet()) {
                Object value = b.get(key);
                Log.d("FragmentComments Petla!", String.format("%s %s (%s)", key, value.toString(), value.getClass().getName()));
            }
        }

        //Calling method to get data to fetch data
        getData(kurs_id);
        getData(kurs_id, user_id);

        //initializing our adapter
        adapter = new CardAdapterComments(listaKomentarze, getActivity().getApplicationContext());

        //Adding adapter to recyclerview
        //recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext()));
        recyclerView.setAdapter(adapter);

        btnDodajKomentarz.setOnClickListener(new View.OnClickListener() {



            public void onClick(View view) {

                String text = editTextComment.getText().toString().trim();

                Log.d("onClick ","Wpisano: " + text);

                if(!checkBoxRegulamin.isChecked()){
                    Toast.makeText(getActivity().getApplicationContext(), "Potwierdź znajomość Regulaminu", Toast.LENGTH_LONG).show();
                }
                else{
                    insertComment(user_id, kurs_id, text);

                    getActivity().finish();
                    startActivity(getActivity().getIntent());
                }



            }

        });

        return view; }

    private void insertComment(final String user_id, final String kurs_id, final String text) {

        String tag_string_req = "req_login";

        Log.d("insertComment ","zmienne: " + user_id + " " + text + " " + kurs_id);

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.DATA_INSERT_COMMENT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {



            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                Log.d("PUT ","zmienne: " + user_id + " " + text + " " + kurs_id);
                params.put("user_id", user_id);
                params.put("lesson_id", kurs_id);
                params.put("text", text);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }


    private void getData(String kurs_id) {
        //Adding the method to the queue by calling the method getDataFromServer
        requestQueue.add(getDataFromServer(kurs_id));
        //Incrementing the request counter
    }

    private void getData(String user_id, String kurs_id) {
        requestQueue.add(getButtonInfo(user_id, kurs_id));
        //Incrementing the request counter
    }

    private JsonArrayRequest getButtonInfo(String user_id, String kurs_id){

        Log.d("DebugTag", "Value user_id:" + user_id);
        //JsonArrayRequest of volley
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(AppConfig.DATA_GET_BUTTON_INFO + String.valueOf(user_id) + "&user_id=" + String.valueOf(kurs_id),
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

    private String parseDataButton(JSONArray array) {



        JSONObject json = null;
        try {
            //Getting json
            json = array.getJSONObject(0);
            czyPokazacButton = json.getString("show");
            Log.d("FragmentComments", "CzyPokazacButton" + czyPokazacButton);



        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (Integer.parseInt(czyPokazacButton) == 1){
            btnDodajKomentarz.setVisibility(View.VISIBLE);
            checkBoxRegulamin.setVisibility(View.VISIBLE);
            editTextComment.setVisibility(View.VISIBLE);


        }
        else {
            btnDodajKomentarz.setVisibility(View.GONE);
            checkBoxRegulamin.setVisibility(View.GONE);
            editTextComment.setVisibility(View.GONE);

        }

        return czyPokazacButton;

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