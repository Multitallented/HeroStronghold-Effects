package main.java.multitallented.plugins.herostronghold.effects;

import java.util.HashMap;
import java.util.HashSet;
import multitallented.redcastlemedia.bukkit.herostronghold.effect.Effect;
import multitallented.redcastlemedia.bukkit.herostronghold.HeroStronghold;
import multitallented.redcastlemedia.bukkit.herostronghold.region.Region;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 *
 * @author Multitallented
 */
public class EffectDenyMobSpawn extends Effect {
    private HashMap<String, HashSet<String>> denied = new HashMap<String, HashSet<String>>();
    
    public EffectDenyMobSpawn(HeroStronghold plugin) {
        super(plugin);
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
        MobListener mobListener = new MobListener(this, plugin);
    }
    
    public class MobListener implements Listener {
        private final EffectDenyMobSpawn effect;
        private final HeroStronghold plugin;
        public MobListener(EffectDenyMobSpawn effect, HeroStronghold plugin) {
            Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
            this.effect = effect;
            this.plugin = plugin;
        }
        
        @EventHandler
        public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
            if (event.isCancelled()) {
                return;
            }
            for (Region r : plugin.getRegionManager().getContainingRegions(event.getLocation())) {
                if (effect.regionHasEffect(plugin.getRegionManager().getRegionType(r.getType()).getEffects(), "denymobspawnnoreagent") != 0) {
                    event.setCancelled(true);
                    return;
                } else if (effect.regionHasEffect(plugin.getRegionManager().getRegionType(r.getType()).getEffects(), "denymobspawn") != 0 &&
                        effect.hasReagents(r.getLocation())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
    
}
