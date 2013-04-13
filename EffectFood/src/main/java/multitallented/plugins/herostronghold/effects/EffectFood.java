package main.java.multitallented.plugins.herostronghold.effects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import multitallented.redcastlemedia.bukkit.herostronghold.HeroStronghold;
import multitallented.redcastlemedia.bukkit.herostronghold.effect.Effect;
import multitallented.redcastlemedia.bukkit.herostronghold.events.*;
import multitallented.redcastlemedia.bukkit.herostronghold.region.Region;
import multitallented.redcastlemedia.bukkit.herostronghold.region.RegionManager;
import multitallented.redcastlemedia.bukkit.herostronghold.region.SuperRegion;
import multitallented.redcastlemedia.bukkit.herostronghold.region.SuperRegionType;
import net.minecraft.server.MobEffect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author Multitallented
 */
public class EffectFood extends Effect {
    protected HashSet<SuperRegion> unfedRegions;
    protected HashMap<SuperRegion, ArrayList<Region>> fedRegions;
    protected final RegionManager rm;
    private final String EFFECT_NAME = "food";
    private final int EFFECT_ID = 17; //hunger
    private final int EFFECT_DURATION = 600; //ticks
    private final double EFFECT_CHANCE = 0.005;
    
    public EffectFood(HeroStronghold plugin) {
        super(plugin);
        this.rm = plugin.getRegionManager();
        registerEvent(new UpkeepListener(this));
    }
    
    public class UpkeepListener implements Listener {
        private final EffectFood effect;
        public UpkeepListener(EffectFood effect) {
            this.effect = effect;
            loadSuperRegions();
        }
        
        
        @EventHandler
        public void onCustomEvent(TwoSecondEvent event) {
            RegionManager rm = effect.rm;
            for (SuperRegion sr : unfedRegions) {
                for (String s : sr.getOwners()) {
                    Player p = Bukkit.getPlayer(s);
                    if (p == null || Math.random() > EFFECT_CHANCE || !rm.getContainingSuperRegions(p.getLocation()).contains(sr)) {
                        continue;
                    }
                    forceHunger(p);
                }
                for (String s : sr.getMembers().keySet()) {
                    Player p = Bukkit.getPlayer(s);
                    if (p == null || !sr.getMember(s).contains("member") || Math.random() > 0.01 || 
                            !rm.getContainingSuperRegions(p.getLocation()).contains(sr)) {
                        continue;
                    }
                    forceHunger(p);
                }
            }
        }
        
        private void forceHunger(Player p) {
            CraftPlayer cp = (CraftPlayer) p;
            cp.getHandle().addEffect(new MobEffect(EFFECT_ID, EFFECT_DURATION, 3));
            p.sendMessage(ChatColor.GRAY + "[HeroStronghold] There is a shortage of food in this town");
        }
        
        @EventHandler
        public void onSuperRegionCreated(SuperRegionCreatedEvent event) {
            loadSuperRegions();
            /*SuperRegion sr = rm.getSuperRegion(event.getName());
            SuperRegionType srt = rm.getSuperRegionType(sr.getType());
            if (!srt.hasEffect(EFFECT_NAME)) {
                return;
            }
            for (Region r : rm.getContainedRegions(sr)) {
                if (regionHasEffect(r, EFFECT_NAME) != 0) {
                    if (fedRegions.containsKey(sr)) {
                        fedRegions.get(sr).add(r);
                    } else {
                        ArrayList<Region> re = new ArrayList<Region>();
                        re.add(r);
                        fedRegions.put(sr, re);
                    }
                    return;
                }
            }
            unfedRegions.add(sr);*/
        }
        
        @EventHandler
        public void onSuperRegionDestroyed(SuperRegionDestroyedEvent event) {
            loadSuperRegions();
            /*
            SuperRegion sr = event.getSuperRegion();
            if (unfedRegions.contains(sr)) {
                unfedRegions.remove(sr);
            }
            if (fedRegions.containsKey(sr)) {
                fedRegions.remove(sr);
            }*/
        }
        
        @EventHandler
        public void onRegionCreated(RegionCreatedEvent event) {
            loadSuperRegions();
            /*RegionManager rm = getPlugin().getRegionManager();
            Region r = event.getRegion();
            if (r == null || rm.getRegionType(r.getType()) == null) {
                return;
            }
            if (effect.regionHasEffect(r, EFFECT_NAME) == 0) {
                return;
            }
            outer: for (SuperRegion sr : rm.getContainingSuperRegions(r.getLocation())) {
                SuperRegionType srt = rm.getSuperRegionType(sr.getType());
                if (!srt.hasEffect(EFFECT_NAME)) {
                    continue;
                }
                if (fedRegions.containsKey(sr)) {
                    fedRegions.get(sr).add(r);
                } else {
                    ArrayList<Region> re = new ArrayList<Region>();
                    re.add(r);
                    fedRegions.put(sr, re);
                }
                
                if (unfedRegions.contains(sr)) {
                    unfedRegions.remove(sr);
                }
            }*/
            
        }
        
        @EventHandler
        public void onRegionDestroyed(RegionDestroyedEvent event) {
            loadSuperRegions();
            /*Region r = event.getRegion();
            if (effect.regionHasEffect(r, EFFECT_NAME) == 0) {
                return;
            }
            outer: for (SuperRegion sr : rm.getContainingSuperRegions(r.getLocation())) {
                SuperRegionType srt = rm.getSuperRegionType(sr.getType());
                if (!srt.hasEffect(EFFECT_NAME)) {
                    continue;
                }
                if (fedRegions.containsKey(sr)) {
                    ArrayList<Region> re = fedRegions.get(sr);
                    if (re.contains(r)) {
                        re.remove(r);
                        if (re.isEmpty()) {
                            fedRegions.remove(sr);
                            unfedRegions.add(sr);
                        }
                    }
                } else if (!unfedRegions.contains(sr)) {
                    unfedRegions.add(sr);
                }
            }*/
        }
        
        private void loadSuperRegions() {
            unfedRegions = new HashSet<SuperRegion>();
            outer: for (SuperRegion sr : rm.getSortedSuperRegions()) {
                SuperRegionType srt = rm.getSuperRegionType(sr.getType());
                if (!srt.hasEffect(EFFECT_NAME)) {
                    continue;
                }
                boolean fed = false;
                for (Region r : rm.getContainedRegions(sr)) {
                    if (regionHasEffect(r, EFFECT_NAME) != 0) {
                        fed = true;
                        break;
                    }
                }
                if (!fed) {
                    unfedRegions.add(sr);
                }
            }
        }
    }
    
}
