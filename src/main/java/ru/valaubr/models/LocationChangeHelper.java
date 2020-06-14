package ru.valaubr.models;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ru.valaubr.controllers.HeroController;
import ru.valaubr.models.mapgen.TileChar;
import ru.valaubr.models.mapgen.bsp.BspMapCreator;

import java.util.List;
import java.util.Random;

public class LocationChangeHelper {
    private GridPane thisLevel;
    private VBox inventory;
    private HBox equipment;
    private Label status;

    int mapWidth, mapHeight;
    public char[][] map;

    public int spawnPointX;
    public int spawnPointY;

    private final Random rnd = new Random();

    private final Image enemy1 = new Image("/enemy1.png");
    private final Image enemy2 = new Image("/enemy2.png");
    private final Image enemy3 = new Image("/enemy3.png");

    private List<Enemy> enemies;

    private HeroController hero;

    public LocationChangeHelper(int mapWidth, int mapHeight, List<Enemy> enemies, GridPane thisLevel, VBox inventory, HBox equipment, Label status) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.enemies = enemies;
        this.thisLevel = thisLevel;
        this.inventory = inventory;
        this.equipment = equipment;
        this.status = status;
    }

    public void createMap(GridPane thisLevel) {
        BspMapCreator bspMapCreator = new BspMapCreator();
        bspMapCreator.setMinRoomSize(8);
        bspMapCreator.setMaxIterations(128);
        bspMapCreator.setMapDimension(mapWidth, mapHeight);
        bspMapCreator.setOut(System.out);
        map = bspMapCreator.createMap();
        drawMap(thisLevel);
    }

    private void drawMap(GridPane thisLevel) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == TileChar.charWall) {
                    thisLevel.add(new ImageView(new Image("/wall.png")), j, i);
                } else if (map[i][j] == TileChar.charFloor || map[i][j] == TileChar.charRoomFloor || map[i][j] == TileChar.charTemp) {
                    thisLevel.add(new ImageView(new Image("/floor.png")), j, i);
                } else if (map[i][j] == TileChar.charTrap) {
                    thisLevel.add(new ImageView(new Image("/trap.png")), j, i);
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

    public void restartGame(MouseEvent mouseEvent) {
        inventory.getChildren().removeAll();
        hero.updateHP();
        mapWidth = 20;
        mapHeight = 20;
        enemies.clear();
        generate();
        equipment.getChildren().clear();

        equipment.getChildren().add(startingEquip());
        equipment.getChildren().add(startingEquip());
        hero.spawn(spawnPointX, spawnPointY);

        status.setVisible(false);
    }

    private VBox startingEquip() {
        VBox item = new VBox();
        item.getChildren().add(new ImageView(new Image("bag.png")));
        item.getChildren().add(new Label("Empty"));
        item.getChildren().add(new Label("0"));
        return item;
    }

    public void generate() {
        thisLevel.getChildren().clear();
        mapWidth = (int) (mapWidth * 1.2f);
        mapHeight = (int) (mapHeight * 1.2f);
        createMap(thisLevel);
        hero.spawn(spawnPointX, spawnPointY);
        hero.setNewCameraParam(mapWidth, mapHeight);
    }

    public void setHero(HeroController hero) {
        this.hero = hero;
    }
}
