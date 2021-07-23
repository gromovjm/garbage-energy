package net.jmorg.garbageenergy;

import cofh.CoFHCore;
import cofh.core.util.ConfigHandler;
import cofh.thermalfoundation.ThermalFoundation;
import net.jmorg.garbageenergy.common.blocks.GarbageEnergyBlock;
import net.jmorg.garbageenergy.common.gui.GuiHandler;
import net.jmorg.garbageenergy.crafting.RecipeManager;
import net.jmorg.garbageenergy.network.PacketCore;
import net.jmorg.garbageenergy.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = GarbageEnergy.MODID, name = GarbageEnergy.MODNAME, guiFactory = GarbageEnergy.MODGUIFACTORY, version = GarbageEnergy.VERSION, dependencies = GarbageEnergy.DEPENDENCIES, updateJSON = GarbageEnergy.UPDATE_URL, customProperties = @Mod.CustomProperty(k = "cofhversion", v = "true"))
public class GarbageEnergy
{
    public static final String MODID = "garbageenergy";
    public static final String MODNAME = "Garbage Energy";
    public static final String MODGUIFACTORY = "net.jmorg.garbageenergy.common.gui.GuiFactory";
    public static final String VERSION = "2.1.3";
    public static final String VERSION_MAX = "2.2.0";
    public static final String VERSION_GROUP = "required-after:" + MODID + "@[" + VERSION + "," + VERSION_MAX + ");";
    public static final String DEPENDENCIES = CoFHCore.VERSION_GROUP + ThermalFoundation.VERSION_GROUP;
    public static final String UPDATE_URL = "https://raw.github.com/gromovjm/garbage-energy/mc1.12.2/update.json";

    @Mod.Instance(MODID)
    public static GarbageEnergy instance;

    @SidedProxy(clientSide = "net.jmorg.garbageenergy.proxy.ClientProxy", serverSide = "net.jmorg.garbageenergy.proxy.ServerProxy")
    public static CommonProxy proxy;

    public static final Logger log = LogManager.getLogger(MODID);

    public static final ConfigHandler config = new ConfigHandler(VERSION);
    public static final ConfigHandler configClient = new ConfigHandler(VERSION);

    public static final GuiHandler guiHandler = new GuiHandler();

    public GarbageEnergy()
    {
        // Mod constructor
    }

    public static final CreativeTabs garbageEnergyTab = new CreativeTabs(MODID)
    {
        @Override
        public ItemStack getTabIconItem()
        {
            return GarbageEnergyBlock.generator.crucible;
        }
    };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        GEProperties.configDir = event.getModConfigurationDirectory();

        config.setConfiguration(new Configuration(new File(GEProperties.configDir, "jmorg/garbageenergy/common.cfg"), true));
        configClient.setConfiguration(new Configuration(new File(GEProperties.configDir, "jmorg/garbageenergy/client.cfg"), true));

        GEProperties.initialize();
        PacketCore.initialize();

        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void initialize(FMLInitializationEvent event)
    {
        proxy.initialize(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event)
    {
        config.cleanUp(false, true);
        configClient.cleanUp(false, true);

        log.info(GarbageEnergy.MODNAME + ": Load Complete.");
    }

    @Mod.EventHandler
    public void serverStart(FMLServerAboutToStartEvent event)
    {
        GEProperties.server = event.getServer();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartedEvent event)
    {
        RecipeManager.initialize();
    }
}
