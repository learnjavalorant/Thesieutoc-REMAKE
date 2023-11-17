// Decompiled with: FernFlower
// Class Version: 11
package net.thesieutoc.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.Map;
import net.thesieutoc.Thesieutoc;
import org.bukkit.Bukkit;

public class Task {
    public static void syncTask(Runnable r, int tick) {
        Bukkit.getScheduler().runTaskLater(Thesieutoc.getInstance(), r, (long)tick);
    }

    public static void syncTask(Runnable r, int start, int tick) {
        Bukkit.getScheduler().runTaskTimer(Thesieutoc.getInstance(), r, (long)start, (long)tick);
    }

    public static void syncTask(Runnable r) {
        Bukkit.getScheduler().runTask(Thesieutoc.getInstance(), r);
    }

    public static void asyncTask(Runnable r, int tick) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(Thesieutoc.getInstance(), r, (long)tick);
    }

    public static void asyncTask(Runnable r, int start, int tick) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Thesieutoc.getInstance(), r, (long)start, (long)tick);
    }

    public static void asyncTask(Runnable r) {
        Bukkit.getScheduler().runTaskAsynchronously(Thesieutoc.getInstance(), r);
    }
}
