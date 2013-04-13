package main.java.Sevaron.plugins.herostronghold.effects;

import java.util.Iterator;
import multitallented.redcastlemedia.bukkit.herostronghold.HeroStronghold;
import multitallented.redcastlemedia.bukkit.herostronghold.effect.Effect;
import multitallented.redcastlemedia.bukkit.herostronghold.region.*;
import org.bukkit.Location;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

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
      if (event.isCancelled() || !(event.getEntity() instanceof Monster) || event.getSpawnReason().equals(SpawnReason.CUSTOM)) {
          return;
     }

    Location l = event.getLocation();
    RegionManager rm = this.getPlugin().getRegionManager();
    if (rm.shouldTakeAction(l, null, 0, "denymobspawn", true) ||
            rm.shouldTakeAction(l, null, 0, "denymobspawnnoreagent", false)) {
        event.setCancelled(true);
    }
  }

}