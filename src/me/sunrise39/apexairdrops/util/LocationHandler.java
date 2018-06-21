package me.sunrise39.apexairdrops.util;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import me.sunrise39.apexairdrops.ApexAirdrops;

public class LocationHandler {

	private static ApexAirdrops plugin = ApexAirdrops.getInstance();
	
	public static Location offsetLocation(Location location) {
		int offset = 50;
		
		if (!plugin.getWorldMaxOffset().containsKey(location.getWorld().toString())) {
			Bukkit.getConsoleSender().sendMessage("The offset for world " + location.getWorld().getName() + " does not exist! Setting offset to 50.");
		} else {
			offset = plugin.getMaxOffset(location.getWorld().toString());
		}
		
		Random random = new Random();
		
		int x = (random.nextInt(offset * 2) + 1) - offset;
		int z = (random.nextInt(offset * 2) + 1) - offset;
		
		return location.add(x, 100, z);
	}
	
	public static Location getRandomLocation() {
		Random random = new Random();
		int worldInt = random.nextInt(plugin.getActiveWorlds().size());
		World world = Bukkit.getWorld(plugin.getActiveWorlds().get(worldInt));
		int maxBoundX = plugin.getMaxBoundX(world.getName());
		int minBoundX = plugin.getMinBoundX(world.getName());
		int maxBoundZ = plugin.getMaxBoundZ(world.getName());
		int minBoundZ = plugin.getMaxBoundZ(world.getName());
		int x = random.nextInt(maxBoundX + 1 + maxBoundX) - minBoundX;
		int z = random.nextInt(maxBoundZ + 1 + maxBoundZ) - minBoundZ;
		
		return new Location(world, x, 0, z);
	}
}
