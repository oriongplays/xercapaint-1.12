package xerca.xercapaint.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import xerca.xercapaint.common.CanvasType;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.item.Items;

import javax.annotation.Nullable;

public class EntityTransparentCanvas extends EntityCanvas {
    private boolean vulnerable = false;
    private static final double OFFSET = -0.9D / 16D;
    public EntityTransparentCanvas(World world, NBTTagCompound tag, BlockPos pos, EnumFacing facing) {
        super(world, tag, pos, facing, CanvasType.SMALL);
    }

    public EntityTransparentCanvas(World world) {
        super(world);
    }

    @Override
    protected void updateFacingWithBoundingBox(EnumFacing facing) {
        super.updateFacingWithBoundingBox(facing);
        double dx = facing.getFrontOffsetX() * OFFSET;
        double dy = facing.getFrontOffsetY() * OFFSET;
        double dz = facing.getFrontOffsetZ() * OFFSET;
        this.posX += dx;
        this.posY += dy;
        this.posZ += dz;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
    }

    @Override
    public void onBroken(@Nullable Entity brokenEntity) {
        // Do not drop canvas item
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            if (MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(player, this))) {
                return true;
            }
            BlockPos attach = this.getHangingPosition().offset(this.getHorizontalFacing().getOpposite());
            ItemStack original = player.getHeldItem(hand);
            player.setHeldItem(hand, new ItemStack(net.minecraft.init.Items.ITEM_FRAME));
            EnumActionResult res = net.minecraft.init.Items.ITEM_FRAME.onItemUse(player, world, attach, hand, this.getHorizontalFacing(), 0.5F, 0.5F, 0.5F);
            player.setHeldItem(hand, original);
            if (res != EnumActionResult.SUCCESS) {
                return true;
            }
            for (EntityItemFrame frame : world.getEntitiesWithinAABB(EntityItemFrame.class, new AxisAlignedBB(this.getHangingPosition()))) {
                if (frame.getHangingPosition().equals(this.getHangingPosition()) && frame.getHorizontalFacing() == this.getHorizontalFacing()) {
                    frame.setDead();
                }
            }
        }
        ItemStack main = player.getHeldItem(hand);
        ItemStack off = player.getHeldItemOffhand();
        if (main.getItem() == Items.ITEM_SOLVENT) {
            if (!world.isRemote) {
                main.shrink(1);
            }
            vulnerable = true;
            return true;
        }
        if (main.getItem() == Items.ITEM_SPRAY_CAN && off.getItem() == Items.ITEM_SPRAY_PALETTE) {
            if (world.isRemote) {
                NBTTagCompound palette = off.getTagCompound();
                XercaPaint.proxy.showSprayGui(player, this.getCanvasNBT(), palette, this.getHangingPosition());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (!vulnerable) {
            return false;
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setBoolean("vulnerable", vulnerable);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        vulnerable = tagCompound.getBoolean("vulnerable");
    }
    
    @Override
    public int getBrightnessForRender() {
        return 0xF000F0; // Full brightness to avoid darkening inside blocks
    }
}