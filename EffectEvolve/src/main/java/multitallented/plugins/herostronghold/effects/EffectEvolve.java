package main.java.multitallented.plugins.herostronghold.effects;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import multitallented.redcastlemedia.bukkit.herostronghold.ConfigManager;
import multitallented.redcastlemedia.bukkit.herostronghold.HeroStronghold;
import multitallented.redcastlemedia.bukkit.herostronghold.effect.Effect;
import multitallented.redcastlemedia.bukkit.herostronghold.events.UpkeepSuccessEvent;
import multitallented.redcastlemedia.bukkit.herostronghold.region.Region;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionManager;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionType;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

/**
 *
 * @author Multitallented
 */
public class EffectEvolve extends Effect {
    private final RegionManager rm;
    private final ConfigManager cm;
    private final HashMap<String, String> evolutions = new HashMap<String, String>();
    private final HashMap<Region, Integer> upkeeps = new HashMap<Region, Integer>();
    private final HashMap<Region, Integer> lastSave = new HashMap<Region, Integer>();
    private HeroStronghold plugin;
    public EffectEvolve(HeroStronghold plugin) {
        super(plugin);
        this.rm = plugin.getRegionManager();
        this.cm = HeroStronghold.getConfigManager();
        registerEvent(new IntruderListener(this));
    }
    
    /**
     * Creates the evolutions.yml config file if it doesn't exist. Reads in
     * settings from evolutions.yml and stores them in a HashMap called evolutions.
     * @param plugin 
     */
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
        this.plugin = plugin;
        
        File config = new File(plugin.getDataFolder(), "evolutions.yml");
        if (!config.exists()) {
            try {
                config.createNewFile();
            } catch (IOException ex) {
                plugin.warning("Could not create evolutions.yml");
                return;
            }
        }
        FileConfiguration conf = new YamlConfiguration();
        try {
            conf.load(config);
            for (String s : conf.getKeys(false)) {
                evolutions.put(s, conf.getString(s));
            }
            
        } catch (Exception e) {
            plugin.warning("Could not load evolutions.yml");
        }
    }
    
    public class IntruderListener implements Listener {
        private final EffectEvolve effect;
        public IntruderListener(EffectEvolve effect) {
            this.effect = effect;
        }
        
        /**
         * If the region has the "evolve" effect, it will track the number
         * of successful upkeeps until the max upkeeps is reached. When the max
         * upkeeps are reached, it will destroy the region, and create a new one.
         * Region evolutions are defined in evolutions.yml and upkeeps are saved
         * once every 10 successful upkeeps to the region file in the data folder.
         * @param event 
         */
        @EventHandler
        public void onCustomEvent(UpkeepSuccessEvent event) {
            if (evolutions.isEmpty()) {
                return;
            }
          
            Location l = event.getRegionLocation();
            Region r = rm.getRegion(l);
            RegionType rt = rm.getRegionType(r.getType());
            ArrayList<String> effects = effect.rm.getRegionType(effect.rm.getRegion(l).getType()).getEffects();

            //Check if the region has the shoot arrow effect and return arrow velocity
            int evolve = effect.regionHasEffect(effects, "evolve");
            if (evolve == 0) {
                return;
            }
            
            //Check if config contains regiontype
            if (!evolutions.containsKey(r.getType())) {
                return;
            }
            
            //Get number of successful upkeeps and increase by one
            if (upkeeps.containsKey(r)) {
                upkeeps.put(r, upkeeps.get(r) + 1);
            } else {
                File regionFolder = new File(plugin.getDataFolder(), "data");
                File regionFile = new File(regionFolder, r.getID() + ".yml");
                if (!regionFile.exists()) {
                    return;
                }
                int successes = -1;
                FileConfiguration rConfig = new YamlConfiguration();
                try {
                    rConfig.load(regionFile);
                    successes = rConfig.getInt("successful-upkeeps", -1);
                } catch (Exception e) {
                    return;
                }
                if (successes < 0) {
                    return;
                }
                upkeeps.put(r, successes + 1);
            }
            
            //Check if upkeeps limit reached
            //If reached, evolve the region
            //If not, check if needs to be saved
            if (upkeeps.get(r) >= evolve) {
                ArrayList<Location> regions = event.getEvent().getRegionsToDestroy();
                regions.add(l);
                event.getEvent().setRegionsToDestroy(regions);
                ArrayList<Region> cRegions = event.getEvent().getRegionsToCreate();
                ArrayList<String> owners = r.getOwners();
                ArrayList<String> members = r.getMembers();
                cRegions.add(new Region(r.getID(), l, evolutions.get(r.getType()), owners, members));
                event.getEvent().setRegionsToCreate(cRegions);
                upkeeps.remove(r);
                lastSave.remove(r);
            } else {
                if (lastSave.containsKey(r)) {
                    lastSave.put(r, lastSave.get(r) + 1);
                    if (lastSave.get(r) > 9) {
                        File regionFolder = new File(plugin.getDataFolder(), "data");
                        File regionFile = new File(regionFolder, r.getID() + ".yml");
                        if (!regionFile.exists()) {
                            return;
                        }
                        FileConfiguration rConfig = new YamlConfiguration();
                        try {
                            rConfig.load(regionFile);
                            rConfig.set("successful-upkeeps", upkeeps.get(r));
                            rConfig.save(regionFile);
                            lastSave.put(r, 0);
                        } catch (Exception e) {
                            return;
                        }
                    }
                } else {
                    lastSave.put(r, 1);
                }
            }
        }
        
        /**
         * This method attempts to save all unsaved successful upkeeps to their
         * region files when the plugin is disabled.
         * @param event 
         */
        @EventHandler
        public void onPluginDisable(PluginDisableEvent event) {
            if (!event.getPlugin().getDescription().getName().equalsIgnoreCase("HeroStronghold")) {
                return;
            }
            System.out.println("[HeroStronghold] Saving all region evolutions...");
            File regionFolder = new File(plugin.getDataFolder(), "data");
            for (Region r : lastSave.keySet()) {
                if (lastSave.get(r) > 0) {
                    //Save the upkeeps
                    File regionFile = new File(regionFolder, r.getID() + ".yml");
                    if (!regionFile.exists()) {
                        continue;
                    }
                    FileConfiguration rConfig = new YamlConfiguration();
                    try {
                        rConfig.load(regionFile);
                        rConfig.set("successful-upkeeps", upkeeps.get(r));
                        lastSave.remove(r);
                        rConfig.save(regionFile);
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
            System.out.println("[HeroStronghold] All region evolutions saved.");
        }
    }
    
}
