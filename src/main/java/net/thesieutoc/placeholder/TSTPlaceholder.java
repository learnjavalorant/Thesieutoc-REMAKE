// Decompiled with: CFR 0.152
// Class Version: 11
package net.thesieutoc.placeholder;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.thesieutoc.Thesieutoc;
import net.thesieutoc.utils.Task;
import net.thesieutoc.utils.Utils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TSTPlaceholder
        extends PlaceholderExpansion {
    Thesieutoc m;
    HashMap<String, Map<String, Integer>> cache = new HashMap();
    HashMap<String, Long> cache_gay = new HashMap();

    public TSTPlaceholder(Thesieutoc m) {
        this.m = m;
        Task.asyncTask(() -> {
            for (String placeholder : new HashSet<String>(this.cache_gay.keySet())) {
                if (System.currentTimeMillis() - this.cache_gay.get(placeholder) < 60000L) continue;
                this.cache.remove(placeholder);
                this.cache_gay.remove(placeholder);
            }
        }, 0, 1200);
    }

    public boolean persist() {
        return true;
    }

    public boolean canRegister() {
        return true;
    }

    @NotNull
    public String getAuthor() {
        String string = this.m.getDescription().getAuthors().toString();
        if (string == null) {
            TSTPlaceholder.$$$reportNull$$$0(0);
        }
        return string;
    }

    @NotNull
    public String getIdentifier() {
        return "tst";
    }

    @NotNull
    public String getVersion() {
        String string = this.m.getDescription().getVersion();
        if (string == null) {
            TSTPlaceholder.$$$reportNull$$$0(1);
        }
        return string;
    }

    public String onPlaceholderRequest(Player p, @NotNull String placeholder) {
        long lums;
        if (placeholder == null) {
            TSTPlaceholder.$$$reportNull$$$0(2);
        }
        if (p == null) {
            return "";
        }
        placeholder = placeholder.toLowerCase();
        DecimalFormat value_format = new DecimalFormat(this.m.getLang("placeholder.value_format"));
        String[] args = placeholder.split("_");
        Date date = new Date();
        String type = args[0];
        String supcua = type.equals("top") || type.equals("total") ? "" : p.getName();
        long startDate = 0L;
        long endDate = 0L;
        int index = 0;
        if (args.length > 1) {
            switch (args[1]) {
                case "today": {
                    startDate = this.m.cmd_tst.day(date).get(0) / 1000L;
                    endDate = this.m.cmd_tst.day(date).get(1) / 1000L;
                    break;
                }
                case "month": {
                    startDate = this.m.cmd_tst.month(date).get(0) / 1000L;
                    endDate = this.m.cmd_tst.month(date).get(1) / 1000L;
                    break;
                }
                case "year": {
                    startDate = this.m.cmd_tst.year().get(0) / 1000L;
                    endDate = this.m.cmd_tst.year().get(1) / 1000L;
                    break;
                }
            }
            try {
                index = Integer.parseInt(args[args.length - 1]);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if ((lums = this.cache_gay.getOrDefault(placeholder, 0L).longValue()) < System.currentTimeMillis()) {
            this.cache.put(placeholder, this.m.tsttop.get(supcua, startDate, endDate));
            this.cache_gay.put(placeholder, System.currentTimeMillis() + 3000L);
        }
        Map<String, Integer> cashcharged = this.cache.getOrDefault(placeholder, this.m.tsttop.get(supcua, startDate, endDate));
        this.cache.put(placeholder, cashcharged);
        if (type.equals("top")) {
            if (index == 0) {
                return "Placeholder thiếu giá trị hạng #";
            }
            HashMap<Integer, String> dx = Utils.sortByComparator(cashcharged, false, index);
            String wow = dx.getOrDefault(index, "");
            int cursed = cashcharged.getOrDefault(wow, 0);
            String placeholder_top_format = wow.isEmpty() ? this.m.getLang("placeholder.top_empty") : this.m.getLang("placeholder.top");
            return MessageFormat.format(placeholder_top_format, index, wow, value_format.format(cursed));
        }
        int total = 0;
        for (String key : cashcharged.keySet()) {
            total += cashcharged.get(key).intValue();
        }
        String placeholder_format = this.m.getLang("placeholder." + type);
        return MessageFormat.format(placeholder_format, total);
    }

    private static void $$$reportNull$$$0(int n) {
        RuntimeException runtimeException;
        Object[] objectArray;
        Object[] objectArray2;
        int n2;
        String string;
        switch (n) {
            default: {
                string = "@NotNull method %s.%s must not return null";
                break;
            }
            case 2: {
                string = "Argument for @NotNull parameter '%s' of %s.%s must not be null";
                break;
            }
        }
        switch (n) {
            default: {
                n2 = 2;
                break;
            }
            case 2: {
                n2 = 3;
                break;
            }
        }
        Object[] objectArray3 = new Object[n2];
        switch (n) {
            default: {
                objectArray2 = objectArray3;
                objectArray3[0] = "net/thesieutoc/placeholder/TSTPlaceholder";
                break;
            }
            case 2: {
                objectArray2 = objectArray3;
                objectArray3[0] = "placeholder";
                break;
            }
        }
        switch (n) {
            default: {
                objectArray = objectArray2;
                objectArray2[1] = "getAuthor";
                break;
            }
            case 1: {
                objectArray = objectArray2;
                objectArray2[1] = "getVersion";
                break;
            }
            case 2: {
                objectArray = objectArray2;
                objectArray2[1] = "net/thesieutoc/placeholder/TSTPlaceholder";
                break;
            }
        }
        switch (n) {
            default: {
                break;
            }
            case 2: {
                objectArray = objectArray;
                objectArray[2] = "onPlaceholderRequest";
                break;
            }
        }
        String string2 = String.format(string, objectArray);
        switch (n) {
            default: {
                runtimeException = new IllegalStateException(string2);
                break;
            }
            case 2: {
                runtimeException = new IllegalArgumentException(string2);
                break;
            }
        }
        throw runtimeException;
    }
}
