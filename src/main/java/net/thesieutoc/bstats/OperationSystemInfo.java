// Decompiled with: FernFlower
// Class Version: 11
package net.thesieutoc.bstats;

import com.google.gson.TypeAdapter;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.thesieutoc.bstats.os.LinuxProc;
import net.thesieutoc.bstats.os.WindowsWmic;

public class OperationSystemInfo {
    private static final Pattern SPACE_COLON_SPACE_PATTERN = Pattern.compile("\\s+:\\s");

    public static void poll() {
        String name = null;
        String version = null;

        for(String line : LinuxProc.OSINFO.read()) {
            if (line.startsWith("PRETTY_NAME") && line.length() > 13) {
                name = line.substring(13).replace('"', ' ').trim();
            }
        }

        for(String line : WindowsWmic.OS_GET_CAPTION_AND_VERSION.read()) {
            if (line.startsWith("Caption") && line.length() > 18) {
                name = line.substring(18).trim();
            } else if (line.startsWith("Version")) {
                version = line.substring(8).trim();
            }
        }

        if (name == null) {
            name = System.getProperty("os.name");
        }

        if (version == null) {
            version = System.getProperty("os.version");
        }

        String arch = System.getProperty("os.arch");
    }

    public static String queryCpuModel() {
        for(String line : LinuxProc.CPUINFO.read()) {
            String[] splitLine = SPACE_COLON_SPACE_PATTERN.split(line);
            if (splitLine[0].equals("model name") || splitLine[0].equals("Processor")) {
                return splitLine[1];
            }
        }

        for(String line : WindowsWmic.CPU_GET_NAME.read()) {
            if (line.startsWith("Name")) {
                return line.substring(5).trim();
            }
        }

        return "";
    }
}
