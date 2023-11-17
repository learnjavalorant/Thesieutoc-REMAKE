// Decompiled with: CFR 0.152
// Class Version: 11
package net.thesieutoc.command;

import com.google.gson.JsonObject;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import me.clip.placeholderapi.PlaceholderAPI;
import net.thesieutoc.Thesieutoc;
import net.thesieutoc.card.CardPrice;
import net.thesieutoc.utils.Task;
import net.thesieutoc.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Command_tst
        implements CommandExecutor {
    Thesieutoc m = Thesieutoc.getInstance();

    public Command_tst() {
        this.m.cmd_tst = this;
    }

    public static Calendar getCalendar(Date date) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static void setTimeToBeginningOfDay(Calendar calendar) {
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
    }

    public static void setTimeToEndofDay(Calendar calendar) {
        calendar.set(11, 23);
        calendar.set(12, 59);
        calendar.set(13, 59);
        calendar.set(14, 999);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String a, String[] args) {
        if (sender == null) {
            Command_tst.$$$reportNull$$$0(0);
        }
        if (cmd == null) {
            Command_tst.$$$reportNull$$$0(1);
        }
        if (a == null) {
            Command_tst.$$$reportNull$$$0(2);
        }
        if (args.length == 0) {
            sender.sendMessage("§e/thesieutoc give [tên người chơi] [mệnh giá]§f: Nạp cho người chơi số tiền tương ứng");
            sender.sendMessage("§e/thesieutoc top§f: Xem top nạp thẻ");
            sender.sendMessage("§e/thesieutoc reload§f: Tải lại các file config.");
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("thesieutoc.admin") && sender instanceof Player) {
                sender.sendMessage("§cBạn không có quyền để sử dụng lệnh này!");
                return false;
            }
            this.m.reloadConfig();
            this.m.reloadLang();
            this.m.reloadMenu();
            this.m.reloadNapTheoMoc();
            this.m.reloadKey();
            this.m.reloadDiscordWebhook();
            sender.sendMessage("§eReload config TheSieuToc thanh cong!");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("test")) {
            sender.sendMessage(PlaceholderAPI.setPlaceholders((Player)((Object)sender), args[1]));
        }
        if (args.length >= 1 && args[0].equalsIgnoreCase("give")) {
            if (sender.hasPermission("thesieutoc.admin") || !(sender instanceof Player)) {
                switch (args.length) {
                    case 2: {
                        sender.sendMessage("§c/thesieutoc give [tên người chơi] [mệnh giá]§f: Nạp cho người chơi số tiền tương ứng");
                        break;
                    }
                    case 3: {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target != null) {
                            int menhgia = Integer.parseInt(args[2]);
                            if (CardPrice.getPrice(menhgia).getId() == -1) {
                                sender.sendMessage("§cMệnh giá không hợp lệ!");
                                return false;
                            }
                            JsonObject json = new JsonObject();
                            json.addProperty("cardtype", "GIVE");
                            json.addProperty("cardprice", menhgia);
                            json.addProperty("seri", "0");
                            json.addProperty("pin", "0");
                            json.addProperty("msg", "thanh cong");
                            this.m.WEB_CALLBACK.napthanhcong(target, json);
                            Thesieutoc.getInstance().db.writeLog(target, json);
                            sender.sendMessage("§aNạp thành công §f" + menhgia + "§a VNĐ cho " + target.getName() + "!");
                            break;
                        }
                        sender.sendMessage("§cNgười chơi §e" + args[1] + "§c không online!");
                        break;
                    }
                }
            } else {
                sender.sendMessage("§cBạn không có quyền để sử dụng lệnh này!");
                return false;
            }
        }
        if (args.length >= 1 && args[0].equalsIgnoreCase("top")) {
            if (!sender.hasPermission("thesieutoc.admin") && !sender.hasPermission("thesieutoc.top") && sender instanceof Player) {
                sender.sendMessage("§cBạn không có quyền để sử dụng lệnh này!");
                return false;
            }
            Task.asyncTask(() -> this.top(sender, args));
        }
        return true;
    }

    public void top(CommandSender sender, String[] args) {
        block1 : switch (args.length) {
            case 1: {
                this.sendHelp(sender);
                break;
            }
            case 2: {
                switch (args[1].toLowerCase()) {
                    case "help": {
                        this.sendHelp(sender);
                        break block1;
                    }
                    case "today": {
                        Date date = new Date();
                        long startDate = this.day(date).get(0) / 1000L;
                        long endDate = this.day(date).get(1) / 1000L;
                        this.printTop(sender, startDate, endDate);
                        break block1;
                    }
                    case "month": {
                        Date date = new Date();
                        long startDate = this.month(date).get(0) / 1000L;
                        long endDate = this.month(date).get(1) / 1000L;
                        this.printTop(sender, startDate, endDate);
                        break block1;
                    }
                    case "year": {
                        long startDate = this.year().get(0) / 1000L;
                        long endDate = this.year().get(1) / 1000L;
                        this.printTop(sender, startDate, endDate);
                        break block1;
                    }
                    case "total": {
                        this.printTop(sender, 0L, 0L);
                        break block1;
                    }
                }
                SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    Date date = dateformat.parse(args[1]);
                    long startDate = this.day(date).get(0) / 1000L;
                    long endDate = this.day(date).get(1) / 1000L;
                    this.printTop(sender, startDate, endDate);
                    break;
                }
                catch (ParseException e) {
                    sender.sendMessage("§cThời gian không hợp lệ!");
                }
            }
        }
    }

    public void sendHelp(CommandSender sender) {
        sender.sendMessage("§e/thesieutoc top total:§f Tính tổng top từ trước đến nay");
        sender.sendMessage("§e/thesieutoc top today:§f Tính top của hôm nay");
        sender.sendMessage("§e/thesieutoc top month:§f Tính top của tháng này");
        sender.sendMessage("§e/thesieutoc top year:§f Tính top của năm nay");
        sender.sendMessage("§e/thesieutoc top [ngày/tháng/năm]:§f Tính top của ngày được chỉ định");
    }

    public void printTop(CommandSender sender, long startDate, long endDate) {
        Task.asyncTask(() -> {
            Map<String, Integer> cashcharged = this.m.tsttop.get("ALL", startDate, endDate);
            if (cashcharged.isEmpty()) {
                sender.sendMessage(this.m.getLang("top_empty"));
                return;
            }
            HashMap<Integer, String> top = Utils.sortByComparator(cashcharged, false, 10);
            String format = this.m.getLang("placeholder.top");
            for (int t : top.keySet()) {
                sender.sendMessage(MessageFormat.format(format, t, top.get(t), new DecimalFormat("#,###").format(cashcharged.get(top.get(t)))));
            }
        });
    }

    public List<Long> day(Date date) {
        LinkedList<Long> dd = new LinkedList<Long>();
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);
        dd.add(date.getTime());
        date.setHours(23);
        date.setMinutes(59);
        date.setSeconds(59);
        dd.add(date.getTime());
        return dd;
    }

    public List<Long> month(Date date) {
        LinkedList<Long> dd = new LinkedList<Long>();
        Calendar calendar = Command_tst.getCalendar(date);
        calendar.set(5, calendar.getActualMinimum(5));
        Command_tst.setTimeToBeginningOfDay(calendar);
        Date beginning = calendar.getTime();
        calendar = Command_tst.getCalendar(date);
        calendar.set(5, calendar.getActualMaximum(5));
        Command_tst.setTimeToEndofDay(calendar);
        Date end = calendar.getTime();
        dd.add(this.day(beginning).get(0));
        dd.add(this.day(end).get(1));
        return dd;
    }

    public List<Long> year() {
        LinkedList<Long> dd = new LinkedList<Long>();
        Calendar cal = Calendar.getInstance();
        cal = Calendar.getInstance();
        cal.set(6, 1);
        Date yearStartDate = cal.getTime();
        dd.add(this.day(yearStartDate).get(0));
        cal.set(6, cal.getActualMaximum(6));
        Date yearEndDate = cal.getTime();
        dd.add(this.day(yearEndDate).get(1));
        return dd;
    }

    private static void $$$reportNull$$$0(int n) {
        Object[] objectArray;
        Object[] objectArray2 = new Object[3];
        switch (n) {
            default: {
                objectArray = objectArray2;
                objectArray2[0] = "sender";
                break;
            }
            case 1: {
                objectArray = objectArray2;
                objectArray2[0] = "cmd";
                break;
            }
            case 2: {
                objectArray = objectArray2;
                objectArray2[0] = "a";
                break;
            }
        }
        objectArray[1] = "net/thesieutoc/command/Command_tst";
        objectArray[2] = "onCommand";
        throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", objectArray));
    }
}
