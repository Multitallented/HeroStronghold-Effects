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

/**
 *
 * @author Multitallented
 */
public class EffectDenyBlockBuild extends Effect {
    public EffectDenyBlockBuild(HeroStronghold plugin) {
        super(plugin);
        DenyBuildListener dbListener = new DenyBuildListener(this);
        registerEvent(Type.BLOCK_PLACE, dbListener, Priority.Highest);
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
    }
    
    public class DenyBuildListener extends BlockListener {
        private final EffectDenyBlockBuild effect;
        public DenyBuildListener(EffectDenyBlockBuild effect) {
            this.effect = effect;
        }
        
        
        @Override
        public void onBlockPlace(BlockPlaceEvent event) {
            if (event.isCancelled())
                return;
            Location loc = event.getBlock().getLocation();
            Player player = event.getPlayer();
            RegionManager rm = effect.getPlugin().getRegionManager();
            for (Location l : rm.getRegionLocations()) {
                if (l.getWorld().getName().equals(loc.getWorld().getName())) {
                    Region r = rm.getRegion(l);
                    RegionType rt = rm.getRegionType(r.getType());
                    if (rt.getRadius() >= Math.sqrt(l.distanceSquared(loc))) {
                        if ((r.isOwner(player.getName()) || r.isMember(player.getName())) || effect.regionHasEffect(rt.getEffects(), "denyblockbuild") == 0 ||
                                !effect.hasReagents(l))
                            return;

                        player.sendMessage(ChatColor.GRAY + "[HeroStronghold] This region is protected by a " + r.getType());
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }
    
}
