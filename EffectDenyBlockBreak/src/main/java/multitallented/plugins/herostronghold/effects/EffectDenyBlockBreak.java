package main.java.multitallented.plugins.herostronghold.effects;

import main.java.multitallented.plugins.herostronghold.Effect;
import main.java.multitallented.plugins.herostronghold.HeroStronghold;
import main.java.multitallented.plugins.herostronghold.Region;
import main.java.multitallented.plugins.herostronghold.RegionManager;
import main.java.multitallented.plugins.herostronghold.RegionType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EndermanPickupEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.painting.PaintingBreakEvent;

/**
 *
 * @author Multitallented
 */
public class EffectDenyBlockBreak extends Effect {
    public EffectDenyBlockBreak(HeroStronghold plugin) {
        super(plugin);
        DenyBuildListener dbListener = new DenyBuildListener();
        registerEvent(Type.BLOCK_BREAK, dbListener, Priority.Highest);
        registerEvent(Type.BLOCK_DAMAGE, dbListener, Priority.High);
        registerEvent(Type.BLOCK_FROMTO, dbListener, Priority.Normal);
        registerEvent(Type.BLOCK_IGNITE, dbListener, Priority.High);
        registerEvent(Type.BLOCK_BURN, dbListener, Priority.High);
        registerEvent(Type.SIGN_CHANGE, dbListener, Priority.High);
        registerEvent(Type.BLOCK_PISTON_EXTEND, dbListener, Priority.High);
        registerEvent(Type.BLOCK_PISTON_RETRACT, dbListener, Priority.High);
        registerEvent(Type.PAINTING_BREAK, new PListener(), Priority.High);
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
    }
    
    private boolean shouldTakeAction(Location loc, Player player, int modifier) {
            RegionManager rm = getPlugin().getRegionManager();
            for (Location l : rm.getRegionLocations()) {
                if (l.getWorld().getName().equals(loc.getWorld().getName())) {
                    Region r = rm.getRegion(l);
                    RegionType rt = rm.getRegionType(r.getType());
                    if (rt.getRadius() + modifier >= Math.sqrt(l.distanceSquared(loc))) {
                        if ((player != null && (r.isOwner(player.getName()) || r.isMember(player.getName()))) || regionHasEffect(rt.getEffects(), "denyblockbuild") == 0 ||
                                !hasReagents(l))
                            return false;
                        return true;
                    }
                }
            }
            return false;
        }
    
    public class DenyBuildListener extends BlockListener {
        
        @Override
        public void onBlockBreak(BlockBreakEvent event) {
            if (event.isCancelled())
                return;
            if (shouldTakeAction(event.getBlock().getLocation(), event.getPlayer(), 0)) {
                event.getPlayer().sendMessage(ChatColor.GRAY + "[HeroStronghold] This region is protected");
                event.setCancelled(true);
                return;
            }
        }
        
        @Override
        public void onBlockDamage(BlockDamageEvent event) {
            if (event.isCancelled() || !event.getBlock().getType().equals(Material.CAKE_BLOCK))
                return;
            if (shouldTakeAction(event.getBlock().getLocation(), event.getPlayer(), 0)) {
                event.getPlayer().sendMessage(ChatColor.GRAY + "[HeroStronghold] This region is protected");
                event.setCancelled(true);
                return;
            }
        }
        
        @Override
        public void onBlockFromTo(BlockFromToEvent event) {
            if (event.isCancelled() || !shouldTakeAction(event.getToBlock().getLocation(), null, 0))
                return;
            
            Block blockFrom = event.getBlock();

            // Check the fluid block (from) whether it is air.
           if (blockFrom.getTypeId() == 0 || blockFrom.getTypeId() == 8 || blockFrom.getTypeId() == 9) {
                event.setCancelled(true);
                return;
            }
            if (blockFrom.getTypeId() == 10 || blockFrom.getTypeId() == 11) {
                event.setCancelled(true);
                return;
            }
        }
        
