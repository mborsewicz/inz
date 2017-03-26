package pl.brsk.brsk.aplikacjakursyprojektinz;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;

/**
 * Created by borse on 25.03.2017.
 */

public class CardAdapterComments extends RecyclerView.Adapter<CardAdapterComments.ViewHolder> {

    //Imageloader to load image
    private ImageLoader imageLoader;
    private Context context;

    //List to store all superheroes
    List<Komentarz> komentarze;

    //Constructor of this class
    public CardAdapterComments(List<Komentarz> komentarze, Context context){
        super();
        //Getting all superheroes
        this.komentarze = komentarze;
        this.context = context;
    }

    @Override
    public CardAdapterComments.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.komentarze_list, parent, false);

        CardAdapterComments.ViewHolder viewHolder = new CardAdapterComments.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CardAdapterComments.ViewHolder holder, int position) {

        //Getting the particular item from the list
        Komentarz komentarz=  komentarze.get(position);
        String id_komentarz = komentarz.getId();

        //Showing data on the views

        holder.textViewName.setText(komentarz.getName());
        Log.d("CARDA ADAPTER: ", "wartosc name " + komentarz.getName());

        holder.textViewText.setText(komentarz.getText());
        holder.textViewCreated.setText(komentarz.getCreated());

    }

    @Override
    public int getItemCount() {
        return komentarze.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        //Views

        public TextView textViewName;
        public TextView textViewText;
        public TextView textViewCreated;


        //Initializing Views
        public ViewHolder(View itemView) {
            super(itemView);

            textViewName = (TextView) itemView.findViewById(R.id.name);
            textViewText = (TextView) itemView.findViewById(R.id.komentarz);
            textViewCreated = (TextView) itemView.findViewById(R.id.data);


        }
    }
}
