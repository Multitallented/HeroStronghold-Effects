package main.java.multitallented.plugins.herostronghold.effects;

import java.util.Date;
import java.util.HashMap;
import multitallented.redcastlemedia.bukkit.herostronghold.HeroStronghold;
import multitallented.redcastlemedia.bukkit.herostronghold.effect.Effect;
import multitallented.redcastlemedia.bukkit.herostronghold.events.UpkeepEvent;
import multitallented.redcastlemedia.bukkit.herostronghold.region.Region;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionType;
import multitallented.redcastlemedia.bukkit.herostronghold.region.SuperRegion;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author Multitallented
 */
public class EffectSiegeWeapon extends Effect {
    private HashMap<Location, Long> lastUpkeep = new HashMap<Location, Long>();
    
    public EffectSiegeWeapon(HeroStronghold plugin) {
        super(plugin);
        registerEvent(new UpkeepListener(plugin, this));
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
    }
    
    public class UpkeepListener implements Listener {
        private final EffectSiegeWeapon effect;
        private final HeroStronghold plugin;
        public UpkeepListener(HeroStronghold plugin, EffectSiegeWeapon effect) {
            this.effect = effect;
            this.plugin = plugin;
        }
        
        
        @EventHandler
        public void onCustomEvent(UpkeepEvent event) {
            Location l = event.getRegionLocation();
            Region r = getPlugin().getRegionManager().getRegion(event.getRegionLocation());
            if (r == null)
                return;
            RegionType rt = getPlugin().getRegionManager().getRegionType(r.getType()); 
            
            //Check if the region has the shoot arrow effect and return arrow velocity
            long period = effect.regionHasEffect(rt.getEffects(), "siegeweapon");
            if (period == 0) {
                return;
            }
            
            period *= 1000;            
            
            if (lastUpkeep.get(l) != null && period + lastUpkeep.get(l) > new Date().getTime()) {
                return;
            }
            
            //Check if valid siege machine position
            if (l.getBlock().getRelative(BlockFace.UP).getY() < l.getWorld().getHighestBlockAt(l).getY()) {
                return;
            }
            
            //Check to see if the HeroStronghold has enough reagents
            if (!effect.hasReagents(l)) {
                return;
            }
            
            Block b = l.getBlock().getRelative(BlockFace.UP);
            if (!(b.getState() instanceof Sign)) {
                return;
            }
            
            //Find target Super-region
            Sign sign = (Sign) b.getState();
            String srName = sign.getLine(0);
            SuperRegion sr = plugin.getRegionManager().getSuperRegion(srName);
            if (sr == null) {
                return;
            }
            //Check if too far away
            int radius = plugin.getRegionManager().getSuperRegionType(sr.getType()).getRadius();
            int rawRadius = plugin.getRegionManager().getSuperRegionType(sr.getType()).getRawRadius();
            try {
                if (sr.getLocation().distanceSquared(l) > radius + 10000) {
                    return;
                }
            } catch (IllegalArgumentException iae) {
                return;
            }
            
            //Run upkeep but don't need to know if upkeep occured
            effect.forceUpkeep(l);
            lastUpkeep.put(l, new Date().getTime());
            
            Location spawnLoc = l.getBlock().getRelative(BlockFace.UP, 3).getLocation();
            Location srLoc = sr.getLocation();
            Location loc = new Location(spawnLoc.getWorld(), spawnLoc.getX(), spawnLoc.getY() + 15, spawnLoc.getZ());
            final Location loc1 = new Location(spawnLoc.getWorld(), spawnLoc.getX(), spawnLoc.getY() + 20, spawnLoc.getZ());
            final Location loc2 = new Location(spawnLoc.getWorld(), spawnLoc.getX(), spawnLoc.getY() + 25, spawnLoc.getZ());
            final Location loc3 = new Location(spawnLoc.getWorld(), spawnLoc.getX(), spawnLoc.getY() + 30, spawnLoc.getZ());
            TNTPrimed tnt = l.getWorld().spawn(loc, TNTPrimed.class);
            tnt.setFuseTicks(1);
            
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                TNTPrimed tnt = loc1.getWorld().spawn(loc1, TNTPrimed.class);
                tnt.setFuseTicks(1);
            }
            }, 5L);
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                TNTPrimed tnt = loc2.getWorld().spawn(loc2, TNTPrimed.class);
                tnt.setFuseTicks(1);
            }
            }, 10L);
            
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                TNTPrimed tnt = loc3.getWorld().spawn(loc3, TNTPrimed.class);
                tnt.setFuseTicks(1);
            }
            }, 15L);
            
            double randX = srLoc.getX() + Math.random()*rawRadius*(-1 * (int) (Math.random() + 0.5));
            double randZ = srLoc.getZ() + Math.random()*rawRadius*(-1 * (int) (Math.random() + 0.5));
            final Location endLoc = new Location(srLoc.getWorld(), randX, 240, randZ);
            
            plugin.getRegionManager().reduceRegion(sr);
            if (sr.getPower() < 1 && plugin.getConfigManager().getDestroyNoPower()) {
                event.setRegionsToDestroy(event.getRegionsToDestroy());
            }
            
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                TNTPrimed tnt = endLoc.getWorld().spawn(endLoc, TNTPrimed.class);
                tnt.setFuseTicks(500);
            }
            }, 100L);
        }
    }
    
}
