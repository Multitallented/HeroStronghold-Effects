package main.java.multitallented.plugins.herostronghold.effects;

import main.java.multitallented.plugins.herostronghold.Effect;
import main.java.multitallented.plugins.herostronghold.HeroStronghold;
import main.java.multitallented.plugins.herostronghold.Region;
import main.java.multitallented.plugins.herostronghold.RegionType;
import main.java.multitallented.plugins.herostronghold.UpkeepEvent;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.CreatureType;
import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;

/**
 *
 * @author Multitallented
 */
public class EffectPeriodicFarm extends Effect {
    public EffectPeriodicFarm(HeroStronghold plugin) {
        super(plugin);
        registerEvent(Type.CUSTOM_EVENT, new UpkeepListener(this), Priority.Highest);
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
    }
    
    public class UpkeepListener extends CustomEventListener {
        private final EffectPeriodicFarm effect;
        public UpkeepListener(EffectPeriodicFarm effect) {
            this.effect = effect;
        }
        
        
        @Override
        public void onCustomEvent(Event event) {
            if (!(event instanceof UpkeepEvent))
                return;
            UpkeepEvent uEvent = (UpkeepEvent) event;
            Location l = uEvent.getRegionLocation();
            Region r = getPlugin().getRegionManager().getRegion(uEvent.getRegionLocation());
            if (r == null)
                return;
            RegionType rt = getPlugin().getRegionManager().getRegionType(r.getType()); 
            
            //Check if the region has the periodic farm effect
            int animalType = effect.regionHasEffect(rt.getEffects(), "periodicfarm");
            CreatureType ct = null;
            switch (animalType) {
                case 0:
                   return;
                case 1:
                   ct = CreatureType.COW;
                   break;
                case 2:
                   ct = CreatureType.PIG;
                   break;
                case 3:
                   ct = CreatureType.SHEEP;
                   break;
                case 4:
                   ct = CreatureType.CHICKEN;
                   break;
                case 5:
                   ct = CreatureType.SQUID;
                   break;
                case 6:
                   ct = CreatureType.WOLF;
                   break;
                case 7:
                   ct = CreatureType.SNOWMAN;
                   break;
                case 8:
                   ct = CreatureType.SPIDER;
                   break;
                case 9:
                   ct = CreatureType.ZOMBIE;
                   break;
                case 10:
                   ct = CreatureType.SKELETON;
                   break;
                case 11:
                   ct = CreatureType.MUSHROOM_COW;
                   break;
                case 12:
                   ct = CreatureType.SLIME;
                   break;
                case 13:
                   ct = CreatureType.ENDERMAN;
                   break;
                case 14:
                   ct = CreatureType.MAGMA_CUBE;
                   break;
                case 15:
                   ct = CreatureType.GHAST;
                   break;
                case 16:
                   ct = CreatureType.PIG_ZOMBIE;
                   break;
            }
            
            //Check to see if the HeroStronghold has enough reagents
            if (!effect.hasReagents(l))
                return;
            //Run upkeep but don't need to know if upkeep occured
            effect.forceUpkeep(l);
            
            l.getWorld().spawnCreature(l.getBlock().getRelative(BlockFace.UP).getLocation(), ct);
        }
    }
    
}
