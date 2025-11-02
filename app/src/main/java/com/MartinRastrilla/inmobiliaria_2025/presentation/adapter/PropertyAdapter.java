package com.MartinRastrilla.inmobiliaria_2025.presentation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.MartinRastrilla.inmobiliaria_2025.R;
import com.MartinRastrilla.inmobiliaria_2025.data.model.Inmueble;
import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder> {
    private List<Inmueble> propertyList;
    private OnPropertyClickListener listener;
    private String baseUrl = "http://Dominio o IP/";

    public interface OnPropertyClickListener {
        void onPropertyClick(Inmueble inmueble);
    }

    public PropertyAdapter(List<Inmueble> propertyList, OnPropertyClickListener listener) {
        this.propertyList = propertyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_property, parent, false);
        return new PropertyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        Inmueble property = propertyList.get(position);
        holder.bind(property, listener);
    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }

    class PropertyViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPropertyImage;
        private TextView tvPropertyTitle;
        private TextView tvPropertyAddress;
        private TextView tvPropertyPrice;
        private TextView tvPropertyRooms;
        private TextView tvStatus;

        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPropertyImage = itemView.findViewById(R.id.ivPropertyImage);
            tvPropertyTitle = itemView.findViewById(R.id.tvPropertyTitle);
            tvPropertyAddress = itemView.findViewById(R.id.tvPropertyAddress);
            tvPropertyPrice = itemView.findViewById(R.id.tvPropertyPrice);
            tvPropertyRooms = itemView.findViewById(R.id.tvPropertyRooms);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }

        public void bind(Inmueble property, OnPropertyClickListener listener) {
            tvPropertyTitle.setText(property.getTitle());
            tvPropertyAddress.setText(property.getAddress());

            // Formatear precio
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));
            tvPropertyPrice.setText(formatter.format(property.getPrice()));

            // Habitaciones
            String roomsText = property.getRooms() + " habitación" + (property.getRooms() > 1 ? "es" : "");
            tvPropertyRooms.setText(roomsText);

            // Estado
            if (property.isAvailable()) {
                tvStatus.setText("Disponible");
                tvStatus.setBackgroundResource(R.drawable.status_available_background);
            } else {
                tvStatus.setText("No Disponible");
                tvStatus.setBackgroundResource(R.drawable.status_unavailable_background);
            }

            // Imagen (por ahora placeholder, luego agregaremos cuando haya ruta de foto)
            // TODO: Cargar imagen cuando el modelo tenga ruta de foto
            Glide.with(itemView.getContext())
                    .load(R.drawable.card_background)
                    .placeholder(R.drawable.card_background)
                    .into(ivPropertyImage);

            // Click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPropertyClick(property);
                }
            });
        }
    }
}