        @Override
        public void onBlockIgnite(BlockIgniteEvent event) {
            if (event.isCancelled()) {
                return;
            }

            IgniteCause cause = event.getCause();

            boolean isFireSpread = cause == IgniteCause.SPREAD;
            
            if (cause == IgniteCause.LIGHTNING && shouldTakeAction(event.getBlock().getLocation(), null, 0)) {
                event.setCancelled(true);
                return;
            }

            if (cause == IgniteCause.LAVA && shouldTakeAction(event.getBlock().getLocation(), null, 0)) {
                event.setCancelled(true);
                return;
            }

            if (isFireSpread && shouldTakeAction(event.getBlock().getLocation(), null, 0)) {
                event.setCancelled(true);
                return;
            }

            if (cause == IgniteCause.FLINT_AND_STEEL && shouldTakeAction(event.getBlock().getLocation(), event.getPlayer(), 1)) {
                event.setCancelled(true);
                if (event.getPlayer() != null)
                    event.getPlayer().sendMessage(ChatColor.GRAY + "[HeroStronghold] This region is protected");
                return;
            }

        }
        
        @Override
        public void onBlockBurn(BlockBurnEvent event) {
            if (event.isCancelled() && !shouldTakeAction(event.getBlock().getLocation(), null, 0)) {
                return;
            }
            event.setCancelled(true);
        }
        
        @Override
        public void onSignChange(SignChangeEvent event) {
            if (event.isCancelled() || !shouldTakeAction(event.getBlock().getLocation(), event.getPlayer(), 0))
                return;
            event.setCancelled(true);
            if (event.getPlayer() != null)
                event.getPlayer().sendMessage(ChatColor.GRAY + "[HeroStronghold] This region is protected");
        }
        
        @Override
        public void onBlockPistonExtend(BlockPistonExtendEvent event) {
            if (event.isCancelled()) {
                return;
            }
            
            for (Block b : event.getBlocks()) {
                if (shouldTakeAction(b.getLocation(), null, 0)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        @Override
        public void onBlockPistonRetract(BlockPistonRetractEvent event) {
            if (event.isCancelled() || !event.isSticky() || !shouldTakeAction(event.getBlock().getLocation(), null, 0))
                return;
            
            event.setCancelled(true);
        }
    }
    
    public class PListener extends EntityListener {
        @Override
        public void onPaintingBreak(PaintingBreakEvent event) {
            if (event.isCancelled() || !(event instanceof PaintingBreakByEntityEvent))
                return;
            PaintingBreakByEntityEvent pEvent = (PaintingBreakByEntityEvent) event;
            if (!(pEvent.getRemover() instanceof Player))
                return;
            Player player = (Player) pEvent.getRemover();
            if (!shouldTakeAction(event.getPainting().getLocation(), player, 0))
                return;
            
            event.setCancelled(true);
            player.sendMessage(ChatColor.GRAY + "[HeroStronghold] This region is protected");
        }
        
        
        @Override
        public void onEntityExplode(EntityExplodeEvent event) {
            if (event.isCancelled()) {
                return;
            }
            Location l = event.getLocation();
            Entity ent = event.getEntity();
            if ((!(ent instanceof Creeper) && !(ent instanceof EnderDragon) && !(ent instanceof TNTPrimed) && !(ent instanceof Fireball)
                    && !shouldTakeAction(l, null, 4)))
                event.setCancelled(true);
        }
        
        @Override
        public void onExplosionPrime(ExplosionPrimeEvent event) {
            if (event.isCancelled() || !shouldTakeAction(event.getEntity().getLocation(), null, 0))
                return;
            event.setCancelled(true);
        }
        
        @Override
        public void onEndermanPickup(EndermanPickupEvent event) {
            if (event.isCancelled() || !shouldTakeAction(event.getBlock().getLocation(), null, 0))
                return;
            event.setCancelled(true);
        }
        
    }
    
}
