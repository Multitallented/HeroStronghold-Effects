package main.java.multitallented.plugins.herostronghold.effects;

import multitallented.redcastlemedia.bukkit.herostronghold.HeroStronghold;
import multitallented.redcastlemedia.bukkit.herostronghold.effect.Effect;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionManager;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
/**
 *
 * @author Multitallented
 */
public class EffectWilderness extends Effect {
    private final RegionManager rm;
    public EffectWilderness(HeroStronghold plugin) {
        super(plugin);
        this.rm = plugin.getRegionManager();
        registerEvent(new IntruderListener(this));
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
    }
    
    public class IntruderListener implements Listener {
        private final EffectWilderness effect;
        private final RegionManager rm;
        public IntruderListener(EffectWilderness effect) {
            this.effect = effect;
            this.rm = effect.getPlugin().getRegionManager();
        }
        
        @EventHandler
        public void onBlockBreakEvent(BlockBreakEvent event) {
            if (event.isCancelled()) {
                return;
            }
            if (rm.getContainingRegions(event.getBlock().getLocation()).isEmpty() &&
                    rm.getContainingSuperRegions(event.getBlock().getLocation()).isEmpty()) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.GRAY + "[HeroStronghold] Make a region first. See /skills");
            }
        }
        @EventHandler
        public void onBlockBuildEvent(BlockPlaceEvent event) {
            if (event.isCancelled()) {
                return;
            }
            if (rm.getContainingRegions(event.getBlock().getLocation()).isEmpty() &&
                    rm.getContainingSuperRegions(event.getBlock().getLocation()).isEmpty()) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.GRAY + "[HeroStronghold] Make a region first. See /skills");
            }
        }
        @EventHandler
        public void onBucketEvent(PlayerBucketEmptyEvent event) {
            if (event.isCancelled()) {
                return;
            }
            if (rm.getContainingRegions(event.getBlockClicked().getLocation()).isEmpty() &&
                    rm.getContainingSuperRegions(event.getBlockClicked().getLocation()).isEmpty()) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.GRAY + "[HeroStronghold] Make a region first. See /skills");
            }
        }
        @EventHandler
        public void onBucketEvent(PlayerBucketFillEvent event) {
            if (event.isCancelled()) {
                return;
            }
            if (rm.getContainingRegions(event.getBlockClicked().getLocation()).isEmpty() &&
                    rm.getContainingSuperRegions(event.getBlockClicked().getLocation()).isEmpty()) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.GRAY + "[HeroStronghold] Make a region first. See /skills");
            }
        }
    }
}