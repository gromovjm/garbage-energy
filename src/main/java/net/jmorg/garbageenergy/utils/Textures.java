package net.jmorg.garbageenergy.utils;

import cofh.core.init.CoreProps;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

public class Textures
{
    private Textures()
    {
    }

    public static void registerTextures(TextureMap map)
    {
        textureMap = map;

        blockTop = register(BLOCKS + "top_side");
        blockBottom = register(BLOCKS + "bottom_side");
        blockSide = register(BLOCKS + "side");

        configNone = register(config + "none");
        configBlue = registerCB(config + "blue");
        configRed = registerCB(config + "red");
        configYellow = registerCB(config + "yellow");
        configOrange = registerCB(config + "orange");
        configGreen = registerCB(config + "green");
        configPurple = registerCB(config + "purple");
        configOpen = register(config + "open");
        configOmni = registerCB(config + "omni");

        config = new TextureAtlasSprite[]{
                configNone,
                configBlue,
                configRed,
                configYellow,
                configOrange,
                configGreen,
                configPurple,
                configOpen,
                configOmni
        };

        // Crucible
        crucibleFace = register(GENERATOR + "crucible_face");
        crucibleFaceActive = register(GENERATOR_ACTIVE + "crucible_face");
        crucibleOpposite = register(GENERATOR + "crucible_opposite");
        crucibleOppositeActive = register(GENERATOR_ACTIVE + "crucible_opposite");
    }

    private static TextureMap textureMap;

    private static TextureAtlasSprite register(String sprite)
    {
        return textureMap.registerSprite(new ResourceLocation(sprite));
    }

    private static TextureAtlasSprite registerCB(String sprite)
    {
        if (CoreProps.enableColorBlindTextures) {
            sprite += CB_POSTFIX;
        }
        return register(sprite);
    }

//    private static TextureAtlasSprite registerAnim(String sprite)
//    {
//        if (TEProps.animatedDynamoCoilTexture) {
//            sprite += ANIM_POSTFIX;
//        }
//        return register(sprite);
//    }

//    private static final String ANIM_POSTFIX = "_anim";
    private static final String CB_POSTFIX = "_cb";

    private static final String BLOCKS = GarbageEnergy.MODID + ":blocks/";
    private static final String CONFIG = BLOCKS + "config/config_";

    private static final String GENERATOR = BLOCKS + "generator/";
    private static final String GENERATOR_ACTIVE = GENERATOR + "active_";

    /* REFERENCES */
    public static TextureAtlasSprite[] config;
    public static TextureAtlasSprite configNone;
    public static TextureAtlasSprite configBlue;
    public static TextureAtlasSprite configRed;
    public static TextureAtlasSprite configYellow;
    public static TextureAtlasSprite configOrange;
    public static TextureAtlasSprite configGreen;
    public static TextureAtlasSprite configPurple;
    public static TextureAtlasSprite configOpen;
    public static TextureAtlasSprite configOmni;

    public static TextureAtlasSprite blockTop;
    public static TextureAtlasSprite blockBottom;
    public static TextureAtlasSprite blockSide;

    // Crucible
    public static TextureAtlasSprite crucibleFace;
    public static TextureAtlasSprite crucibleFaceActive;
    public static TextureAtlasSprite crucibleOpposite;
    public static TextureAtlasSprite crucibleOppositeActive;
}
