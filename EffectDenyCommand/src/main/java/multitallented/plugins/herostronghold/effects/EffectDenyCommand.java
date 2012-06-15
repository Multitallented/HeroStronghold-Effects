package main.java.multitallented.plugins.herostronghold.effects;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import multitallented.redcastlemedia.bukkit.herostronghold.effect.Effect;
import multitallented.redcastlemedia.bukkit.herostronghold.HeroStronghold;
import multitallented.redcastlemedia.bukkit.herostronghold.region.Region;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 *
 * @author Multitallented
 */
public class EffectDenyCommand extends Effect {
    private HashMap<String, HashSet<String>> denied = new HashMap<String, HashSet<String>>();
    
    public EffectDenyCommand(HeroStronghold plugin) {
        super(plugin);
    }
    
    @Override
    public void init(HeroStronghold plugin) {
        super.init(plugin);
        CommandListenener tpListener = new CommandListenener(this, plugin);
        
        
        File config = new File(plugin.getDataFolder(), "config.yml");
        if (!config.exists()) {
            plugin.warning("config.yml non-existent.");
            return;
        }
        FileConfiguration conf = new YamlConfiguration();
        try {
            conf.load(config);
            ConfigurationSection cs = conf.getConfigurationSection("denycommand");
            if (cs == null) {
                conf.createSection("denycommand");
                List<String> tempArray = new ArrayList<String>();
                tempArray.add("home");
                conf.set("denycommand.blacklist1", tempArray);
                tempArray.clear();
                conf.set("denycommand.whitelist2", tempArray);
                HashSet<String> tempSet = new HashSet<String>();
                tempSet.add("home");
                denied.put("blacklist1", tempSet);
                denied.put("whitelist2", new HashSet<String>());
            } else {
                for (String s : cs.getKeys(false)) {
                    List<String> tempList = conf.getStringList("denycommand." + s);
                    if (tempList != null) {
                        HashSet<String> tempSet = new HashSet<String>();
                        for (String st : tempList) {
                            tempSet.add(st);
                        }
                        denied.put(s, tempSet);
                    }
                }
            }
            conf.save(config);
        } catch (Exception e) {
            plugin.warning("Could not load settings for EffectPort.jar");
        }
    }
    
    public class CommandListenener implements Listener {
        private final EffectDenyCommand effect;
        private final HeroStronghold plugin;
        public CommandListenener(EffectDenyCommand effect, HeroStronghold plugin) {
            Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
            this.effect = effect;
            this.plugin = plugin;
        }
        
        @EventHandler
        public void onCommandEffectEvent(PlayerCommandPreprocessEvent event) {
            if (event.isCancelled()) {
                return;
            }
            String command = event.getMessage().split(" ")[0].replace("/", "");

            Player p = event.getPlayer();
            //Check permission
            if (HeroStronghold.perms == null || HeroStronghold.perms.has(p, "herostronghold.admin")) {
                return;
            }
            
            //Check Location
            for (Region r : plugin.getRegionManager().getContainingRegions(p.getLocation())) {
                if (r.isMember(p.getName()) || r.isOwner(p.getName())) {
                    continue;
                }
                int denyCommand = effect.regionHasEffect(plugin.getRegionManager().getRegionType(r.getType()).getEffects(), "denycommand");
                int denyCommandNoReagent = effect.regionHasEffect(plugin.getRegionManager().getRegionType(r.getType()).getEffects(), "denycommandnoreagent");
                if (denyCommand != 0 && effect.hasReagents(r.getLocation())) {
                    if (denied.containsKey("blacklist" + denyCommand)) {
                        if (denied.get("blacklist" + denyCommand).contains(command)) {
                            p.sendMessage(ChatColor.GRAY + "[HeroStronghold] You can't use that command here.");
                            event.setCancelled(true);
                            return;
                        }
                    } else if (denied.containsKey("whitelist" + denyCommand)) {
                        if (!denied.get("whitelist" + denyCommand).contains(command)) {
                            p.sendMessage(ChatColor.GRAY + "[HeroStronghold] You can't use that command here.");
                            event.setCancelled(true);
                            return;
                        }
                    }
                } else if (denyCommandNoReagent != 0) {
                    if (denied.containsKey("blacklist" + denyCommandNoReagent)) {
                        if (denied.get("blacklist" + denyCommandNoReagent).contains(command)) {
                            p.sendMessage(ChatColor.GRAY + "[HeroStronghold] You can't use that command here.");
                            event.setCancelled(true);
                            return;
                        }
                    } else if (denied.containsKey("whitelist" + denyCommandNoReagent)) {
                        if (!denied.get("whitelist" + denyCommandNoReagent).contains(command)) {
                            p.sendMessage(ChatColor.GRAY + "[HeroStronghold] You can't use that command here.");
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
    }
    
}
