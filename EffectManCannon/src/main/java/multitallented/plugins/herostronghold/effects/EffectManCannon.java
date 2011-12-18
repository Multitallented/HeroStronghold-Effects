package main.java.multitallented.plugins.herostronghold.effects;

import main.java.multitallented.plugins.herostronghold.Effect;
import main.java.multitallented.plugins.herostronghold.HeroStronghold;
import main.java.multitallented.plugins.herostronghold.PlayerInRegionEvent;
import main.java.multitallented.plugins.herostronghold.Region;
import main.java.multitallented.plugins.herostronghold.RegionManager;
import main.java.multitallented.plugins.herostronghold.RegionType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.util.Vector;

/**
 *
 * @author Multitallented
 */
public class EffectManCannon extends Effect {
    public EffectManCannon(HeroStronghold plugin) {
        super(plugin);
        registerEvent(Type.CUSTOM_EVENT, new TeleportListener(this), Priority.Highest);
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
    }
    
    public class TeleportListener extends CustomEventListener {
        private final EffectManCannon effect;
        public TeleportListener(EffectManCannon effect) {
            this.effect = effect;
        }
        
        
        @Override
        public void onCustomEvent(Event event) {
            if (!(event instanceof PlayerInRegionEvent))
                return;
            PlayerInRegionEvent pIREvent = (PlayerInRegionEvent) event;
            Location l = pIREvent.getRegionLocation();
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
            if (!effect.hasReagents(l))
                return;
            
            //Run upkeep but don't need to know if upkeep occured
            effect.forceUpkeep(l);
            
            
            //Launch the player into the air
            Player player = pIREvent.getPlayer();
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
