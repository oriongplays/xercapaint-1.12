package xerca.xercapaint.common;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xerca.xercapaint.common.item.Items;

public class PaintCreativeTab extends CreativeTabs {
    public PaintCreativeTab() {
        super("paint_tab");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getTabIconItem() {
        return new ItemStack(Items.ITEM_PALETTE);
    }
}
