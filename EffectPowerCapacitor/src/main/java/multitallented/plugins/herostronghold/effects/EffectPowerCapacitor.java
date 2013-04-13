package main.java.multitallented.plugins.herostronghold.effects;

import java.util.ArrayList;
import java.util.HashMap;
import multitallented.redcastlemedia.bukkit.herostronghold.HeroStronghold;
import multitallented.redcastlemedia.bukkit.herostronghold.effect.Effect;
import multitallented.redcastlemedia.bukkit.herostronghold.events.RegionCreatedEvent;
import multitallented.redcastlemedia.bukkit.herostronghold.events.RegionDestroyedEvent;
import multitallented.redcastlemedia.bukkit.herostronghold.region.Region;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionManager;
import multitallented.redcastlemedia.bukkit.herostronghold.region.SuperRegion;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author Multitallented
 */
public class EffectPowerCapacitor extends Effect {
    public EffectPowerCapacitor(HeroStronghold plugin) {
        super(plugin);
        registerEvent(new EffectPowerCapacitor.UpkeepListener(this));
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
        
    }
    
    public class UpkeepListener implements Listener {
        private final EffectPowerCapacitor effect;
        public UpkeepListener(EffectPowerCapacitor effect) {
            this.effect = effect;
        }
        
        @EventHandler
        public void onRegionCreated(RegionCreatedEvent event) {
            Region r = event.getRegion();
            RegionManager rm = effect.getPlugin().getRegionManager();
            int amount = effect.regionHasEffect(rm.getRegionType(r.getType()).getEffects(), "powercapacitor");
            if (amount < 1) {
                return;
            }
            
            for (SuperRegion sr : rm.getContainingSuperRegions(r.getLocation())) {
                sr.setMaxPower(sr.getMaxPower() + amount);
            }
        }
        
        @EventHandler
        public void onRegionDestroyed(RegionDestroyedEvent event) {
            Region r = event.getRegion();
            RegionManager rm = effect.getPlugin().getRegionManager();
            int amount = effect.regionHasEffect(rm.getRegionType(r.getType()).getEffects(), "powercapacitor");
            if (amount < 1) {
                return;
            }
            
            for (SuperRegion sr : rm.getContainingSuperRegions(r.getLocation())) {
                if (sr.getMaxPower() - amount < 0) {
                    sr.setMaxPower(1);
                    continue;
                }
                sr.setMaxPower(sr.getMaxPower() - amount);
            }
        }
    }
}