package pl.brsk.brsk.aplikacjakursyprojektinz;

/**
 * Created by brsk on 2016-12-10.
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;

import java.util.HashMap;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    //Imageloader to load image
    private ImageLoader imageLoader;
    private Context context;

    //List to store all superheroes
    List<Kurs> kursy;

    //Constructor of this class
    public CardAdapter(List<Kurs> kursy, Context context){
        super();
        //Getting all superheroes
        this.kursy = kursy;
        this.context = context;
    }

   /* *//*SQLiteHandler db = new SQLiteHandler(context);
    // Fetching user details from sqlite
    HashMap<String, String> user = db.getUserDetails();*//*

    final String id = user.get("user_id");*/

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.kursy_list, parent, false);

        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        //Getting the particular item from the list
        Kurs kurs =  kursy.get(position);
        String id_kursu = kurs.getId();

        //Loading image from url
        imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
        imageLoader.get(AppConfig.URL_IMAGE + "kursy/" + id_kursu + "/" + kurs.getImage(), ImageLoader.getImageListener(holder.imageView, android.R.drawable.ic_dialog_info, android.R.drawable.ic_dialog_alert));

        //Showing data on the views

        holder.imageView.setImageUrl(AppConfig.URL_IMAGE + kurs.getImage(), imageLoader);
        holder.textViewName.setText(kurs.getTitle());
        holder.textViewPublisher.setText(kurs.getPrice());
        holder.textViewShortDescription.setText(kurs.getShortDescription());

    }

    @Override
    public int getItemCount() {
        return kursy.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        //Views
        public CircularNetworkImageView imageView;
        public TextView textViewName;
        public TextView textViewPublisher;
        public TextView textTytul;
        public TextView textCena;
        public TextView textViewShortDescription;


        //Initializing Views
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (CircularNetworkImageView) itemView.findViewById(R.id.imageViewHero);
            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            textTytul = (TextView) itemView.findViewById(R.id.tytul);
            textCena = (TextView) itemView.findViewById(R.id.cena);
            textViewPublisher = (TextView) itemView.findViewById(R.id.textViewPublisher);
            textViewShortDescription = (TextView)itemView.findViewById(R.id.textViewShortDescription);
        }
    }
}