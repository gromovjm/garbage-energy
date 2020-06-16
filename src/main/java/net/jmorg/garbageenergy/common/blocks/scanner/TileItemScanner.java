package net.jmorg.garbageenergy.common.blocks.scanner;

import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.MathHelper;
import cpw.mods.fml.common.registry.GameRegistry;
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
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class TileItemScanner extends TileScanner
{
    private static final int NUM_CONFIG = 0;
    private static final int[] SLOTS = new int[]{0, 1, 2};
    private static final RecipeManager recipeManager = RecipeManager.getInstance();

    public float itemEnergyModifier = 0.0F;
    public String[] item = {"", ""};
    List<String> queue = new ArrayList<String>();

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
            item[0] = RecipeManager.itemName(inventory[0]);
            item[1] = inventory[0].getDisplayName();
            progressMax = queue.size() * 1200;
            ecc = 5;
        } else if (progress % 1200 == 0) {
            // Gets the last item form the queue.
            String itemUniqueId = queue.get(queue.size() - 1);
            // Detect than it has configured energy modifier and return that value,
            // else we return default value.
            if (ItemFuelManager.isBurnable(itemUniqueId)) {
                itemEnergyModifier += (float) ItemFuelManager.getBurningTime(itemUniqueId);
            } else {
                itemEnergyModifier += 0.1F;
            }
            // Remove the last queue item.
            queue.remove(queue.size() - 1);
        } else if (progress >= progressMax) {
            // When we finish progress we mark this tile,
            // set it to finished state and
            // consume scanned item.
            setFinished(true);
            tracker.markTime(worldObj);
            inventory[0] = ItemHelper.consumeItem(inventory[0]);
            ecc = 0;
        }
        // Else increase the progress.
        progress += 1;
        ecc = 1;
    }

    @Override
    protected boolean check()
    {
        return !finished && inventory[0] == null;
    }

    protected void scanItemStack(ItemStack itemStack)
    {
        if (isCraftable(itemStack)) {
            List<ItemStack> ingredients = recipeManager.getRecipeIngredients(RecipeManager.itemName(itemStack));
            if (ingredientsCraftedFromItem(ingredients, itemStack)) {
                queue.add(Utils.getItemUniqueId(itemStack.getItem()));
            } else {
                for (ItemStack ingredient : ingredients) {
                    scanItemStack(ingredient);
                }
            }
        } else {
            queue.add(Utils.getItemUniqueId(itemStack.getItem()));
        }
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

    @Override
    public void resetResult(boolean send)
    {
        queue = new ArrayList<String>();
        itemEnergyModifier = 0.0F;
        item[0] = "";
        item[1] = "";
        progress = 0;
        progressMax = 0;
        super.resetResult(send);
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
        itemEnergyModifier = nbt.getFloat("ItemEnergyModifier");
        ecc = nbt.getInteger("EnergyMod");
        progress = nbt.getLong("Progress");
        progressMax = nbt.getLong("ProgressMax");

        String stringQueue = nbt.getString("Queue");
        if (!stringQueue.isEmpty()) {
            queue = new ArrayList<String>(Arrays.asList(stringQueue.split("/")));
        } else {
            queue = new ArrayList<String>();
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        nbt.setBoolean("Finished", finished);
        nbt.setString("ItemName", item[0]);
        nbt.setString("ItemDisplayName", item[1]);
        nbt.setFloat("ItemEnergyModifier", itemEnergyModifier);
        nbt.setInteger("EnergyMod", ecc);
        nbt.setLong("Progress", progress);
        nbt.setLong("ProgressMax", progressMax);
        nbt.setString("Queue", String.join("/", queue));
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
        payload.addFloat(itemEnergyModifier);
        payload.addInt(ecc);
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
        itemEnergyModifier = payload.getFloat();
        ecc = payload.getInt();
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
