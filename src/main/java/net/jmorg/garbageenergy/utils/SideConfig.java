package net.jmorg.garbageenergy.utils;

public class SideConfig
{
    /* Number of Side Configs */
    public int numConfig;

    /* Slot Groups accessibble per Config */
    public int[][] slotGroups;

    /* Whether or not the SIDE allows insertion */
    public boolean[] allowInsertionSide;

    /* Whether or not the SIDE allows extraction */
    public boolean[] allowExtractionSide;

    /* Whether or not the SLOT allows input */
    public boolean[] allowInsertionSlot;

    /* Whether or not the SLOT allows extraction */
    public boolean[] allowExtractionSlot;

    /* Config Textures to use on Sides */
    public int[] sideTex;

    /* Default Side configuration for freshly placed block */
    public byte[] defaultSides;
}
