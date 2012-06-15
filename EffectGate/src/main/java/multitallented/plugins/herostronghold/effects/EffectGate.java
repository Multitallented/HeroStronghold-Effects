package main.java.multitallented.plugins.herostronghold.effects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import multitallented.redcastlemedia.bukkit.herostronghold.HeroStronghold;
import multitallented.redcastlemedia.bukkit.herostronghold.effect.Effect;
import multitallented.redcastlemedia.bukkit.herostronghold.region.Region;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionManager;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;

/**
 *
 * @author Multitallented
 */
public class EffectGate extends Effect {
    private final RegionManager rm;
    private final Map<Location, Set<Block>> gates = new HashMap<Location, Set<Block>>();
    private final Set<Location> openGates = new HashSet<Location>();
    public EffectGate(HeroStronghold plugin) {
        super(plugin);
        this.rm = plugin.getRegionManager();
        registerEvent(new IntruderListener(this));
        registerEvent(new CloseGateListener());
        //registerEvent(Type.CUSTOM_EVENT, new GateDestroyedListener(), Priority.Normal);
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
    }
    
    /*public class GateDestroyedListener extends CustomEventListener {
        @Override
        public void onCustomEvent(Event event) {
            if (!(event instanceof RegionDestroyedEvent)) {
                return;
            }
            RegionDestroyedEvent rde = (RegionDestroyedEvent) event;
            Location l  = rde.getLocation();
            if (!openGates.contains(l)) {
                return;
            }
            
            for (Block b : gates.get(l)) {
                b.setTypeId(85);
            }
            
        }
    }*/
    
    public class CloseGateListener implements Listener {
        @EventHandler
        public void onPluginDisable(PluginDisableEvent event) {
            //Close gates if the plugin is going to be disabled
            if (event.getPlugin().getDescription().getName().equals("HeroStronghold")) {
                for (Location l : openGates) {
                    for (Block b : gates.get(l)) {
                        b.setTypeId(85);
                    }
                }
            }
        }
    }
    
    public class IntruderListener implements Listener {
        private final EffectGate effect;
        public IntruderListener(EffectGate effect) {
            this.effect = effect;
        }
        
        @EventHandler
        public void onPlayerInteract(PlayerInteractEvent event) {
            Block block = event.getClickedBlock();
            //Check if a sign
            try {
                if (!(block.getState() instanceof Sign)) {
                    return;
                }
            } catch (NullPointerException npe) {
                return;
            }
            //Check if its a gate sign
            Sign sign = (Sign) block.getState();
            if (!sign.getLine(0).equalsIgnoreCase("[Gate]")) {
                return;
            }
            
            Location currentLocation = block.getLocation();
            
            double x1 = currentLocation.getX();
            Location loc = null;
            for (Region r : rm.getSortedRegions()) {
                int radius = rm.getRegionType(r.getType()).getRadius();
                Location l = r.getLocation();
                if (l.getX() + radius < x1) {
                    return;
                }
                try {
                    if (!(l.getX() - radius > x1) && l.distanceSquared(currentLocation) < radius) {
                        loc = l;
                        break;
                    }
                } catch (IllegalArgumentException iae) {

                }
            }
            if (loc == null) {
                return;
            }
            
            //Check if the region has the shoot arrow effect and return arrow velocity
            double speed = effect.regionHasEffect(effect.rm.getRegionType(effect.rm.getRegion(loc).getType()).getEffects(), "gate");
            if (speed == 0)
                return;
            
            Player player = event.getPlayer();
            
            //Check if the player owns or is a member of the region
            if (!effect.isOwnerOfRegion(player, loc) && !effect.isMemberOfRegion(player, loc)) {
                return;
            }
            
            //Check to see if the HeroStronghold has enough reagents
            if (!effect.hasReagents(loc))
                return;
            
            //Run upkeep but don't need to know if upkeep occured
            effect.forceUpkeep(loc);
            
            //Open or close the gate
            if (!gates.containsKey(loc)) {
                RegionType currentRegionType = rm.getRegionType(rm.getRegion(loc).getType());
                
                int radius = (int) Math.sqrt(currentRegionType.getRadius());

                int lowerLeftX = (int) loc.getX() - radius;
                int lowerLeftY = (int) loc.getY() - radius;
                lowerLeftY = lowerLeftY < 0 ? 0 : lowerLeftY;
                int lowerLeftZ = (int) loc.getZ() - radius;

                int upperRightX = (int) loc.getX() + radius;
                int upperRightY = (int) loc.getY() + radius;
                upperRightY = upperRightY > 255 ? 255 : upperRightY;
                int upperRightZ = (int) loc.getZ() + radius;
                
                World world = loc.getWorld();
                
                Set<Block> tempSet = new HashSet<Block>();
                
                outer: for (int x=lowerLeftX; x<upperRightX; x++) {
                    
                    for (int z=lowerLeftZ; z<upperRightZ; z++) {
                        
                        for (int y=lowerLeftY; y<upperRightY; y++) {
                            
                            Block currentBlock = world.getBlockAt(x, y, z);
                            int type = currentBlock.getTypeId();
                            if (type == 85) {
                                tempSet.add(currentBlock);
                            }
                        }
                        
                    }
                    
                }
                if (tempSet.isEmpty()) {
                    rm.destroyRegion(loc);
                    rm.removeRegion(loc);
                    return;
                }
                gates.put(loc, tempSet);
            }
            
            event.setCancelled(true);
            if (openGates.contains(loc)) {
                //Gate is open
                for (Block b : gates.get(loc)) {
                    b.setTypeId(85);
                }
                openGates.remove(loc);
            } else {
                //Gate is closed
                for (Block b : gates.get(loc)) {
                    b.setTypeId(0);
                }
                openGates.add(loc);
            }
        }
    }
    
}
