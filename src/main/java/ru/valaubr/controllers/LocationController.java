package ru.valaubr.controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ru.valaubr.models.mapgen.TileChar;
import ru.valaubr.models.mapgen.bsp.BspMapCreator;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class LocationController implements Initializable {
    public GridPane thisLevel;
    public VBox inventory;
    public HBox equipment;
    public ProgressBar hp;
    char[][] map;

    private int mapWidth = 20;
    private int mapHeight = 20;
    public ScrollPane camera;
    private int spawnPointX;
    private int spawnPointY;

    private Hero hero;
    private final Random rnd = new Random();

    private final List<Enemy> enemies = new ArrayList<>();
    private final Image enemy1 = new Image("/enemy1.png");
    private final Image enemy2 = new Image("/enemy2.png");
    private final Image enemy3 = new Image("/enemy3.png");

    //start game
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        BspMapCreator bspMapCreator = new BspMapCreator();
        bspMapCreator.setMinRoomSize(8);
        bspMapCreator.setMaxIterations(128);
        bspMapCreator.setMapDimension(mapWidth, mapHeight);
        bspMapCreator.setOut(System.out);
        map = bspMapCreator.createMap();

        hero = new Hero(thisLevel, map.length, map[0].length, new Camera(camera, mapWidth, mapHeight), enemies, inventory, equipment, hp);
        drawMap(map);
        hero.spawn(spawnPointX, spawnPointY);
    }

    // change floor
    public void generate() {
        thisLevel.getChildren().clear();
        BspMapCreator bspMapCreator = new BspMapCreator();
        bspMapCreator.setMinRoomSize(8);
        bspMapCreator.setMaxIterations(128);
        float i = mapWidth * 1.2f;
        float j = mapHeight * 1.2f;
        mapWidth = (int) i;
        mapHeight = (int) j;
        bspMapCreator.setMapDimension(mapWidth, mapHeight);
        bspMapCreator.setOut(System.out);
        map = bspMapCreator.createMap();

        drawMap(map);
        hero.thisLevel = thisLevel;
        hero.row = map.length;
        hero.column = map[0].length;
        hero.camera = new Camera(camera, mapWidth, mapHeight);
        hero.enemies = enemies;
        hero.spawn(spawnPointX, spawnPointY);
    }

    public void move(KeyEvent keyEvent) {
        switch (keyEvent.getCode().getName()) {
            case "W":
                hero.setX(hero.getX() - 1);
                hero.enemiesMove();
                checkToChange();
                break;
            case "S":
                hero.setX(hero.getX() + 1);
                hero.enemiesMove();
                checkToChange();
                break;
            case "A":
                hero.setY(hero.getY() - 1);
                hero.enemiesMove();
                checkToChange();
                break;
            case "D":
                hero.setY(hero.getY() + 1);
                hero.enemiesMove();
                checkToChange();
                break;
        }
    }

    private void checkToChange() {
        if (hero.changeLVL) {
            hero.changeLVL = false;
            generate();
        }
    }

    private void drawMap(char[][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == TileChar.charWall) {
                    thisLevel.add(new ImageView(new Image("/wall.png")), j, i);
                } else if (map[i][j] == TileChar.charFloor || map[i][j] == TileChar.charRoomFloor) {
                    thisLevel.add(new ImageView(new Image("/floor.png")), j, i);
                } else if (map[i][j] == TileChar.charStartFloor) {
                    thisLevel.add(new ImageView(new Image("/spawnPoint.png")), j, i);
                    spawnPointX = i;
                    spawnPointY = j;
                } else if (map[i][j] == TileChar.charNextFloor) {
                    thisLevel.add(new ImageView(new Image("/nextFloor.png")), j, i);
                } else if (map[i][j] == TileChar.charDoorV || map[i][j] == TileChar.charDoorH) {
                    thisLevel.add(new ImageView(new Image("/door.png")), j, i);
                } else if (map[i][j] == TileChar.charEnemy) {
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
                } else if (map[i][j] == TileChar.charWeapon) {
                    int l = rnd.nextInt(3);
                    if (l == 0) {
                        thisLevel.add(new ImageView(new Image("/weapon0_floor.png")), j, i);
                    } else if (l == 1) {
                        thisLevel.add(new ImageView(new Image("/weapon1_floor.png")), j, i);
                    } else {
                        thisLevel.add(new ImageView(new Image("/weapon2_floor.png")), j, i);
                    }
                } else if (map[i][j] == TileChar.charHealingPotion) {
                    thisLevel.add(new ImageView(new Image("/heal_floor.png")), j, i);
                } else if (map[i][j] == TileChar.charArmor) {
                    thisLevel.add(new ImageView(new Image("/armor_floor.png")), j, i);
                } else {
                    thisLevel.add(new ImageView(new Image("/dark.png")), j, i);
                }
            }
        }
    }
}