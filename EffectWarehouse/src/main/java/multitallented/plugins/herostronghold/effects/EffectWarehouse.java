package main.java.multitallented.plugins.herostronghold.effects;

import multitallented.redcastlemedia.bukkit.herostronghold.HeroStronghold;
import multitallented.redcastlemedia.bukkit.herostronghold.effect.Effect;
import multitallented.redcastlemedia.bukkit.herostronghold.events.RegionCreatedEvent;
import multitallented.redcastlemedia.bukkit.herostronghold.events.UpkeepEvent;
import multitallented.redcastlemedia.bukkit.herostronghold.region.Region;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionManager;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionType;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

/**
 *
 * @author Multitallented
 */
public class EffectWarehouse extends Effect {
    public EffectWarehouse(HeroStronghold plugin) {
        super(plugin);
        registerEvent(new UpkeepListener(this));
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
    }
    
    public class UpkeepListener implements Listener {
        private final EffectWarehouse effect;
        public UpkeepListener(EffectWarehouse effect) {
            this.effect = effect;
        }
        
        
        @EventHandler
        public void onCustomEvent(UpkeepEvent event) {
            Location l = event.getLocation();
            RegionManager rm = getPlugin().getRegionManager();
            Region r = rm.getRegion(l);
            if (r == null) {
                return;
            }
            RegionType rt = rm.getRegionType(r.getType());
            
            int warehouse = effect.regionHasEffect(rt.getEffects(), "warehouse");
            
            //Check if the region is a teleporter
            if (warehouse == 0) {
                return;
            }
            
            //Check to see if the HeroStronghold has enough reagents
            if (!effect.hasReagents(l)) {
                return;
            }
            
            //Check if any regions nearby need items
            
        }
        
        /*@EventHandler
        public void onRegionCreated(RegionCreatedEvent event) {
            RegionManager rm = getPlugin().getRegionManager();
            Region r = rm.getRegion(event.getLocation());
            RegionType rt = null;
            try {
                rm.getRegionType(r.getType());
            } catch (NullPointerException npe) {
                return;
            }
            if (effect.regionHasEffect(rt.getEffects(), "warehouse") == 0) {
                return;
            }
            Chest chest = null;
            try {
                chest = (Chest) event.getLocation().getBlock().getState();
            } catch (Exception e) {
                return;
            }
            
        }*/
    }
    
}
