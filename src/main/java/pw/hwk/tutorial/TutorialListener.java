package pw.hwk.tutorial;

import pw.hwk.tutorial.api.EndTutorialEvent;
import pw.hwk.tutorial.api.ViewSwitchEvent;
import pw.hwk.tutorial.data.Caching;
import pw.hwk.tutorial.data.DataLoading;
import pw.hwk.tutorial.data.Getters;
import pw.hwk.tutorial.enums.MessageType;
import pw.hwk.tutorial.enums.ViewType;
import pw.hwk.tutorial.rewards.TutorialEco;
import pw.hwk.tutorial.util.TutorialUtils;
import pw.hwk.tutorial.util.UUIDFetcher;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class TutorialListener implements Listener {

    private static ServerTutorial plugin = ServerTutorial.getInstance();
    private static Map<UUID, BukkitRunnable> restoreQueue = new HashMap<UUID, BukkitRunnable>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        if (event.getAction() != Action.PHYSICAL) {
            if (Getters.getGetters().isInTutorial(name) && Getters.getGetters().getCurrentTutorial(name).getViewType() != ViewType.TIME) {
                if (Getters.getGetters().getCurrentTutorial(name).getTotalViews() == Getters.getGetters().getCurrentView(name)) {
                    plugin.getEndTutorial().endTutorial(player);
                } else {
                    plugin.incrementCurrentView(name);
                    TutorialUtils.getTutorialUtils().messageUtils(player);
                    Caching.getCaching().setTeleport(player.getUniqueId(), true);
                    player.teleport(Getters.getGetters().getTutorialView(name).getLocation());
                }
            }
        }
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) && !Getters.getGetters().isInTutorial(name)) {
            Block block = event.getClickedBlock();
            if (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) {
                Sign sign = (Sign) block.getState();
                String match = ChatColor.stripColor(TutorialUtils.color(Getters.getGetters().getConfigs().signSetting()));
                if (sign.getLine(0).equalsIgnoreCase(match)) {
                    if (sign.getLine(1) == null) {
                        return;
                    }
                    plugin.startTutorial(sign.getLine(1), player);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        if (Getters.getGetters().isInTutorial(p.getName())) {
            event.setCancelled(true);
            return;
        }

        HashSet<Player> set = new HashSet<Player>(event.getRecipients());
        for (Player setPlayer : set) {
            if (setPlayer == null) {
                continue;
            }

            if (Getters.getGetters().isInTutorial(setPlayer.getName())) {
                event.getRecipients().remove(setPlayer);
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player p = event.getPlayer();
        if (!Getters.getGetters().isInTutorial(p.getName())) {
            return;
        }

        if (Caching.getCaching().canTeleport(p.getUniqueId())) {
            Caching.getCaching().setTeleport(p.getUniqueId(), false);
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (Getters.getGetters().isInTutorial(player.getName())) {
            Caching.getCaching().setTeleport(player.getUniqueId(), true);
            player.teleport(Getters.getGetters().getTutorialView(player.getName()).getLocation());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (Getters.getGetters().isInTutorial(event.getPlayer().getName())) {
            final GameMode gm = Caching.getCaching().getGameMode(player.getUniqueId());
            final boolean allowFlight = plugin.getFlight(player.getName());
            final String name = player.getName();
            final Location loc = plugin.getFirstLoc(player.getName());
            final ItemStack[] contents = plugin.getInventory(player.getName());
            restoreQueue.put(player.getUniqueId(), new BukkitRunnable() {
                @Override
                public void run() {
                    Player player = Bukkit.getPlayerExact(name);
                    player.closeInventory();
                    player.getInventory().clear();
                    player.setGameMode(gm);
                    player.setAllowFlight(allowFlight);
                    player.setFlying(false);
                    plugin.removeFlight(name);
                    Caching.getCaching().setTeleport(player.getUniqueId(), true);
                    player.teleport(loc);
                    plugin.cleanFirstLoc(name);
                    player.getInventory().setContents(contents);
                    plugin.cleanInventory(name);
                }
            });
            plugin.removeFromTutorial(event.getPlayer().getName());
        }
        if (!plugin.getServer().getOnlineMode()) {
            try {
                Caching.getCaching().getResponse().remove(player.getName());
            } catch (Exception ignored) {

            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (Getters.getGetters().isInTutorial(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (Getters.getGetters().isInTutorial(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (Getters.getGetters().isInTutorial(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(PlayerDropItemEvent event) {
        if (Getters.getGetters().isInTutorial(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWhee(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (Getters.getGetters().isInTutorial(event.getEntity().getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final String playerName = player.getName();
        if (!plugin.getServer().getOnlineMode()) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    try {
                        Caching.getCaching().getResponse().put(playerName, UUIDFetcher.getUUIDOf(playerName));
                    } catch (Exception ignored) {

                    }
                }
            });
        }
        for (String name : Getters.getGetters().getAllInTutorial()) {
            Player tut = plugin.getServer().getPlayerExact(name);
            if (tut != null) {
                player.hidePlayer(tut);
            }
        }
        if (!player.hasPlayedBefore()) {
            if (Getters.getGetters().getConfigs().firstJoin()) {
                plugin.startTutorial(Getters.getGetters().getConfigs().firstJoinTutorial(), player);
            }
        }

        BukkitRunnable runnable = restoreQueue.get(player.getUniqueId());
        if (runnable != null) {
            runnable.runTaskLater(plugin, 20L);
        }
        restoreQueue.remove(player.getUniqueId());
    }

    @EventHandler
    public void onViewSwitch(ViewSwitchEvent event) {
        Player player = event.getPlayer();
        if (Getters.getGetters().getConfigs().getExpCountdown()) {
            player.setExp(player.getExp() - 1f);
        }
        if (Getters.getGetters().getConfigs().getRewards()) {
            if (!seenTutorial(player.getName(), event.getTutorial().getName())) {
                if (Getters.getGetters().getConfigs().getViewExp()) {
                    player.setTotalExperience(player.getTotalExperience() + Getters.getGetters().getConfigs().getPerViewExp());
                    player.sendMessage(ChatColor.BLUE + "You received " + Getters.getGetters().getConfigs().getViewExp());
                }
                if (Getters.getGetters().getConfigs().getViewMoney()) {
                    if (TutorialEco.getTutorialEco().setupEconomy()) {
                        EconomyResponse ecoResponse = TutorialEco.getTutorialEco().getEcon().depositPlayer(player, Getters.getGetters().getConfigs().getPerViewMoney());
                        if (ecoResponse.transactionSuccess()) {
                            player.sendMessage(ChatColor.BLUE + "You received " + ecoResponse.amount + " New Balance: " + ecoResponse.balance);
                        } else {
                            plugin.getLogger().log(Level.WARNING, "There was an error processing Economy for player: {0}", player.getName());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onTutorialEnd(EndTutorialEvent event) {
        if (Getters.getGetters().getConfigs().getRewards()) {
            Player player = event.getPlayer();
            String playerName = player.getName().toLowerCase();
            if (!seenTutorial(playerName, event.getTutorial().getName())) {
                if (TutorialEco.getTutorialEco().setupEconomy()) {
                    if (Getters.getGetters().getConfigs().getTutorialMoney()) {
                        EconomyResponse ecoResponse = TutorialEco.getTutorialEco().getEcon().depositPlayer(player, Getters.getGetters().getConfigs().getPerTutorialMoney());
                        if (ecoResponse.transactionSuccess()) {
                            player.sendMessage(ChatColor.BLUE + "You received " + ecoResponse.amount + ". New Balance: " + ecoResponse.balance);
                        } else {
                            plugin.getLogger().log(Level.WARNING, "There was an error processing Economy for player: {0}", player.getName());
                        }
                    }
                    if (Getters.getGetters().getConfigs().getTutorialExp()) {
                        player.setExp(player.getTotalExperience() + Getters.getGetters().getConfigs().getPerTutorialExp());
                    }
                }
            } else {
                player.sendMessage(ChatColor.BLUE + "You have been through this tutorial already. You will not collect rewards!");
            }
        }
        DataLoading.getDataLoading().getPlayerData().set("players." + Caching.getCaching().getUUID(event.getPlayer()) + ".tutorials." + event.getTutorial().getName(), "true");
        DataLoading.getDataLoading().savePlayerData();
        Caching.getCaching().reCachePlayerData();
    }

    public boolean seenTutorial(String name, String tutorial) {
        if (Getters.getGetters().getPlayerData().containsKey(name)) {
            if (Getters.getGetters().getPlayerData(name).getPlayerTutorialData().containsKey(tutorial)) {
                return Getters.getGetters().getPlayerData(name).getPlayerTutorialData().get(tutorial).getSeen();
            }
        }
        return false;
    }
}