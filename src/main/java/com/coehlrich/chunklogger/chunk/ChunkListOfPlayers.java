package com.coehlrich.chunklogger.chunk;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

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
		playersInChunk.add(new PlayerInChunk(player.getUniqueID().toString()));
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
			if (player.getPlayer().equals(playerToFind.getUniqueID().toString())) {
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
			Calendar enterTime = Calendar.getInstance();
			try {
				enterTime.setTimeInMillis(new SimpleDateFormat("yyyyMMWWuukkmmss").parse(nbt.getString("EnterTime")).getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String player = nbt.getString("PlayerUUID");
			if (nbt.getBoolean("HasLeft")) {
				Calendar leaveTime = Calendar.getInstance();
				try {
					leaveTime.setTimeInMillis(new SimpleDateFormat("yyyyMMWWuukkmmss").parse(nbt.getString("LeaveTime")).getTime());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				NBTTagList stayTime = nbt.getTagList("StayTime", Constants.NBT.TAG_COMPOUND);
				int howMuchToLoop = stayTime.tagCount();
				Map<TimeUnit, Long> timeMap = new LinkedHashMap<TimeUnit, Long>();
				for (int j = 0; j < howMuchToLoop; j++) {
					NBTTagCompound time = stayTime.getCompoundTagAt(j);
					timeMap.put(TimeUnit.valueOf(time.getString("TimeUnit")), time.getLong("time"));
				}
				playersInChunk.add(new PlayerInChunk(enterTime, leaveTime, timeMap, player));
			} else {
				playersInChunk.add(new PlayerInChunk(enterTime, player));
			}
		}
	}
	
	public boolean hasPlayerBeenInChunk(EntityPlayerMP playerToFind) {
		for (PlayerInChunk player : playersInChunk) {
			if (playerToFind.getUniqueID().toString().equals(player.getPlayer())) {
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<PlayerInChunk> getPlayersInChunk() {
		return playersInChunk;
	}
}