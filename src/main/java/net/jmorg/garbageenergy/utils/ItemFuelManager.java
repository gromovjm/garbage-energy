package net.jmorg.garbageenergy.utils;

import com.google.common.base.CaseFormat;
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
            burningTimes.add(configs.get(fuel).getDouble(0.1));
            burnable.add(parseFuelName(fuel));
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

    private static String parseFuelName(String fuel)
    {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fuel).replace('.', '@').replace('_', '.');
    }
}
