package main.java.multitallented.plugins.herostronghold.effects;

import java.util.ArrayList;
import main.java.multitallented.plugins.herostronghold.Effect;
import main.java.multitallented.plugins.herostronghold.HeroStronghold;
import main.java.multitallented.plugins.herostronghold.PlayerInRegionEvent;
import main.java.multitallented.plugins.herostronghold.RegionManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;

/**
 *
 * @author Multitallented
 */
public class EffectLandMine extends Effect {
    public EffectLandMine(HeroStronghold plugin) {
        super(plugin);
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

            //Check if the region has the shoot arrow effect and return arrow velocity
            int explode = effect.regionHasEffect(pIREvent.getEffects(), "landmine");
            if (explode == 0)
                return;
            
            Location l = pIREvent.getRegionLocation();
            
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
            if (rm.hasExplode()) {
                rm.destroyRegion(l);
            } else {
                rm.destroyRegion(l);
                l.getBlock().setTypeId(46);
                l.getBlock().getRelative(BlockFace.DOWN).setType(Material.REDSTONE_TORCH_ON);
            }
            
            //Set the event to destroy the region
            ArrayList<Location> tempArray = new ArrayList<Location>();
            tempArray.add(l);
            pIREvent.setRegionsToDestroy(tempArray);
        }
    }
    
}
