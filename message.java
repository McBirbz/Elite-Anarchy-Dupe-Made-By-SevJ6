package me.impurity.plus.dupe;

import me.impurity.plus.Instance;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class MinecartDupe implements Listener, DupeData, Instance {
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExplode(EntityExplodeEvent event) {
        boolean duped = false;
        Entity entity = event.getEntity();
        if (entity instanceof ExplosiveMinecart) {
            ArrayList<ItemStack> itemStacks = new ArrayList<>();
            for (Player player : entity.getLocation().getNearbyPlayers(6)) {
                if (dupeMap.containsKey(player)) {
                    for (int i = 0; i < player.getOpenInventory().countSlots(); i++) {
                        try {
                            if (player.getOpenInventory().getItem(i).getType() != Material.AIR && player.getOpenInventory().getItem(i) != null) {
                                itemStacks.add(i, player.getOpenInventory().getItem(i));
                                player.getInventory().addItem(itemStacks.get(i));
                                duped = true;
                            }
                        } catch (IndexOutOfBoundsException ignored) {
                        }
                    }
                }
                if (duped) {
                    System.out.println("duped");
                    dupeMap.remove(player);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        System.out.println("this event was called");
        boolean isDupeSetup = false;
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.CHEST) {
            System.out.println("is chest");
            Block block = event.getClickedBlock();
            for (Entity entity : block.getLocation().getNearbyEntities(6, 6, 6)) {
                if (entity.getType() == EntityType.MINECART_TNT) {
                    System.out.println("is tnt minecart");
                    isDupeSetup = true;
                    break;
                }
            }
            if (isDupeSetup) {
                if (!dupeMap.containsKey(player)) {
                    dupeMap.put(player, block);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> removePlayer(player), 20 * 15L);
                    System.out.println("put player in map");
                } else {
                    System.out.println("player already in map");
                }
            }
        }
    }

    private void removePlayer(Player player) {
        dupeMap.remove(player);
        System.out.println("removed player from map");
    }
}
