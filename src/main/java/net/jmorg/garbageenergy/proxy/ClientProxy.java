package net.jmorg.garbageenergy.proxy;

import cofh.lib.util.helpers.StringHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.jmorg.garbageenergy.crafting.RecipeManager;
import net.jmorg.garbageenergy.utils.ItemFuelManager;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class ClientProxy extends CommonProxy
{
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event)
    {
        String itemName = RecipeManager.itemName(event.itemStack);

        if (ItemFuelManager.isBurnable(itemName)) {
            double burningTile = ItemFuelManager.getBurningTime(itemName);
            event.toolTip.add(StringHelper.localize("tooltip.EMC.unique") + ": " + burningTile);
        }
    }
}
