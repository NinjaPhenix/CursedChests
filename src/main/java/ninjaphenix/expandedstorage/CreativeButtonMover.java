package ninjaphenix.expandedstorage;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class CreativeButtonMover implements ClientModInitializer
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final File FOLDER = new File(FabricLoader.getInstance().getConfigDirectory(), "fabric");
    public static Integer x;
    public static Integer y;

    public static void loadValues()
    {
        if (!createConfigDirectory())
        {
            x = 170;
            y = 4;
            return;
        }
        try (FileInputStream inputStream = new FileInputStream(new File(FOLDER, "ninjaphenix-item-groups-v0.properties")))
        {
            Properties props = new Properties();
            props.load(inputStream);
            x = Integer.valueOf((String) props.computeIfAbsent("x_position", (v) -> "170"));
            y = Integer.valueOf((String) props.computeIfAbsent("y_position", (v) -> "4"));
        }
        catch (IOException e)
        {
            LOGGER.info("[ninjaphenix-creativebuttonmover] Failed to read from ninjaphenix-item-groups-v0 config.");
            x = 170;
            y = 4;
        }
        saveValues();
    }

    public static void saveValues()
    {
        if (!createConfigDirectory()) { return; }
        File configFile = new File(FOLDER, "ninjaphenix-item-groups-v0.properties");
        Properties props = new Properties();
        props.put("x_position", x.toString());
        props.put("y_position", y.toString());
        try (FileOutputStream outputStream = new FileOutputStream(configFile))
        {
            props.store(outputStream, "Fabric creative button positions.");
        }
        catch (IOException e)
        {
            LOGGER.info("[ninjaphenix-creativebuttonmover] Failed to save values to ninjaphenix-item-groups-v0 config.");
        }
    }

    public static boolean createConfigDirectory()
    {
        if (!FOLDER.exists())
            if (!FOLDER.mkdir())
            {
                LOGGER.info("[ninjaphenix-creativebuttonmover] Failed to make fabric sub-directory in config folder.");
                return false;
            }
        return true;
    }

    @Override
    public void onInitializeClient() { loadValues(); }
}
