package main.java.multitallented.plugins.herostronghold.effects;

import multitallented.redcastlemedia.bukkit.herostronghold.HeroStronghold;
import multitallented.redcastlemedia.bukkit.herostronghold.effect.Effect;
import multitallented.redcastlemedia.bukkit.herostronghold.events.PlayerInRegionEvent;
import multitallented.redcastlemedia.bukkit.herostronghold.region.Region;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionType;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author Multitallented
 */
public class EffectPeriodicFarm extends Effect {
    public EffectPeriodicFarm(HeroStronghold plugin) {
        super(plugin);
        registerEvent(new PlayerInRegionListener(this));
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
    }
    
    public class PlayerInRegionListener implements Listener {
        private final EffectPeriodicFarm effect;
        public PlayerInRegionListener(EffectPeriodicFarm effect) {
            this.effect = effect;
        }
        
        
        @EventHandler
        public void onCustomEvent(PlayerInRegionEvent event) {
            Location l = event.getLocation();
            Region r = getPlugin().getRegionManager().getRegion(event.getLocation());
            if (r == null) {
                return;
            }
            RegionType rt = getPlugin().getRegionManager().getRegionType(r.getType()); 
            
            //Check if player is member or owner
            Player p = event.getPlayer();
            if (!r.isMember(p.getName()) && !r.isOwner(p.getName())) {
                return;
            }
            
            //Check if the region has the periodic farm effect
            int animalType = effect.regionHasEffect(rt.getEffects(), "periodicfarm");
            CreatureType ct = null;
            switch (animalType) {
                default:
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
                case 17:
                   ct = CreatureType.BLAZE;
                   break;
                case 18:
                   ct = CreatureType.VILLAGER;
                   break;
            }
            if (ct == null) {
                return;
            }
            int radius = (int) Math.sqrt(rt.getRadius());
            int i = 0;
            for (Entity e : event.getPlayer().getNearbyEntities(radius, radius, radius)) {
                if (e instanceof Creature) {
                    i++;
                }
            }
            if (i > 7) {
                return;
            }
            
            //Check to see if the HeroStronghold has enough reagents
            if (!effect.hasReagents(l)) {
                return;
            }
            //Check for upkeep
            if (!effect.upkeep(event)) {
                return;
            }
            l.getWorld().spawn(l.getBlock().getRelative(BlockFace.UP).getLocation(), ct.getEntityClass());
            //l.getWorld().spawnCreature(l.getBlock().getRelative(BlockFace.UP).getLocation(), ct);
        }
    }
    
}
