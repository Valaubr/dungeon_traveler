package ru.valaubr.models;

import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import ru.valaubr.controllers.HeroController;

import java.util.List;

public class LocationModel {
    GridPane thisLevel;
    HeroController hero;
    List<Enemy> enemies;
    LocationChangeHelper view;

    public LocationModel(GridPane thisLevel, HeroController hero, List<Enemy> enemies, LocationChangeHelper view) {
        this.thisLevel = thisLevel;
        this.hero = hero;
        this.enemies = enemies;
        this.view = view;
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
        if (hero.checkToChangeLVL()) {
            hero.setNewLvl();
            view.generate();
        }
    }
}
