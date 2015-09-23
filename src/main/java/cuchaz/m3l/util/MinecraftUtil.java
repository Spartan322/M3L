/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.util;

public class MinecraftUtil {
    public static final int BlockNumberMaxVanilla = 255; // reserved for vanilla
    public static final int BlockNumberMin = BlockNumberMaxVanilla + 1;
    public static final int BlockNumberMax = 4095;
    public static final int BlockNumberSize = BlockNumberMax - BlockNumberMin + 1;

    public static final int ItemNumberMaxVanilla = 2400; // reserved for vanilla
    public static final int ItemNumberMinBlock = 2401;
    public static final int ItemNumberMaxBlock = ItemNumberMinBlock + BlockNumberSize - 1;
    public static final int ItemNumberMin = ItemNumberMaxBlock + 1;
    public static final int ItemNumberMax = Short.MAX_VALUE;
    public static final int ItemNumberSize = ItemNumberMax - ItemNumberMin + 1;
    public static final int ItemNumberOffsetFromBlock = ItemNumberMinBlock - BlockNumberMin;

    public static boolean isVanillaBlock(int blockNumber) {
        return blockNumber >= 0 && blockNumber <= BlockNumberMaxVanilla;
    }

    public static boolean isModBlock(int blockNumber) {
        return blockNumber >= BlockNumberMin && blockNumber <= BlockNumberMax;
    }

    public static boolean isModItemBlock(int itemNumber) {
        return itemNumber >= ItemNumberMinBlock && itemNumber <= ItemNumberMaxBlock;
    }

    public static int getModBlockNumberFromModItemNumber(int itemNumber) {
        return itemNumber - ItemNumberOffsetFromBlock;
    }

    public static int getModItemNumberFromModBlockNumber(int blockNumber) {
        return blockNumber + ItemNumberOffsetFromBlock;
    }
}
