package xerca.xercapaint.common.item;

import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemHangingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import xerca.xercapaint.common.CanvasType;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.entity.EntityCanvas;

import javax.annotation.Nullable;

public class ItemCanvas extends ItemHangingEntity {
    private CanvasType canvasType;

    ItemCanvas(String name, CanvasType canvasType) {
        super(EntityCanvas.class);
        this.setRegistryName(name);
        this.setUnlocalizedName(name);
        this.setCreativeTab(Items.paintTab);
        this.setMaxStackSize(1);
        this.canvasType = canvasType;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
                ItemStack stack = playerIn.getHeldItem(hand);
        if (!playerIn.canPlayerEdit(playerIn.getPosition().down(), EnumFacing.UP, stack)) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        XercaPaint.proxy.showCanvasGui(playerIn);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

        ItemStack itemstack = player.getHeldItem(hand);
        BlockPos blockpos = pos.offset(facing);

        if (facing != EnumFacing.DOWN && facing != EnumFacing.UP && player.canPlayerEdit(blockpos, facing, itemstack))
        {
            NBTTagCompound tag = itemstack.getTagCompound();
            if(tag == null || !tag.hasKey("pixels") || !tag.hasKey("name")){
                return EnumActionResult.SUCCESS;
            }

            EntityCanvas test = new EntityCanvas(worldIn, tag, blockpos, facing, canvasType);

            if (test.onValidSurface())
            {
                if (!worldIn.isRemote)
                {
                    if (MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(player, test)))
                    {
                        return EnumActionResult.FAIL;
                    }
                    ItemStack original = player.getHeldItem(hand);
                    player.setHeldItem(hand, new ItemStack(net.minecraft.init.Items.ITEM_FRAME));
                    EnumActionResult res = net.minecraft.init.Items.ITEM_FRAME.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
                    player.setHeldItem(hand, original);
                    if (res != EnumActionResult.SUCCESS)
                    {
                        return EnumActionResult.FAIL;
                    }
                    for (EntityItemFrame frame : worldIn.getEntitiesWithinAABB(EntityItemFrame.class, new AxisAlignedBB(blockpos)))
                    {
                        if (frame.getHangingPosition().equals(blockpos) && frame.getHorizontalFacing() == facing)
                        {
                            frame.setDead();
                        }
                    }
                    EntityCanvas entityCanvas = new EntityCanvas(worldIn, tag, blockpos, facing, canvasType);
                    entityCanvas.playPlaceSound();
                    worldIn.spawnEntity(entityCanvas);
                }

                itemstack.shrink(1);
            }

            return EnumActionResult.SUCCESS;
        }
        else
        {
            return EnumActionResult.FAIL;
        }
    }

    public int getWidth() {
        return CanvasType.getWidth(canvasType);
    }

    public int getHeight() {
        return CanvasType.getHeight(canvasType);
    }

    public CanvasType getCanvasType() {
        return canvasType;
    }

}
