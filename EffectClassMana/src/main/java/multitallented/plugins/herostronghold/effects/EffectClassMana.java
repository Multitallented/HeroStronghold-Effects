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
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author Multitallented
 */
public class EffectClassMana extends Effect {
    public final HeroStronghold aPlugin;
    public EffectClassMana(HeroStronghold plugin) {
        super(plugin);
        this.aPlugin = plugin;
        registerEvent(new IntruderListener(this));
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
    }
    
    public class IntruderListener implements Listener {
        private final EffectClassMana effect;
        public IntruderListener(EffectClassMana effect) {
            this.effect = effect;
        }
        
        @EventHandler
        public void onCustomEvent(Event event) {
            if (!(event instanceof PlayerInRegionEvent) || HeroStronghold.heroes == null) {
                return;
            }
            PlayerInRegionEvent pIREvent = (PlayerInRegionEvent) event;
            Player player = pIREvent.getPlayer();
            Hero hero = null;
            Heroes heroes = HeroStronghold.heroes;
            hero = heroes.getHeroManager().getHero(player);
            
            
            Location l = pIREvent.getRegionLocation();
            RegionManager rm = effect.getPlugin().getRegionManager();
            Region r = rm.getRegion(l);
            RegionType rt = rm.getRegionType(r.getType());
            
            //Check if the region has the shoot arrow effect and return arrow velocity
            int addMana = effect.regionHasEffect(rt.getEffects(), "classmana");
            if (addMana == 0) {
                return;
            }
            boolean friendly = false;
            
            //Check if the player owns or is a member of the region
            if (!rt.containsFriendlyClass(hero.getHeroClass().getName())) {
                if (!rt.containsEnemyClass(hero.getHeroClass().getName())) {
                    return;
                } else if (hero.getMana() == 0) {
                    return;
                }
            } else {
                friendly = true;
                if (hero.getMana() == 100) {
                    return;
                }
            }
            
            //Check to see if the HeroStronghold has enough reagents
            if (!effect.hasReagents(l)) {
                return;
            }
            
            //Run upkeep but don't need to know if upkeep occured
            effect.forceUpkeep(l);
            
            //grant the player food
            if (friendly) {
                if (hero.getMana() + addMana > 100) {
                    hero.setMana(100);
                } else {
                    hero.setMana(hero.getMana() + addMana);
                }
            } else {
                if (hero.getMana() - addMana < 0) {
                    hero.setMana(0);
                } else {
                    hero.setMana(hero.getMana() - addMana);
                }
            }
        }
    }
    
}
