package io.github.devvydoo.levelingoverhaul.tasks;

import org.bukkit.World;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.scheduler.BukkitRunnable;

public class XPOrbKillerTask extends BukkitRunnable {

    private World world;
    private int times;
    private int currentIteration = 0;

    public XPOrbKillerTask(World world, int times) {
        this.world = world;
        this.times = times;
    }

    /**
     * Every time this task runs, we are going to thanos snap all the XP orbs in the currently loaded chunks
     * It's super hacky, but i dont think there is a better way to do it unfortunately
     */
    @Override
    public void run() {
        for (ExperienceOrb e : world.getEntitiesByClass(ExperienceOrb.class)) {
            if (e != null) {
                e.remove();
            }
        }
        currentIteration++;
        if (currentIteration >= times) {
            this.cancel();
        }
    }
}
