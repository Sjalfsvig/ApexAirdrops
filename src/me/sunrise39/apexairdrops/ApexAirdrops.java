package me.sunrise39.apexairdrops;

import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import me.sunrise39.apexairdrops.util.ApexFile;
import net.md_5.bungee.api.ChatColor;

public class ApexAirdrops extends JavaPlugin {
	
	private static ApexAirdrops instance;
	private ArrayList<Location> currentDrops; // The location of the current drops in the world. Saved with location to get block and entity.
	
	// General
	private String prefix; // The message prefix, e.g. [ApexAirdrops].
	private String announcement; // The message to be sent when an airdrop drops.
	private String inventoryTitle; // The title of the GUI that contains items.
	private String hologramText; // The text of the hologram (armor stand custom name) over the drop.
	private boolean broadcastEverywhere; // Should the plugin broadcast every drop to every world?
	private boolean randomWorld; // If multiple worlds, should airdrops drop in a random world instead of using specific world settings? Will use first world settings.
	private boolean destroyOnClose; // Should drops be destroyed after they've been closed?
	private boolean giveItemsOnClose; // Should items be automatically placed in the players inventory after the drop has been closed?
	private boolean dropItemsOnClose; // Should items be dropped when the drop has been closed/destroyed?
	
	// World Settings
	private ArrayList<String> worlds; // The worlds for airdrops to be active in.
	private Map<String, Long> interval; // The time between a drop per world.
	private Map<String, Integer> removeAfter; // How long after a drop should an airdrop be removed, per world? Set to 0 to use destory-after-open for removing.
	private Map<String, Material> blocks; // The block that should be used for the airdrop, per world.
	private Map<String, Short> blockData; // The data of the block that should be used for the airdrop, e.g. STONE:3.
	
	// Bounds
	private Map<String, Integer> maxBoundX; // The maxmimum x bound per world.
	private Map<String, Integer> minBoundX; // The minimum x bound per world.
	private Map<String, Integer> maxBoundZ; // The maximum z bound per world.
	private Map<String, Integer> minBoundZ; // The minimum z bound per world. 
	private Map<String, Integer> maxOffset; // The maximum distance between the announced coords and the actual drop.
	
	// Flare Item
	private String itemName; // The display name of the item used to summon an airdrop.
	private ArrayList<String> itemLore; // The lore of the item used to summon an airdrop.
	
	// Files
	private ApexFile configFile;
	private ApexFile lootFile;
	private ApexFile airdropsFile;
	
	@Override
	public void onEnable() {
		instance = this;
		
		configFile = new ApexFile("config");
		lootFile = new ApexFile("loot");
		airdropsFile = new ApexFile("airdrops");
		this.init();
		
		this.prefix = ChatColor.translateAlternateColorCodes('&', this.getConfigFile().getConfig().getString("ApexAirdrops.general.prefix"));
		this.announcement = ChatColor.translateAlternateColorCodes('&', this.getConfigFile().getConfig().getString("ApexAirdrops.general.announcement"));
		this.broadcastEverywhere = this.getConfigFile().getConfig().getBoolean("ApexAirdrops.general.broadcast-everywhere");
		this.inventoryTitle = ChatColor.translateAlternateColorCodes('&', this.getConfigFile().getConfig().getString("ApexAirdrops.general.inventory-title"));
		this.hologramText = ChatColor.translateAlternateColorCodes('&', this.getConfigFile().getConfig().getString("ApexAirdrops.general.hologram-text"));
		this.randomWorld = this.getConfigFile().getConfig().getBoolean("ApexAirdrops.general.random-world");
		this.destroyOnClose = this.getConfigFile().getConfig().getBoolean("ApexAirdrops.general.destroy-on-close");
		this.giveItemsOnClose = this.getConfigFile().getConfig().getBoolean("ApexAirdrops.general.give-items-on-close");
		this.dropItemsOnClose = this.getConfigFile().getConfig().getBoolean("ApexAirdrops.general.drop-items-on-close");
		this.itemName = ChatColor.translateAlternateColorCodes('&', this.getConfigFile().getConfig().getString("ApexAirdrops.flare.item-name"));
		this.itemLore = new ArrayList<String>();
		for (String lore : this.getConfigFile().getConfig().getStringList("ApexAirdrops.flare.item-lore")) {
			itemLore.add(ChatColor.translateAlternateColorCodes('&', lore));
		}
		this.worlds = new ArrayList<String>();
		for (String world : this.getConfigFile().getConfig().getConfigurationSection("ApexAirdrops.worlds").getKeys(false)) {
			worlds.add(world);
			this.interval.put(world, this.getConfigFile().getConfig().getLong("ApexAirdrops.worlds." + world + ".interval"));
			this.removeAfter.put(world, this.getConfigFile().getConfig().getInt("ApexAirdrops.worlds." + world + ".remove-after"));
			String blockWithData = this.getConfigFile().getConfig().getString("ApexAirdrops.worlds." + world + ".block");
			String worldBlockWithData[] = blockWithData.split(":");
			this.blocks.put(world, Material.valueOf(worldBlockWithData[0]));
			this.blockData.put(world, Short.parseShort(worldBlockWithData[1]));
			this.maxBoundX.put(world, this.getConfigFile().getConfig().getInt("ApexAirdrops.worlds." + world + ".bounds.max-bound-x"));
			this.minBoundX.put(world, this.getConfigFile().getConfig().getInt("ApexAirdrops.worlds." + world + ".bounds.min-bound-x"));
			this.maxBoundZ.put(world, this.getConfigFile().getConfig().getInt("ApexAirdrops.worlds." + world + ".bounds.max-bound-z"));
			this.minBoundZ.put(world, this.getConfigFile().getConfig().getInt("ApexAirdrops.worlds." + world + ".bounds.min-bound-z"));
			this.maxOffset.put(world, this.getConfigFile().getConfig().getInt("ApexAIrdrops.worlds." + world + ".max-offset"));
		}
		
	}
	
