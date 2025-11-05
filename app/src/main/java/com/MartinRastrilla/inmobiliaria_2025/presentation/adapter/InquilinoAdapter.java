package com.MartinRastrilla.inmobiliaria_2025.presentation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.MartinRastrilla.inmobiliaria_2025.R;
import com.MartinRastrilla.inmobiliaria_2025.data.model.Inquilino;

import java.util.List;

public class InquilinoAdapter extends RecyclerView.Adapter<InquilinoAdapter.InquilinoViewHolder> {
    private List<Inquilino> inquilinoList;
    private OnInquilinoClickListener listener;

    public interface OnInquilinoClickListener {
        void onInquilinoClick(Inquilino inquilino);
    }

    public InquilinoAdapter(List<Inquilino> inquilinoList, OnInquilinoClickListener listener) {
        this.inquilinoList = inquilinoList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InquilinoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inquilino, parent, false);
        return new InquilinoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InquilinoViewHolder holder, int position) {
        Inquilino inquilino = inquilinoList.get(position);
        holder.bind(inquilino);
    }

    @Override
    public int getItemCount() {
        return inquilinoList.size();
    }

    class InquilinoViewHolder extends RecyclerView.ViewHolder {
        private TextView tvInquilinoName;
        private TextView tvInquilinoDocument;
        private TextView tvInquilinoPhone;
        private TextView tvInquilinoEmail;
        private TextView tvPaymentResponsible;

        public InquilinoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInquilinoName = itemView.findViewById(R.id.tvInquilinoName);
            tvInquilinoDocument = itemView.findViewById(R.id.tvInquilinoDocument);
            tvInquilinoPhone = itemView.findViewById(R.id.tvInquilinoPhone);
            tvInquilinoEmail = itemView.findViewById(R.id.tvInquilinoEmail);
            tvPaymentResponsible = itemView.findViewById(R.id.tvPaymentResponsible);
        }

        public void bind(Inquilino inquilino) {
            // Nombre completo
            String fullName = (inquilino.getName() != null ? inquilino.getName() : "") + " " +
                    (inquilino.getLastName() != null ? inquilino.getLastName() : "");
            tvInquilinoName.setText(fullName.trim());

            // DNI
            if (inquilino.getDocumentNumber() != null && !inquilino.getDocumentNumber().isEmpty()) {
                tvInquilinoDocument.setText("DNI: " + inquilino.getDocumentNumber());
                tvInquilinoDocument.setVisibility(View.VISIBLE);
            } else {
                tvInquilinoDocument.setVisibility(View.GONE);
            }

            // Teléfono
            if (inquilino.getPhone() != null && !inquilino.getPhone().isEmpty()) {
                tvInquilinoPhone.setText("Tel: " + inquilino.getPhone());
                tvInquilinoPhone.setVisibility(View.VISIBLE);
            } else {
                tvInquilinoPhone.setVisibility(View.GONE);
            }

            if (inquilino.getEmail() != null && !inquilino.getEmail().isEmpty()) {
                tvInquilinoEmail.setText(inquilino.getEmail());
                tvInquilinoEmail.setVisibility(View.VISIBLE);
            } else {
                tvInquilinoEmail.setVisibility(View.GONE);
            }

            if (inquilino.isPaymentResponsible()) {
                tvPaymentResponsible.setText("Responsable de pago");
                tvPaymentResponsible.setVisibility(View.VISIBLE);
            } else {
                tvPaymentResponsible.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onInquilinoClick(inquilino);
                }
            });
        }
    }
}
