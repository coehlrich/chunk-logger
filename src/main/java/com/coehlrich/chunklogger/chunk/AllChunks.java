package com.coehlrich.chunklogger.chunk;

import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.annotation.Nullable;

import com.coehlrich.chunklogger.ChunkLoggerConfig;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.chunk.Chunk;

public class AllChunks {
	
	public static ArrayList<ChunkListOfPlayers> chunks = new ArrayList<ChunkListOfPlayers>();
	
	public static void playerEnteredChunk(Chunk chunk, EntityPlayerMP player) {
		ChunkListOfPlayers chunkWithPlayers = new ChunkListOfPlayers(chunk);
		if (!chunks.contains(chunkWithPlayers)) {
			chunks.add(chunkWithPlayers);
		}
		getChunk(chunk).playerEnteredChunk(player);
	}
	public static void playerLeft(Chunk chunk, EntityPlayerMP player) {
		if (getChunk(chunk) != null && getChunk(chunk).hasPlayerBeenInChunk(player)) {
			getChunk(chunk).playerLeft(player);
		}
	}
	
	@Nullable
	public static ChunkListOfPlayers getChunk(Chunk chunk2) {
		for (ChunkListOfPlayers chunk1 : chunks) {
			if (chunk1.getChunk().zPosition == chunk2.zPosition && chunk1.getChunk().xPosition == chunk2.xPosition && chunk1.getChunk().getWorld().provider.getDimension() == chunk2.getWorld().provider.getDimension()) {
				return chunk1;
			}
		}
		return null;
	}
	
	public static void addChunk(ChunkListOfPlayers chunk) {
		if (!chunks.contains(chunk)) {
			chunks.add(chunk);
		}
	}
	
	@Nullable
	public static Chunk getChunkPlayerWasLastSeenIn(EntityPlayerMP player) {
		for (ChunkListOfPlayers chunk : chunks) {
			for (PlayerInChunk playerInChunk : chunk.playersInChunk) {
				if (!playerInChunk.hasLeft() && playerInChunk.getPlayer().equals(player.getUniqueID().toString())) {
					return chunk.getChunk();
				}
			}
		}
		return null;
	}
	
	public static void onTick() {
		for (ChunkListOfPlayers chunk : chunks) {
			ArrayList<PlayerInChunk> playersToRemove = new ArrayList<PlayerInChunk>();
			for (PlayerInChunk player : chunk.getPlayersInChunk()) {
				if (player.hasLeft()) {
					LocalDateTime date = player.leaveTimeCalendar.plus(ChunkLoggerConfig.timeFromLeft);
					if (LocalDateTime.now().isAfter(date)) {
						playersToRemove.add(player);
					}
				} else {
					LocalDateTime date = player.enterTimeCalendar.plus(ChunkLoggerConfig.timeFromEnter);
					if (LocalDateTime.now().isAfter(date)) {
						playersToRemove.add(player);
					}
				}
			}
			chunk.getPlayersInChunk().removeAll(playersToRemove);
		}
	}
}