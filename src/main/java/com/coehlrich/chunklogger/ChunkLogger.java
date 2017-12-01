package com.coehlrich.chunklogger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import com.coehlrich.chunklogger.command.GetPlayersInChunk;
import com.coehlrich.chunklogger.command.GetPlayersInChunks;
import com.coehlrich.chunklogger.proxy.CommonProxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = ChunkLogger.MODID, name = ChunkLogger.MODNAME, version = ChunkLogger.VERSION, serverSideOnly = true, acceptableRemoteVersions = "*")
public class ChunkLogger {
	
	@SidedProxy(serverSide = "com.coehlrich.chunklogger.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	public static final String MODID = "chunklogger";
	public static final String MODNAME = "Chunk Logger";
	public static final String VERSION = "1.3";
	
	public static Logger logger;
	
	@Mod.Instance(MODID)
	public static ChunkLogger instance;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		ChunkLoggerConfig.preInit(event);
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
		
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}
	
	@Mod.EventHandler
	public void serverStart(FMLServerStartingEvent event) {
		event.registerServerCommand(new GetPlayersInChunk());
		event.registerServerCommand(new GetPlayersInChunks());
	}
	
	public static void log(Level level, String message) {
		logger.log(level, message);
	}
}