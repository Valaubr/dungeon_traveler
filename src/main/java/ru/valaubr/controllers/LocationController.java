package ru.valaubr.controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import ru.valaubr.models.mapgen.bsp.BspMapCreator;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class LocationController implements Initializable {

    public GridPane thisLevel;
    static char[][] map;
    private int mapWidth = 40;
    private int mapHeight = 40;
    public ScrollPane camera;

    private Hero hero;
    private Random rnd = new Random();

    private List<Enemy> enemies = new ArrayList<>();
    private Image enemy1 = new Image("/enemy1.png");
    private Image enemy2 = new Image("/enemy2.png");
    private Image enemy3 = new Image("/enemy3.png");

    private void drawMap(char[][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == '#') {
                    thisLevel.add(new ImageView(new Image("/wall.png")), j, i);
                } else if (map[i][j] == ',' || map[i][j] == '.') {

                    thisLevel.add(new ImageView(new Image("/floor0.png")), j, i);
                } else if (map[i][j] == '|' || map[i][j] == '-') {
                    thisLevel.add(new ImageView(new Image("/door.png")), j, i);
                } else if (map[i][j] == '*') {
                    int l = rnd.nextInt(3);
                    if (l == 0) {
                        thisLevel.add(new ImageView(new Image("/enemy1.png")), j, i);
                        enemies.add(new Enemy(enemy1, thisLevel, i, j));
                    } else if (l == 1) {
                        thisLevel.add(new ImageView(new Image("/enemy2.png")), j, i);
                        enemies.add(new Enemy(enemy2, thisLevel, i, j));
                    } else {
                        thisLevel.add(new ImageView(new Image("/enemy3.png")), j, i);
                        enemies.add(new Enemy(enemy3, thisLevel, i, j));
                    }
                } else {
                    thisLevel.add(new ImageView(new Image("/dark.png")), j, i);
                }
            }
        }
    }

    //start game
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        BspMapCreator bspMapCreator = new BspMapCreator();
        bspMapCreator.setMinRoomSize(5);
        bspMapCreator.setMaxIterations(20);
        bspMapCreator.setMapDimension(mapWidth, mapHeight);
        bspMapCreator.setOut(System.out);
        map = bspMapCreator.createMap();

        drawMap(map);

        hero = new Hero(thisLevel, map.length, map[0].length, new Camera(camera, mapWidth, mapHeight), enemies);
        hero.spawn();
    }

    // change floor
    public void generate() {
        BspMapCreator bspMapCreator = new BspMapCreator();
        bspMapCreator.setMinRoomSize(5);
        bspMapCreator.setMaxIterations(12);
        bspMapCreator.setMapDimension(70, 70);
        bspMapCreator.setOut(System.out);
        map = bspMapCreator.createMap();

        drawMap(map);
        hero.spawn();
    }

    public void move(KeyEvent keyEvent) {
        if (keyEvent.getCode().getName() == "W") {
            hero.setX(hero.getX() - 1);
            hero.enemiesMove();
        } else if (keyEvent.getCode().getName() == "S") {
            hero.setX(hero.getX() + 1);
            hero.enemiesMove();
        } else if (keyEvent.getCode().getName() == "A") {
            hero.setY(hero.getY() - 1);
            hero.enemiesMove();
        } else if (keyEvent.getCode().getName() == "D") {
            hero.setY(hero.getY() + 1);
            hero.enemiesMove();
        }
    }
}