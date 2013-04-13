package main.java.multitallented.plugins.herostronghold.effects;

import java.util.ArrayList;
import multitallented.redcastlemedia.bukkit.herostronghold.HeroStronghold;
import multitallented.redcastlemedia.bukkit.herostronghold.effect.Effect;
import multitallented.redcastlemedia.bukkit.herostronghold.events.UpkeepSuccessEvent;
import multitallented.redcastlemedia.bukkit.herostronghold.region.Region;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionManager;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionType;
import multitallented.redcastlemedia.bukkit.herostronghold.region.SuperRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
/**
 *
 * @author Multitallented
 */
public class EffectDrainPower extends Effect {
    private final RegionManager rm;
    public EffectDrainPower(HeroStronghold plugin) {
        super(plugin);
        this.rm = plugin.getRegionManager();
        registerEvent(new IntruderListener(this));
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
    }
    
    public class IntruderListener implements Listener {
        private final EffectDrainPower effect;
        public IntruderListener(EffectDrainPower effect) {
            this.effect = effect;
        }
        
        @EventHandler
        public void onCustomEvent(UpkeepSuccessEvent event) {
            Region r = rm.getRegion(event.getRegionLocation());
            RegionType rt = rm.getRegionType(r.getType());
            if (effect.regionHasEffect(r, "drainpower") == 0) {
                return;
            }
            ArrayList<SuperRegion> srs = rm.getContainingSuperRegions(r.getLocation());
            if (srs.isEmpty()) {
                return;
            }
        }
    }
    
}
