package main.java.multitallented.plugins.herostronghold.effects;

import main.java.multitallented.plugins.herostronghold.Effect;
import main.java.multitallented.plugins.herostronghold.HeroStronghold;
import main.java.multitallented.plugins.herostronghold.PlayerInRegionEvent;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
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
public class EffectShootArrow extends Effect {
    public EffectShootArrow(HeroStronghold plugin) {
        super(plugin);
        registerEvent(Type.CUSTOM_EVENT, new IntruderListener(this), Priority.Highest);
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
    }
    
    public class IntruderListener extends CustomEventListener {
        private final EffectShootArrow effect;
        public IntruderListener(EffectShootArrow effect) {
            this.effect = effect;
        }
        
        @Override
        public void onCustomEvent(Event event) {
            if (!(event instanceof PlayerInRegionEvent))
                return;
            PlayerInRegionEvent pIREvent = (PlayerInRegionEvent) event;
            
            //Check if the region has the shoot arrow effect and return arrow velocity
            double speed = effect.regionHasEffect(pIREvent.getEffects(), "shootarrow") / 10;
            if (speed == 0)
                return;
            
            Player player = pIREvent.getPlayer();
            Location l = pIREvent.getRegionLocation();
            
            //Check if the player owns or is a member of the region
            if (effect.isOwnerOfRegion(player, l) || effect.isMemberOfRegion(player, l)) {
                return;
            }
            
            //Check to see if the HeroStronghold has enough reagents
            if (!effect.hasReagents(l))
                return;
            
            //Run upkeep but don't need to know if upkeep occured
            effect.forceUpkeep(l);
            
            //Calculate trajectory of the arrow
            Location loc = l.getBlock().getRelative(BlockFace.UP, 2).getLocation();
            Location playerLoc = player.getLocation();
            Vector vel = new Vector(playerLoc.getX() - loc.getX(), playerLoc.getY() - loc.getY(), playerLoc.getZ() - loc.getZ());
            
            //Spawn and set velocity of the arrow
            Arrow arrow = l.getWorld().spawn(loc, Arrow.class);
            arrow.setVelocity(vel.multiply(speed));
        }
    }
    
}
