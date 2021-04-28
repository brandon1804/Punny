package sg.edu.rp.id18044455.punny;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Random;

public class FavouritePunsAdapter extends RecyclerView.Adapter<FavouritePunsAdapter.ViewHolder> {

    ArrayList<String> favPuns;
    int[] colours;


    public FavouritePunsAdapter(ArrayList<String> favPuns, int[] colours){
        this.favPuns = favPuns;
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

        if(getItemCount() != 0){
            holder.tV.setText(favPuns.get(position));
        }
        else{
            holder.tV.setText("You have no favourite puns");
        }

        holder.linearLayout.setBackgroundColor(colours[randColour]);

    }

    @Override
    public int getItemCount() {
        return favPuns.size();
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

