package com.MartinRastrilla.inmobiliaria_2025.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.MartinRastrilla.inmobiliaria_2025.R;

public class ToastHelper {
    
    public enum ToastType {
        SUCCESS, ERROR, INFO, WARNING
    }

    public static void showToast(Context context, String message, ToastType type) {
        showToast(context, message, type, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, String message, ToastType type, int duration) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custom_toast, null);

        TextView toastMessage = layout.findViewById(R.id.toastMessage);
        TextView toastIcon = layout.findViewById(R.id.toastIcon);

        toastMessage.setText(message);

        // Configurar icono según el tipo
        switch (type) {
            case SUCCESS:
                toastIcon.setText("✓");
                toastIcon.setTextColor(context.getResources().getColor(android.R.color.holo_green_light));
                toastIcon.setVisibility(View.VISIBLE);
                break;
            case ERROR:
                toastIcon.setText("✕");
                toastIcon.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
                toastIcon.setVisibility(View.VISIBLE);
                break;
            case INFO:
                toastIcon.setText("ℹ");
                toastIcon.setTextColor(context.getResources().getColor(android.R.color.holo_blue_light));
                toastIcon.setVisibility(View.VISIBLE);
                break;
            case WARNING:
                toastIcon.setText("⚠");
                toastIcon.setTextColor(context.getResources().getColor(android.R.color.holo_orange_light));
                toastIcon.setVisibility(View.VISIBLE);
                break;
        }

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();
    }

    public static void showSuccess(Context context, String message) {
        showToast(context, message, ToastType.SUCCESS);
    }

    public static void showError(Context context, String message) {
        showToast(context, message, ToastType.ERROR, Toast.LENGTH_LONG);
    }

    public static void showInfo(Context context, String message) {
        showToast(context, message, ToastType.INFO);
    }

    public static void showWarning(Context context, String message) {
        showToast(context, message, ToastType.WARNING);
    }
}
