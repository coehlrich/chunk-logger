package com.coehlrich.chunklogger.chunk;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;

public class PlayerInChunk {
	public LocalDateTime enterTimeCalendar = LocalDateTime.now();
	public LocalDateTime leaveTimeCalendar;
	public UUID playerInChunk;
	public boolean hasleft = false;
	public Duration stayTime;
	
	public PlayerInChunk(UUID player) {
		playerInChunk = player;
	}
	
	public PlayerInChunk(LocalDateTime clock, UUID player) {
		enterTimeCalendar = clock;
		playerInChunk = player;
		hasleft = false;
	}
	
	public PlayerInChunk(LocalDateTime entertime, LocalDateTime leavetime, Duration staytime, UUID player) {
		enterTimeCalendar = entertime;
		leaveTimeCalendar = leavetime;
		stayTime = staytime;
		playerInChunk = player;
		hasleft = true;
	}
	
	public UUID getPlayer() {
		return playerInChunk;
	}
	
	@Nullable
	public LocalDateTime getLeaveTimeCalendar() {
		return leaveTimeCalendar;
	}
	
	@Nullable
	public LocalDateTime getEnterTimeCalendar() {
		return enterTimeCalendar;
	}
	
	public void playerLeft() {
		leaveTimeCalendar = LocalDateTime.now();
		stayTime = Duration.between(enterTimeCalendar, leaveTimeCalendar);
		hasleft = true;
	}
	
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setLong("enterTime", enterTimeCalendar.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		nbt.setUniqueId("PlayerUUID", playerInChunk);
		nbt.setBoolean("HasLeft", hasleft);
		if (hasleft) {
			nbt.setLong("LeaveTime", leaveTimeCalendar.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
			nbt.setLong("StayTime", stayTime.toMillis());
		}
	}
	
	public boolean hasLeft() {
		return hasleft;
	}
	
	public Duration getStayTime() {
		return stayTime;
	}
}
