// Decompiled with: FernFlower
// Class Version: 11
package net.thesieutoc.bstats.os;

import com.google.gson.TypeAdapter;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public enum WindowsWmic {
    CPU_GET_NAME("wmic", "cpu", "get", "name", "/FORMAT:list"),
    OS_GET_CAPTION_AND_VERSION("wmic", "os", "get", "caption,version", "/FORMAT:list");

    private static final boolean SUPPORTED = System.getProperty("os.name").startsWith("Windows");
    private final String[] cmdArgs;

    private WindowsWmic(String... cmdArgs) {
        this.cmdArgs = cmdArgs;
    }

    public List<String> read() {
        if (SUPPORTED) {
            ProcessBuilder process = (new ProcessBuilder(this.cmdArgs)).redirectErrorStream(true);

            try {
                BufferedReader buf = new BufferedReader(new InputStreamReader(process.start().getInputStream()));

                try {
                    List<String> lines = new ArrayList();

                    String line;
                    while((line = buf.readLine()) != null) {
                        lines.add(line);
                    }

                    buf.close();
                    return lines;
                } catch (Throwable var7) {
                    try {
                        buf.close();
                    } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                    }

                    throw var7;
                }
            } catch (Exception var8) {
            }
        }

        return Collections.emptyList();
    }
}
