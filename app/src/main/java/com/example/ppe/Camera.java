package com.example.ppe;

public class Camera {
    // Attributs privés (comme dans le cours)
    private int id;
    private String url;
    private String description;
    private boolean estPriv;

    // Constructeur (comme PointDEau dans le cours)
    public Camera(int id, String url, String description, boolean estPriv) {
        this.id = id;
        this.url = url;
        this.description = description;
        this.estPriv = estPriv;
    }

    // Getters (méthodes pour accéder aux attributs)
    public int getId() { return id; }
    public String getUrl() { return url; }
    public String getDescription() { return description; }
    public boolean isEstPriv() { return estPriv; }
}