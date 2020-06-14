package ru.valaubr.models;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ru.valaubr.views.HeroView;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HeroModel {
    //Плохо конечно, слабой связи нет...
    //Так получилось что я все же решил использовать GridPane и из - за этого отвалилось больщое количество GameDev паттернов :(
    //(т.к. данный компонент потребляет очень много ресурсов
    //приходится перерисовывать всё во view "поклеточно", даже game event loop`а как такового нет :C)
    private HeroView view;
    private ImageView imageView;

    //state
    private float health = 100;
    private boolean isAlive = true;

    //hero position
    private int x;
    private int y;
    public boolean changeLVL = false;

    private final Random rnd = new Random();
    private final int enemyDmg = 5;

    int row;
    int column;

    private final Pattern enemy = Pattern.compile("enemy");
    private final Pattern weapon = Pattern.compile("weapon");
    List<Enemy> enemies;

    private Label status;
    private Button restart;

    public HeroModel(int row, int column, List<Enemy> enemies, HeroView view, Label status, Button restart) {
        this.row = row;
        this.column = column;
        this.enemies = enemies;
        this.view = view;
        this.status = status;
        this.restart = restart;
    }

    public void spawn(int x, int y) {
        this.x = x + 1;
        this.y = y + 2;
        checkToChangePos(this.x, this.y);
    }

    private void checkToChangePos(int x, int y) {
        imageView = view.getImageViewOnPosition(x, y);
        Matcher isWeapon = weapon.matcher(imageView.getImage().getUrl());
        if (Objects.equals(imageView.getImage().getUrl(), view.imgFloor.getUrl())
                || Objects.equals(imageView.getImage().getUrl(), view.imgDoor.getUrl())) {
            changePos(x, y);
        } else if (Objects.equals(imageView.getImage().getUrl(), view.imgNextFloor.getUrl())) {
            if (enemies.isEmpty()) {
                changeLVL = true;
            }
        } else if (Objects.equals(imageView.getImage().getUrl(), view.imgTrap.getUrl())) {
            health -= 10;
            view.hp.setProgress(health / 100);
            changePos(x, y);
        } else if (isWeapon.find()
                || Objects.equals(imageView.getImage().getUrl(), view.imgArmor.getUrl())
                || Objects.equals(imageView.getImage().getUrl(), view.imgHealingPotion.getUrl())) {
            if (inventoryAddItem(imageView)) {
                changePos(x, y);
            }
        } else {
            if (isAlive) {
                checkEnemyCollision(x, y);
            } else {
                status.setVisible(true);
                restart.setVisible(true);
            }
        }
        view.camera.setPos(x, y);
    }

    private void changePos(int x, int y) {
        imageView.setImage(view.imgHero);
        if (this.y > y) {
            imageView = view.getImageViewOnPosition(x, y + 1);
        } else if (this.y < y) {
            imageView = view.getImageViewOnPosition(x, y - 1);
        } else if (this.x > x) {
            imageView = view.getImageViewOnPosition((x + 1), y);
        } else {
            imageView = view.getImageViewOnPosition((x - 1), y);
        }
        imageView.setImage(view.imgFloor);
        this.y = y;
        this.x = x;
    }

    private void checkEnemyCollision(int x, int y) {
        imageView = view.getImageViewOnPosition(x, y);
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
                        view.hp.setProgress(health / 100);
                    } catch (NullPointerException e) {
                        Logger.getLogger("Exception in 'get(N)'");
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
        VBox armorDownDmg = (VBox) view.equip.getChildren().get(0);
        VBox weaponDownDmg = (VBox) view.equip.getChildren().get(1);
        finalEnemyDmg += getStat(armorDownDmg);
        finalEnemyDmg += getStat(weaponDownDmg);
        return finalEnemyDmg;
    }

    private int getStat(VBox item) {
        int strength = Integer.parseInt(((Label) item.getChildren().get(2)).getText());
        if (strength > 0) {
            strength -= 1;
            ((Label) item.getChildren().get(2)).setText(String.valueOf(strength));
            return -2;
        }
        return 0;
    }

    private boolean inventoryAddItem(ImageView imageView) {
        if (view.inventory.getChildren().size() <= 8) {
            HBox item = new HBox();
            view.addItemParam(item, imageView, rnd);
            item.setOnMouseClicked(mouseEvent -> {
                onItemClickLogic(item);
            });
            item.getChildren().stream().forEach(node -> {
                node.setStyle("-fx-padding: 5px");
            });
            view.inventory.getChildren().add(item);
            return true;
        } else {
            System.out.println("Bag Full!");
            return false;
        }
    }

    private void onItemClickLogic(HBox item) {
        item.getChildren().forEach(node -> {
            node.setStyle("-fx-padding: 0px");
        });
        Label type = (Label) item.getChildren().get(1);
        Label stats = (Label) item.getChildren().get(2);
        switch (type.getText()) {
            case "heal":
                health += Integer.parseInt(stats.getText());
                if (health > 100) {
                    health = 100;
                }
                view.hp.setProgress(health / 100);
                break;
            case "armor":
                try {
                    view.equip.getChildren().remove(0);
                } catch (IndexOutOfBoundsException e) {
                    Logger.getLogger("Armor is clear.");
                }
                view.equip.getChildren().add(0, toVBox(item));
                break;
            case "weapon":
                try {
                    view.equip.getChildren().remove(1);
                } catch (IndexOutOfBoundsException e) {
                    Logger.getLogger("Weapon is clear.");
                }
                view.equip.getChildren().add(1, toVBox(item));
                break;
        }
        view.inventory.getChildren().remove(item);
    }

    private VBox toVBox(HBox item) {
        VBox newItem = new VBox();
        newItem.getChildren().add(item.getChildren().get(0));
        newItem.getChildren().add(item.getChildren().get(0));
        newItem.getChildren().add(item.getChildren().get(0));
        return newItem;
    }

    public void enemiesMove() {
        enemies.forEach(Enemy::move);
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

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }
}
