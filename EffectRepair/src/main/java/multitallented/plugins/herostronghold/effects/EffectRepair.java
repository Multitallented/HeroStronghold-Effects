package main.java.multitallented.plugins.herostronghold.effects;

import multitallented.redcastlemedia.bukkit.herostronghold.HeroStronghold;
import multitallented.redcastlemedia.bukkit.herostronghold.effect.Effect;
import multitallented.redcastlemedia.bukkit.herostronghold.region.Region;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class EffectRepair extends Effect
{
  public final HeroStronghold aPlugin;

  public EffectRepair(HeroStronghold plugin)
  {
    super(plugin);
    this.aPlugin = plugin;
    registerEvent(new RepairListener(this));
  }

    @Override
  public void init(HeroStronghold plugin)
  {
    super.init(plugin);
  }
  public class RepairListener implements Listener {
    private final EffectRepair effect;

    public RepairListener(EffectRepair effect) {
      this.effect = effect;
    }

    private Material getRequiredReagent(Material material) {
      switch (material) {
      case LEATHER_CHESTPLATE:
      case LEATHER_HELMET:
      case LEATHER_LEGGINGS:
      case LEAVES:
      case NETHERRACK:
        return Material.WOOD;
      case LEVER:
      case LOCKED_CHEST:
      case LOG:
      case LONG_GRASS:
      case NETHER_BRICK:
        return Material.COBBLESTONE;
      case IRON_PICKAXE:
      case IRON_SPADE:
      case IRON_SWORD:
      case JACK_O_LANTERN:
      case LEATHER_BOOTS:
      case NETHER_BRICK_STAIRS:
      case POTION:
      case POWERED_MINECART:
      case POWERED_RAIL:
      case PUMPKIN:
      case STONE_HOE:
        return Material.IRON_INGOT;
      case MINECART:
      case MOB_SPAWNER:
      case MONSTER_EGG:
      case MONSTER_EGGS:
      case NETHER_STALK:
      case RAW_CHICKEN:
      case RAW_FISH:
      case RECORD_10:
      case RECORD_11:
        return Material.GOLD_INGOT;
      case MAGMA_CREAM:
      case MAP:
      case MELON:
      case MELON_BLOCK:
      case NETHER_FENCE:
      case PUMPKIN_SEEDS:
      case PUMPKIN_STEM:
      case RAILS:
      case RAW_BEEF:
        return Material.DIAMOND;
      case PAINTING:
      case PAPER:
      case PISTON_BASE:
      case PISTON_EXTENSION:
        return Material.LEATHER;
      case LADDER:
      case SNOW_BLOCK:
        return Material.STRING;
      case JUKEBOX:
      case LAPIS_BLOCK:
      case LAPIS_ORE:
      case LAVA:
      case LAVA_BUCKET:
      case LEATHER:
      case MELON_SEEDS:
      case MELON_STEM:
      case MILK_BUCKET:
      case MOSSY_COBBLESTONE:
      case MUSHROOM_SOUP:
      case MYCEL:
      case NETHER_WARTS:
      case NOTE_BLOCK:
      case OBSIDIAN:
      case PISTON_MOVING_PIECE:
      case PISTON_STICKY_BASE:
      case PORK:
      case PORTAL:
      case RECORD_3:
      case RECORD_4:
      case RECORD_5:
      case RECORD_6:
      case RECORD_7:
      case RECORD_8:
      case RECORD_9:
      case REDSTONE:
      case REDSTONE_ORE:
      case REDSTONE_TORCH_OFF:
      case REDSTONE_TORCH_ON:
      case REDSTONE_WIRE:
      case RED_MUSHROOM:
      case RED_ROSE:
      case ROTTEN_FLESH:
      case SADDLE:
      case SAND:
      case SANDSTONE:
      case SAPLING:
      case SEEDS:
      case SHEARS:
      case SIGN:
      case SIGN_POST:
      case SLIME_BALL:
      case SMOOTH_BRICK:
      case SMOOTH_STAIRS:
      case SNOW:
      case SNOW_BALL:
      case SOIL:
      case SOUL_SAND:
      case SPECKLED_MELON:
      case SPIDER_EYE:
      case SPONGE:
      case STATIONARY_LAVA:
      case STATIONARY_WATER:
      case STEP:
      case STICK:
      case STONE:
      case STONE_AXE:
      case STONE_BUTTON: } return null;
    }

    private int getRepairCost(ItemStack is)
    {
      Material mat = is.getType();
      int amt = 1;
      switch (mat) {
      case LADDER:
      case SNOW_BLOCK:
        amt = (int)(is.getDurability() / mat.getMaxDurability() * 2.0D);
        return amt < 1 ? 1 : amt;
      case PISTON_EXTENSION:
      case PUMPKIN:
      case RAW_BEEF:
      case RECORD_11:
        amt = (int)(is.getDurability() / mat.getMaxDurability() * 3.0D);
        return amt < 1 ? 1 : amt;
      case PAINTING:
      case POTION:
      case PUMPKIN_SEEDS:
      case RAW_CHICKEN:
        amt = (int)(is.getDurability() / mat.getMaxDurability() * 4.0D);
        return amt < 1 ? 1 : amt;
      case PAPER:
      case POWERED_MINECART:
      case PUMPKIN_STEM:
      case RAW_FISH:
        amt = (int)(is.getDurability() / mat.getMaxDurability() * 7.0D);
        return amt < 1 ? 1 : amt;
      case PISTON_BASE:
      case POWERED_RAIL:
      case RAILS:
      case RECORD_10:
        amt = (int)(is.getDurability() / mat.getMaxDurability() * 6.0D);
        return amt < 1 ? 1 : amt;
      case LEATHER_BOOTS:
      case LEATHER_CHESTPLATE:
      case LEVER:
      case MAGMA_CREAM:
      case MINECART:
        amt = (int)(is.getDurability() / mat.getMaxDurability() * 2.0D);
        return amt < 1 ? 1 : amt;
      case IRON_SPADE:
      case LEATHER_LEGGINGS:
      case LOG:
      case MELON:
      case MONSTER_EGG:
        amt = (int)(is.getDurability() / mat.getMaxDurability() * 2.0D);
        return amt < 1 ? 1 : amt;
      case NETHERRACK:
      case NETHER_BRICK:
      case NETHER_BRICK_STAIRS:
      case NETHER_FENCE:
      case NETHER_STALK:
        amt = (int)(is.getDurability() / mat.getMaxDurability() * 2.0D);
        return amt < 1 ? 1 : amt;
      case IRON_PICKAXE:
      case LEATHER_HELMET:
      case LOCKED_CHEST:
      case MAP:
      case MOB_SPAWNER:
        amt = (int)(is.getDurability() / mat.getMaxDurability() * 1.0D);
        return amt < 1 ? 1 : amt;
      case IRON_SWORD:
      case LEAVES:
      case LONG_GRASS:
      case MELON_BLOCK:
      case MONSTER_EGGS:
        amt = (int)(is.getDurability() / mat.getMaxDurability() * 2.0D);
        return amt < 1 ? 1 : amt;
      case JACK_O_LANTERN:
      case JUKEBOX:
      case LAPIS_BLOCK:
      case LAPIS_ORE:
      case LAVA:
      case LAVA_BUCKET:
      case LEATHER:
      case MELON_SEEDS:
      case MELON_STEM:
      case MILK_BUCKET:
      case MOSSY_COBBLESTONE:
      case MUSHROOM_SOUP:
      case MYCEL:
      case NETHER_WARTS:
      case NOTE_BLOCK:
      case OBSIDIAN:
      case PISTON_MOVING_PIECE:
      case PISTON_STICKY_BASE:
      case PORK:
      case PORTAL:
      case RECORD_3:
      case RECORD_4:
      case RECORD_5:
      case RECORD_6:
      case RECORD_7:
      case RECORD_8:
      case RECORD_9:
      case REDSTONE:
      case REDSTONE_ORE:
      case REDSTONE_TORCH_OFF:
      case REDSTONE_TORCH_ON:
      case REDSTONE_WIRE:
      case RED_MUSHROOM:
      case RED_ROSE:
      case ROTTEN_FLESH:
      case SADDLE:
      case SAND:
      case SANDSTONE:
      case SAPLING:
      case SEEDS:
      case SHEARS:
      case SIGN:
      case SIGN_POST:
      case SLIME_BALL:
      case SMOOTH_BRICK:
      case SMOOTH_STAIRS:
      case SNOW:
      case SNOW_BALL: } return 0;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
      if ((event.isCancelled()) || (!event.getClickedBlock().getType().equals(Material.IRON_BLOCK))) {
        return;
      }

      Region r = this.effect.getContainingRegion(event.getClickedBlock().getLocation());
      if (r == null) {
        return;
      }

      if (this.effect.regionHasEffect(this.effect.aPlugin.getRegionManager().getRegionType(r.getType()).getEffects(), "repair") == 0) {
        return;
      }

      Player player = event.getPlayer();

      if ((!r.isMember(player.getName())) && (!r.isOwner(player.getName()))) {
        return;
      }

      ItemStack is = player.getItemInHand();
      Material reagent = getRequiredReagent(is.getType());
      if (is == null) {
        player.sendMessage(ChatColor.GRAY + "[HeroStronghold] You must hold the item you wish to repair.");
        return;
      }
      int repairCost = getRepairCost(is);
      if (repairCost == 0) {
        player.sendMessage(ChatColor.GRAY + "[HeroStronghold] That item isn't something you can repair here.");
        event.setCancelled(true);
        return;
      }
      ItemStack cost = new ItemStack(reagent, repairCost);
      if (!hasReagentCost(player, cost)) {
        player.sendMessage(ChatColor.GRAY + "[HeroStronghold] You don't have enough " + reagent.name().toLowerCase().replace("_", " "));
        return;
      }

      String isName = is.toString();
      player.getInventory().remove(cost);
      is.setDurability((short) 0);
    }

    protected boolean hasReagentCost(Player player, ItemStack itemStack) {
      int amount = 0;
      for (ItemStack stack : player.getInventory().all(itemStack.getType()).values()) {
        amount += stack.getAmount();
        if (amount >= itemStack.getAmount()) {
          return true;
        }
      }
      return false;
    }
  }
}