package main.java.multitallented.plugins.herostronghold.effects;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import multitallented.redcastlemedia.bukkit.herostronghold.HeroStronghold;
import multitallented.redcastlemedia.bukkit.herostronghold.Util;
import multitallented.redcastlemedia.bukkit.herostronghold.effect.Effect;
import multitallented.redcastlemedia.bukkit.herostronghold.events.UpkeepEvent;
import multitallented.redcastlemedia.bukkit.herostronghold.region.Region;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionManager;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionType;
import multitallented.redcastlemedia.bukkit.herostronghold.region.SuperRegion;
import org.bukkit.Location;
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
            ArrayList<Region> deliverTo = new ArrayList<Region>();
            //Check if any regions nearby need items
            for (SuperRegion sr : rm.getContainingSuperRegions(r.getLocation())) {
                for (Region re : rm.getContainedRegions(sr)) {
                    try {
                        if (!re.getOwners().contains(r.getOwners().get(0))) {
                            continue;
                        }
                    } catch (ArrayIndexOutOfBoundsException aioobe) {
                        continue;
                    }
                    deliverTo.add(re);
                }
            }
            for (Region re : deliverTo) {
                try {
                    Chest chest = (Chest) re.getLocation().getBlock().getState();
                    RegionType ret = rm.getRegionType(re.getType());
                    if (!Util.containsItems(ret.getUpkeep(), chest.getInventory())) {
                        //check if warehouse has items
                        
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        }
        
        private ArrayList<Location> processLocationList(List<String> input, World world) {
            ArrayList<Location> tempList = new ArrayList<Location>();
            for (String s : input) {
                String[] splitString = s.split(":");
                if (s.length() != 3) {
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
