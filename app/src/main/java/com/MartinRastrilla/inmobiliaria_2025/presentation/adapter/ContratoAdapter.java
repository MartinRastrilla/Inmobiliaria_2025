package com.MartinRastrilla.inmobiliaria_2025.presentation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.MartinRastrilla.inmobiliaria_2025.R;
import com.MartinRastrilla.inmobiliaria_2025.data.model.Contrato;
import com.MartinRastrilla.inmobiliaria_2025.utils.FormatterUtils;

import java.util.List;

public class ContratoAdapter extends RecyclerView.Adapter<ContratoAdapter.ContratoViewHolder> {
    private List<Contrato> contratoList;
    private OnContratoClickListener listener;

    public interface OnContratoClickListener {
        void onContratoClick(Contrato contrato);
    }

    public ContratoAdapter(List<Contrato> contratoList, OnContratoClickListener listener) {
        this.contratoList = contratoList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContratoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contract, parent, false);
        return new ContratoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContratoViewHolder holder, int position) {
        Contrato contrato = contratoList.get(position);
        holder.bind(contrato);
    }

    @Override
    public int getItemCount() {
        return contratoList.size();
    }

    class ContratoViewHolder extends RecyclerView.ViewHolder {
        private TextView tvContractTitle;
        private TextView tvContractPrice;
        private TextView tvContractStatus;

        public ContratoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContractTitle = itemView.findViewById(R.id.tvContractTitle);
            tvContractPrice = itemView.findViewById(R.id.tvContractPrice);
            tvContractStatus = itemView.findViewById(R.id.tvContractStatus);
        }

        public void bind(Contrato contrato) {
            if (contrato.getInmueble() != null) {
                tvContractTitle.setText(contrato.getInmueble().getTitle());
            } else {
                tvContractTitle.setText(FormatterUtils.formatContractId(contrato.getId()));
            }

            tvContractPrice.setText(FormatterUtils.formatPrice(contrato.getTotalPrice()));

            String statusText = contrato.getStatusText();
            tvContractStatus.setText(statusText);
            tvContractStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.white));

            int statusCode = contrato.getStatusCode();
            switch (statusCode) {
                case 0: // Pendiente - Amarillo
                    tvContractStatus.setBackgroundResource(R.drawable.status_pending_background);
                    break;
                case 1: // Finalizado - Verde
                    tvContractStatus.setBackgroundResource(R.drawable.status_finalized_background);
                    break;
                case 2: // Expirado - Naranja
                    tvContractStatus.setBackgroundResource(R.drawable.status_expired_background);
                    break;
                case 3: // Cancelado - Rojo
                    tvContractStatus.setBackgroundResource(R.drawable.status_cancelled_background);
                    break;
                default:
                    tvContractStatus.setBackgroundResource(R.drawable.status_pending_background);
                    break;
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onContratoClick(contrato);
                }
            });
        }
    }
}
