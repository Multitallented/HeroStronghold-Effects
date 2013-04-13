package main.java.multitallented.plugins.herostronghold.effects;

import java.io.File;
import java.util.*;
import multitallented.redcastlemedia.bukkit.herostronghold.HeroStronghold;
import multitallented.redcastlemedia.bukkit.herostronghold.effect.Effect;
import multitallented.redcastlemedia.bukkit.herostronghold.events.RegionCreatedEvent;
import multitallented.redcastlemedia.bukkit.herostronghold.events.UpkeepEvent;
import multitallented.redcastlemedia.bukkit.herostronghold.region.Region;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionManager;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionType;
import multitallented.redcastlemedia.bukkit.herostronghold.region.SuperRegion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Multitallented
 */
public class EffectWarehouse extends Effect {
    public EffectWarehouse(HeroStronghold plugin) {
        super(plugin);
        registerEvent(new UpkeepListener(this));
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
        
    }
    
    public class UpkeepListener implements Listener {
        private final EffectWarehouse effect;
        public HashMap<Region, ArrayList<Location>> invs = new HashMap<Region, ArrayList<Location>>();
        public UpkeepListener(EffectWarehouse effect) {
            this.effect = effect;
        }
        
        
        @EventHandler
        public void onCustomEvent(UpkeepEvent event) {
            Location l = event.getLocation();
            RegionManager rm = getPlugin().getRegionManager();
            Region r = rm.getRegion(l);
            if (r == null) {
                return;
            }
            RegionType rt = rm.getRegionType(r.getType());
            
            int warehouse = effect.regionHasEffect(rt.getEffects(), "warehouse");
            
            //Check if the region is a teleporter
            if (warehouse == 0) {
                return;
            }
            
            //Check to see if the HeroStronghold has enough reagents
            if (!effect.hasReagents(l)) {
                return;
            }
            
            ArrayList<Chest> availableItems = new ArrayList<Chest>();
            
            //Check for excess chests
            if (!invs.containsKey(r)) {
                File dataFolder = new File(getPlugin().getDataFolder(), "data");
                if (!dataFolder.exists()) {
                    return;
                }
                File dataFile = new File(dataFolder, r.getID() + ".yml");
                if (!dataFile.exists()) {
                    return;
                }
                FileConfiguration config = new YamlConfiguration();
                try {
                    config.load(dataFile);
                    ArrayList<Location> tempLocations = processLocationList(config.getStringList("chests"), event.getLocation().getWorld());
                    for (Location lo : tempLocations) {
                        try {
                            Chest chest = (Chest) lo.getBlock().getState();
                            availableItems.add(chest);
                        } catch (Exception ex) {
                            continue;
                        }
                    }
                    invs.put(r, tempLocations);
                } catch (Exception e) {
                    System.out.println("failed to load dataFile");
                    e.printStackTrace();
                    return;
                }
            } else {
                for (Location lo : invs.get(r)) {
                    try {
                        availableItems.add((Chest) lo.getBlock().getState());
                    } catch (Exception e) {
                        
                    }
                }
            }
            
            //Move items from main chest to aux chests if possible
            Chest tempChest = null;
            HashSet<ItemStack> removeLater = new HashSet<ItemStack>();
            try {
                tempChest = (Chest) l.getBlock().getState();
                for (ItemStack is : tempChest.getInventory().getContents()) {
                    if (is != null) {
                        for (Location lo : invs.get(r)) {
                            try {
                                Chest aChest = (Chest) lo.getBlock().getState();
                                if (aChest.getInventory().firstEmpty() > -1) {
                                    removeLater.add(is);
                                    aChest.getInventory().addItem(is);
                                    break;
                                }
                            } catch (Exception e) {
                                
                            }
                        }
                    }
                }
            } catch (Exception e) {
                
            }
            for (ItemStack is : removeLater) {
                tempChest.getInventory().removeItem(is);
            }
            
            ArrayList<Region> deliverTo = new ArrayList<Region>();
            //Check if any regions nearby need items
            for (SuperRegion sr : rm.getContainingSuperRegions(r.getLocation())) {
                for (Region re : rm.getContainedRegions(sr)) {
                    if (re.equals(r) || effect.hasReagents(re.getLocation())) {
                        continue;
                    }
                    boolean continueT = true;
                    for (String owner : r.getOwners()) {
                        if (re.getOwners().contains(owner)) {
                            continueT = false;
                            break;
                        }
                    }
                    if (continueT) {
                        continue;
                    }
                    /*if (!re.getOwners().contains(r.getOwners().get(0))) {
                        continue;
                    */
                    deliverTo.add(re);
                }
            }
            HashMap<Chest, Material> sendItems = new HashMap<Chest, Material>();
            for (Region re : deliverTo) {
                try {
                    Chest chest = (Chest) re.getLocation().getBlock().getState();
                    if (chest.getInventory().firstEmpty() < 0) {
                        continue;
                    }
                    RegionType ret = rm.getRegionType(re.getType());
                    outer: for (ItemStack is : ret.getReagents()) {
                        if (is == null) {
                            continue;
                        }
                        int amount = is.getAmount();
                        for (ItemStack iss : chest.getInventory().getContents()) {
                            if (iss == null) {
                                continue;
                            }
                            if (iss.getType() == is.getType()) {
                                amount -= iss.getAmount();
                                if (amount < 1) {
                                    continue outer;
                                }
                            }
                        }
                        sendItems.put(chest, is.getType());
                        break;
                    }
                    
                    
                } catch (Exception e) {
                    continue;
                }
            }
            if (sendItems.isEmpty()) {
                return;
            }
            
            for (Chest chest : availableItems) {
                ArrayList<Integer> removeItems = new ArrayList<Integer>();
                for (int i = 0; i< chest.getInventory().getContents().length; i++) {
                    ItemStack is = chest.getInventory().getItem(i);
                    try {
                        if (sendItems.containsValue(is.getType())) {
                            Chest toChest = null;
                            for (Iterator<Chest> itr = sendItems.keySet().iterator(); itr.hasNext();) {
                                toChest = itr.next();
                                if (sendItems.get(toChest) == is.getType()) {
                                    toChest.getInventory().addItem(is);
                                    toChest.update();
                                    removeItems.add(i);
                                    break;
                                }
                            }
                            sendItems.remove(toChest);
                        }
                    } catch (NullPointerException npe) {
                        
                    }
                }
                for (Integer i : removeItems) {
                    chest.getInventory().clear(i);
                }
                chest.update();
            }
        }
        
