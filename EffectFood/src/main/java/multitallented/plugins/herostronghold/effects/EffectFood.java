package main.java.multitallented.plugins.herostronghold.effects;

import java.util.HashMap;
import multitallented.redcastlemedia.bukkit.herostronghold.HeroStronghold;
import multitallented.redcastlemedia.bukkit.herostronghold.effect.Effect;
import multitallented.redcastlemedia.bukkit.herostronghold.events.RegionCreatedEvent;
import multitallented.redcastlemedia.bukkit.herostronghold.events.UpkeepEvent;
import multitallented.redcastlemedia.bukkit.herostronghold.region.Region;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionManager;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionType;
import multitallented.redcastlemedia.bukkit.herostronghold.region.SuperRegion;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author Multitallented
 */
public class EffectFood extends Effect {
    private HashMap<SuperRegion, Region> fedRegions = new HashMap<SuperRegion, Region>();
    
    public EffectFood(HeroStronghold plugin) {
        super(plugin);
        registerEvent(new UpkeepListener(this));
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
    }
    
    public class UpkeepListener implements Listener {
        private final EffectFood effect;
        public UpkeepListener(EffectFood effect) {
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
        
        @EventHandler
        public void onRegionCreated(RegionCreatedEvent event) {
            RegionManager rm = getPlugin().getRegionManager();
            Region r = rm.getRegion(event.getLocation());
            RegionType rt = null;
            try {
                rm.getRegionType(r.getType());
            } catch (NullPointerException npe) {
                return;
            }
            if (effect.regionHasEffect(rt.getEffects(), "food") == 0) {
                return;
            }
            
        }
    }
    
}
