package hyreon.spectreSpawn;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SpectreListener implements Listener {

    SpectreSpawn spectreSpawn;

    public SpectreListener(SpectreSpawn spectreSpawn) {
        this.spectreSpawn = spectreSpawn;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.SPECTATOR) return;
        Spectre.deintegrate(e.getPlayer().getUniqueId(), e.getJoinMessage());
        e.setJoinMessage(null);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (!Spectre.playerIsSpectre(e.getPlayer())) return;
        Spectre.integrate(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (!Spectre.playerIsSpectre(e.getPlayer())) return;
        if (e.getTo() == null) return;
        if (e.getTo().getX() != e.getFrom().getX() ||
                e.getTo().getY() != e.getFrom().getY() ||
                e.getTo().getZ() != e.getFrom().getZ()) {
            Spectre.integrate(e.getPlayer().getUniqueId());
        }
        e.getTo().setX(e.getFrom().getX());
        e.getTo().setY(e.getFrom().getY());
        e.getTo().setZ(e.getFrom().getZ());
    }

}
