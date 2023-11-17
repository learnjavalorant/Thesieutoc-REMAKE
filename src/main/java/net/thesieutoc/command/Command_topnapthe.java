// Decompiled with: FernFlower
// Class Version: 11
package net.thesieutoc.command;

import com.google.gson.TypeAdapter;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.Map;
import net.thesieutoc.Thesieutoc;
import net.thesieutoc.menu.Menu_topnap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_topnapthe implements CommandExecutor {
    Thesieutoc m = Thesieutoc.getInstance();

    public boolean onCommand(CommandSender sender, Command cmd, String a, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cCommand chi xai duoc trong game!");
            return false;
        } else {
            Player p = (Player)sender;
            if (p.hasPermission("thesieutoc.top")) {
                if (this.m.getConfig().getBoolean("menu", true)) {
                    p.openInventory(Menu_topnap.get());
                } else {
                    this.m.cmd_tst.printTop(sender, 0L, 0L);
                }
            } else {
                sender.sendMessage("§cBạn không có quyền để sử dụng lệnh này!");
            }

            return true;
        }
    }
}