	public void init() {
		if (this.getConfigFile().getConfig().getConfigurationSection("ApexAirdrops") == null) {
			this.saveResource("config.yml", true);
		}
	}
	
	public ApexFile getConfigFile() {
		return this.configFile;
	}
	
	public ApexFile getLootFile() {
		return this.lootFile;
	}
	
	public ApexFile getAirropsFile() {
		return this.airdropsFile;
	}
	
	public static ApexAirdrops getInstance() {
		return instance;
	}
	
	public ArrayList<Location> getCurrentAirdrops(){
		return this.currentDrops;
	}
	
	public void addCurrentAirdrop(Location airdropLocation) {
		this.currentDrops.add(airdropLocation);
	}
	
	public void removeCurrentAirdrop(Location airdropLocation) {
		this.currentDrops.remove(airdropLocation);
	}
	
	public String getPrefix() {
		return this.prefix;
	}
	
	public String getAnnouncement() {
		return this.announcement;
	}
	
	public String getInventoryTitle() {
		return this.inventoryTitle;
	}
	
	public String getHologramText() {
		return this.hologramText;
	}
	
	public boolean canBroadcastEverywhere() {
		return this.broadcastEverywhere;
	}
	
	public boolean useRandomWorld() {
		return this.randomWorld;
	}
	
	public boolean willDestroyOnClose() {
		return this.destroyOnClose;
	}
	
	public boolean willGiveItemsOnClose() {
		return this.giveItemsOnClose;
	}
	
	public boolean willDropItemsOnClose() {
		return this.dropItemsOnClose;
	}
	
	public ArrayList<String> getActiveWorlds(){
		return this.worlds;
	}
	
	public void addActiveWorld(String worldName) {
		this.worlds.add(worldName);
	}
	
	public void removeActiveWorld(String worldName) {
		this.worlds.remove(worldName);
	}
	
	public Map<String, Long> getWorldIntervals(){
		return this.interval;
	}
	
	public Long getInterval(String world) {
		return this.interval.get(world);
	}
	
	public void setWorldInterval(String world, Long interval) {
		this.interval.put(world, interval);
	}
	
	public Map<String, Integer> getWorldRemoveAfter(){
		return this.removeAfter;
	}
	
	public Integer getRemoveAfter(String world) {
		return this.removeAfter.get(world);
	}
	
	public void setWorldRemoveAfter(String world, Integer removeAfter) {
		this.removeAfter.put(world, removeAfter);
	}
	
	public Map<String, Material> getWorldDropMaterial(){
		return this.blocks;
	}
	
	public Material getDropMaterial(String world) {
		return this.blocks.get(world);
	}
	
	public void setWorldMaterial(String world, Material block) {
		this.blocks.put(world, block);
	}
	
	public Map<String, Short> getWorldDropMaterialData(){
		return this.blockData;
	}
	
	public Short getDropMaterialData(String world) {
		return this.blockData.get(world);
	}
	
	public void setDropMaterialData(String world, Short data) {
		this.blockData.put(world, data);
	}
	
	public Map<String, Integer> getWorldMaxBoundX(){
		return this.maxBoundX;
	}
	
	public Integer getMaxBoundX(String world) {
		return this.maxBoundX.get(world);
	}
	
	public void setMaxBoundX(String world, Integer bound) {
		this.maxBoundX.put(world, bound);
	}
	
	public Map<String, Integer> getWorldMinBoundX(){
		return this.minBoundX;
	}
	
	public Integer getMinBoundX(String world) {
		return this.minBoundX.get(world);
	}
	
	public void setMinBoundX(String world, Integer bound) {
		this.minBoundX.put(world, bound);
	}
	
	public Map<String, Integer> getWorldMaxBoundZ(){
		return this.maxBoundZ;
	}
	
	public Integer getMaxBoundZ(String world) {
		return this.maxBoundZ.get(world);
	}
	
	public void setMaxBoundZ(String world, Integer bound) {
		this.maxBoundZ.put(world, bound);
	}
	
	public Map<String, Integer> getWorldMinBoundZ(){
		return this.minBoundZ;
	}
	
	public Integer getMinBoundZ(String world) {
		return this.minBoundZ.get(world);
	}
	
	public void setMinBoundZ(String world, Integer bound) {
		this.minBoundZ.put(world, bound);
	}
	
	public Map<String, Integer> getWorldMaxOffset(){
		return this.maxOffset;
	}
	
	public Integer getMaxOffset(String world) {
		return this.maxOffset.get(world);
	}
	
	public void setMaxOffset(String world, Integer offset) {
		this.maxOffset.put(world, offset);
	}
	
	public String getItemName() {
		return this.itemName;
	}
	
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
	public ArrayList<String> getItemLore(){
		return this.itemLore;
	}
	
	public void addItemLore(String itemLore) {
		this.itemLore.add(itemLore);
	}
	
	public void removeItemLore(String itemLore) {
		this.itemLore.remove(itemLore);
	}
}
