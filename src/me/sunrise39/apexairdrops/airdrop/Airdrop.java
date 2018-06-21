package me.sunrise39.apexairdrops.airdrop;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import me.sunrise39.apexairdrops.ApexAirdrops;
import me.sunrise39.apexairdrops.util.LocationHandler;

public class Airdrop {

	private ApexAirdrops plugin;
	private ArmorStand fallingAirdrop;
	private Block landedAirdrop;
	private Material block;
	private short blockData;
	private Location startLocation;
	private Location endLocation;
	
	public Airdrop(Location location, Material block) {
		plugin = ApexAirdrops.getInstance();
		this.startLocation = LocationHandler.offsetLocation(location.add(0, 100, 0));
		this.endLocation = startLocation.subtract(0, 100, 0);
		this.block = Material.CHEST;
		this.blockData = 0;
		
		if (!plugin.getWorldDropMaterial().containsKey(location.getWorld().getName())) {
			Bukkit.getConsoleSender().sendMessage("The offset for world " + location.getWorld().getName() + " does not exist! Setting drop material (block) to CHEST.");
		} else {
			this.block = plugin.getDropMaterial(location.getWorld().getName());
		}
		
		if (!plugin.getWorldDropMaterialData().containsKey(location.getWorld().getName())) {
			Bukkit.getConsoleSender().sendMessage("The offset for world " + location.getWorld().getName() + " does not exist! Setting drop block data to 0.");
		} else {
			this.blockData = plugin.getDropMaterialData(location.getWorld().getName());
		}
		
		this.fallingAirdrop = (ArmorStand) location.getWorld().spawnEntity(startLocation, EntityType.ARMOR_STAND);
		this.fallingAirdrop.setCustomName(plugin.getHologramText());
		this.fallingAirdrop.setCustomNameVisible(true);
		this.fallingAirdrop.setInvulnerable(true);
		this.fallingAirdrop.setVisible(false);
		this.fallingAirdrop.setHelmet(new ItemStack(block, 1, blockData));
		
		emitFireworks();
		checkIsLanded();
	}
	
	public void remove() {
		landedAirdrop.setType(Material.AIR);
		fallingAirdrop.remove();
	}

	private void emitFireworks() {
		new BukkitRunnable() {
			public void run() {
				if (isFalling()) {
					Firework firework = (Firework) startLocation.getWorld().spawnEntity(fallingAirdrop.getLocation(), EntityType.FIREWORK);
					FireworkMeta fireworkMeta = firework.getFireworkMeta();
					fireworkMeta.addEffect(FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.RED).withColor(Color.WHITE).build());
					firework.setFireworkMeta(fireworkMeta);
					firework.detonate();
				} else {
					this.cancel();
				}
			}
		}.runTaskTimer(plugin, 1L, 5L);
	}
	
	private void checkIsLanded() {
		new BukkitRunnable() {
			public void run() {
				if (!isFalling()) {
					landedAirdrop = fallingAirdrop.getLocation().getBlock();
					fallingAirdrop.setHelmet(new ItemStack(Material.AIR));
					landedAirdrop.setType(block);
					endLocation = landedAirdrop.getLocation();
					fallingAirdrop.teleport(landedAirdrop.getLocation().add(0.5D, 0D, 0.5D).subtract(0D, 0.5D, 0D));
					this.cancel();
				}
			}
		}.runTaskTimer(plugin, 20L, 1L);
	}
	
	public Block asBukkitBlock() {
		if (this.isFalling()) {
			return this.landedAirdrop;
		} else {
			return null;
		}
	}
	
	public ArmorStand asBukkitArmorStand() {
		return this.fallingAirdrop;
	}
	
	public boolean isFalling() {
		return !fallingAirdrop.isOnGround();
	}
	
	public boolean canFall() {
		return fallingAirdrop.hasGravity();
	}
	
	public void setCanFall(boolean canFall) {
		fallingAirdrop.setGravity(canFall);
	}
	
	public Material getBlock() {
		return this.block;
	}
	
	public void setBlock(Material block) {
		this.block = block;
		fallingAirdrop.setHelmet(new ItemStack(block));
		landedAirdrop.setType(block);
	}
	
	public Location getStartLocation() {
		return this.startLocation;
	}
	
	public Location getEndLocation() {
		return this.endLocation;
	}
}
