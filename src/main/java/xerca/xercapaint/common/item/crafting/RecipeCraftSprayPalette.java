package xerca.xercapaint.common.item.crafting;

import com.google.gson.JsonObject;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockPlanks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import xerca.xercapaint.common.item.Items;

import javax.annotation.Nullable;
import java.util.ArrayList;

@MethodsReturnNonnullByDefault
public class RecipeCraftSprayPalette extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    public RecipeCraftSprayPalette() {
        super();
    }

    private boolean isPlank(ItemStack stack){
        Item item = stack.getItem();
        if(item instanceof ItemBlock){
            ItemBlock itemBlock = (ItemBlock) item;
            return itemBlock.getBlock() instanceof BlockPlanks;
        }
        return false;
    }

    private boolean isDye(ItemStack stack){
        return stack.getItem() instanceof ItemDye;
    }

    private boolean isPlankRow(InventoryCrafting inv, int row){
        int plankCount = 0;
        for(int j = 0; j < inv.getWidth(); ++j) {
            int id = row*inv.getWidth() + j;
            ItemStack stack = inv.getStackInSlot(id);
            if(isPlank(stack)){
                plankCount++;
            }
        }
        return plankCount == 3;
    }

    private int findPlankRow(InventoryCrafting inv){
        for(int i = 0; i < inv.getHeight(); ++i) {
            if(isPlankRow(inv, i)){
                return i;
            }
        }
        return -1;
    }

    @Nullable
    private ArrayList<ItemStack> findDyes(InventoryCrafting inv, int plankRow){
        ArrayList<ItemStack> dyes = new ArrayList<>();
        for(int i = 0; i < inv.getHeight(); ++i) {
            if(i == plankRow){
                continue;
            }
            for(int j = 0; j < inv.getWidth(); ++j) {
                int id = i*inv.getWidth() + j;
                ItemStack stack = inv.getStackInSlot(id);
                if(isDye(stack)){
                    dyes.add(stack);
                }
                else if(!stack.isEmpty()){
                    return null;
                }
            }
        }
        return dyes;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        int plankRow = findPlankRow(inv);
        if(plankRow < 0){
            return false;
        }
        ArrayList<ItemStack> dyes = findDyes(inv, plankRow);
        if(dyes == null || dyes.isEmpty()){
            return false;
        }

        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        int plankRow = findPlankRow(inv);
        if(plankRow < 0){
            return ItemStack.EMPTY;
        }
        ArrayList<ItemStack> dyes = findDyes(inv, plankRow);
        if(dyes == null || dyes.isEmpty()){
            return ItemStack.EMPTY;
        }

        byte[] basicColors = new byte[16];
        for(ItemStack dye : dyes){
            basicColors[dye.getMetadata()] = 1;
        }
        ItemStack result = new ItemStack(Items.ITEM_SPRAY_PALETTE);
        NBTTagCompound tag = result.getTagCompound();
        if(tag == null){
            tag = new NBTTagCompound();
            result.setTagCompound(tag);
        }
        tag.setByteArray("basic", basicColors);
        return result;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canFit(int width, int height) {
        return width >= 3 && height >= 3;
    }

    /**
     * If true, this recipe does not appear in the recipe book and does not respect recipe unlocking (and the
     * doLimitedCrafting gamerule)
     */
    @Override
    public boolean isDynamic() {
        return true;
    }

    /**
     * Get the result of this recipe, usually for display purposes (e.g. recipe book). If your recipe has more than one
     * possible result (e.g. it's dynamic and depends on its inputs), then return an empty stack.
     */
    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    public static class Factory implements IRecipeFactory {
        @Override
        public IRecipe parse(JsonContext context, JsonObject json) {
            return new RecipeCraftSprayPalette();
        }
    }
}