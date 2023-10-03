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
    public void onEnable() {

        getServer().getPluginManager().registerEvents(new SpectreListener(this), this);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, Spectre::tickAll, 20L, 20L);

    }
}
