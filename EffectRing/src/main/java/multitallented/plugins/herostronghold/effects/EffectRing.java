package main.java.multitallented.plugins.herostronghold.effects;

import multitallented.redcastlemedia.bukkit.herostronghold.HeroStronghold;
import multitallented.redcastlemedia.bukkit.herostronghold.effect.Effect;
import multitallented.redcastlemedia.bukkit.herostronghold.events.SuperRegionCreatedEvent;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionManager;
import multitallented.redcastlemedia.bukkit.herostronghold.region.SuperRegion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author Multitallented
 */
public class EffectRing extends Effect {
    private final RegionManager rm;
    private final HeroStronghold aPlugin;
    public EffectRing(HeroStronghold plugin) {
        super(plugin);
        this.rm = plugin.getRegionManager();
        this.aPlugin = plugin;
        registerEvent(new IntruderListener());
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
    }
    
    public class IntruderListener implements Listener {
        private int x= 0;
        private int z= 0;
        
        @EventHandler
        public void onCustomEvent(SuperRegionCreatedEvent event) {
            final SuperRegion sr = rm.getSuperRegion(event.getName());
            
            //Check if super-region has the effect
            if (!rm.getSuperRegionType(sr.getType()).hasEffect("ring")) {
                return;
            }
            
            final Location l = sr.getLocation();
            final int radius = (int) Math.sqrt(rm.getSuperRegionType(sr.getType()).getRadius());
            final World world = l.getWorld();
            x = 0;
            z = 0;
            final int threadID = aPlugin.getServer().getScheduler().scheduleSyncRepeatingTask(aPlugin,
                    new Runnable() {
                        @Override
                        public void run() {
                            
                            if (x <= radius) {
                                int xp = (int) l.getX() + x;
                                int xn = (int) l.getX() - x;
                                int asdf = (int) Math.sqrt(radius*radius - (x * x));
                                int zp = asdf + (int) l.getZ();
                                int zn = (int) l.getZ() - asdf;
                                world.getBlockAt(xp, 125, zp).setType(Material.OBSIDIAN);
                                world.getBlockAt(xn, 125, zp).setType(Material.OBSIDIAN);
                                world.getBlockAt(xp, 125, zn).setType(Material.OBSIDIAN);
                                world.getBlockAt(xn, 125, zn).setType(Material.OBSIDIAN);
                                world.getBlockAt(xp, 126, zp).setType(Material.TORCH);
                                world.getBlockAt(xn, 126, zp).setType(Material.TORCH);
                                world.getBlockAt(xp, 126, zn).setType(Material.TORCH);
                                world.getBlockAt(xn, 126, zn).setType(Material.TORCH);
                                    
                            }
                            x++;
                        }
                    }, 0, 2L);
            final int threadID1 = aPlugin.getServer().getScheduler().scheduleSyncRepeatingTask(aPlugin,
                    new Runnable() {
                        @Override
                        public void run() {
                            
                            if (z <= radius) {
                                int zp = (int) l.getZ() + z;
                                int zn = (int) l.getZ() - z;
                                int asdf = (int) Math.sqrt(radius*radius - (z * z));
                                int xp = asdf + (int) l.getX();
                                int xn = (int) l.getX() - asdf;
                                world.getBlockAt(xp, 125, zp).setType(Material.OBSIDIAN);
                                world.getBlockAt(xn, 125, zp).setType(Material.OBSIDIAN);
                                world.getBlockAt(xp, 125, zn).setType(Material.OBSIDIAN);
                                world.getBlockAt(xn, 125, zn).setType(Material.OBSIDIAN);
                                world.getBlockAt(xp, 126, zp).setType(Material.TORCH);
                                world.getBlockAt(xn, 126, zp).setType(Material.TORCH);
                                world.getBlockAt(xp, 126, zn).setType(Material.TORCH);
                                world.getBlockAt(xn, 126, zn).setType(Material.TORCH);
                                    
                            }
                            z++;
                        }
                    }, 0, 2L);
            aPlugin.getServer().getScheduler().scheduleSyncDelayedTask(aPlugin, new Runnable() {
                @Override
                public void run() {
                    aPlugin.getServer().getScheduler().cancelTask(threadID);
                    aPlugin.getServer().getScheduler().cancelTask(threadID1);
                    x =0;
                    z=0;
                }
            }, 2 * radius);
            
        }
    }
    
}
