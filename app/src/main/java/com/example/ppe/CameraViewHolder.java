package com.example.ppe;

import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ppe.Camera;
import com.example.ppe.R;

public class CameraViewHolder extends RecyclerView.ViewHolder {

    TextView tvDescription, tvCameraId, tvUrl, tvStatus;
    WebView webView;
    Button btnCapture, btnDelete;

    public CameraViewHolder(@NonNull View itemView) {
        super(itemView);

        // Récupération des références des vues
        tvDescription = itemView.findViewById(R.id.tvDescription);
        tvCameraId = itemView.findViewById(R.id.tvCameraId);
        tvUrl = itemView.findViewById(R.id.tvUrl);
        tvStatus = itemView.findViewById(R.id.tvStatus);
        webView = itemView.findViewById(R.id.webView);
        btnCapture = itemView.findViewById(R.id.btnCapture);
        btnDelete = itemView.findViewById(R.id.btnDelete);
    }

    public void bindData(Camera camera) {
        // Remplir les vues avec les données de la caméra
        tvDescription.setText("📷 " + camera.getDescription());
        tvCameraId.setText(String.valueOf(camera.getId()));
        tvUrl.setText(camera.getUrl());

        // Gestion du statut
        if (camera.isEstPriv()) {
            tvStatus.setText("Privé");
            tvStatus.setBackgroundColor(0xFFE74C3C); // Rouge
        } else {
            tvStatus.setText("Public");
            tvStatus.setBackgroundColor(0xFF27AE60); // Vert
        }

        // Configuration du WebView pour l'iframe
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        String html = "<html><body style='margin:0;padding:0;'>" +
                "<iframe width='100%' height='100%' src='" +
                camera.getUrl() + "' frameborder='0'></iframe>" +
                "</body></html>";
        webView.loadData(html, "text/html", "utf-8");
    }
}