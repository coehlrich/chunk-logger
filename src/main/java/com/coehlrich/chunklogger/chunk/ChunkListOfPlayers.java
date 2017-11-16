package com.coehlrich.chunklogger.chunk;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.Constants;

public class ChunkListOfPlayers {
	public Chunk chunk;
	public ArrayList<PlayerInChunk> playersInChunk = new ArrayList<PlayerInChunk>();
	
	public ChunkListOfPlayers(Chunk chunk2) {
		chunk = chunk2;
	}
	
	public void playerEnteredChunk(EntityPlayerMP player) {
		playersInChunk.add(new PlayerInChunk(EntityPlayer.getUUID(player.getGameProfile())));
	}
	
	public void playerLeft(EntityPlayerMP player) {
		PlayerInChunk player2 = getPlayer(player);
		if (player2 != null) {
			player2.playerLeft();
		}
	}
	
	@Nullable
	public PlayerInChunk getPlayer(EntityPlayerMP playerToFind) {
		ArrayList<PlayerInChunk> playersInChunkReversed = (ArrayList<PlayerInChunk>) playersInChunk.clone();
		Collections.reverse(playersInChunkReversed);
		for (PlayerInChunk player : playersInChunkReversed) {
			if (player.getPlayer().equals(playerToFind.getGameProfile().getId())) {
				return player;
			}
		}
		return null;
	}
	
	public Chunk getChunk() {
		return chunk;
	}
	
	public void writeToNBT(NBTTagList nbt) {
		for (PlayerInChunk player : playersInChunk) {
			NBTTagCompound playerNBT = new NBTTagCompound();
			player.writeToNBT(playerNBT);
			nbt.appendTag(playerNBT);
		}
	}
	
	public void readFromNBT(NBTTagList listOfPlayers) {
		int howManyTimesToLoop = listOfPlayers.tagCount();
		for (int i = 0; i < howManyTimesToLoop; i++) {
			NBTTagCompound nbt = listOfPlayers.getCompoundTagAt(i);
			LocalDateTime enterTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(nbt.getLong("enterTime")), ZoneId.systemDefault());
			UUID player = nbt.getUniqueId("PlayerUUID");
			if (nbt.getBoolean("HasLeft")) {
				LocalDateTime leaveTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(nbt.getLong("LeaveTime")), ZoneId.systemDefault());
				Duration stayTime = Duration.ofMillis(nbt.getLong("StayTime"));
				playersInChunk.add(new PlayerInChunk(enterTime, leaveTime, stayTime, player));
			} else {
				playersInChunk.add(new PlayerInChunk(enterTime, player));
			}
		}
	}
	
	public boolean hasPlayerBeenInChunk(EntityPlayerMP playerToFind) {
		for (PlayerInChunk player : playersInChunk) {
			if (playerToFind.getUniqueID().equals(player.getPlayer())) {
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<PlayerInChunk> getPlayersInChunk() {
		return playersInChunk;
	}
}