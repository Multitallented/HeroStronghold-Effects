package main.java.multitallented.plugins.herostronghold.effects;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.hero.Hero;
import multitallented.redcastlemedia.bukkit.herostronghold.HeroStronghold;
import multitallented.redcastlemedia.bukkit.herostronghold.effect.Effect;
import multitallented.redcastlemedia.bukkit.herostronghold.events.PlayerInRegionEvent;
import multitallented.redcastlemedia.bukkit.herostronghold.region.Region;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionManager;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

/**
 *
 * @author Multitallented
 */
public class EffectClassHealth extends Effect {
    public final HeroStronghold aPlugin;
    public EffectClassHealth(HeroStronghold plugin) {
        super(plugin);
        this.aPlugin = plugin;
        registerEvent(Type.CUSTOM_EVENT, new IntruderListener(this), Priority.Highest);
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
    }
    
    public class IntruderListener extends CustomEventListener {
        private final EffectClassHealth effect;
        public IntruderListener(EffectClassHealth effect) {
            this.effect = effect;
        }
        
        @Override
        public void onCustomEvent(Event event) {
            if (!(event instanceof PlayerInRegionEvent) || HeroStronghold.heroes == null) {
                return;
            }
            PlayerInRegionEvent pIREvent = (PlayerInRegionEvent) event;
            Heroes heroes = HeroStronghold.heroes;
            Hero hero = heroes.getHeroManager().getHero(pIREvent.getPlayer());
            
            
            Location l = pIREvent.getRegionLocation();
            RegionManager rm = effect.getPlugin().getRegionManager();
            Region r = rm.getRegion(l);
            RegionType rt = rm.getRegionType(r.getType());
            
            //Check if the region has the shoot arrow effect and return arrow velocity
            int addHealth = effect.regionHasEffect(rt.getEffects(), "classhealth");
            if (addHealth == 0) {
                return;
            }
            
            boolean friendly = true;
            //Check if the player owns or is a member of the region
            if (!rt.containsFriendlyClass(hero.getHeroClass().getName())) {
                if (!rt.containsEnemyClass(hero.getHeroClass().getName())) {
                    return;
                } else {
                    friendly = false;
                    if (hero.getHealth() <= 0) {
                        return;
                    }
                }
            } else {
                if (hero.getHealth() >= hero.getMaxHealth()) {
                    return;
                }
            }
            
            //Check to see if the HeroStronghold has enough reagents
            if (!effect.hasReagents(l))
                return;
            
            //Run upkeep but don't need to know if upkeep occured
            effect.forceUpkeep(l);
            
            //grant the player hp
            if (friendly) {
                EntityRegainHealthEvent e = new EntityRegainHealthEvent(hero.getPlayer(), (int) (addHealth + hero.getHealth()), RegainReason.CUSTOM);
                effect.aPlugin.getServer().getPluginManager().callEvent(e);
            } else {
                EntityDamageEvent e = new EntityDamageEvent(hero.getPlayer(), DamageCause.CUSTOM, addHealth);
                effect.aPlugin.getServer().getPluginManager().callEvent(e);
            }
        }
    }
    
}
