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
import xerca.xercapaint.common.item.ItemSprayPalette;
import xerca.xercapaint.common.item.Items;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RecipeFillSprayPalette extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    public RecipeFillSprayPalette() {
        super();
    }

    private boolean isPalette(ItemStack stack){
        return stack.getItem() instanceof ItemSprayPalette;
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

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        int paletteId = findPalette(inv);
        if(paletteId < 0){
            return false;
        }
        ArrayList<ItemStack> dyes = findDyes(inv, paletteId);
        return dyes != null && !dyes.isEmpty();
    }

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
        }
        else{
            basicColors = new byte[16];
        }

        for(ItemStack dye : dyes){
            int realColorId = dye.getMetadata();
            if(basicColors[realColorId] > 0){
                return ItemStack.EMPTY;
            }
            basicColors[realColorId] = 1;
        }
        orgTag.setByteArray("basic", basicColors);

        ItemStack result = new ItemStack(Items.ITEM_SPRAY_PALETTE);
        result.setTagCompound(orgTag);
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
    }

    @Override
    public boolean canFit(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    public static class Factory implements IRecipeFactory {
        @Override
        public IRecipe parse(JsonContext context, JsonObject json) {
            return new RecipeFillSprayPalette();
        }
    }
}
