package ru.valaubr.controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ru.valaubr.views.Camera;
import ru.valaubr.models.Enemy;
import ru.valaubr.models.LocationChangeHelper;
import ru.valaubr.models.LocationModel;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LocationController implements Initializable {
    public GridPane thisLevel;
    public VBox inventory;
    public HBox equipment;
    public ProgressBar hp;
    public Label status;
    public Button restart;

    private int mapWidth = 20;
    private int mapHeight = 20;
    public ScrollPane camera;

    private HeroController hero;

    private final List<Enemy> enemies = new ArrayList<>();

    LocationChangeHelper view;
    LocationModel model;

    //start game
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        view = new LocationChangeHelper(mapWidth, mapHeight, enemies, thisLevel, inventory, equipment, status);
        view.createMap(thisLevel);
        hero = new HeroController(thisLevel, view.map.length, view.map[0].length,
                new Camera(camera, mapWidth, mapHeight), enemies, inventory,
                equipment, hp, status, restart);
        model = new LocationModel(thisLevel, hero, enemies, view);
        view.setHero(hero);
        hero.spawn(view.spawnPointX, view.spawnPointY);
    }

    // change floor
    public void generate() {
        view.generate();
    }

    public void move(KeyEvent keyEvent) {
        model.move(keyEvent);
    }

    public void restartGame(MouseEvent mouseEvent) {
        view.restartGame(mouseEvent);
    }
}