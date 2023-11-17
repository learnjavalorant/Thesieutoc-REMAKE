// Decompiled with: FernFlower
// Class Version: 11
package net.thesieutoc;

import com.google.gson.TypeAdapter;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import net.thesieutoc.Thesieutoc;
import net.thesieutoc.utils.Task;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateChecker implements Listener {
    private final String url = "https://raw.githubusercontent.com/motcainit/Thesieutoc/main/version";
    String newVersion1 = "";
    boolean newVersion = false;

    public UpdateChecker() {
        Task.asyncTask(() -> {
            try {
                InputStream inputStream = (new URL("https://raw.githubusercontent.com/motcainit/Thesieutoc/main/version")).openStream();

                try {
                    Scanner scanner = new Scanner(inputStream);

                    try {
                        if (scanner.hasNext()) {
                            String a = scanner.next();
                            this.newVersion1 = a;
                            if (!Thesieutoc.getInstance().getDescription().getVersion().equals(a)) {
                                this.newVersion = true;
                                Bukkit.getConsoleSender().sendMessage("§a[Thesieutoc] Da co phien ban moi §f" + this.newVersion1 + "§a, ban hay cap nhat de fix loi!");
                            }
                        }
                    } catch (Throwable var7) {
                        try {
                            scanner.close();
                        } catch (Throwable var6) {
                            var7.addSuppressed(var6);
                        }

                        throw var7;
                    }

                    scanner.close();
                } catch (Throwable var8) {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable var5) {
                            var8.addSuppressed(var5);
                        }
                    }

                    throw var8;
                }

                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException var9) {
            }

        });
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        if (e.getPlayer().isOp() && this.newVersion) {
            e.getPlayer().sendMessage("§a[Thesieutoc] Đã có phiên bản mới §f" + this.newVersion1 + "§a, bạn hãy cập nhật để được fix lỗi!");
        }

    }
}
