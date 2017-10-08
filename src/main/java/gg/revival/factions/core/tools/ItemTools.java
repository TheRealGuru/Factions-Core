package gg.revival.factions.core.tools;

import gg.revival.factions.core.FC;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Iterator;

public class ItemTools {

    @Getter private FC core;

    public ItemTools(FC core) {
        this.core = core;
    }

    /**
     * Deletes a crafting recipe from the server
     * @param item The items crafting recipe to be deleted
     */
    public void deleteRecipe(ItemStack item) {
        Iterator<Recipe> recipeIterator = core.getServer().recipeIterator();

        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();

            if (recipe != null && recipe.getResult().getType().equals(item.getType()) && recipe.getResult().getDurability() == item.getDurability()) {
                recipeIterator.remove();
            }
        }
    }

}
