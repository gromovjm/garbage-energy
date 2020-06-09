package net.jmorg.garbageenergy.proxy;

import cofh.lib.util.helpers.StringHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.jmorg.garbageenergy.utils.ItemFuelManager;
import net.jmorg.garbageenergy.utils.Utils;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class ClientProxy extends CommonProxy
{
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event)
    {
        String itemName = Utils.getItemUniqueId(event.itemStack.getItem());

        if (ItemFuelManager.isBurnable(itemName)) {
            double burningTile = ItemFuelManager.getBurningTime(itemName);
            event.toolTip.add(StringHelper.localize("tooltip.itemRgGenerationFromItem") + ": " + burningTile);
        }
    }
}
