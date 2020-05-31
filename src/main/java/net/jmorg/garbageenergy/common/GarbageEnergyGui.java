package net.jmorg.garbageenergy.common;

import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;

import java.util.ArrayList;
import java.util.List;

public class GarbageEnergyGui extends GuiConfig
{
    public GarbageEnergyGui(GuiScreen parentScreen)
    {
        super(parentScreen, getConfigElements(parentScreen), GarbageEnergy.MODID, false, false, GarbageEnergy.MODNAME);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static List<IConfigElement> getConfigElements(GuiScreen parent)
    {
        List<IConfigElement> list = new ArrayList<IConfigElement>();

        list.add(new DummyCategoryElement("Client", "config.Client", getClientConfigElements()));
        list.add(new DummyCategoryElement("Common", "config.Common", getCommonConfigElements()));

        return list;
    }

    @SuppressWarnings("rawtypes")
    private static List<IConfigElement> getClientConfigElements()
    {
        List<IConfigElement> list = new ArrayList<IConfigElement>();
        list.add(new ConfigElement<ConfigCategory>(GarbageEnergy.configClient.getCategory("Generator")));
        return list;
    }

    @SuppressWarnings("rawtypes")
    private static List<IConfigElement> getCommonConfigElements()
    {
        List<IConfigElement> list = new ArrayList<IConfigElement>();
        list.add(new ConfigElement<ConfigCategory>(GarbageEnergy.config.getCategory("Generator")));
        return list;
    }
}