        @EventHandler
        public void onRegionCreated(RegionCreatedEvent event) {
            RegionManager rm = getPlugin().getRegionManager();
            Region r = event.getRegion();
            RegionType rt = rm.getRegionType(r.getType());
            if (effect.regionHasEffect(rt.getEffects(), "warehouse") == 0) {
                return;
            }
            recordAllChests(r.getLocation());
        }
        
        private void recordAllChests(Location l) {
            RegionManager rm = getPlugin().getRegionManager();
            Region r = rm.getRegion(l);
            RegionType rt = rm.getRegionType(r.getType());
            ArrayList<Location> chests = new ArrayList<Location>();
            int lowerX = (int) r.getLocation().getX() - rt.getRawBuildRadius();
            int upperX = (int) r.getLocation().getX() + rt.getRawBuildRadius();
            int lowerY = (int) r.getLocation().getY() - rt.getRawBuildRadius();
            lowerY = lowerY < 0 ? 0 : lowerY;
            int upperY = (int) r.getLocation().getY() + rt.getRawBuildRadius();
            upperY = upperY > 255 ? 255 : upperY;
            int lowerZ = (int) r.getLocation().getZ() - rt.getRawBuildRadius();
            int upperZ = (int) r.getLocation().getZ() + rt.getRawBuildRadius();
            
            for (int x = lowerX; x < upperX; x++) {
                for (int y = lowerY; y < upperY; y++) {
                    for (int z = lowerZ; z < upperZ; z++) {
                        try {
                            Chest chest = (Chest) l.getWorld().getBlockAt(x, y, z).getState();
                            chests.add(chest.getLocation());
                        } catch (Exception e) {
                            
                        }
                    }
                }
            }
            
            invs.put(r, chests);
            File dataFolder = new File(getPlugin().getDataFolder(), "data");
            if (!dataFolder.exists()) {
                getPlugin().warning("Unable to save data/" + r.getID() + ".yml");
                return;
            }
            File dataFile = new File(dataFolder, r.getID() + ".yml");
            if (!dataFile.exists()) {
                getPlugin().warning("Unable to write to data/" + r.getID() + ".yml");
                return;
            }
            FileConfiguration config = new YamlConfiguration();
            try {
                config.load(dataFile);
                ArrayList<String> preparedList = new ArrayList<String>();
                for (Location lo : chests) {
                    preparedList.add(lo.getX() + ":" + lo.getY() + ":" + lo.getZ());
                }
                config.set("chests", preparedList);
                config.save(dataFile);
            } catch (Exception e) {
            }
        }
        
        private ArrayList<Location> processLocationList(List<String> input, World world) {
            ArrayList<Location> tempList = new ArrayList<Location>();
            for (String s : input) {
                String[] splitString = s.split(":");
                if (splitString.length != 3) {
                    continue;
                }
                tempList.add(new Location(world, Double.parseDouble(splitString[0]),
                        Double.parseDouble(splitString[1]),
                        Double.parseDouble(splitString[2])));
            }
            return tempList;
        }
    }
    
}
