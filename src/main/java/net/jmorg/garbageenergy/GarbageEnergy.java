package net.jmorg.garbageenergy;

import cofh.mod.BaseMod;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.jmorg.garbageenergy.common.bloks.generator.BlockGenerator;
import net.jmorg.garbageenergy.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = GarbageEnergy.MODID, name = GarbageEnergy.MODNAME, version = GarbageEnergy.VERSION, dependencies = GarbageEnergy.DEPENDENCIES)
public class GarbageEnergy extends BaseMod
{
    public static final String MODID = "GarbageEnergy";
    public static final String MODNAME = "Garbage Energy";
    public static final String VERSION = "0.1.0";
    public static final String DEPENDENCIES = "required-after:Forge@[10.13.4.1448,10.14);";
    @Mod.Instance(MODID)
    public static GarbageEnergy instance;
    @SidedProxy(clientSide = "net.jmorg.garbageenergy.proxy.ClientProxy", serverSide = "net.jmorg.garbageenergy.proxy.ServerProxy")
    public static CommonProxy proxy;
    public static final Logger log = LogManager.getLogger(MODID);

    public GarbageEnergy()
    {
        super(log);
    }

    public static final CreativeTabs garbageEnergyTab = new CreativeTabs("garbageEnergy")
    {
        @Override
        public Item getTabIconItem()
        {
            return Item.getItemById(331);
        }
    };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit();
    }

    public synchronized void handleIdMapping()
    {
        BlockGenerator.refreshItemStacks();
    }

    @Override
    public String getModId()
    {
        return MODID;
    }

    @Override
    public String getModName()
    {
        return MODNAME;
    }

    @Override
    public String getModVersion()
    {
        return VERSION;
    }
}
