package main.java.multitallented.plugins.herostronghold.effects;

import main.java.multitallented.plugins.herostronghold.Effect;
import main.java.multitallented.plugins.herostronghold.HeroStronghold;
import main.java.multitallented.plugins.herostronghold.Region;
import main.java.multitallented.plugins.herostronghold.RegionType;
import main.java.multitallented.plugins.herostronghold.UpkeepEvent;
import org.bukkit.Location;
import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;

/**
 *
 * @author Multitallented
 */
public class EffectPeriodicUpkeep extends Effect {
    public EffectPeriodicUpkeep(HeroStronghold plugin) {
        super(plugin);
        registerEvent(Type.CUSTOM_EVENT, new UpkeepListener(this), Priority.Highest);
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
    }
    
    public class UpkeepListener extends CustomEventListener {
        private final EffectPeriodicUpkeep effect;
        public UpkeepListener(EffectPeriodicUpkeep effect) {
            this.effect = effect;
        }
        
        
        @Override
        public void onCustomEvent(Event event) {
            if (!(event instanceof UpkeepEvent))
                return;
            UpkeepEvent uEvent = (UpkeepEvent) event;
            Location l = uEvent.getRegionLocation();
            Region r = getPlugin().getRegionManager().getRegion(uEvent.getRegionLocation());
            RegionType rt = getPlugin().getRegionManager().getRegionType(r.getType()); 
            
            //Check if the region has the shoot arrow effect and return arrow velocity
            if (effect.regionHasEffect(rt.getEffects(), "periodicupkeep") == 0)
                return;
            
            //Check to see if the HeroStronghold has enough reagents
            if (!effect.hasReagents(l))
                return;
            //Run upkeep but don't need to know if upkeep occured
            effect.forceUpkeep(l);
        }
    }
    
}
