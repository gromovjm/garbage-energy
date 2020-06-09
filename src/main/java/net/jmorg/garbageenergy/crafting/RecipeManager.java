package net.jmorg.garbageenergy.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.*;

public class RecipeManager
{
    private static final RecipeManager instance = new RecipeManager();
    private final Map<String, List<ItemStack>> recipes = new HashMap<String, List<ItemStack>>();

    private RecipeManager()
    {

    }

    public static void initialize()
    {
        for (Object recipe : CraftingManager.getInstance().getRecipeList()) {
            registerRecipe((IRecipe) recipe);
        }
    }

    public static RecipeManager getInstance()
    {
        return instance;
    }

    private static void registerRecipe(IRecipe recipe)
    {
        if (recipe == null) return;
        if (recipe.getRecipeOutput() == null) return;

        String outputItem = itemName(recipe.getRecipeOutput());
        List<ItemStack> outputIngredients = new ArrayList<ItemStack>();
        List ingredients = null;

        if (recipe instanceof ShapedRecipes) {
            ingredients = Arrays.asList(((ShapedRecipes) recipe).recipeItems);
        } else if (recipe instanceof ShapelessRecipes) {
            ingredients = ((ShapelessRecipes) recipe).recipeItems;
        } else if (recipe instanceof ShapedOreRecipe) {
            ingredients = Arrays.asList(((ShapedOreRecipe) recipe).getInput());
        } else if (recipe instanceof ShapelessOreRecipe) {
            ingredients = ((ShapelessOreRecipe) recipe).getInput();
        }

        if (ingredients == null || instance.recipes.containsKey(outputItem)) return;

        for (Object ingredient : ingredients) {
            if (ingredient instanceof ItemStack) {
                outputIngredients.add((ItemStack) ingredient);
            } else if (ingredient != null) {
                outputIngredients.add((ItemStack) ((List) ingredient).get(0));
            }
        }

        instance.recipes.put(outputItem, outputIngredients);
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
        return itemStack.toString().replaceFirst("\\dx", "");
    }

    public static boolean recipeIngredientsContain(String recipeName, ItemStack itemStack)
    {
        List<ItemStack> ingredients = instance.getRecipeIngredients(recipeName);
        List<String> ingredientsNames = new ArrayList<String>();
        for (ItemStack ingredient : ingredients) {
            if (ingredient == null) continue;
            ingredientsNames.add(itemName(ingredient));
        }
        return ingredientsNames.contains(itemName(itemStack));
    }
}
