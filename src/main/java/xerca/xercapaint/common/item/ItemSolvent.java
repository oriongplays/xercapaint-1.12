package xerca.xercapaint.common.item;

import net.minecraft.item.Item;

public class ItemSolvent extends Item {
    ItemSolvent(String name) {
        this.setRegistryName(name);
        this.setUnlocalizedName(name);
        this.setCreativeTab(Items.paintTab);
        this.setMaxStackSize(1);
    }
}