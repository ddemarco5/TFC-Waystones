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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistryEntry;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;

public class ItemWarpStone extends net.blay09.mods.waystones.item.ItemWarpStone implements IItemSize {

    private static final ResourceLocation resourceOverride = new ResourceLocation(Waystones.MOD_ID, "warp_stone");
    private static final int numCharges = 4;

    public ItemWarpStone() {
        super();
    }

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

    @Override
    public EnumAction getItemUseAction(ItemStack itemStack) {
        return EnumAction.DRINK;
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
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        ItemStack item = super.onItemUseFinish(stack, worldIn, entityLiving);
        int chargesLeft = 0;
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Charges")) {
            chargesLeft = stack.getTagCompound().getInteger("Charges");
            stack.getTagCompound().setInteger("Charges", chargesLeft - 1);
        }
        else {
            TFCWaystones.logger.error("BAD! We don't have an NBT tag when we should!!!!");
        }

        //if ((chargesLeft - 1) <= 0) {
        //    return ItemStack.EMPTY;
        //}
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
        stack.setTagCompound(nbt);
    }


}
