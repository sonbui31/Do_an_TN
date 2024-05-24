package com.example.quanlychitieu;

import java.text.DecimalFormat;

public class CurrencyFormatter {

    public static String formatVND(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount) + " VNĐ";
    }
}
