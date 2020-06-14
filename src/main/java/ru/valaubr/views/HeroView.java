package ru.valaubr.views;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Objects;
import java.util.Random;

public class HeroView {

    //state
    public ProgressBar hp;

    //bag
    public final VBox inventory;
    public final HBox equip;

    GridPane thisLevel;
    public Camera camera;
    public final Image imgHero = new Image("/hero.png");
    public final Image imgFloor = new Image("/floor.png");
    public final Image imgTrap = new Image("/trap.png");
    public final Image imgDoor = new Image("/door.png");
    public final Image imgNextFloor = new Image("/nextFloor.png");
    public final Image imgArmor = new Image("/armor_floor.png");
    public final Image imgHealingPotion = new Image("/heal_floor.png");

    public HeroView(GridPane thisLevel, Camera camera, VBox inventory, HBox equipment, ProgressBar hp){
        this.thisLevel = thisLevel;
        this.camera = camera;
        this.inventory = inventory;
        this.equip = equipment;
        this.hp = hp;
    }

    public ImageView getImageViewOnPosition(int x, int y){
        return (ImageView) thisLevel.getChildren().get(x * thisLevel.getRowCount() + y);
    }

    public void addItemParam(HBox item, ImageView imageView, Random rnd) {
        String img = Objects.requireNonNull(checkItemType(imageView));
        item.getChildren().add(new ImageView(new Image(img)));
        if (img.startsWith("armor")) {
            item.getChildren().add(new Label("armor"));
            item.getChildren().add(new Label(String.valueOf(rnd.nextInt(10))));
        } else if (img.startsWith("heal")) {
            item.getChildren().add(new Label("heal"));
            item.getChildren().add(new Label(String.valueOf(rnd.nextInt(15))));
        } else {
            item.getChildren().add(new Label("weapon"));
            item.getChildren().add(new Label(String.valueOf(rnd.nextInt(7))));
        }
    }

    private String checkItemType(ImageView imageView) {
        if (imageView.getImage().getUrl().endsWith("weapon0_floor.png")) {
            return "weapon0_bag.png";
        } else if (imageView.getImage().getUrl().endsWith("weapon1_floor.png")) {
            return "weapon1_bag.png";
        } else if (imageView.getImage().getUrl().endsWith("weapon2_floor.png")) {
            return "weapon2_bag.png";
        } else if (imageView.getImage().getUrl().endsWith("armor_floor.png")) {
            return "armor_bag.png";
        } else if (imageView.getImage().getUrl().endsWith("heal_floor.png")) {
            return "heal_bag.png";
        }
        return null;
    }
}
