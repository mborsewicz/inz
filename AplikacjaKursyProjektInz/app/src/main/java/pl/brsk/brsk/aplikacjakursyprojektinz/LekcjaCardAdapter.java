package pl.brsk.brsk.aplikacjakursyprojektinz;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

/**
 * Created by borse on 04.03.2017.
 */

public class LekcjaCardAdapter extends RecyclerView.Adapter<LekcjaCardAdapter.ViewHolder> {

    //Imageloader to load image
    private ImageLoader imageLoader;
    private Context context;

    //List to store all superheroes
    List<Lekcja> lekcje;

    //Constructor of this class
    public LekcjaCardAdapter(List<Lekcja> lekcje, Context context){
        super();
        //Getting all superheroes
        this.lekcje = lekcje;
        this.context = context;
    }

    @Override
    public LekcjaCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lekcje_list, parent, false);

        LekcjaCardAdapter.ViewHolder viewHolder = new LekcjaCardAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(LekcjaCardAdapter.ViewHolder holder, int position) {

        //Getting the particular item from the list
        Lekcja lekcja =  lekcje.get(position);

        //Showing data on the views

        holder.textViewTitle.setText(lekcja.getTitle());
        holder.textViewDescription.setText(lekcja.getDescription());
        holder.numerLekcji.setText(Integer.toString(position+1));

    }

    @Override
    public int getItemCount() {
        return lekcje.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        //Views
        public TextView textViewTitle;
        public TextView textViewDescription;
        public TextView numerLekcji;


        //Initializing Views
        public ViewHolder(View itemView) {
            super(itemView);
            textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
            textViewDescription = (TextView) itemView.findViewById(R.id.textViewDescription);
            numerLekcji = (TextView) itemView.findViewById(R.id.lekcjaNr);

        }
    }
}
