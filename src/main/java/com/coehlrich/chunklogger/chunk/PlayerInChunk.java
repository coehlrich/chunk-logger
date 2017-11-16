package com.coehlrich.chunklogger.chunk;

import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

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
