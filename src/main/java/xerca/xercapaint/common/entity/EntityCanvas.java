package xerca.xercapaint.common.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import xerca.xercapaint.common.CanvasType;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.item.Items;

import javax.annotation.Nullable;


public class EntityCanvas extends EntityHanging implements IEntityAdditionalSpawnData {
    private NBTTagCompound canvasNBT;
    private int tickCounter1 = 0;
    private CanvasType canvasType;

    public EntityCanvas(World world, NBTTagCompound canvasNBT, BlockPos pos, EnumFacing facing, CanvasType canvasType) {
        super(world, pos);
        this.canvasNBT = canvasNBT;
        this.canvasType = canvasType;

        this.updateFacingWithBoundingBox(facing);
    }

    public EntityCanvas(World world) {
        super(world);
    }

    public NBTTagCompound getCanvasNBT() {
        return canvasNBT;
    }

    public void setCanvasNBT(NBTTagCompound canvasNBT) {
        this.canvasNBT = canvasNBT;
    }

    @Override
    public int getWidthPixels() {
        return CanvasType.getWidth(canvasType);
    }

    @Override
    public int getHeightPixels() {
        return CanvasType.getHeight(canvasType);
    }

    @Override
    public void onBroken(@Nullable Entity brokenEntity) {
//        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            this.playSound(SoundEvents.ENTITY_PAINTING_BREAK, 1.0F, 1.0F);
            if (brokenEntity instanceof EntityPlayer) {
                EntityPlayer EntityPlayer = (EntityPlayer)brokenEntity;
                if (EntityPlayer.capabilities.isCreativeMode) {
                    return;
                }
            }
            ItemStack canvasItem;
            if(canvasType == CanvasType.SMALL){
                canvasItem = new ItemStack(Items.ITEM_CANVAS);
            }
            else if(canvasType == CanvasType.LONG){
                canvasItem = new ItemStack(Items.ITEM_CANVAS_LONG);
            }
            else if(canvasType == CanvasType.TALL){
                canvasItem = new ItemStack(Items.ITEM_CANVAS_TALL);
            }
            else if(canvasType == CanvasType.LARGE){
                canvasItem = new ItemStack(Items.ITEM_CANVAS_LARGE);
            }
            else{
                XercaPaint.LOGGER.error("Invalid canvas type");
                return;
            }
            canvasItem.setTagCompound(this.canvasNBT.copy());
            this.entityDropItem(canvasItem, 0.5f);
//        }
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.tickCounter1++ == 50 && !this.world.isRemote) {
            this.tickCounter1 = 0;
            if (!this.isDead && !this.onValidSurface()) {
                this.setDead();
                this.onBroken(null);
            }
        }

    }

    @Override
    public void playPlaceSound() {

    }

    @Override
    protected void updateBoundingBox(){
        if(canvasType != null){
            super.updateBoundingBox();
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompound) {
        this.canvasNBT = tagCompound.getCompoundTag("canvas");
        this.canvasType = CanvasType.fromByte(tagCompound.getByte("ctype"));
        super.readEntityFromNBT(tagCompound);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        tagCompound.setTag("canvas", canvasNBT);
        tagCompound.setByte("ctype", (byte)canvasType.ordinal());
        super.writeEntityToNBT(tagCompound);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        ByteBufUtils.writeTag(buffer, canvasNBT);
        buffer.writeInt(facingDirection.getIndex());
        buffer.writeByte(canvasType.ordinal());
        buffer.writeInt(hangingPosition.getX()); // this has to be written, otherwise pos gets broken
        buffer.writeInt(hangingPosition.getY()); // this has to be written, otherwise pos gets broken
        buffer.writeInt(hangingPosition.getZ()); // this has to be written, otherwise pos gets broken
//        XercaPaint.LOGGER.debug("writeSpawnData Pos: " + this.hangingPosition.toString() + " posY: " + this.posY);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        canvasNBT = ByteBufUtils.readTag(buffer);
        facingDirection = EnumFacing.VALUES[buffer.readInt()];
        canvasType = CanvasType.fromByte(buffer.readByte());
        hangingPosition = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());

        updateFacingWithBoundingBox(facingDirection);
//        XercaPaint.LOGGER.debug("readSpawnData Pos: " + this.hangingPosition.toString() + " posY: " + this.posY);
    }
}
