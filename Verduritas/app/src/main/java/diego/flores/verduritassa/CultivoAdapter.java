package diego.flores.verduritassa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CultivoAdapter extends RecyclerView.Adapter<CultivoAdapter.CultivoViewHolder> {
    private final List<Verduras> cultivosList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onMenuClick(View view, int position);
    }

    public CultivoAdapter(List<Verduras> cultivosList, OnItemClickListener listener) {
        this.cultivosList = cultivosList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CultivoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cultivo, parent, false);
        return new CultivoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CultivoViewHolder holder, int position) {
        Verduras cultivo = cultivosList.get(position);
        holder.aliasText.setText(cultivo.getAlias());
        holder.fechaText.setText(cultivo.getFecha());

        holder.menuButton.setOnClickListener(v ->
                listener.onMenuClick(holder.menuButton, holder.getAdapterPosition())
        );
    }

    @Override
    public int getItemCount() {
        return cultivosList.size();
    }

    static class CultivoViewHolder extends RecyclerView.ViewHolder {
        TextView aliasText;
        TextView fechaText;
        ImageButton menuButton;

        CultivoViewHolder(View itemView) {
            super(itemView);
            aliasText = itemView.findViewById(R.id.cultivoAlias);
            fechaText = itemView.findViewById(R.id.cultivoFecha);
            menuButton = itemView.findViewById(R.id.menuButton);
        }
    }
}
