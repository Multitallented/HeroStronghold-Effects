package main.java.multitallented.plugins.herostronghold.effects;

import java.util.ArrayList;
import multitallented.redcastlemedia.bukkit.herostronghold.ConfigManager;
import multitallented.redcastlemedia.bukkit.herostronghold.HeroStronghold;
import multitallented.redcastlemedia.bukkit.herostronghold.effect.Effect;
import multitallented.redcastlemedia.bukkit.herostronghold.events.PlayerInRegionEvent;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;

/**
 *
 * @author Multitallented
 */
public class EffectLandMine extends Effect {
    private final RegionManager rm;
    private final ConfigManager cm;
    public EffectLandMine(HeroStronghold plugin) {
        super(plugin);
        this.rm = plugin.getRegionManager();
        this.cm = plugin.getConfigManager();
        registerEvent(Type.CUSTOM_EVENT, new IntruderListener(this), Priority.Highest);
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
    }
    
    public class IntruderListener extends CustomEventListener {
        private final EffectLandMine effect;
        public IntruderListener(EffectLandMine effect) {
            this.effect = effect;
        }
        
        @Override
        public void onCustomEvent(Event event) {
            if (!(event instanceof PlayerInRegionEvent))
                return;
            PlayerInRegionEvent pIREvent = (PlayerInRegionEvent) event;
            Player player = pIREvent.getPlayer();
            
            Location l = pIREvent.getRegionLocation();
            ArrayList<String> effects = effect.rm.getRegionType(effect.rm.getRegion(l).getType()).getEffects();

            //Check if the region has the shoot arrow effect and return arrow velocity
            int explode = effect.regionHasEffect(effects, "landmine");
            if (explode == 0)
                return;
            
            
            //Check if the player owns or is a member of the region
            if (effect.isOwnerOfRegion(player, l) || effect.isMemberOfRegion(player, l)) {
                return;
            }
            
            //Check to see if the HeroStronghold has enough reagents
            if (!effect.hasReagents(l))
                return;
            
            //Run upkeep but don't need to know if upkeep occured
            effect.forceUpkeep(l);
            
            RegionManager rm = effect.getPlugin().getRegionManager();
            //Check to see if exploding regions are enabled
            if (cm.getExplode()) {
                rm.destroyRegion(l);
            } else {
                rm.destroyRegion(l);
                l.getBlock().setTypeId(0);
                TNTPrimed tnt = l.getWorld().spawn(l, TNTPrimed.class);
                tnt.setFuseTicks(1);
                //l.getBlock().getRelative(BlockFace.DOWN).setType(Material.REDSTONE_TORCH_ON);
            }
            
            //Set the event to destroy the region
            ArrayList<Location> tempArray = new ArrayList<Location>();
            tempArray.add(l);
            pIREvent.setRegionsToDestroy(tempArray);
        }
    }
    
}
