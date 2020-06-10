package ru.valaubr.controllers;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hero {
    //state
    float health = 100;
    boolean isAlive = true;
    ProgressBar hp;

    //bag
    private final VBox inventory;
    private final HBox equip;

    //hero position
    private int x;
    private int y;
    protected boolean changeLVL = false;

    private final Random rnd = new Random();
    private final int enemyDmg = 5;

    GridPane thisLevel;
    Camera camera;
    int row;
    int column;
    private ImageView imageView;
    private final Image imgHero = new Image("/hero.png");
    private final Image imgFloor = new Image("/floor.png");
    private final Image imgDoor = new Image("/door.png");
    private final Image imgNextFloor = new Image("/nextFloor.png");
    private final Image imgArmor = new Image("/armor_floor.png");
    private final Image imgHealingPotion = new Image("/heal_floor.png");

    private final Pattern enemy = Pattern.compile("enemy");
    private final Pattern weapon = Pattern.compile("weapon");
    List<Enemy> enemies;


    public Hero(GridPane thisLevel, int row, int column, Camera camera, List<Enemy> enemies, VBox inventory, HBox equipment, ProgressBar hp) {
        this.thisLevel = thisLevel;
        this.row = row;
        this.column = column;
        this.camera = camera;
        this.enemies = enemies;
        this.inventory = inventory;
        this.equip = equipment;
        this.hp = hp;
    }

    public void spawn(int x, int y) {
        this.x = x + 1;
        this.y = y + 2;
        checkToChangePos(this.x, this.y);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        checkToChangePos(x, y);
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        checkToChangePos(x, y);
    }

    private void checkToChangePos(int x, int y) {
        imageView = (ImageView) thisLevel.getChildren().get(x * row + y);
        Matcher isWeapon = weapon.matcher(imageView.getImage().getUrl());
        if (Objects.equals(imageView.getImage().getUrl(), imgFloor.getUrl())
                || Objects.equals(imageView.getImage().getUrl(), imgDoor.getUrl())) {
            imageView = (ImageView) thisLevel.getChildren().get(x * row + y);
            changePos(x, y);
        } else if (Objects.equals(imageView.getImage().getUrl(), imgNextFloor.getUrl())) {
            //Кастыль, но если честно я не совсем понимаю как мне сделать это иначе
            //Т.к. я делаю игру без event loop, опираясь исключительно на "шаги" вызов "generate"
            //оказалось сложной задачей...
            //(с удовольствием бы воспользовался "наблюдателем" да только, герой то - "наблюдаемый")
            if (enemies.isEmpty()) {
                changeLVL = true;
            }
        } else if (isWeapon.find()
                || Objects.equals(imageView.getImage().getUrl(), imgArmor.getUrl())
                || Objects.equals(imageView.getImage().getUrl(), imgHealingPotion.getUrl())) {
            if (inventoryAddItem(imageView)) {
                changePos(x, y);
            }
        } else {
            if (isAlive) {
                checkEnemyCollision(x, y);
            } else {
                System.out.println("Game Over!");
            }
        }
        camera.setPos(x, y);
    }

    private void changePos(int x, int y) {
        imageView.setImage(imgHero);
        if (this.y > y) {
            imageView = (ImageView) thisLevel.getChildren().get(x * row + y + 1);
        } else if (this.y < y) {
            imageView = (ImageView) thisLevel.getChildren().get(x * row + y - 1);
        } else if (this.x > x) {
            imageView = (ImageView) thisLevel.getChildren().get((x + 1) * row + y);
        } else {
            imageView = (ImageView) thisLevel.getChildren().get((x - 1) * row + y);
        }
        imageView.setImage(imgFloor);
        this.y = y;
        this.x = x;
    }

    private void checkEnemyCollision(int x, int y) {
        imageView = (ImageView) thisLevel.getChildren().get(x * row + y);
        Matcher isEnemy = enemy.matcher(imageView.getImage().getUrl());
        if (isEnemy.find()) {
            //O(n) обход в данном случае вполне уместен, нет смысла изощряться
            //т.к. для более быстрого доступа к объектам придется использовать доп память
            //а раз уж это не RTS, объектов для быстрого доступа не то что бы много...
            for (int i = 0; i < enemies.size(); i++) {
                if (enemies.get(i).getX() == x && enemies.get(i).getY() == y) {
                    enemies.remove(i);
                    try {
                        health -= getFinalEnemyDmg();
                        hp.setProgress(health / 100);
                    } catch (NullPointerException e){
                        Logger.getLogger("TODO: Пофикси этот кастыль, игра и так неприлично много ест.");
                    }
                    if (health <= 0) {
                        isAlive = false;
                    }
                }
            }
            changePos(x, y);
        }
    }

    private int getFinalEnemyDmg() {
        int finalEnemyDmg = enemyDmg;
        HBox armorDownDmg = (HBox) equip.getChildren().get(0);
        HBox weaponDownDmg = (HBox) equip.getChildren().get(1);
        finalEnemyDmg += getStat(armorDownDmg);
        finalEnemyDmg += getStat(weaponDownDmg);
        return finalEnemyDmg;
    }

    private int getStat(HBox item) {
        int strength = Integer.parseInt(((Label) item.getChildren().get(2)).getText());
        if (strength > 0) {
            strength -= 1;
            ((Label) item.getChildren().get(2)).setText(String.valueOf(strength));
            return -2;
        }
        return 0;
    }

    private boolean inventoryAddItem(ImageView imageView) {
        if (inventory.getChildren().size() <= 8) {
            HBox item = new HBox();
            addItemParam(item, imageView);
            item.setOnMouseClicked(mouseEvent -> {
                System.out.println("clicked");
                Label type = (Label) item.getChildren().get(1);
                Label stats = (Label) item.getChildren().get(2);
                if (type.getText().equals("heal")) {
                    health += Integer.parseInt(stats.getText());
                    if (health > 100){
                        health = 100;
                    }
                    hp.setProgress(health / 100);
                } else if (type.getText().equals("armor")) {
                    try {
                        equip.getChildren().remove(0);
                    } catch (IndexOutOfBoundsException e){
                        Logger.getLogger("No items equipped");
                    }
                    equip.getChildren().add(0, item);
                } else if (type.getText().equals("weapon")) {
                    try {
                        equip.getChildren().remove(1);
                    } catch (IndexOutOfBoundsException e){
                        Logger.getLogger("No items equipped");
                    }
                    equip.getChildren().add(1, item);
                }
                inventory.getChildren().remove(item);
            });
            inventory.getChildren().add(item);
            return true;
        } else {
            System.out.println("Bag Full!");
            return false;
        }
    }

    private void addItemParam(HBox item, ImageView imageView) {
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

    public void enemiesMove() {
        enemies.forEach(Enemy::move);
    }
}
