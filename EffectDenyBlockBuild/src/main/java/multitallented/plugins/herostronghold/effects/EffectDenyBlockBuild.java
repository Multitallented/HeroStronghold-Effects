package main.java.multitallented.plugins.herostronghold.effects;

import main.java.multitallented.plugins.herostronghold.Effect;
import main.java.multitallented.plugins.herostronghold.HeroStronghold;
import main.java.multitallented.plugins.herostronghold.Region;
import main.java.multitallented.plugins.herostronghold.RegionManager;
import main.java.multitallented.plugins.herostronghold.RegionType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EndermanPlaceEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.painting.PaintingPlaceEvent;

/**
 *
 * @author Multitallented
 */
public class EffectDenyBlockBuild extends Effect {
    public EffectDenyBlockBuild(HeroStronghold plugin) {
        super(plugin);
        PListener pListener = new PListener();
        registerEvent(Type.BLOCK_PLACE, new DenyBuildListener(), Priority.Highest);
        registerEvent(Type.PAINTING_PLACE, new PListener(), Priority.High);
        registerEvent(Type.ENDERMAN_PLACE, pListener, Priority.High);
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
    }
    
    public boolean shouldTakeAction(Location loc, Player player) {
        RegionManager rm = getPlugin().getRegionManager();
        for (Location l : rm.getRegionLocations()) {
            if (l.getWorld().getName().equals(loc.getWorld().getName())) {
                Region r = rm.getRegion(l);
                RegionType rt = rm.getRegionType(r.getType());
                if (rt.getRadius() >= Math.sqrt(l.distanceSquared(loc))) {
                    if ((r.isOwner(player.getName()) || r.isMember(player.getName())) || regionHasEffect(rt.getEffects(), "denyblockbuild") == 0 ||
                            !hasReagents(l))
                        return false;
                    return true;
                }
            }
        }
        return false;
    }
    
    public class DenyBuildListener extends BlockListener {
        @Override
        public void onBlockPlace(BlockPlaceEvent event) {
            if (event.isCancelled() || !shouldTakeAction(event.getBlock().getLocation(), event.getPlayer()))
                return;
            
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.GRAY + "[HeroStronghold] This region is protected");
        }
    }
    
    public class PListener extends EntityListener {
        @Override
        public void onPaintingPlace(PaintingPlaceEvent event) {
            if (event.isCancelled() || !shouldTakeAction(event.getPainting().getLocation(), event.getPlayer()))
                return;
            
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.GRAY + "[HeroStronghold] This region is protected");
        }
        
        @Override
        public void onEndermanPlace(EndermanPlaceEvent event) {
            if (event.isCancelled() || !shouldTakeAction(event.getLocation(), null))
                return;
            event.setCancelled(true);
        }
    }
    
}
