// Decompiled with: FernFlower
// Class Version: 11
package net.thesieutoc.card;

import com.google.common.base.Equivalence;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.function.Supplier;

import net.thesieutoc.Thesieutoc;
import net.thesieutoc.utils.DiscordWebhook;
import org.bukkit.configuration.file.FileConfiguration;
import net.thesieutoc.event.PlayerCardChargedEvent;
import net.thesieutoc.utils.Task;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WebCallback {
    private static FileConfiguration config;
    Thesieutoc m;
    public HashMap<String, HashMap<String, JsonObject>> queue = new HashMap();
    public WebCallback(Thesieutoc m) {
        this.m = m;
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        Task.asyncTask(() -> {
            for(String name : this.queue.keySet()) {
                Player p = Bukkit.getPlayer(name);
                if (p != null && p.isOnline()) {
                    HashMap<String, JsonObject> card_queue = (HashMap)this.queue.get(name);
                    Iterator<String> transid_ite = card_queue.keySet().iterator();
                    if (transid_ite.hasNext()) {
                        String trans_id = (String)transid_ite.next();
                        JsonObject card_response = m.WEB_REQUEST.checkCard(m.API_key, m.API_secret, trans_id);
                        if (!card_queue.containsKey(trans_id)) {
                            Bukkit.getLogger().warning("§a[Thesieutoc] §cCo loi xay ra khi duyet the (khong co du lieu the), vui long bao cao cho staff TheSieuToc");
                            Bukkit.getLogger().warning("§a[Thesieutoc-Debug] §cTen nguoi choi: " + p.getName());
                            Bukkit.getLogger().warning("§a[Thesieutoc-Debug] §cTrans ID: " + trans_id);
                        } else {
                            JsonObject cardinfo = (JsonObject) card_queue.get(trans_id);
                            if (card_response.get("status").getAsString().equals("-9")) {
                                int retry = cardinfo.has("retry") ? cardinfo.get("retry").getAsInt() : 0;
                                if (retry > 100) {
                                    card_queue.remove(trans_id);
                                } else {
                                    cardinfo.addProperty("retry", retry + 1);
                                    card_queue.put(trans_id, cardinfo);
                                }
                            } else {
                                if (card_response.get("status").getAsString().equals("-10")) {
                                    p.sendMessage(m.getLang("nap_the_that_bai"));
                                    cardinfo.addProperty("msg", "that bai");
                                    String playername = p.getPlayer().getName();
                                    int cardprice = cardinfo.get("cardprice").getAsInt();


                                    DiscordWebhook webhook = new DiscordWebhook(m.getDiscordWebhook().getString("WebHookAPI"));
                                    webhook.setContent(m.getDiscordWebhook().getString("Content"));
                                    webhook.setAvatarUrl("https://minotar.net/helm/" + playername + "/100.png");
                                    webhook.setUsername(m.getDiscordWebhook().getString("WebhookName"));
                                        webhook.addEmbed(new DiscordWebhook.EmbedObject()
                                                .setTitle(m.getDiscordWebhook().getString("Title_ThatBai"))
                                            .setDescription("Người chơi " + playername + " đã nạp thẻ thất bại!!!!")
                                            .setColor(Color.RED)
                                            .addField("["+ df.format(now) +"]", "NGƯỜI CHƠI: " + playername + "", false)
                                            .addField("SERI:", "||" + cardinfo.get("seri").getAsString() +"||", true)
                                            .addField("MÃ THẺ:", "||" + cardinfo.get("pin").getAsString() +"||", true)
                                            .addField("MỆNH GIÁ:", "||" + cardinfo.get("cardprice").getAsString() +"||", true)
                                                .addField("LOẠI THẺ:", "||" + cardinfo.get("cardtype").getAsString() +"||", true)
                                                .addField("TRẠNG THÁI:", m.getDiscordWebhook().getString("Status_ThatBai"), false)
                                            .setThumbnail(m.getDiscordWebhook().getString("Thumbnail_URL"))
                                            .setFooter(m.getDiscordWebhook().getString("FooterContent"), m.getDiscordWebhook().getString("Footer_URL"))
                                            .setImage(m.getDiscordWebhook().getString("Image_URL"))
                                            .setAuthor(m.getDiscordWebhook().getString("AuthorName"), m.getDiscordWebhook().getString("Author_URL"), m.getDiscordWebhook().getString("Avatar_Author_URL"))
                                            .setUrl(m.getDiscordWebhook().getString("Title_URL")));
                                    try {
                                        webhook.execute();
                                    } catch (MalformedURLException e) {
                                        System.out.println("[MinecraftDiscordWebhook] Invalid webhook URL");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (card_response.get("status").getAsString().equals("10")) {
                                    p.sendMessage(m.getLang("sai_menh_gia"));
                                    cardinfo.addProperty("msg", "sai menh gia");


                                    String playername = p.getPlayer().getName();
                                    DiscordWebhook webhook = new DiscordWebhook(m.getDiscordWebhook().getString("WebHookAPI"));
                                    webhook.setContent(m.getDiscordWebhook().getString("Content"));
                                    webhook.setAvatarUrl("https://minotar.net/helm/" + playername + "/100.png");
                                    webhook.setUsername(m.getDiscordWebhook().getString("WebhookName"));
                                    webhook.addEmbed(new DiscordWebhook.EmbedObject()
                                            .setTitle(m.getDiscordWebhook().getString("Title_ThatBai"))
                                            .setDescription("Người chơi " + playername + " đã nạp thẻ " + cardinfo.get("cardprice").getAsString() + " sai mệnh giá!!!!")
                                            .setColor(Color.RED)
                                            .addField("["+ df.format(now) +"]", "NGƯỜI CHƠI: " + playername + "", false)
                                            .addField("SERI:", "||" + cardinfo.get("seri").getAsString() +"||", true)
                                            .addField("MÃ THẺ:", "||" + cardinfo.get("pin").getAsString() +"||", true)
                                            .addField("MỆNH GIÁ:", "||" + cardinfo.get("cardprice").getAsString() +"||", true)
                                            .addField("LOẠI THẺ:", "||" + cardinfo.get("cardtype").getAsString() +"||", true)
                                            .addField("TRẠNG THÁI:", m.getDiscordWebhook().getString("Status_ThatBai"), false)
                                            .setThumbnail(m.getDiscordWebhook().getString("Thumbnail_URL"))
                                            .setFooter(m.getDiscordWebhook().getString("FooterContent"), m.getDiscordWebhook().getString("Footer_URL"))
                                            .setImage(m.getDiscordWebhook().getString("Image_URL"))
                                            .setAuthor(m.getDiscordWebhook().getString("AuthorName"), m.getDiscordWebhook().getString("Author_URL"), m.getDiscordWebhook().getString("Avatar_Author_URL"))
                                            .setUrl(m.getDiscordWebhook().getString("Title_URL")));
                                    try {
                                        webhook.execute();
                                    } catch (MalformedURLException e) {
                                        System.out.println("[MinecraftDiscordWebhook] Invalid webhook URL");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }



                                if (card_response.get("status").getAsString().equals("00")) {
                                    if (!cardinfo.has("cardprice")) {
                                        Bukkit.getLogger().warning("§a[Thesieutoc] §cCo loi xay ra khi duyet the (thieu gia tri the), vui long bao cao cho staff TheSieuToc");
                                        Bukkit.getLogger().warning("§a[Thesieutoc-Debug] §cTen nguoi choi: " + p.getName());
                                        Bukkit.getLogger().warning("§a[Thesieutoc-Debug] §cThong tin: " + cardinfo.toString());
                                        continue;
                                    }

                                    int cardprice = cardinfo.get("cardprice").getAsInt();
                                    if (p == null || !p.isOnline()) {
                                        Bukkit.getLogger().warning("loi xay ra khi duyet the (nguoi choi da offline), ban vui long give qua = tay");
                                        Bukkit.getLogger().warning("nguoi choi: " + name);
                                        Bukkit.getLogger().warning("gia the: " + cardprice);
                                        Bukkit.getLogger().warning("the: " + cardinfo.get("cardtype").getAsString());
                                        continue;
                                    }
                                    p.sendMessage(MessageFormat.format(m.getLang("nap_the_thanh_cong"), new Object[] { Integer.valueOf(cardprice) }));
                                    napthanhcong(p, cardinfo);

                                    String playername = p.getPlayer().getName();
                                    DiscordWebhook webhook = new DiscordWebhook(m.getDiscordWebhook().getString("WebHookAPI"));
                                    webhook.setContent(m.getDiscordWebhook().getString("Content"));
                                    webhook.setAvatarUrl("https://minotar.net/helm/" + playername + "/100.png");
                                    webhook.setUsername(m.getDiscordWebhook().getString("WebhookName"));
                                    webhook.addEmbed(new DiscordWebhook.EmbedObject()
                                            .setTitle(m.getDiscordWebhook().getString("Title_ThanhCong"))
                                            .setDescription("Người chơi " + playername + " đã nạp thẻ " + cardinfo.get("cardprice").getAsString() + " thành công!!!!")
                                            .setColor(Color.RED)
                                            .addField("["+ df.format(now) +"]", "NGƯỜI CHƠI: " + playername + "", false)
                                            .addField("SERI:", "||" + cardinfo.get("seri").getAsString() +"||", true)
                                            .addField("MÃ THẺ:", "||" + cardinfo.get("pin").getAsString() +"||", true)
                                            .addField("MỆNH GIÁ:", "||" + cardinfo.get("cardprice").getAsString() +"||", true)
                                            .addField("LOẠI THẺ:", "||" + cardinfo.get("cardtype").getAsString() +"||", true)
                                            .addField("TRẠNG THÁI:", m.getDiscordWebhook().getString("Status_ThanhCong"), false)
                                            .setThumbnail(m.getDiscordWebhook().getString("Thumbnail_URL"))
                                            .setFooter(m.getDiscordWebhook().getString("FooterContent"), m.getDiscordWebhook().getString("Footer_URL"))
                                            .setImage(m.getDiscordWebhook().getString("Image_URL"))
                                            .setAuthor(m.getDiscordWebhook().getString("AuthorName"), m.getDiscordWebhook().getString("Author_URL"), m.getDiscordWebhook().getString("Avatar_Author_URL"))
                                            .setUrl(m.getDiscordWebhook().getString("Title_URL")));
                                    try {
                                        webhook.execute();
                                    } catch (MalformedURLException e) {
                                        System.out.println("[MinecraftDiscordWebhook] Invalid webhook URL");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }



                                Thesieutoc.getInstance().db.writeLog(p, cardinfo);
                                card_queue.remove(trans_id);
                                if (this.queue.containsKey(name) && ((HashMap)this.queue.get(name)).size() == 0) {
                                    this.queue.remove(name);
                                }
                            }
                        }
                    }
                }
            }

        }, 0, 100);
    }

    public void addQueue(Player p, String trans_id, JsonObject cardinfo) {
        HashMap<String, JsonObject> card_queue = (HashMap)this.queue.getOrDefault(p.getName(), new HashMap());
        card_queue.put(trans_id, cardinfo);
        this.queue.put(p.getName(), card_queue);
    }

    public void napthanhcong(Player p, JsonObject cardinfo) {
        int cardprice = cardinfo.get("cardprice").getAsInt();
        if (!this.m.getConfig().contains("card.command")) {
            Bukkit.getLogger().warning("§a[Thesieutoc] §cKhong co lenh thuc thi cho card trong config, vui long kiem tra!");
        } else {
            List<String> commands = (List<String>)(this.m.getConfig().contains("card.command." + cardprice) ? this.m.getConfig().getStringList("card.command." + cardprice) : new LinkedList());
            if (commands.isEmpty()) {
                Bukkit.getLogger().warning("§a[Thesieutoc] §cKhong co lenh thuc thi cho card menh gia §f" + cardprice + "§c VND trong config, vui long kiem tra!");
            } else {
                Task.syncTask(() -> {
                    for(String command : commands) {
                        Thesieutoc.getInstance().dispatchCommand(p, command);
                    }

                });
            }
        }

        cardinfo.addProperty("msg", "thanh cong");
        int total_charged = Thesieutoc.getInstance().db.getPlayerTotalCharged(p) + cardprice;
        Task.syncTask(() -> new PlayerCardChargedEvent(p, cardinfo.get("cardtype").getAsString(), cardprice, total_charged));
    }
}
