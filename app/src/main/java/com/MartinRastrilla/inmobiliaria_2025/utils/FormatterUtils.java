package com.MartinRastrilla.inmobiliaria_2025.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatterUtils {
    
    public static String formatDate(String dateString) {
        try {
            String datePart = dateString.split("T")[0];
            String[] parts = datePart.split("-");
            return parts[2] + "/" + parts[1] + "/" + parts[0];
        } catch (Exception e) {
            return dateString;
        }
    }
    
    public static String formatPrice(double price) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));
        return formatter.format(price);
    }
    
    public static String formatRoomsText(int rooms) {
        return rooms + " habitación" + (rooms > 1 ? "es" : "");
    }
    
    public static String formatContractId(int id) {
        return "Contrato #" + id;
    }
    
    public static String formatCreatedAtText(String date) {
        if (date == null) {
            return "";
        }
        return "Creado: " + formatDate(date);
    }
    
    public static String formatMonthlyPriceText(double price) {
        return "Precio mensual: " + formatPrice(price);
    }
    
    public static String formatMaxGuestsText(Integer maxGuests) {
        if (maxGuests == null) {
            return "";
        }
        return maxGuests + " huéspedes máximo";
    }
}

