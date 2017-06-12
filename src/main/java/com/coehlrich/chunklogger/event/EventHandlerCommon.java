package com.coehlrich.chunklogger.event;

import java.util.ArrayList;

import com.coehlrich.chunklogger.chunk.AllChunks;
import com.coehlrich.chunklogger.chunk.ChunkListOfPlayers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.EntityEvent.EnteringChunk;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class EventHandlerCommon {
	
	@SubscribeEvent
	public void onChunkSave(ChunkDataEvent.Save event) {
		ChunkListOfPlayers chunk = AllChunks.getChunk(event.getChunk());
		if (chunk != null) {
			NBTTagList nbt = new NBTTagList();
			AllChunks.getChunk(event.getChunk()).writeToNBT(nbt);;
			event.getData().setTag("PlayerInChunk", nbt);
		}
	}
	
	@SubscribeEvent
	public void onChunkLoad(ChunkDataEvent.Load event) {
		if (event.getData().hasKey("PlayerInChunk")) {
			ChunkListOfPlayers chunk = new ChunkListOfPlayers(event.getChunk());
			AllChunks.addChunk(chunk);
			chunk.readFromNBT(event.getData().getTagList("PlayerInChunk", Constants.NBT.TAG_COMPOUND));
		}
	}
	
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		EntityPlayerMP player = (EntityPlayerMP) event.player;
		Chunk oldChunk = AllChunks.getChunkPlayerWasLastSeenIn(player);
		int newChunkX = event.player.getPosition().getX() >> 4;
		int newChunkZ = event.player.getPosition().getZ() >> 4;
		Chunk newChunk = new Chunk(event.player.worldObj, (int) newChunkX, (int) newChunkZ);
		if (oldChunk == null || !(oldChunk.zPosition == newChunk.zPosition && oldChunk.xPosition == newChunk.xPosition &&oldChunk.getWorld().provider.getDimension() == newChunk.getWorld().provider.getDimension())) {
			AllChunks.playerEnteredChunk(newChunk, player);
			if (oldChunk != null) {
				AllChunks.playerLeft(oldChunk, player);
			}
		}
	}
	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		AllChunks.onTick();
	}
}