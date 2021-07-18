package net.jmorg.garbageenergy.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeManager
{
    private static final RecipeManager INSTANCE = new RecipeManager();
    private final Map<String, List<ItemStack>> recipes = new HashMap<String, List<ItemStack>>();

    private static void registerRecipe(IRecipe recipe)
    {
        if (recipe == null) return;

        String outputItem = itemName(recipe.getRecipeOutput());
        List<ItemStack> outputIngredients = new ArrayList<ItemStack>();

        if (INSTANCE.recipes.containsKey(outputItem)) return;

        // TODO: fill ingredients output.
        INSTANCE.recipes.put(outputItem, outputIngredients);
    }

    public Map<String, List<ItemStack>> getRecipes()
    {
        return recipes;
    }

    public List<ItemStack> getRecipeIngredients(String recipeName)
    {
        return recipes.get(recipeName);
    }

    public static String itemName(ItemStack itemStack)
    {
        return itemStack.toString().replaceFirst("\\d*x", "");
    }

    public static boolean recipeIngredientsContain(String recipeName, ItemStack itemStack)
    {
        List<ItemStack> ingredients = INSTANCE.getRecipeIngredients(recipeName);
        List<String> ingredientsNames = new ArrayList<String>();
        for (ItemStack ingredient : ingredients) {
            if (ingredient == null) continue;
            ingredientsNames.add(itemName(ingredient));
        }
        return ingredientsNames.contains(itemName(itemStack));
    }

    public static void initialize()
    {
        for (IRecipe recipe : CraftingManager.REGISTRY)
        {
            registerRecipe(recipe);
        }
    }

    private RecipeManager()
    {

    }
}
