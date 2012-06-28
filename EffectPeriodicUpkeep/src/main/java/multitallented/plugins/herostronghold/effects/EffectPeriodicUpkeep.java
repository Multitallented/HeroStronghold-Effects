package main.java.multitallented.plugins.herostronghold.effects;

import multitallented.redcastlemedia.bukkit.herostronghold.HeroStronghold;
import multitallented.redcastlemedia.bukkit.herostronghold.effect.Effect;
import multitallented.redcastlemedia.bukkit.herostronghold.events.UpkeepEvent;
import multitallented.redcastlemedia.bukkit.herostronghold.region.Region;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionType;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author Multitallented
 */
public class EffectPeriodicUpkeep extends Effect {
    public EffectPeriodicUpkeep(HeroStronghold plugin) {
        super(plugin);
        registerEvent(new UpkeepListener(this));
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
    }
    
    public class UpkeepListener implements Listener {
        private final EffectPeriodicUpkeep effect;
        public UpkeepListener(EffectPeriodicUpkeep effect) {
            this.effect = effect;
        }
        
        
        @EventHandler
        public void onCustomEvent(UpkeepEvent event) {
            Location l = event.getLocation();
            Region r = getPlugin().getRegionManager().getRegion(event.getLocation());
            if (r == null) {
                return;
            }
            RegionType rt = getPlugin().getRegionManager().getRegionType(r.getType()); 
            
            //Check if the region has the shoot arrow effect and return arrow velocity
            if (effect.regionHasEffect(rt.getEffects(), "periodicupkeep") == 0) {
                return;
            }
            
            //Check to see if the HeroStronghold has enough reagents
            if (!effect.hasReagents(l)) {
                return;
            }
            //Run upkeep but don't need to know if upkeep occured
            effect.forceUpkeep(event);
            //effect.forceUpkeep(l);
        }
    }
    
}
