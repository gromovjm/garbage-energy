package net.jmorg.garbageenergy;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.jmorg.garbageenergy.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

@Mod(modid=GarbageEnergy.MODID, name=GarbageEnergy.MODNAME, version=GarbageEnergy.VERSION)
public class GarbageEnergy
{
    public static final String MODID = "garbageenergy";
    public static final String MODNAME = "Garbage Energy";
    public static final String VERSION = "0.1.0";

    @Mod.Instance(MODID)
    public static GarbageEnergy instance;

    @SidedProxy(clientSide="net.jmorg.garbageenergy.proxy.ClientProxy", serverSide="net.jmorg.garbageenergy.proxy.ServerProxy")
    public static CommonProxy proxy;

    public static final CreativeTabs garbageEnergyTab = new CreativeTabs("garbageEnergy")
    {
        @Override
        public Item getTabIconItem() { return Item.getItemById(331); }
    };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit(event);
    }
}
