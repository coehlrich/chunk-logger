package com.coehlrich.chunklogger.proxy;

import com.coehlrich.chunklogger.event.EventHandlerCommon;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class CommonProxy {
	
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new EventHandlerCommon());
	}	
}