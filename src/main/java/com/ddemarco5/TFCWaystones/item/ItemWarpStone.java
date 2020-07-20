package com.ddemarco5.TFCWaystones.item;

import com.ddemarco5.TFCWaystones.TFCWaystones;
import net.blay09.mods.waystones.Waystones;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistryEntry;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;

//TODO: We need a better way to detect a teleportation other than a fall listener.

@Mod.EventBusSubscriber
public class ItemWarpStone extends net.blay09.mods.waystones.item.ItemWarpStone implements IItemSize {

    private static final ResourceLocation resourceOverride = new ResourceLocation(Waystones.MOD_ID, "warp_stone");
    private static final int numCharges = 4;

    public void patchResource() {

        // Turbo hack. Forge won't let us set the registryName twice, so we have to use reflection to force a change
        // All because they use setRegistryName in the constructor
        try {
            Field resourcelocationField = IForgeRegistryEntry.Impl.class.getDeclaredField("registryName");
            resourcelocationField.setAccessible(true);
            resourcelocationField.set(this, resourceOverride);
            TFCWaystones.logger.info("Reflection patched resource {} to {}", this.toString(), resourceOverride.toString());

        } catch (Exception e) {
            TFCWaystones.logger.error("FOOK: {}", e);
        }

    }

    /*
    @Override
    public EnumAction getItemUseAction(ItemStack itemStack) {
        return EnumAction.DRINK;
    }
    */

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

    public void registerOres(){
        OreDictionary.registerOre("warpStone", this);
        OreDictionary.registerOre("warpStoneCharged", this);
    }

    // Set NBT tag when the item is crafted
    @Override
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        super.onCreated(stack, worldIn, playerIn);
        makeNBT(stack);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (playerIn.getHeldItemMainhand().getItem() == Waystones.itemWarpStone) { // can only use in main hand
            return super.onItemRightClick(worldIn, playerIn, handIn);
        }
        return new ActionResult<>(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        ItemStack item = super.onItemUseFinish(stack, worldIn, entityLiving);
        setTeleported(stack, true);

        /*
        int chargesLeft = 0;
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Charges")) {
            chargesLeft = stack.getTagCompound().getInteger("Charges");
            stack.getTagCompound().setInteger("Charges", chargesLeft - 1);
        }
        else {
            TFCWaystones.logger.error("BAD! We don't have an NBT tag when we should!!!!");
        }
        */


        return item;

    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
        if (player.capabilities.isCreativeMode) {
            TFCWaystones.logger.info("In creative mode, adding charges this way");
            makeNBT(item);
        }
        return super.onDroppedByPlayer(item, player);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        // Because addinformation is always run when hovering an item, we can add our nbt tag here if we don't already have it
        if ((!stack.hasTagCompound()) || (!stack.getTagCompound().hasKey("Charges"))) {
            makeNBT(stack);
        }
        tooltip.add("Charges: " + stack.getTagCompound().getInteger("Charges"));
    }

    private void makeNBT(ItemStack stack) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("Charges", numCharges);
        nbt.setBoolean("Teleported", false);
        stack.setTagCompound(nbt);
    }

    private static void setTeleported(ItemStack stack, boolean bool) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Teleported")) {
            //TFCWaystones.logger.info("Setting teleported tag to: {}", bool);
            boolean tag = stack.getTagCompound().getBoolean("Teleported");
            stack.getTagCompound().setBoolean("Teleported", bool);
        }
    }

    //// These two functions are a workaround for waystone charges.
    //// You NEED the waystone's exact waystone item in your hand in order to teleport, but it always puts you
    //// a little above the ground, so we can detect the fall event and change our item there
    // This one is for survival mode
    @SubscribeEvent
    public static void fallEventSurvival(LivingFallEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof EntityPlayer) {
            //TFCWaystones.logger.info("Teleported, Survival!");
            handleWarpStone((EntityPlayer) entity);
        }
    }

    // This one is for creative
    @SubscribeEvent
    public static void fallEventCreative(PlayerFlyableFallEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof EntityPlayer) {
            //TFCWaystones.logger.info("Teleported, Creative!");
            handleWarpStone((EntityPlayer) entity);
        }
    }

    private static void handleWarpStone(EntityPlayer player) {
        ItemStack item = player.getHeldItemMainhand();
        NBTTagCompound tagcompound = item.getTagCompound();
        if (item.hasTagCompound() && tagcompound.hasKey("Charges") && tagcompound.hasKey("Teleported")) {
            boolean teleported = tagcompound.getBoolean("Teleported");
            if ((item.getItem() instanceof ItemWarpStone) && teleported) { // We're holding the stone and we teleported
                TFCWaystones.logger.info("Checking warpstone...");
                if (item.getTagCompound().getInteger("Charges") <= 1) { // We used our last charge
                    TFCWaystones.logger.info("Ur out of charges bich");
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(TFCWaystones.EMPTY_WARP_STONE));
                }
                else { // We had charges and we use one
                    TFCWaystones.logger.info("used a charge");
                    int chargesLeft = item.getTagCompound().getInteger("Charges");
                    item.getTagCompound().setInteger("Charges", chargesLeft - 1);
                }
                setTeleported(item, false);
            }
        }
    }


}
