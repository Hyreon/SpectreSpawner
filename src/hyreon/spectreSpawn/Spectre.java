package hyreon.spectreSpawn;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.logging.Level;

public class Spectre {

    static Map<UUID, Spectre> spectres = new HashMap<>();

    UUID uuid;
    long clock;
    GameMode previousGameMode;
    String joinMessage;

    public Spectre(Player player, long clock, String joinMessage) {
        uuid = player.getUniqueId();
        this.clock = clock;
        this.joinMessage = joinMessage;
        previousGameMode = player.getGameMode();
        if (previousGameMode == GameMode.SPECTATOR) {
            previousGameMode = Bukkit.getDefaultGameMode();
        }
    }

    public static boolean playerIsSpectre(Player player) {
        return spectres.containsKey(player.getUniqueId());
    }


    public long getClock() {
        return clock;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public void tick() {
        clock--;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public static void tickAll() {
        try {
            Set<UUID> markedForRemoval = new HashSet<>();
            for (Spectre spectre: spectres.values()) {
                if (spectre.getClock() == 0) {
                    markedForRemoval.add(spectre.getUniqueId());
                    continue;
                }
                String message = ChatColor.LIGHT_PURPLE + "You will spawn here. Move, or wait " + spectre.getClock() + " seconds to enter the world.";
                Player player = Bukkit.getPlayer(spectre.getUniqueId());
                if (player != null) {
                    player.spawnParticle(Particle.SMOKE_LARGE, player.getLocation(), 12, 0, 1, 0, 0.1);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
                }
                spectre.tick();
            }
            for (UUID uuid: markedForRemoval) {
                integrate(uuid);
            }
        } catch (ConcurrentModificationException e) {
            Bukkit.getLogger().log(Level.WARNING, "Concurrent modification when trying to display spawn status. Retry already scheduled.");
        }
    }

    public static void deintegrate(UUID uuid, String joinFormat) {
        try {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) return;
            player.setGameMode(GameMode.SPECTATOR);
            spectres.put(uuid, new Spectre(player, 30L, joinFormat));
        } catch (ConcurrentModificationException e) {
            Bukkit.getLogger().log(Level.WARNING, "Concurrent modification when trying to make " + uuid + " a spectre. Retrying...");
            Bukkit.getScheduler().scheduleSyncDelayedTask(SpectreSpawn.getPlugin(SpectreSpawn.class),
                    () -> deintegrate(uuid, joinFormat), 9L);
        }

    }

    public static void integrate(UUID uuid) {
        try {
            Spectre spectre = spectres.get(uuid);
            Player player = Bukkit.getPlayer(uuid);
            String message = ChatColor.YELLOW + "You have spawned.";
            if (player != null) {
                player.setGameMode(spectre.previousGameMode);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
                Bukkit.broadcastMessage(spectre.joinMessage);
            }
            spectres.remove(uuid);
        } catch (ConcurrentModificationException e) {
            Bukkit.getLogger().log(Level.WARNING, "Concurrent modification when trying to integrate " + uuid + ". Retrying...");
            Bukkit.getScheduler().scheduleSyncDelayedTask(SpectreSpawn.getPlugin(SpectreSpawn.class),
                    () -> integrate(uuid), 8L);
        }
    }

}
