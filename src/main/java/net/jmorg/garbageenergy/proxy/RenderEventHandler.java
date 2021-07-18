package net.jmorg.garbageenergy.proxy;

import net.jmorg.garbageenergy.utils.Textures;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RenderEventHandler
{
    public static final RenderEventHandler INSTANCE = new RenderEventHandler();

    @SubscribeEvent
    public void handleTextureStitchPreEvent(TextureStitchEvent.Pre event)
    {
        Textures.registerTextures(event.getMap());
    }
}
