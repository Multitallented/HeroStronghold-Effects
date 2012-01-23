package main.java.multitallented.plugins.herostronghold.effects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import multitallented.redcastlemedia.bukkit.herostronghold.HeroStronghold;
import multitallented.redcastlemedia.bukkit.herostronghold.effect.Effect;
import multitallented.redcastlemedia.bukkit.herostronghold.events.UpkeepEvent;
import multitallented.redcastlemedia.bukkit.herostronghold.region.Region;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionManager;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Multitallented
 */
public class EffectConveyorBelt extends Effect {
    public EffectConveyorBelt(HeroStronghold plugin) {
        super(plugin);
        registerEvent(Type.CUSTOM_EVENT, new UpkeepListener(this, plugin.getRegionManager()), Priority.Highest);
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
    }
    
    public class UpkeepListener extends CustomEventListener {
        private final EffectConveyorBelt effect;
        private final RegionManager rm;
        private Map<Location, ConveyorBelt> belts = new HashMap<Location, ConveyorBelt>();
        public UpkeepListener(EffectConveyorBelt effect, RegionManager rm) {
            this.effect = effect;
            this.rm = rm;
        }
        
        
        @Override
        public void onCustomEvent(Event event) {
            if (!(event instanceof UpkeepEvent))
                return;
            UpkeepEvent uEvent = (UpkeepEvent) event;
            Location l = uEvent.getRegionLocation();
            Region r = getPlugin().getRegionManager().getRegion(uEvent.getRegionLocation());
            if (r == null) {
                return;
            }
            RegionType rt = getPlugin().getRegionManager().getRegionType(r.getType()); 
            
            //Check if the region has the shoot arrow effect and return arrow velocity
            int length = effect.regionHasEffect(rt.getEffects(), "conveyorbelt");
            if (length == 0) {
                return;
            }
            
            
            //If not already known, find the start location and end location for the conveyor belt
            if (!belts.containsKey(l)) {
                Block block = l.getBlock().getRelative(BlockFace.UP);
                if (!(block.getState() instanceof Sign)) {
                    return;
                }

                Sign sign = (Sign) block.getState();

                Location loc1 = processStringLocation(l.getWorld(), sign.getLine(0));
                Location loc2 = processStringLocation(l.getWorld(), sign.getLine(1));
                Set<Integer> item1 = processStringItemList(sign.getLine(2)); 
                item1.addAll(processStringItemList(sign.getLine(3)));

                if (loc1 == null || item1 == null || loc2 == null) {
                    return;
                }

                if (Math.sqrt(loc1.distanceSquared(l)) > length) {
                    return;
                }
                
                Region r1 = null;
                
                double x1 = loc1.getX();
                for (Region re : rm.getSortedRegions()) {
                    int radius = rm.getRegionType(re.getType()).getRadius();
                    Location lo = re.getLocation();
                    if (lo.getX() + radius < x1) {
                        break;
                    }
                    try {
                        if (!(lo.getX() - radius > x1) && lo.distanceSquared(loc1) < radius) {
                            r1 = re;
                            break;
                        }
                    } catch (IllegalArgumentException iae) {

                    }
                }
                
                Region r2 = null;
                
                double x2 = loc2.getX();
                for (Region re : rm.getSortedRegions()) {
                    int radius = rm.getRegionType(re.getType()).getRadius();
                    Location lo = re.getLocation();
                    if (lo.getX() + radius < x2) {
                        break;
                    }
                    try {
                        if (!(lo.getX() - radius > x2) && lo.distanceSquared(loc2) < radius) {
                            r2 = re;
                            break;
                        }
                    } catch (IllegalArgumentException iae) {

                    }
                }

                if (r1 == null) {
                    sign.setLine(0, "invalid");
                    return;
                }
                if (r2 == null) {
                    sign.setLine(1, "invalid");
                    return;
                }
                
                RegionType currentRegionType = rm.getRegionType(r1.getType());

                int radius = (int) Math.sqrt(currentRegionType.getRadius());

                int lowerLeftX = (int) loc1.getX() - radius;
                int lowerLeftY = (int) loc1.getY() - radius;
                lowerLeftY = lowerLeftY < 0 ? 0 : lowerLeftY;
                int lowerLeftZ = (int) loc1.getZ() - radius;

                int upperRightX = (int) loc1.getX() + radius;
                int upperRightY = (int) loc1.getY() + radius;
                upperRightY = upperRightY > 128 ? 128 : upperRightY;
                int upperRightZ = (int) loc1.getZ() + radius;
                
                World world = loc1.getWorld();
                
                Location startPoint = null;
                outer: for (int x=lowerLeftX; x<upperRightX; x++) {
                    
                    for (int z=lowerLeftZ; z<upperRightZ; z++) {
                        
                        for (int y=lowerLeftY; y<upperRightY; y++) {
                            
                            Block currentBlock = world.getBlockAt(x, y, z);
                            int type = currentBlock.getTypeId();
                            if (type == 27) {
                                startPoint = currentBlock.getLocation();
                            }
                        }
                        
                    }
                    
                }
                if (startPoint == null) {
                    return;
                }
                belts.put(l, new ConveyorBelt(r1.getLocation(), startPoint, r2.getLocation(), new HashMap<StorageMinecart, Location>(), item1));
            }
            
            ConveyorBelt belt = belts.get(l);
            Location startPoint = belt.getStartPoint();
            Set<StorageMinecart> carts = belt.getCarts().keySet();
            boolean cartNearBy = false;
            if (!carts.isEmpty()) {
                for (StorageMinecart sm : carts) {
                    try {
                        if (sm.getLocation().distanceSquared(startPoint) < 16) {
                            cartNearBy = true;
                            break;
                        }
                    } catch (IllegalArgumentException iae) {
                        
                    }
                }
            }
            if (!cartNearBy && effect.hasReagents(l)) {
                StorageMinecart sm = startPoint.getWorld().spawn(startPoint, StorageMinecart.class);
                BlockState bs = belt.getStartCenter().getBlock().getState();
                ArrayList<Integer> removedItems = new ArrayList<Integer>();
                if (bs instanceof Chest) {
                    Chest chest = (Chest) bs;
                    Inventory inv = chest.getInventory();
                    Inventory smInv = sm.getInventory();
                    int i =0;
                    for (ItemStack is : inv.getContents()) {
                        if (is != null && belt.getItems().contains(is.getTypeId())) {
                            for (ItemStack is1 : smInv.getContents()) {
                                if (is1 == null) {
                                    is1 = is.clone();
                                    removedItems.add(i);
                                    i++;
                                    break;
                                } else if (is1.getTypeId() == is.getTypeId() && is1.getAmount() < is.getMaxStackSize()) {
                                    int space = is.getMaxStackSize() - is1.getAmount();
                                    if (space >= is.getAmount()) {
                                        is1.setAmount(is.getAmount() + is1.getAmount());
                                        removedItems.add(i);
                                        i++;
                                        break;
                                    } else if (space > 0) {
                                        is1.setAmount(is1.getAmount() + space);
                                        is.setAmount(is.getAmount() - space);
                                    }
                                }
                            }
                        }
                    }
                    for (Integer j : removedItems) {
                        inv.remove(j);
                    }
                }
                if (removedItems.isEmpty()) {
                    sm.remove();
                } else {
                    belt.addCart(sm, belt.getEndCenter());
                    effect.forceUpkeep(l);
                }
            }
            
            for (StorageMinecart sm : belt.getCarts().keySet()) {
                try {
                    if (sm.getLocation().distanceSquared(l) < rm.getRegionType(rm.getRegion(l).getType()).getRadius()) {
                        BlockState bs = belt.getEndCenter().getBlock().getState();
                            Set<ItemStack> remainingItems = new HashSet<ItemStack>();
                        if (bs instanceof Chest) {
                            Chest chest = (Chest) bs;
                            Inventory inv = chest.getInventory();
                            Inventory smInv = sm.getInventory();
                            int i =0;
                            ArrayList<Integer> removedItems = new ArrayList<Integer>();
                            for (ItemStack is : smInv.getContents()) {
                                if (is != null && belt.getItems().contains(is.getTypeId())) {
                                    for (ItemStack is1 : inv.getContents()) {
                                        if (is1 == null) {
                                            is1 = is.clone();
                                            removedItems.add(i);
                                            i++;
                                            break;
                                        } else if (is1.getTypeId() == is.getTypeId() && is1.getAmount() < is.getMaxStackSize()) {
                                            int space = is.getMaxStackSize() - is1.getAmount();
                                            if (space >= is.getAmount()) {
                                                is1.setAmount(is.getAmount() + is1.getAmount());
                                                removedItems.add(i);
                                                i++;
                                                break;
                                            } else if (space > 0) {
                                                is1.setAmount(is1.getAmount() + space);
                                                is.setAmount(is.getAmount() - space);
                                            }
                                        }
                                    }
                                }
                            }
                            for (Integer j : removedItems) {
                                smInv.remove(j);
                            }
                            for (ItemStack is : smInv.getContents()) {
                                if (is != null) {
                                    remainingItems.add(is);
                                }
                            }
                        }
                        if (remainingItems.isEmpty()) {
                            sm.remove();
                            belt.removeCart(sm);
                        }
                    }
                } catch (IllegalArgumentException iae) {
                    
                }
            }
        }
        
