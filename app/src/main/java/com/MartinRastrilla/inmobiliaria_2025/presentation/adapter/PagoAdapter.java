package com.MartinRastrilla.inmobiliaria_2025.presentation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.MartinRastrilla.inmobiliaria_2025.R;
import com.MartinRastrilla.inmobiliaria_2025.data.model.Pago;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PagoAdapter extends RecyclerView.Adapter<PagoAdapter.PagoViewHolder> {
    private List<Pago> pagoList;

    public PagoAdapter(List<Pago> pagoList) {
        this.pagoList = pagoList;
    }

    @NonNull
    @Override
    public PagoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_payment, parent, false);
        return new PagoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PagoViewHolder holder, int position) {
        Pago pago = pagoList.get(position);
        holder.bind(pago);
    }

    @Override
    public int getItemCount() {
        return pagoList.size();
    }

    class PagoViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPaymentAmount;
        private TextView tvPaymentDate;
        private TextView tvPaymentMethod;
        private TextView tvInquilinoName;

        public PagoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPaymentAmount = itemView.findViewById(R.id.tvPaymentAmount);
            tvPaymentDate = itemView.findViewById(R.id.tvPaymentDate);
            tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
            tvInquilinoName = itemView.findViewById(R.id.tvInquilinoName);
        }

        public void bind(Pago pago) {
            // Monto formateado
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));
            tvPaymentAmount.setText(formatter.format(pago.getAmount()));

            // Fecha formateada
            if (pago.getPaymentDate() != null) {
                String date = formatDate(pago.getPaymentDate());
                tvPaymentDate.setText(date);
            }

            // Método de pago traducido
            tvPaymentMethod.setText(pago.getPaymentMethodText());

            // Nombre del inquilino
            if (pago.getInquilino() != null) {
                String fullName = (pago.getInquilino().getName() != null ? pago.getInquilino().getName() : "") + " " +
                        (pago.getInquilino().getLastName() != null ? pago.getInquilino().getLastName() : "");
                tvInquilinoName.setText(fullName.trim());
            } else {
                tvInquilinoName.setText("N/A");
            }
        }

        private String formatDate(String dateString) {
            try {
                String datePart = dateString.split("T")[0];
                String[] parts = datePart.split("-");
                return parts[2] + "/" + parts[1] + "/" + parts[0];
            } catch (Exception e) {
                return dateString;
            }
        }
    }
}