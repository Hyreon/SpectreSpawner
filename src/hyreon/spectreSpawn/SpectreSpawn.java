package hyreon.spectreSpawn;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Level;

public class SpectreSpawn extends JavaPlugin {

    Map<UUID, Long> spectreClocks = new HashMap<>();
    Map<UUID, GameMode> spectrePreviousGamemode = new HashMap<>();

    public void onEnable() {

        getServer().getPluginManager().registerEvents(new SpectreListener(this), this);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            try {
                Set<UUID> markedForRemoval = new HashSet<>();
                for (UUID uuid: spectreClocks.keySet()) {
                    if (spectreClocks.get(uuid) == 0) {
                        markedForRemoval.add(uuid);
                        continue;
                    }
                    String message = ChatColor.LIGHT_PURPLE + "You will spawn here. Move down, or wait " + spectreClocks.get(uuid) + " seconds to enter the world.";
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        player.spawnParticle(Particle.SMOKE_LARGE, player.getLocation(), 12, 0, 1, 0, 0.1);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
                    }
                    spectreClocks.put(uuid, spectreClocks.get(uuid) - 1);
                }
                for (UUID uuid: markedForRemoval) {
                    integrate(uuid);
                }
            } catch (ConcurrentModificationException e) {
                Bukkit.getLogger().log(Level.WARNING, "Concurrent modification when trying to display spawn status. Retry already scheduled.");
            }
        }, 20L, 20L);


    }

    public long getCurrentTime() {
        return Bukkit.getWorlds().get(0).getFullTime();
    }

    public void deintegrate(UUID uuid) {
        try {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) return;
            spectreClocks.put(uuid, 30L);
            if (spectrePreviousGamemode.get(uuid) == null) {
                GameMode gameMode = player.getGameMode();
                if (gameMode == GameMode.SPECTATOR) {
                    gameMode = Bukkit.getDefaultGameMode();
                }
                spectrePreviousGamemode.put(uuid, gameMode);
            }
            player.setGameMode(GameMode.SPECTATOR);
        } catch (ConcurrentModificationException e) {
            Bukkit.getLogger().log(Level.WARNING, "Concurrent modification when trying to make " + uuid + " a spectre. Retrying...");
            Bukkit.getScheduler().scheduleSyncDelayedTask(this,
                    () -> deintegrate(uuid), 9L);
        }

    }

    public void integrate(UUID uuid) {
        try {
            spectreClocks.remove(uuid);
            Player player = Bukkit.getPlayer(uuid);
            String message = ChatColor.YELLOW + "You have spawned.";
            if (player != null) {
                player.setGameMode(spectrePreviousGamemode.get(uuid));
                spectrePreviousGamemode.remove(uuid);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
                Bukkit.broadcastMessage(ChatColor.YELLOW + player.getDisplayName() + " joined the game");
            }
        } catch (ConcurrentModificationException e) {
            Bukkit.getLogger().log(Level.WARNING, "Concurrent modification when trying to integrate " + uuid + ". Retrying...");
            Bukkit.getScheduler().scheduleSyncDelayedTask(this,
                    () -> integrate(uuid), 8L);
        }
    }
}
