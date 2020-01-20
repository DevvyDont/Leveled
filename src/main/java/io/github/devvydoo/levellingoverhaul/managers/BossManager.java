package io.github.devvydoo.levellingoverhaul.managers;

public class BossManager {

    public double calculateEnderDragonHealth(int level) {
        return (Math.pow(level, 4) / 1000.) + (Math.pow(level, 3) / 25.);
    }

}
