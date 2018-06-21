package me.sunrise39.apexairdrops.item;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import me.sunrise39.apexairdrops.ApexAirdrops;

public class ItemFlare {
	
	private static ApexAirdrops plugin = ApexAirdrops.getInstance();
	
	public static ItemStack getItem(int amount) {
		ItemStack flare = new ItemStack(Material.FIREWORK);
		FireworkMeta flareMeta = (FireworkMeta) flare.getItemMeta();
		flare.setAmount(amount);
		flareMeta.setDisplayName(plugin.getItemName());
		flareMeta.setLore(plugin.getItemLore());
		flareMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		flareMeta.addEffect(FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.RED).withFade(Color.WHITE).withFlicker().withTrail().build());
		flareMeta.setPower(2);
		flare.setItemMeta(flareMeta);
		
		return flare;
	}
}
