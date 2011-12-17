package main.java.multitallented.plugins.herostronghold.effects;

import main.java.multitallented.plugins.herostronghold.Effect;
import main.java.multitallented.plugins.herostronghold.HeroStronghold;
import main.java.multitallented.plugins.herostronghold.PlayerInRegionEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;

/**
 *
 * @author Multitallented
 */
public class EffectGainStamina extends Effect {
    public EffectGainStamina(HeroStronghold plugin) {
        super(plugin);
        registerEvent(Type.CUSTOM_EVENT, new IntruderListener(this), Priority.Highest);
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
    }
    
    public class IntruderListener extends CustomEventListener {
        private final EffectGainStamina effect;
        public IntruderListener(EffectGainStamina effect) {
            this.effect = effect;
        }
        
        @Override
        public void onCustomEvent(Event event) {
            if (!(event instanceof PlayerInRegionEvent))
                return;
            PlayerInRegionEvent pIREvent = (PlayerInRegionEvent) event;
            Player player = pIREvent.getPlayer();
            if (player.getFoodLevel() == 20)
                return;
            //Check if the region has the shoot arrow effect and return arrow velocity
            int food = effect.regionHasEffect(pIREvent.getEffects(), "gainstamina");
            if (food == 0)
                return;
            
            Location l = pIREvent.getRegionLocation();
            
            //Check if the player owns or is a member of the region
            if (!effect.isOwnerOfRegion(player, l) && !effect.isMemberOfRegion(player, l)) {
                return;
            }
            
            //Check to see if the HeroStronghold has enough reagents
            if (!effect.hasReagents(l))
                return;
            
            //Run upkeep but don't need to know if upkeep occured
            effect.forceUpkeep(l);
            
            //grant the player food
            if (player.getFoodLevel() + food <= 20) {
                player.setFoodLevel(player.getFoodLevel() + food);
            } else {
                player.setFoodLevel(20);
            }
        }
    }
    
}
