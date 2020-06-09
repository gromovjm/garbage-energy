package net.jmorg.garbageenergy.common.blocks.scanner;

import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.gui.client.scanner.ItemScannerGui;
import net.jmorg.garbageenergy.common.gui.container.scanner.ItemScannerContainer;
import net.jmorg.garbageenergy.crafting.RecipeManager;
import net.jmorg.garbageenergy.utils.EnergyConfig;
import net.jmorg.garbageenergy.utils.ItemFuelManager;
import net.jmorg.garbageenergy.utils.Utils;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TileItemScanner extends TileScanner
{
    private static final int NUM_CONFIG = 0;
    private static final int[] SLOTS = new int[]{0, 1, 2};
    private static final RecipeManager recipeManager = RecipeManager.getInstance();

    public float energyModifier = 0.0F;
    public String[] item = {"", ""};
    List<ItemStack> queue = new ArrayList<ItemStack>();

    public TileItemScanner()
    {
        super(BlockScanner.Types.ITEM);
        inventory = new ItemStack[SLOTS.length];
    }

    public static void initialize()
    {
        int type = BlockScanner.Types.ITEM.ordinal();

        String category = "Scanner.Item";
        int basePower = MathHelper.clamp(GarbageEnergy.config.get(category, "BasePower", 20), 10, 500);
        GarbageEnergy.config.set(category, "BasePower", basePower);
        defaultEnergyConfig[type] = new EnergyConfig();
        defaultEnergyConfig[type].setParamsPower(basePower);

        GameRegistry.registerTileEntity(TileItemScanner.class, GarbageEnergy.MODID + ".Scanner.Item");
    }

    @Override
    protected void scan()
    {
        if (progress < 1) {
            // When scanning is started we prepare a queue.
            // On the next steps we handle the queue.
            scanItemStack(inventory[0]);
            progressMax = queue.size() * 1200;
        } else if (progress % 1200 == 0) {
            // Gets the last item form the queue.
            String itemUniqueId = Utils.getItemUniqueId(queue.get(queue.size() - 1).getItem());
            // Detect than it has configured energy modifier and return that value,
            // else we return default value.
            if (ItemFuelManager.isBurnable(itemUniqueId)) {
                energyModifier += (float) ItemFuelManager.getBurningTime(itemUniqueId);
            } else {
                energyModifier += 0.1F;
            }
            // Remove the last queue item.
            queue.remove(queue.size() - 1);
        } else if (progress >= progressMax) {
            // When we finish progress we mark this tile,
            // set it to finished state and
            // consume scanned item.
            tracker.markTime(worldObj);
            setFinished(true);
            consumeItem();
        } else {
            // Else increase the progress.
            progress += 1;
        }
    }

    protected void scanItemStack(ItemStack itemStack)
    {
        if (isCraftable(itemStack)) {
            List<ItemStack> ingredients = recipeManager.getRecipeIngredients(RecipeManager.itemName(itemStack));
            if (!ingredientsCraftedFromItem(ingredients, itemStack)) {
                for (ItemStack ingredient : ingredients) {
                    scanItemStack(ingredient);
                }
            }
        }
        queue.add(itemStack);
    }

    private boolean ingredientsCraftedFromItem(List<ItemStack> ingredients, ItemStack itemStack)
    {
        for (ItemStack ingredient : ingredients) {
            if (isCraftable(ingredient) && RecipeManager.recipeIngredientsContain(RecipeManager.itemName(ingredient), itemStack)) {
                return true;
            }
        }
        return false;
    }

    private boolean isCraftable(ItemStack itemStack)
    {
        Set<String> items = recipeManager.getRecipes().keySet();
        return items.contains(RecipeManager.itemName(itemStack));
    }

    public double getItemEnergyModifier()
    {
        return Math.floor(energyModifier * 100) / 100;
    }

    public void setFinished(boolean finished)
    {
        this.finished = finished;
    }

    protected void consumeItem()
    {
        item[0] = RecipeManager.itemName(inventory[0]);
        item[1] = inventory[0].getDisplayName();
        inventory[0] = ItemHelper.consumeItem(inventory[0]);
    }

    public void reset()
    {
        item[0] = "";
        item[1] = "";
        energyModifier = 0.0F;
        progress = 0;
        progressMax = 0;
        setFinished(false);
        if (ServerHelper.isClientWorld(worldObj)) {
            sendUpdatePacket(Side.SERVER);
        }
    }

    //
    // Gui methods.
    @Override
    public Object getGuiClient(InventoryPlayer inventory)
    {
        return new ItemScannerGui(inventory, this);
    }

    @Override
    public Object getGuiServer(InventoryPlayer inventory)
    {
        return new ItemScannerContainer(inventory, this);
    }

    @Override
    public int getScaledProgress(int scale)
    {
        if (finished) {
            return scale;
        }
        if (progressMax <= 0) {
            return 0;
        }
        return (int) (progress * scale / progressMax);
    }

    //
    // IInventory
    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        return SLOTS;
    }

    //
    // ISidedInventory
    @Override
    public int getNumConfig(int side)
    {
        return NUM_CONFIG;
    }

    //
    // NBT methods
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        finished = nbt.getBoolean("Finished");
        item[0] = nbt.getString("ItemName");
        item[1] = nbt.getString("ItemDisplayName");
        energyModifier = nbt.getFloat("EnergyModifier");
        progress = nbt.getLong("Progress");
        progressMax = nbt.getLong("ProgressMax");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        nbt.setBoolean("Finished", finished);
        nbt.setString("ItemName", item[0]);
        nbt.setString("ItemDisplayName", item[1]);
        nbt.setFloat("EnergyModifier", energyModifier);
        nbt.setLong("Progress", progress);
        nbt.setLong("ProgressMax", progressMax);
    }

    //
    // Network
    @Override
    public PacketCoFHBase getPacket()
    {
        PacketCoFHBase payload = super.getPacket();

        payload.addBool(finished);
        payload.addString(item[0]);
        payload.addString(item[1]);
        payload.addFloat(energyModifier);
        payload.addLong(progress);
        payload.addLong(progressMax);

        return payload;
    }

    @Override
    public PacketCoFHBase getGuiPacket()
    {
        PacketCoFHBase payload = super.getGuiPacket();
        payload.addLong(progress);
        payload.addLong(progressMax);
        return payload;
    }

    @Override
    public void handleTilePacket(PacketCoFHBase payload, boolean isServer)
    {
        super.handleTilePacket(payload, isServer);

        setFinished(payload.getBool());
        item[0] = payload.getString();
        item[1] = payload.getString();
        energyModifier = payload.getFloat();
        progress = payload.getLong();
        progressMax = payload.getLong();
    }

    @Override
    protected void handleGuiPacket(PacketCoFHBase payload)
    {
        super.handleGuiPacket(payload);
        progress = payload.getLong();
        progressMax = payload.getLong();
    }
}
