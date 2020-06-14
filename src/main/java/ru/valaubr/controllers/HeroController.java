package ru.valaubr.controllers;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ru.valaubr.views.Camera;
import ru.valaubr.models.Enemy;
import ru.valaubr.models.HeroModel;
import ru.valaubr.views.HeroView;

import java.util.List;

public class HeroController {
    HeroModel model;
    HeroView view;

    public HeroController(GridPane thisLevel, int row, int column, Camera camera, List<Enemy> enemies, VBox inventory, HBox equipment, ProgressBar hp, Label status, Button restart) {
        view = new HeroView(thisLevel, camera, inventory, equipment, hp);
        model = new HeroModel(row, column, enemies, view, status, restart);
    }

    public void spawn(int x, int y) {
        model.spawn(x, y);
    }

    public int getX() {
        return model.getX();
    }

    public void setX(int x) {
        model.setX(x);
    }

    public int getY() {
        return model.getY();
    }

    public void setY(int y) {
        model.setY(y);
    }

    public boolean checkToChangeLVL() {
        return model.changeLVL;
    }

    public void setNewLvl() {
        model.changeLVL = false;
    }

    public void setNewCameraParam(int mapWidth, int mapHeight){
        view.camera.setMapWidth(mapWidth);
        view.camera.setMapHeight(mapHeight);
    }

    public void enemiesMove() {
        model.enemiesMove();
    }

    public void updateHP() {
        model.setHealth(100);
        model.setAlive(true);
        view.hp.setProgress(1);
    }
}
