package xerca.xercapaint.common.item.crafting;

import com.google.gson.JsonObject;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import xerca.xercapaint.common.item.ItemPalette;
import xerca.xercapaint.common.item.Items;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RecipeFillPalette extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    public RecipeFillPalette() {
        super();
    }

    private boolean isPalette(ItemStack stack){
        return stack.getItem() instanceof ItemPalette;
    }

    private boolean isDye(ItemStack stack){
        return stack.getItem() instanceof ItemDye;
    }

    @Nullable
    private int findPalette(InventoryCrafting inv){
        for(int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if(isPalette(stack)){
                return i;
            }
        }
        return -1;
    }

    @Nullable
    private ArrayList<ItemStack> findDyes(InventoryCrafting inv, int paletteId){
        ArrayList<ItemStack> dyes = new ArrayList<>();
        for(int i = 0; i < inv.getSizeInventory(); ++i) {
            if(i == paletteId){
                continue;
            }
            ItemStack stack = inv.getStackInSlot(i);
            if(isDye(stack)){
                dyes.add(stack);
            }
            else if(!stack.isEmpty()){
                return null;
            }
        }
        return dyes;
    }


    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        int paletteId = findPalette(inv);
        if(paletteId < 0){
            return false;
        }
        ArrayList<ItemStack> dyes = findDyes(inv, paletteId);
        return dyes != null && !dyes.isEmpty();
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        int paletteId = findPalette(inv);
        if(paletteId < 0){
            return ItemStack.EMPTY;
        }
        ArrayList<ItemStack> dyes = findDyes(inv, paletteId);
        if(dyes == null || dyes.isEmpty()){
            return ItemStack.EMPTY;
        }

        byte[] basicColors;
        ItemStack inputPalette = inv.getStackInSlot(paletteId);
        NBTTagCompound orgOldTag = inputPalette.getTagCompound();
        if(orgOldTag == null){
            orgOldTag = new NBTTagCompound();
            inputPalette.setTagCompound(orgOldTag);
        }

        NBTTagCompound orgTag = orgOldTag.copy();
        if(orgTag.hasKey("basic")){
            basicColors = orgTag.getByteArray("basic");
//            XercaPaint.LOGGER.debug("Basic found. Len: " + basicColors.length);
        }
        else{
            basicColors = new byte[16];
//            XercaPaint.LOGGER.debug("Basic not found. Creating");
        }

        for(ItemStack dye : dyes){
            int realColorId = dye.getMetadata();
            if(basicColors[realColorId] > 0){
//                XercaPaint.LOGGER.debug("Color already exists in palette.");
                return ItemStack.EMPTY;
            }
            basicColors[realColorId] = 1;
        }
        orgTag.setByteArray("basic", basicColors);

        ItemStack result = new ItemStack(Items.ITEM_PALETTE);
        result.setTagCompound(orgTag);
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canFit(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }
    /**
     * If true, this recipe does not appear in the recipe book and does not respect recipe unlocking (and the
     * doLimitedCrafting gamerule)
     */
    @Override
    public boolean isDynamic() {
        return true;
    }

    public static class Factory implements IRecipeFactory {
        @Override
        public IRecipe parse(JsonContext context, JsonObject json) {
            return new RecipeFillPalette();
        }
    }
}