package main.java.multitallented.plugins.herostronghold.effects;

import java.util.Date;
import java.util.HashMap;
import multitallented.redcastlemedia.bukkit.herostronghold.HeroStronghold;
import multitallented.redcastlemedia.bukkit.herostronghold.effect.Effect;
import multitallented.redcastlemedia.bukkit.herostronghold.events.UpkeepEvent;
import multitallented.redcastlemedia.bukkit.herostronghold.region.Region;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionType;
import org.bukkit.Location;
import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;

/**
 *
 * @author Multitallented
 */
public class EffectScheduledUpkeep extends Effect {
    private HashMap<Location, Long> lastUpkeep = new HashMap<Location, Long>();
    
    public EffectScheduledUpkeep(HeroStronghold plugin) {
        super(plugin);
        registerEvent(Type.CUSTOM_EVENT, new UpkeepListener(this), Priority.Highest);
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
    }
    
    public class UpkeepListener extends CustomEventListener {
        private final EffectScheduledUpkeep effect;
        public UpkeepListener(EffectScheduledUpkeep effect) {
            this.effect = effect;
        }
        
        
        @Override
        public void onCustomEvent(Event event) {
            if (!(event instanceof UpkeepEvent))
                return;
            UpkeepEvent uEvent = (UpkeepEvent) event;
            Location l = uEvent.getRegionLocation();
            Region r = getPlugin().getRegionManager().getRegion(uEvent.getRegionLocation());
            if (r == null)
                return;
            RegionType rt = getPlugin().getRegionManager().getRegionType(r.getType()); 
            
            //Check if the region has the shoot arrow effect and return arrow velocity
            long period = effect.regionHasEffect(rt.getEffects(), "scheduledupkeep");
            if (period == 0) {
                return;
            }
            
            period *= 1000;            
            
            if (lastUpkeep.get(l) == null) {
                //Check to see if the HeroStronghold has enough reagents
                if (!effect.hasReagents(l)) {
                    return;
                }
                //Run upkeep but don't need to know if upkeep occured
                effect.forceUpkeep(l);
                lastUpkeep.put(l, new Date().getTime());
                return;
            } else if (period + lastUpkeep.get(l) > new Date().getTime()) {
                return;
            }
            
            //Check to see if the HeroStronghold has enough reagents
            if (!effect.hasReagents(l))
                return;
            //Run upkeep but don't need to know if upkeep occured
            effect.forceUpkeep(l);
            lastUpkeep.put(l, new Date().getTime());
        }
    }
    
}