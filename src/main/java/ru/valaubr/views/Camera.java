package ru.valaubr.views;

import javafx.scene.control.ScrollPane;

public class Camera {
    ScrollPane camera;
    int mapWidth, mapHeight;
    public Camera(ScrollPane camera, int mapWidth, int mapHeight){
        this.camera = camera;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
    }

    public void setPos(double x, double y){
        camera.setVvalue(x / mapWidth);
        camera.setHvalue(y / mapHeight);
    }


    public void setMapWidth(int mapWidth) {
        this.mapWidth = mapWidth;
    }

    public void setMapHeight(int mapHeight) {
        this.mapHeight = mapHeight;
    }
}
