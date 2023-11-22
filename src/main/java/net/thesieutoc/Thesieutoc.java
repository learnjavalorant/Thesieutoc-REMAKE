// Decompiled with: FernFlower
// Class Version: 11
package net.thesieutoc;

import com.google.gson.JsonObject;

import java.io.File;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import net.thesieutoc.bstats.Metrics;
import net.thesieutoc.bstats.OperationSystemInfo;
import net.thesieutoc.card.ChatListener;
import net.thesieutoc.card.NapTheoMoc;
import net.thesieutoc.card.WebCallback;
import net.thesieutoc.card.WebRequest;
import net.thesieutoc.command.Command_napthe;
import net.thesieutoc.command.Command_topnapthe;
import net.thesieutoc.command.Command_tst;
import net.thesieutoc.database.DB;
import net.thesieutoc.menu.Menu_loaithe;
import net.thesieutoc.menu.Menu_menhgia;
import net.thesieutoc.menu.Menu_seripin;
import net.thesieutoc.menu.Menu_topnap;
import net.thesieutoc.placeholder.TSTPlaceholder;
import net.thesieutoc.utils.Task;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Thesieutoc extends JavaPlugin {
    private static final String MATCH = "(?ium)^(player:|op:|console:|)(.*)$";
    private static final byte[] key = new byte[]{98, 48, 47, -41, 27, -14, 61, 39, 9, -84, 0, -20, 125, -31, -24, 67};
    private static Thesieutoc m;
    private static SecretKeySpec secretKey = null;
    public WebRequest WEB_REQUEST;
    public TSTTop tsttop;
    public WebCallback WEB_CALLBACK;
    public DB db;
    public Command_tst cmd_tst;
    public HashMap<String, JsonObject> REQUESTS = new HashMap();
    public String API_key;
    public String API_secret;
    TSTPlaceholder placeholder;
    File menu;
    FileConfiguration menufc;
    File lang;
    FileConfiguration langfc;
    File NapTheoMoc;
    FileConfiguration NapTheoMocfc;

    File discord;
    FileConfiguration discordfc;




    public static Thesieutoc getInstance() {
        return m;
    }

    public void onEnable() {
        m = this;
        int pluginId = 15608;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new Metrics.DrilldownPie("processor", () -> {
            Map<String, Map<String, Integer>> map = new HashMap();

            try {
                String processor = OperationSystemInfo.queryCpuModel();
                if (!processor.isEmpty()) {
                    String _p = "Other";
                    if (processor.contains("Intel")) {
                        _p = "Intel";
                    }

                    if (processor.contains("AMD")) {
                        _p = "AMD";
                    }

                    Map<String, Integer> entry = new HashMap();
                    entry.put(processor, 1);
                    map.put(_p, entry);
                }
            } catch (Exception var4) {
            }

            return map;
        }));
        this.tsttop = new TSTTop();
        this.saveDefaultConfig();
        this.saveDefaultMenu();
        this.saveDefaultLang();
        this.saveDefaultNapTheoMoc();
        this.saveDefaultDiscord();
        this.reloadKey();
        this.getCommand("napthe").setExecutor(new Command_napthe());
        this.getCommand("topnapthe").setExecutor(new Command_topnapthe());
        this.getCommand("thesieutoc").setExecutor(new Command_tst());
        Bukkit.getPluginManager().registerEvents(new ChatListener(), m);
        Bukkit.getPluginManager().registerEvents(new Menu_loaithe(), m);
        Bukkit.getPluginManager().registerEvents(new Menu_menhgia(), m);
        Bukkit.getPluginManager().registerEvents(new Menu_seripin(), m);
        Bukkit.getPluginManager().registerEvents(new Menu_topnap(), m);
        Bukkit.getPluginManager().registerEvents(new NapTheoMoc(), m);
        Bukkit.getPluginManager().registerEvents(new UpdateChecker(), m);
        this.db = new DB();
        this.WEB_REQUEST = new WebRequest(m);
        this.WEB_CALLBACK = new WebCallback(m);
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            try {
                this.placeholder = new TSTPlaceholder(m);
                this.placeholder.register();
            } catch (Exception var4) {
                var4.printStackTrace();
            }
        }

    }

    public void onDisable() {
        for(String name : this.REQUESTS.keySet()) {
            Player p = Bukkit.getPlayer(name);
            if (p != null) {
                p.closeInventory();
            }
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") && this.placeholder != null) {
            this.placeholder.unregister();
        }

    }

    public void saveDefaultMenu() {
        this.menu = new File(m.getDataFolder(), "menu.yml");
        if (!this.menu.exists()) {
            this.menu.getParentFile().mkdirs();
            this.saveResource("menu.yml", false);
        }

        this.menufc = YamlConfiguration.loadConfiguration(this.menu);
    }

    public FileConfiguration getMenu() {
        return this.menufc;
    }

    public void reloadMenu() {
        this.saveDefaultMenu();
    }

    public void saveDefaultLang() {
        this.lang = new File(m.getDataFolder(), "lang.yml");
        if (!this.lang.exists()) {
            this.lang.getParentFile().mkdirs();
            this.saveResource("lang.yml", false);
        }

        this.langfc = YamlConfiguration.loadConfiguration(this.lang);
    }
    public FileConfiguration getLang() {
        return this.langfc;
    }

    public void reloadLang() {
        this.saveDefaultLang();
    }
    public String getLang(String id) {
        String msg = m.getLang().getString(id);

        try {
            msg = ChatColor.translateAlternateColorCodes('&', msg);
        } catch (Exception var4) {
            var4.printStackTrace();
            Bukkit.getLogger().warning("§a[Thesieutoc] §cThieu §f" + id + "§c trong file lang.yml.");
        }

        return msg;
    }
    public void saveDefaultNapTheoMoc() {
        this.NapTheoMoc = new File(m.getDataFolder(), "naptheomoc.yml");
        if (!this.NapTheoMoc.exists()) {
            this.NapTheoMoc.getParentFile().mkdirs();
            this.saveResource("naptheomoc.yml", false);
        }

        this.NapTheoMocfc = YamlConfiguration.loadConfiguration(this.NapTheoMoc);
    }

    public FileConfiguration getNapTheoMoc() {
        return this.NapTheoMocfc;
    }

    public void reloadNapTheoMoc() {
        this.saveDefaultNapTheoMoc();
    }


    public void saveDefaultDiscord() {
        this.discord = new File(m.getDataFolder(), "discord.yml");
        if (!this.discord.exists()) {
            this.discord.getParentFile().mkdirs();
            this.saveResource("discord.yml", false);
        }

        this.discordfc = YamlConfiguration.loadConfiguration(this.discord);
    }

    public FileConfiguration getDiscordWebhook() {
        return this.discordfc;
    }

    public void reloadDiscordWebhook() {
        this.saveDefaultDiscord();
    }
    public void reloadKey() {
        this.API_key = m.getConfig().getString("A75B67E3ABDA37AA25C45D1722B11965");
        this.API_secret = m.getConfig().getString("821A7E10EC4C1C1E9E4B5181E728769E");
    }

    public void dispatchCommand(Player player, String command) {
        Task.syncTask(() -> {
            String type = command.replaceAll("(?ium)^(player:|op:|console:|)(.*)$", "$1").replace(":", "").toLowerCase();
            String cmd = command.replaceAll("(?ium)^(player:|op:|console:|)(.*)$", "$2").replaceAll("(?ium)([{]Player[}])", player.getName());
            switch(type) {
                case "op":
                    if (player.isOp()) {
                        player.performCommand(cmd);
                    } else {
                        player.setOp(true);
                        player.performCommand(cmd);
                        player.setOp(false);
                    }
                    break;
                case "":
                case "player":
                    player.performCommand(cmd);
                    break;
                case "console":
                default:
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }

        });
    }

    private String decrypt(String strToDecrypt) {
        try {
            if (secretKey == null) {
                secretKey = new SecretKeySpec(key, "AES");
            }

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(2, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception var3) {
            var3.printStackTrace();
            Bukkit.getLogger().warning("§a[Thesieutoc] §cLoi API Key, vui long lien he staff TheSieuToc.");
            return null;
        }
    }
    private String encrypt(String strToEncrypt) {
        try {
            if (secretKey == null) {
                secretKey = new SecretKeySpec(key, "AES");
            }

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(2, secretKey);
            return new String(cipher.doFinal(Base64.getEncoder().encode(strToEncrypt.getBytes())));
        } catch (Exception var3) {
            var3.printStackTrace();
            Bukkit.getLogger().warning("§a[Thesieutoc] §cLoi API Key, vui long lien he staff TheSieuToc.");
            return null;
        }
    }
}