        private Location processStringLocation(World world, String input) {
            String[] args = input.split(",");
            if (args.length < 3) {
                return null;
            }
            try {
                return new Location(world, Integer.parseInt(args[0]), Integer.parseInt(args[0]),Integer.parseInt(args[0]));
            } catch (Exception e) {
                return null;
            }
        }
        private Set<Integer> processStringItemList(String input) {
            String[] args = input.split(",");
            Set<Integer> tempSet = new HashSet<Integer>();
            for (String s : args) {
                if (s.equals("")) {
                    continue;
                }
                try {
                    tempSet.add(Integer.parseInt(s));
                } catch (Exception e) {
                    return null;
                }
            }
            return tempSet;
        }
    }
    public class ConveyorBelt {
        private final Location startCenter;
        private final Location startPoint;
        private final Location endCenter;
        private final Map<StorageMinecart, Location> carts;
        private final Set<Integer> items;
        
        public ConveyorBelt(Location startCenter, Location startPoint, Location endCenter, Map<StorageMinecart, Location> carts, Set<Integer> items) {
            this.startCenter = startCenter;
            this.startPoint = startPoint;
            this.endCenter = endCenter;
            this.carts = carts;
            this.items = items;
        }
        
        public Set<Integer> getItems() {
            return items;
        }
        
        public void removeCart(StorageMinecart cart) {
            carts.remove(cart);
        }
        
        public void addCart(StorageMinecart cart, Location l) {
            carts.put(cart, l);
        }
        
        public Location getStartCenter() {
            return startCenter;
        }
        
        public Location getStartPoint() {
            return startPoint;
        }
        
        public Location getEndCenter() {
            return endCenter;
        }
        
        public Map<StorageMinecart, Location> getCarts() {
            return carts;
        }
    }
    
}
