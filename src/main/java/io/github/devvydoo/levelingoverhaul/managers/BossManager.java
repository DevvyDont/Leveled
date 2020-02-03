package io.github.devvydoo.levelingoverhaul.managers;

public class BossManager {

    public double calculateEnderDragonHealth(int level) {
        return (Math.pow(level, 4) / 156.5) + (Math.pow(level, 3) / 5.) - 102083.082;
    }

    public double calculateWitherHealth(int level){
        return calculateEnderDragonHealth(level);
    }

    public double calculateElderGuardianHealth(int level){
        return calculateEnderDragonHealth(level);
    }

}
