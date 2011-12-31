package main.java.multitallented.plugins.herostronghold.effects;

import multitallented.redcastlemedia.bukkit.herostronghold.effect.Effect;
import multitallented.redcastlemedia.bukkit.herostronghold.HeroStronghold;
import multitallented.redcastlemedia.bukkit.herostronghold.events.PlayerInRegionEvent;
import multitallented.redcastlemedia.bukkit.herostronghold.region.Region;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionManager;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
public class EffectTeleport extends Effect {
    public EffectTeleport(HeroStronghold plugin) {
        super(plugin);
        registerEvent(Type.CUSTOM_EVENT, new TeleportListener(this), Priority.Highest);
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
    }
    
    public class TeleportListener extends CustomEventListener {
        private final EffectTeleport effect;
        public TeleportListener(EffectTeleport effect) {
            this.effect = effect;
        }
        //TODO add sign data so that I can switch exit teleporters with a sign
        
        @Override
        public void onCustomEvent(Event event) {
            if (!(event instanceof PlayerInRegionEvent))
                return;
            PlayerInRegionEvent pIREvent = (PlayerInRegionEvent) event;
            if (!pIREvent.getRegionLocation().getBlock().getRelative(BlockFace.UP).equals(pIREvent.getPlayer().getLocation().getBlock()))
                return;
            Location l = pIREvent.getRegionLocation();
            RegionManager rm = getPlugin().getRegionManager();
            Region r = rm.getRegion(l);
            RegionType rt = rm.getRegionType(r.getType());
            
            //Check if the region is a teleporter
            if (effect.regionHasEffect(rt.getEffects(), "teleport") == 0)
                return;
            
            //Check if there is another teleporter owned by that player
            String owner = r.getOwners().get(0);
            Location targetLoc = null;
            for (Location loc : rm.getRegionLocations()) {
                Region targetR = rm.getRegion(loc);
                if (!targetR.getOwners().isEmpty() && targetR.getOwners().get(0).equals(owner) && targetR != rm.getRegion(l)) {
                    RegionType targetRT = rm.getRegionType(targetR.getType());
                    if (effect.regionHasEffect(targetRT.getEffects(), "teleport") != 0)
                        targetLoc = loc;
                }
            }
            
            Player player = pIREvent.getPlayer();
            if (targetLoc == null) {
                player.sendMessage(ChatColor.GRAY + "[HeroStronghold] There is no exit teleporter owned by " + owner);
                return;
            }
            
            
            //Check to see if the HeroStronghold has enough reagents
            if (!effect.hasReagents(l))
                return;
            
            //Run upkeep but don't need to know if upkeep occured
            effect.forceUpkeep(l);
            player.teleport(targetLoc.getBlock().getRelative(BlockFace.NORTH, 2).getLocation());
        }
    }
    
}
