package com.coehlrich.chunklogger.chunk;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class PlayerInChunk {
	public Calendar enterTimeCalendar = Calendar.getInstance();
	public Calendar leaveTimeCalendar;
	public String playerInChunk;
	public boolean hasleft = false;
	public Map<TimeUnit, Long> stayTime;
	
	public PlayerInChunk(String player) {
		playerInChunk = player;
	}
	
	public PlayerInChunk(Calendar entertime, String player) {
		enterTimeCalendar = entertime;
		playerInChunk = player;
		hasleft = false;
	}
	
	public PlayerInChunk(Calendar entertime, Calendar leavetime, Map<TimeUnit, Long> staytime, String player) {
		enterTimeCalendar = entertime;
		leaveTimeCalendar = leavetime;
		stayTime = staytime;
		playerInChunk = player;
		hasleft = true;
	}
	
	public String getPlayer() {
		return playerInChunk;
	}
	
	@Nullable
	public Calendar getLeaveTimeCalendar() {
		return leaveTimeCalendar;
	}
	
	@Nullable
	public Calendar getEnterTimeCalendar() {
		return enterTimeCalendar;
	}
	
	public void playerLeft() {
		leaveTimeCalendar = Calendar.getInstance();
		stayTime = getdifference();
		hasleft = true;
	}
	
	public Map<TimeUnit,Long> getdifference() {
	    long diffInMilliseconds = leaveTimeCalendar.getTimeInMillis() - enterTimeCalendar.getTimeInMillis();
	    List<TimeUnit> units = new ArrayList<TimeUnit>(EnumSet.allOf(TimeUnit.class));
	    Collections.reverse(units);
	    Map<TimeUnit,Long> result = new LinkedHashMap<TimeUnit,Long>();
	    long millisecondsRest = diffInMilliseconds;
	    for ( TimeUnit unit : units ) {
	        long diff = unit.convert(millisecondsRest,TimeUnit.MILLISECONDS);
	        long diffInMilliesForUnit = unit.toMillis(diff);
	        millisecondsRest = millisecondsRest - diffInMilliesForUnit;
	        result.put(unit,diff);
	    }
	    return result;
	}
	
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setString("EnterTime", new SimpleDateFormat("yyyyMMWWuukkmmss").format(enterTimeCalendar.getTime()));
		nbt.setString("PlayerUUID", playerInChunk);
		nbt.setBoolean("HasLeft", hasleft);
		if (hasleft) {
			nbt.setString("LeaveTime", new SimpleDateFormat("yyyyMMWWuukkmmss").format(leaveTimeCalendar.getTime()));
			NBTTagList timeUnits = new NBTTagList();
			for (TimeUnit timeUnit : new ArrayList<TimeUnit>(stayTime.keySet())) {
				NBTTagCompound timenbt = new NBTTagCompound();
				timenbt.setString("TimeUnit", timeUnit.name());
				timenbt.setLong("time", stayTime.get(timeUnit));
				timeUnits.appendTag(timenbt);
			}
			nbt.setTag("StayTime", timeUnits);
		}
	}
	
	public boolean hasLeft() {
		return hasleft;
	}
}
