package sg.edu.rp.id18044455.punny;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.Random;

public class ProvidedPunsAdapter extends RecyclerView.Adapter<ProvidedPunsAdapter.ViewHolder> {

    ArrayList<String> providedPuns;
    int[] colours;

    public ProvidedPunsAdapter(ArrayList<String> providedPuns, int[] colours){
        this.providedPuns = providedPuns;
        this.colours = colours;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pun_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Random rand = new Random();
        int randColour = rand.nextInt(colours.length);
        holder.tV.setText(providedPuns.get(position));
        holder.linearLayout.setBackgroundColor(colours[randColour]);

    }

    @Override
    public int getItemCount() {
        return providedPuns.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        TextView tV;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            tV = itemView.findViewById(R.id.textView);
            linearLayout = itemView.findViewById(R.id.linearLayout);
        }
    }

}//end of class



/*
public class ProvidedPunsAdapter extends RecyclerView.Adapter<ProvidedPunsAdapter.ViewHolder> {

    String[] providedPuns;
    int[] colours;

    public ProvidedPunsAdapter(String[] providedPuns, int[] colours){
        this.providedPuns = providedPuns;
        this.colours = colours;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pun_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Random rand = new Random();
        int randColour = rand.nextInt(colours.length);
        holder.tV.setText(providedPuns[position]);
        holder.linearLayout.setBackgroundColor(colours[randColour]);

    }

    @Override
    public int getItemCount() {
        return providedPuns.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        TextView tV;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            tV = itemView.findViewById(R.id.textView);
            linearLayout = itemView.findViewById(R.id.linearLayout);
        }
    }

}//end of class

 */

