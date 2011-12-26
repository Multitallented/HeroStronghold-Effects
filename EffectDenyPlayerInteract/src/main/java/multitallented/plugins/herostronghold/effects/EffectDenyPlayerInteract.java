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
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

/**
 *
 * @author Multitallented
 */
public class EffectDenyPlayerInteract extends Effect {
    public EffectDenyPlayerInteract(HeroStronghold plugin) {
        super(plugin);
        DenyPlayerInteractListener dpeListener = new DenyPlayerInteractListener(this);
        registerEvent(Type.PLAYER_INTERACT, dpeListener, Priority.High);
        registerEvent(Type.PLAYER_BED_ENTER, dpeListener, Priority.High);
        registerEvent(Type.PLAYER_BUCKET_FILL, dpeListener, Priority.High);
        registerEvent(Type.PLAYER_BUCKET_EMPTY, dpeListener, Priority.High);
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
    }
    
    public class DenyPlayerInteractListener extends PlayerListener {
        private final EffectDenyPlayerInteract effect;
        public DenyPlayerInteractListener(EffectDenyPlayerInteract effect) {
            this.effect = effect;
        }
        
        private boolean shouldTakeAction(Location loc, Player player) {
            RegionManager rm = effect.getPlugin().getRegionManager();
            for (Location l : rm.getRegionLocations()) {
                if (l.getWorld().getName().equals(loc.getWorld().getName())) {
                    Region r = rm.getRegion(l);
                    RegionType rt = rm.getRegionType(r.getType());
                    if (rt.getRadius() >= Math.sqrt(l.distanceSquared(loc))) {
                        if ((r.isOwner(player.getName()) || r.isMember(player.getName())) || effect.regionHasEffect(rt.getEffects(), "denyplayerinteract") == 0 ||
                                !effect.hasReagents(l))
                            return false;
                        return true;
                    }
                }
            }
            return false;
        }
        
        @Override
        public void onPlayerInteract(PlayerInteractEvent event) {
            if (event.isCancelled() || !shouldTakeAction(event.getClickedBlock().getLocation(), event.getPlayer()))
                return;
            
            event.getPlayer().sendMessage(ChatColor.GRAY + "[HeroStronghold] This region is protected");
            event.setCancelled(true);
        }
        
        @Override
        public void onPlayerBedEnter(PlayerBedEnterEvent event) {
            if (event.isCancelled() || !shouldTakeAction(event.getBed().getLocation(), event.getPlayer()))
                return;
            
            event.getPlayer().sendMessage(ChatColor.GRAY + "[HeroStronghold] This region is protected");
            event.setCancelled(true);
        }
        
        @Override
        public void onPlayerBucketFill(PlayerBucketFillEvent event) {
            if (event.isCancelled() || !shouldTakeAction(event.getBlockClicked().getLocation(), event.getPlayer()))
                return;
            
            event.getPlayer().sendMessage(ChatColor.GRAY + "[HeroStronghold] This region is protected");
            event.setCancelled(true);
        }
        
        @Override
        public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
            if (event.isCancelled() || !shouldTakeAction(event.getBlockClicked().getLocation(), event.getPlayer()))
                return;
            
            event.getPlayer().sendMessage(ChatColor.GRAY + "[HeroStronghold] This region is protected");
            event.setCancelled(true);
        }
    }
    
}
