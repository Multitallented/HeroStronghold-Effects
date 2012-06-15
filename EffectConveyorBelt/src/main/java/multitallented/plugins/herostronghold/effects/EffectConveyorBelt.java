package main.java.multitallented.plugins.herostronghold.effects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import multitallented.redcastlemedia.bukkit.herostronghold.HeroStronghold;
import multitallented.redcastlemedia.bukkit.herostronghold.effect.Effect;
import multitallented.redcastlemedia.bukkit.herostronghold.events.UpkeepEvent;
import multitallented.redcastlemedia.bukkit.herostronghold.region.Region;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionManager;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Multitallented
 */
public class EffectConveyorBelt extends Effect {
    public EffectConveyorBelt(HeroStronghold plugin) {
        super(plugin);
        registerEvent(new UpkeepListener(this, plugin.getRegionManager()));
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
    }
    
    public class UpkeepListener implements Listener {
        private final EffectConveyorBelt effect;
        private final RegionManager rm;
        private HashMap<StorageMinecart, Region> carts = new HashMap<StorageMinecart, Region>();
        private HashMap<Region, Location> cachePoints = new HashMap<Region, Location>();
        public UpkeepListener(EffectConveyorBelt effect, RegionManager rm) {
            this.effect = effect;
            this.rm = rm;
        }
        
        
        @EventHandler
        public void onCustomEvent(UpkeepEvent event) {
            
            
            Location l = event.getRegionLocation();
            
            //Check if has effect conveyor
            Region r = rm.getRegion(l);
            if (r == null) {
                return;
            }
            RegionType rt = rm.getRegionType(r.getType());
            if (rt == null) {
                return;
            }
            int conveyor = effect.regionHasEffect(rt.getEffects(), "conveyor");
            if (conveyor == 0) {
                return;
            }
            
            HashSet<StorageMinecart> removeMe = new HashSet<StorageMinecart>();
            for (StorageMinecart sm : carts.keySet()) {
                if (sm.isDead()) {
                    removeMe.add(sm);
                    continue;
                }
                ArrayList<Region> regions = rm.getContainingBuildRegions(sm.getLocation());
                if (!regions.isEmpty()) {
                    Chest currentChest = null;
                    try {
                        if (regions.get(0).equals(carts.get(sm))) {
                            continue;
                        }
                        currentChest = (Chest) regions.get(0).getLocation().getBlock().getState();
                    } catch (Exception e) {
                        continue;
                    }
                    HashSet<ItemStack> cartInventory = new HashSet<ItemStack>();
                    
                    try {
                        ((Chest) carts.get(sm).getLocation().getBlock().getState()).getInventory().addItem(new ItemStack(Material.STORAGE_MINECART, 1));
                    } catch (Exception e) {

                    }
                    //ignore the cart if the destination is full
                    if (currentChest.getInventory().getSize() >= currentChest.getInventory().getMaxStackSize()) {
                        removeMe.add(sm);
                        continue;
                    }
                    cartInventory.addAll(Arrays.asList(sm.getInventory().getContents()));
                    for (ItemStack is : cartInventory) {
                        try {
                            sm.getInventory().removeItem(is);
                            currentChest.getInventory().addItem(is);
                        } catch (NullPointerException npe) {
                          
                        }
                    }
                    removeMe.add(sm);
                    continue;
                }
            }
            for (StorageMinecart sm : removeMe) {
                carts.remove(sm);
                sm.remove();
            }
            
            
            
            //Check if has reagents
            if (!effect.hasReagents(l)) {
                return;
            }
            
            Chest chest = null;
            try {
                chest = (Chest) l.getBlock().getState();
            } catch (Exception e) {
                return;
            }
            Inventory cInv = chest.getInventory();
            HashSet<ItemStack> iss = new HashSet<ItemStack>();
            
            if (!cInv.contains(Material.STORAGE_MINECART) || !cInv.contains(conveyor)) {
                return;
            }
            
            Location loc = null;
            if (cachePoints.containsKey(r)) {
                loc = cachePoints.get(r);
            } else {
                int radius = rt.getRawBuildRadius();
                int x0 = (int) l.getX();
                int y0 = (int) l.getY();
                int z0 = (int) l.getZ();
                outer: for (int x = x0 - radius; x < x0 + radius; x++) {
                    for (int y = y0 - radius; y < y0 + radius; y++) {
                        for (int z = z0 - radius; z < z0 + radius; z++) {
                            Block b = l.getWorld().getBlockAt(x, y, z);
                            if (b.getType() == Material.POWERED_RAIL) {
                                cachePoints.put(r, b.getRelative(BlockFace.UP).getLocation());
                                break outer;
                            }
                        }
                    }
                }
                if (loc == null) {
                    return;
                }
                loc = cachePoints.get(r);
            }
            
            
            ItemStack tempCart = new ItemStack(Material.STORAGE_MINECART, 1);
            cInv.removeItem(tempCart);
            
            for (ItemStack is : cInv.getContents()) {
                if (is != null) {
                }
                if (is != null && is.getTypeId() == conveyor) {
                    iss.add(is);
                }
            }
            for (ItemStack is : iss) {
                cInv.removeItem(is);
            }
            if (iss.isEmpty()) {
                return;
            }
            
            StorageMinecart cart = loc.getWorld().spawn(loc, StorageMinecart.class);
            
            for (ItemStack is : iss) {
                cart.getInventory().addItem(is);
            }
            
            carts.put(cart, r);
        }
    }
}
