package com.example.ppe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ppe.Camera;
import com.example.ppe.CameraViewHolder;
import com.example.ppe.R;

import java.util.List;

public class CameraAdapter extends RecyclerView.Adapter<CameraViewHolder> {

    private List<Camera> cameras;
    private Context context;
    private OnCameraActionListener listener;

    // Interface pour les actions
    public interface OnCameraActionListener {
        void onCaptureClick(Camera camera);
        void onDeleteClick(Camera camera);
    }

    public CameraAdapter(Context context, List<Camera> cameras, OnCameraActionListener listener) {
        this.context = context;
        this.cameras = cameras;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CameraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Création d'une nouvelle vue
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_camera, parent, false);
        return new CameraViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CameraViewHolder holder, int position) {
        Camera camera = cameras.get(position);
        holder.bindData(camera);

        // Gestion des clics sur les boutons
        holder.btnCapture.setOnClickListener(v -> {
            if (listener != null) listener.onCaptureClick(camera);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(camera);
        });
    }

    @Override
    public int getItemCount() {
        return cameras.size();
    }

    // Méthode pour mettre à jour la liste
    public void updateCameras(List<Camera> newCameras) {
        this.cameras = newCameras;
        notifyDataSetChanged();
    }
}