package ru.valaubr.models;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.Objects;
import java.util.Random;

public class Enemy {
    //meta info
    Image thisEnemy;
    //move support
    GridPane thisLevel;
    public int x;
    public int y;
    public int row;

    //map check supports
    ImageView imageView;
    Image imgFloor = new Image("/floor.png");

    //move logic
    Random rnd = new Random();

    public Enemy(Image thisEnemy, GridPane thisLevel, int x, int y) {
        this.thisEnemy = thisEnemy;
        this.thisLevel = thisLevel;
        this.x = x;
        this.y = y;
        this.row = thisLevel.getColumnCount();
    }

    public void move() {
        int l = rnd.nextInt(4);
        if (l == 0) {
            imageView = (ImageView) thisLevel.getChildren().get((x - 1) * row + y);
            if (Objects.equals(imageView.getImage().getUrl(), imgFloor.getUrl())) {
                updateDraw(x * row + y, (x - 1) * row + y);
                x -= 1;
            }
        } else if (l == 1) {
            imageView = (ImageView) thisLevel.getChildren().get((x + 1) * row + y);
            if (Objects.equals(imageView.getImage().getUrl(), imgFloor.getUrl())) {
                updateDraw(x * row + y, (x + 1) * row + y);
                x += 1;
            }
        } else if (l == 2) {
            imageView = (ImageView) thisLevel.getChildren().get(x * row + y + 1);
            if (Objects.equals(imageView.getImage().getUrl(), imgFloor.getUrl())) {
                updateDraw(x * row + y, x * row + y + 1);
                y += 1;
            }
        } else {
            imageView = (ImageView) thisLevel.getChildren().get(x * row + y - 1);
            if (Objects.equals(imageView.getImage().getUrl(), imgFloor.getUrl())) {
                updateDraw(x * row + y, x * row + y - 1);
                y -= 1;
            }
        }
    }

    private void updateDraw(int newPos, int oldPos){
        imageView = (ImageView) thisLevel.getChildren().get(oldPos);
        imageView.setImage(thisEnemy);
        imageView = (ImageView) thisLevel.getChildren().get(newPos);
        imageView.setImage(new Image("floor.png"));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
