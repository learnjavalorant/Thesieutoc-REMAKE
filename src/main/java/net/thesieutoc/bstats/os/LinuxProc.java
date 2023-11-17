// Decompiled with: FernFlower
// Class Version: 11
package net.thesieutoc.bstats.os;

import com.google.gson.TypeAdapter;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public enum LinuxProc {
    CPUINFO("/proc/cpuinfo"),
    MEMINFO("/proc/meminfo"),
    NET_DEV("/proc/net/dev"),
    OSINFO("/etc/os-release");

    private final Path path;

    private LinuxProc(String path) {
        this.path = resolvePath(path);
    }

    private static Path resolvePath(String path) {
        try {
            Path p = Paths.get(path);
            if (Files.isReadable(p)) {
                return p;
            }
        } catch (Exception var2) {
        }

        return null;
    }

    public List<String> read() {
        if (this.path != null) {
            try {
                return Files.readAllLines(this.path, StandardCharsets.UTF_8);
            } catch (IOException var2) {
            }
        }

        return Collections.emptyList();
    }
}
