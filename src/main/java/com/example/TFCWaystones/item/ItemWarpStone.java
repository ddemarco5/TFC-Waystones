package com.example.TFCWaystones.item;

import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;


import javax.swing.*;

public class ItemWarpStone extends net.blay09.mods.waystones.item.ItemWarpStone {

    @Override
    public EnumAction getItemUseAction(ItemStack itemStack) {
        return EnumAction.DRINK;
    }

}
