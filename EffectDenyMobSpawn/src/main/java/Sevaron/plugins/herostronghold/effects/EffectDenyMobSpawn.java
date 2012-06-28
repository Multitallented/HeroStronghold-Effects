package main.java.Sevaron.plugins.herostronghold.effects;

import multitallented.redcastlemedia.bukkit.herostronghold.HeroStronghold;
import multitallented.redcastlemedia.bukkit.herostronghold.effect.Effect;
import multitallented.redcastlemedia.bukkit.herostronghold.region.Region;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionManager;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionType;
import org.bukkit.Location;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 *
 * @author Sevaron
 */
public class EffectDenyMobSpawn extends Effect implements Listener {
    public final HeroStronghold aPlugin;
  public EffectDenyMobSpawn(HeroStronghold plugin)
  {
    super(plugin);
    this.aPlugin = plugin;
    this.registerEvent(this);
  }
  @Override
  public void init(HeroStronghold plugin){
      super.init(plugin);
  }
  @EventHandler
  public void onCustomEvent(CreatureSpawnEvent event) {
      if (event.isCancelled() || !(event.getEntity() instanceof Monster)) {
          return;
     }

    Location l = event.getLocation();
    RegionManager rm = this.getPlugin().getRegionManager();

    for (Region r : rm.getContainingRegions(l)) {
        RegionType rt = rm.getRegionType(r.getType());
        if (regionHasEffect(rt.getEffects(), "denymobspawn") != 0 && hasReagents(r.getLocation())) {
            event.setCancelled(true);
            return;
        } else if (regionHasEffect(rt.getEffects(), "denymobspawnnoreagent") != 0) {
            event.setCancelled(true);
            return;
        }
    }
  }

}