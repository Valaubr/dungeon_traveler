package ru.valaubr.controllers;

import javafx.scene.control.ScrollPane;

public class Camera {
    ScrollPane camera;
    int mapWidth, mapHeight;
    Camera(ScrollPane camera, int mapWidth, int mapHeight){
        this.camera = camera;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
    }

    public void setPos(double x, double y){
        camera.setVvalue(x/mapWidth);
        camera.setHvalue(y/mapHeight);
    }
}
