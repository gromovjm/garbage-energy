package net.jmorg.garbageenergy;

import cofh.core.init.CoreProps;
import cofh.core.network.PacketBase;
import net.jmorg.garbageenergy.network.PacketCore;
import net.minecraft.server.MinecraftServer;

import java.io.File;

public class GEProperties
{
    public static File configDir;
    public static MinecraftServer server;

    /**
     * Configs
     */
    public static boolean creativeTabShowAllBlockLevels = false;
    public static boolean creativeTabShowCreative = false;
    public static int creativeTabLevel = 0;
    public static final String INTERFACE_CAT = "Interface.CreativeTabs";
    public static final String CRUCIBLE_GENERATOR_CAT = "Generator.Crucible";

    /**
     * Textures path's
     */
    public static final String TEXTURES_GUI_PATH = GarbageEnergy.MODID + ":textures/gui/";

    /**
     * Generator augments
     */
    public static final String ATTENUATE_MODIFIER = "attenuateModifier";
    public static final String ENERGY_AMPLIFIER = "energyAmplifier";

    public static void initialize()
    {
        configClient();
    }

    private static void configClient()
    {
        String comment;

        comment = "Set the default level for the Blocks shown in the Creative Tab, if all levels are not shown.";
        creativeTabLevel = GarbageEnergy.configClient.getConfiguration().getInt("DefaultLevel", INTERFACE_CAT, creativeTabLevel, CoreProps.LEVEL_MIN, CoreProps.LEVEL_MAX, comment);

        comment = "If TRUE, all regular levels for a given Block will show in the Creative Tab.";
        creativeTabShowAllBlockLevels = GarbageEnergy.configClient.get(INTERFACE_CAT, "ShowAllBlockLevels", creativeTabShowAllBlockLevels, comment);

        comment = "If TRUE, Creative version of Blocks will show in the Creative Tab.";
        creativeTabShowCreative = GarbageEnergy.configClient.get(INTERFACE_CAT, "ShowCreativeBlocks", creativeTabShowCreative, comment);
    }

    public static void handleConfigSync(PacketCore payload)
    {
        // null
    }

    public static PacketBase getConfigSync()
    {
        return PacketCore.getPacket(PacketCore.Packet.CONFIG_SYNC);
    }

    private GEProperties()
    {

    }
}
