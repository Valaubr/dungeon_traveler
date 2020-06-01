package ru.valaubr.controllers;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import ru.valaubr.models.equp.Armor;
import ru.valaubr.models.equp.Sword;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hero {
    //state
    int health = 100;
    int[] inventory = new int[12];

    //equipment
    private Armor armor;
    private Sword sword;

    //hero position
    private int x;
    private int y;

    GridPane thisLevel;
    private Camera camera;
    int row;
    int column;
    private ImageView imageView;
    private Image imgFloor = new Image("/floor0.png");
    private Image imgDoor = new Image("/door.png");

    private Pattern enemy = Pattern.compile("enemy");
    private Matcher isEnemy;
    private List<Enemy> enemies;


    public Hero(GridPane thisLevel, int row, int column, Camera camera, List<Enemy> enemies) {
        this.thisLevel = thisLevel;
        this.row = row;
        this.column = column;
        this.camera = camera;
        this.enemies = enemies;
    }

    public void spawn() {
        for (int i = 0; i < row; i++) {
            for (int j = 1; j < column; j++) {
                imageView = (ImageView) thisLevel.getChildren().get(i * row + j);
                if (Objects.equals(imageView.getImage().getUrl(), imgFloor.getUrl())
                        || Objects.equals(imageView.getImage().getUrl(), imgDoor.getUrl())) {
                    thisLevel.add(new Button("Y"), j - 1, i);
                    x = i;
                    y = j;
                    return;
                }
            }
        }
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        changePos(x, y);
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        changePos(x, y);
    }

    private void changePos(int x, int y) {
        imageView = (ImageView) thisLevel.getChildren().get(x * row + y);
        if (Objects.equals(imageView.getImage().getUrl(), imgFloor.getUrl())
                || Objects.equals(imageView.getImage().getUrl(), imgDoor.getUrl())) {
            imageView = (ImageView) thisLevel.getChildren().get(x * row + y);
            imageView.setImage(new Image("hero.png"));
            if (this.y > y) {
                imageView = (ImageView) thisLevel.getChildren().get(x * row + y + 1);
            } else if (this.y < y) {
                imageView = (ImageView) thisLevel.getChildren().get(x * row + y - 1);
            } else if (this.x > x) {
                imageView = (ImageView) thisLevel.getChildren().get((x + 1) * row + y);
            } else {
                imageView = (ImageView) thisLevel.getChildren().get((x - 1) * row + y);
            }
            imageView.setImage(new Image("floor0.png"));
            this.y = y;
            this.x = x;
        } else {
            checkEnemyCollision(x, y);
        }
        camera.setPos(x, y);
    }

    private void checkEnemyCollision(int x, int y) {
        imageView = (ImageView) thisLevel.getChildren().get(x * row + y);
        isEnemy = enemy.matcher(imageView.getImage().getUrl());
        if (isEnemy.find()) {
            System.out.println("Hi Enemy!");
        }
    }

    public void enemiesMove() {
        enemies.forEach(Enemy::move);
    }
}
