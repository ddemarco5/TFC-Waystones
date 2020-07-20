package com.ddemarco5.TFCWaystones.item;

import com.ddemarco5.TFCWaystones.TFCWaystones;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.CreativeTabsTFC;
import net.dries007.tfc.objects.items.ItemTFC;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

//TODO: The life isn't successfully added to the item if the player scrolls the wheel to another item mid charge, the onPlayerStoppedUsing won't fire

public class ItemEmptyWarpStone extends ItemTFC {

    private static final int MAX_LIFE = 1000;
    private static final int USE_DURATION = 36000;  // in ticks, 30 minutes

    private static int LOCAL_CHARGE_DISP_VAL;

    public ItemEmptyWarpStone() {
        setRegistryName(TFCWaystones.MOD_ID, "empty_warp_stone");
        setUnlocalizedName(TFCWaystones.MOD_ID.toLowerCase() + "." + "empty_warp_stone");
        setCreativeTab(CreativeTabsTFC.CT_MISC);
    }

    public void registerOres(){
        OreDictionary.registerOre("warpStone", this);
        OreDictionary.registerOre("warpStoneEmpty", this);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {

        if (!worldIn.isRemote) { // remote
            TFCWaystones.logger.info("empty warpstone onclick");
            if (playerIn.getHeldItemOffhand().getItem() == TFCWaystones.OBSIDIAN_KNIFE &&
                playerIn.getHeldItemMainhand().getItem() == TFCWaystones.EMPTY_WARP_STONE) { // If we have the dagger in offhand
                playerIn.setActiveHand(handIn);
                return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
            }
            TFCWaystones.logger.info("onclick conditions not met");
            //playerIn.resetActiveHand();
        }
        return new ActionResult<>(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        if (!worldIn.isRemote) { // server code

            int life_stolen = (USE_DURATION - timeLeft) - 1; // our time used minus our max time, offset for zero index
            EntityPlayer player = (EntityPlayer)entityLiving;
            TFCWaystones.logger.info("stopped using - {}", life_stolen);

            if (life_stolen > 0) {
                TFCWaystones.logger.info("Adding {} life to waystone", life_stolen);
                addLife(stack, life_stolen);
            }

            if (getLife(stack) == MAX_LIFE) { // if we're charged give the player a warp stone
                player.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(TFCWaystones.WARP_STONE));
                //player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(TFCWaystones.WARP_STONE));
            }
        }
        else { // client code
            LOCAL_CHARGE_DISP_VAL = 0; // Clear the local display val now that we've set the charge in the stone
        }
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase playerIn, int count) {

        if (!playerIn.world.isRemote) { // on server
            int time_used = USE_DURATION - count - 1; // Index the count at 0
            //TFCWaystones.logger.info("time used: {}", time_used);
            float life_to_drain = 1.0f / TFCWaystones.TFC_HP_MOD;
            EntityPlayer player = (EntityPlayer) playerIn;
            if (playerIn.getHeldItemOffhand().getItem() == TFCWaystones.OBSIDIAN_KNIFE &&
                    playerIn.getHeldItemMainhand().getItem() == TFCWaystones.EMPTY_WARP_STONE) { // if right tools
                //if (getLife(stack) + tmp_life_stolen < MAX_LIFE) { // If the stone isn't full
                //TFCWaystones.logger.info("{} < {}",getLife(stack) + time_used, MAX_LIFE);
                if ((getLife(stack) + time_used) < MAX_LIFE) { // If the stone isn't full
                    if (!player.capabilities.isCreativeMode) {
                        // Drain our life
                        TFCWaystones.logger.info("hurting");
                        //player.attackEntityFrom(DamageSource.GENERIC, life_to_drain);
                        player.setHealth(player.getHealth() - life_to_drain);
                    }
                    //TFCWaystones.logger.info("Draining life: {}", getLife(stack));
                    // Into the stone
                    LOCAL_CHARGE_DISP_VAL = time_used;
                    //TFCWaystones.logger.info("Draining life: {}", tmp_life_stolen);
                    TFCWaystones.logger.info("Draining life: {}", count);
                    TFCWaystones.logger.info("Player hp: {}", player.getHealth());
                } else { // Stone is full
                    TFCWaystones.logger.info("Full stone detected");
                    playerIn.stopActiveHand();
                }
            }
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack itemStack) {
        return USE_DURATION;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        // Because addInformation is always run when hovering an item, we can add our nbt tag here if we don't already have it
        //if ((!stack.hasTagCompound()) || (!stack.getTagCompound().hasKey("Life"))) {
        //    makeNBT(stack);
        //}
        tooltip.add("Life: " + getLife(stack) + "/" + MAX_LIFE);
    }

    private void makeNBT(ItemStack stack) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("Life", 0);
        stack.setTagCompound(nbt);
    }

    public void addLife(ItemStack stack, int life_to_add) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Life")) {
            int currentlife = stack.getTagCompound().getInteger("Life");
            if ((life_to_add + currentlife) > MAX_LIFE) {
                stack.getTagCompound().setInteger("Life", MAX_LIFE);
            }
            else {
                stack.getTagCompound().setInteger("Life", currentlife + life_to_add); // Add life to stone
            }
        }
    }

    public int getLife(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Life")) {
            return stack.getTagCompound().getInteger("Life");
        }
        else {
            makeNBT(stack);
        }
        return 0;
    }

    public boolean isCharged(ItemStack stack) {
        return getLife(stack) == MAX_LIFE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean showDurabilityBar(ItemStack itemStack) {
        return getLife(itemStack) < MAX_LIFE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1 - ((getLife(stack) + LOCAL_CHARGE_DISP_VAL) / (double) MAX_LIFE);
    }


    @Override
    public EnumAction getItemUseAction(ItemStack itemStack) {
        return EnumAction.BOW;
    }

    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.NORMAL; // Stored in large vessels
    }

    @Nonnull
    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.MEDIUM;
    }

}
