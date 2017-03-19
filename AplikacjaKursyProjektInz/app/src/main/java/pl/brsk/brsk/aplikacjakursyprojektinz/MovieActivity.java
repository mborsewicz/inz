package pl.brsk.brsk.aplikacjakursyprojektinz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


/**
 * Created by borse on 05.03.2017.
 */

public class MovieActivity extends AppCompatActivity {

    private String film_url;
    private String kurs_id;
    private TextView txtViewURL;
    private TextView txtViewKursId;
    ProgressDialog pDialog;
    VideoView videoview;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            film_url = extras.getString("film_url");
            kurs_id = extras.getString("kurs_id");
        }

        txtViewURL = (TextView)findViewById(R.id.url);
        txtViewURL.setText(film_url);
        txtViewKursId = (TextView)findViewById(R.id.kurs_id);
        txtViewKursId.setText(kurs_id);

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




        videoview = (VideoView) findViewById(R.id.VideoView);
        // Execute StreamVideo AsyncTask

        // Create a progressbar
        pDialog = new ProgressDialog(MovieActivity.this);
        // Set progressbar title
        pDialog.setTitle("Stream wideo");
        // Set progressbar message
        pDialog.setMessage("Trwa ładowanie wideo...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        // Show progressbar
        pDialog.show();

        try {
            // Start the MediaController
            MediaController mediacontroller = new MediaController(
                    MovieActivity.this);
            mediacontroller.setAnchorView(videoview);
            // Get the URL from String VideoURL
            Uri video = Uri.parse(AppConfig.DATA_FILMY + kurs_id + "/" + film_url);
            videoview.setMediaController(mediacontroller);
            videoview.setVideoURI(video);

        } catch (Exception e) {

            e.printStackTrace();
        }

        videoview.requestFocus();
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                pDialog.dismiss();
                videoview.start();
            }
        });

    }

    //-----zapamietanie video w razie zmiany polozenia urzadzenia------
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putInt("possition", videoview.getCurrentPosition());
        videoview.pause();
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        int pos = savedInstanceState.getInt("possition");
        videoview.seekTo(pos);
        super.onRestoreInstanceState(savedInstanceState);
    }


}
