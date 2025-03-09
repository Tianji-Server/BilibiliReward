package flyproject.bili;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static flyproject.bili.Color.color;

public class YAML {
    public static String getData(UUID uuid) {
        File dataFile = new File(BilibiliReward.instance.getDataFolder(), "data.yml");
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(dataFile);
        ConfigurationSection dataSection = configuration.getConfigurationSection("data");
        return dataSection != null ? dataSection.getString(uuid.toString()) : null;
    }

    public static boolean hasData(UUID uuid) {
        File dataFile = new File(BilibiliReward.instance.getDataFolder(), "data.yml");
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(dataFile);
        ConfigurationSection dataSection = configuration.getConfigurationSection("data");
        return dataSection != null && dataSection.contains(uuid.toString());
    }

    public static void setData(UUID uuid, String data) {
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(new File(BilibiliReward.instance.getDataFolder() + "/data.yml"));
        configuration.set("data." + uuid.toString(), data);
        try {
            configuration.save(new File(BilibiliReward.instance.getDataFolder() + "/data.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<UUID, String> getAll() {
        Map<UUID, String> map = new HashMap<>();
        File dataFile = new File(BilibiliReward.instance.getDataFolder(), "data.yml");
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(dataFile);
        ConfigurationSection dataSection = configuration.getConfigurationSection("data");
        if (dataSection == null) {
            return map;
        }

        for (String uuid : dataSection.getKeys(false)) {
            try {
                map.put(UUID.fromString(uuid), configuration.getString("data." + uuid));
            } catch (IllegalArgumentException e) {
                BilibiliReward.instance.getLogger().warning(color("无效的 UUID 格式: " + uuid));
            }
        }
        return map;
    }
}
