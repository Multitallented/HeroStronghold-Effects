package main.java.multitallented.plugins.herostronghold.effects;

import multitallented.redcastlemedia.bukkit.herostronghold.HeroStronghold;
import multitallented.redcastlemedia.bukkit.herostronghold.effect.Effect;
import multitallented.redcastlemedia.bukkit.herostronghold.events.PlayerInRegionEvent;
import multitallented.redcastlemedia.bukkit.herostronghold.region.Region;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionManager;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

/**
 *
 * @author Multitallented
 */
public class EffectManCannon extends Effect {
    public EffectManCannon(HeroStronghold plugin) {
        super(plugin);
        registerEvent(new TeleportListener(this));
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
    }
    
    public class TeleportListener implements Listener {
        private final EffectManCannon effect;
        public TeleportListener(EffectManCannon effect) {
            this.effect = effect;
        }
        
        
        @EventHandler
        public void onCustomEvent(PlayerInRegionEvent event) {
            Location l = event.getLocation();
            RegionManager rm = getPlugin().getRegionManager();
            Region r = rm.getRegion(l);
            if (r == null)
                return;
            RegionType rt = rm.getRegionType(r.getType());
            
            int jumpMult = effect.regionHasEffect(rt.getEffects(), "mancannon");
            
            //Check if the region is a teleporter
            if (jumpMult == 0)
                return;
            
            //Check to see if the HeroStronghold has enough reagents
            if (!effect.hasReagents(l)) {
                return;
            }
            
            //Run upkeep but don't need to know if upkeep occured
            effect.forceUpkeep(event);
            
            
            //Launch the player into the air
            Player player = event.getPlayer();
            float pitch = player.getEyeLocation().getPitch();
            int jumpForwards = 1;
            if (pitch > 45) {
                jumpForwards = 1;
            }
            if (pitch > 0) {
                pitch = -pitch;
            }
            float multiplier = ((90f + pitch) / 50f);
            Vector v = player.getVelocity().setY(1).add(player.getLocation().getDirection().setY(0).normalize().multiply(multiplier * jumpForwards));
            player.setVelocity(v.multiply(jumpMult));
            player.setFallDistance(-8f * jumpMult);
        }
    }
    
}
