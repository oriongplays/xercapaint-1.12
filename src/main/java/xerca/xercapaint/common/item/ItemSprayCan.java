package xerca.xercapaint.common.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import xerca.xercapaint.common.CanvasType;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.entity.EntityTransparentCanvas;
import xerca.xercapaint.common.packets.SpraySoundPacket;

public class ItemSprayCan extends Item {
    ItemSprayCan(String name) {
        this.setRegistryName(name);
        this.setUnlocalizedName(name);
        this.setCreativeTab(Items.paintTab);
        this.setMaxStackSize(1);
        this.setMaxDamage(3);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing,
                                      float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItemDamage() >= stack.getMaxDamage()) {
            return EnumActionResult.FAIL;
        }
        BlockPos placePos = pos.offset(facing);
        if (facing != EnumFacing.DOWN && facing != EnumFacing.UP && player.canPlayerEdit(placePos, facing, stack)) {
            NBTTagCompound tag = new NBTTagCompound();
            int area = CanvasType.getWidth(CanvasType.SMALL) * CanvasType.getHeight(CanvasType.SMALL);
            tag.setIntArray("pixels", new int[area]);
            tag.setString("name", player.getUniqueID().toString() + "_" + (System.currentTimeMillis() / 1000));
            tag.setInteger("v", 0);
            tag.setBoolean("transparent", true);
            EntityTransparentCanvas entity = new EntityTransparentCanvas(worldIn, tag, placePos, facing);
            if (entity.onValidSurface()) {
                if (!worldIn.isRemote) {
                    worldIn.spawnEntity(entity);
                } else {
                    ItemStack off = player.getHeldItemOffhand();
                    NBTTagCompound palette = off.getTagCompound();
                    XercaPaint.proxy.showSprayGui(player, tag, palette);
                }
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.FAIL;
    }
    
    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
        if (entityLiving.world.isRemote) {
            XercaPaint.network.sendToServer(new SpraySoundPacket(SpraySoundPacket.SoundType.SHAKE));
        }
        return super.onEntitySwing(entityLiving, stack);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        int remaining = stack.getMaxDamage() - stack.getItemDamage();
        tooltip.add(TextFormatting.GRAY + I18n.translateToLocalFormatted("spray_can.uses", String.valueOf(remaining)));
    }
}