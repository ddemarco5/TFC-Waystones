package com.ddemarco5.TFCWaystones.item;

import com.ddemarco5.TFCWaystones.TFCWaystones;
import com.ddemarco5.TFCWaystones.gui.GuiHandler;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.CreativeTabsTFC;
import net.dries007.tfc.objects.items.ItemTFC;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class ItemObsidianRock extends ItemTFC {

    public ItemObsidianRock() {
        setRegistryName(TFCWaystones.MOD_ID, "obsidian_rock");
        setUnlocalizedName(TFCWaystones.MOD_ID.toLowerCase() + "." + "obsidian_rock");
        setCreativeTab(CreativeTabsTFC.CT_ROCK_ITEMS);
    }

    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.SMALL; // Stored everywhere
    }

    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.VERY_LIGHT; // Stacksize = 64
    }

    public void registerOres() {
        OreDictionary.registerOre("rock", this);
        OreDictionary.registerOre("rockObsidian", this);
    }


    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {

        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote && !playerIn.isSneaking() && stack.getCount() > 1)
        {
            //TFCGuiHandler.openGui(worldIn, playerIn.getPosition(), playerIn, TFCGuiHandler.Type.KNAPPING_STONE);
            //playerIn.openGui(TFCWaystones.getInstance(), new KnappingType(1,false).ordinal(), worldIn, 0, 0, 0);
            TFCWaystones.logger.info("Yep");
            GuiHandler.openGui(worldIn, playerIn, GuiHandler.Type.KNAPPING_OBSIDIAN);
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        //return super.onItemRightClick(worldIn, playerIn, handIn);
    }

}
