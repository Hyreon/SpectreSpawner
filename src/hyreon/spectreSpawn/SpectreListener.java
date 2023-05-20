package hyreon.spectreSpawn;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SpectreListener implements Listener {

    SpectreSpawn spectreSpawn;

    public SpectreListener(SpectreSpawn spectreSpawn) {
        this.spectreSpawn = spectreSpawn;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        spectreSpawn.deintegrate(e.getPlayer().getUniqueId());
        e.setJoinMessage(null);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (!spectreSpawn.spectreClocks.containsKey(e.getPlayer().getUniqueId())) return;
        spectreSpawn.integrate(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (!spectreSpawn.spectreClocks.containsKey(e.getPlayer().getUniqueId())) return;
        if (e.getTo() == null) return;
        if (e.getTo().getY() < e.getFrom().getY()) {
            spectreSpawn.integrate(e.getPlayer().getUniqueId());
        }
        e.getTo().setX(e.getFrom().getX());
        e.getTo().setY(e.getFrom().getY());
        e.getTo().setZ(e.getFrom().getZ());
    }

}
