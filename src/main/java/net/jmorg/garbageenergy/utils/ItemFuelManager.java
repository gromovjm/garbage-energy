package net.jmorg.garbageenergy.utils;

import net.minecraftforge.common.config.ConfigCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ItemFuelManager
{
    static List<String> burnable = new ArrayList<String>();
    static List<Double> burningTimes = new ArrayList<Double>();

    public static void registerFuels(Set<String> fuels, ConfigCategory configs)
    {
        if (fuels.isEmpty()) return;

        for (String fuel : fuels) {
            burnable.add(fuel.replace('.', ':'));
            burningTimes.add(configs.get(fuel).getDouble(0.1));
        }
    }

    public static double getBurningTime(String name)
    {
        return burningTimes.get(burnable.indexOf(name));
    }

    public static boolean isBurnable(String name)
    {
        return burnable.contains(name);
    }
}
