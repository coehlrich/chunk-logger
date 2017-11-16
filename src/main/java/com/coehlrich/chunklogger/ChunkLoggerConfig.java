package com.coehlrich.chunklogger;

import java.io.File;
import java.time.Duration;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ChunkLoggerConfig {
	public static Duration timeFromLeft;
	public static Duration timeFromEnter;
	
	public static File configFile;
	public static Configuration config;
	
	public static ConfigCategory timeFromLeftCategory;
	public static ConfigCategory timeFromEnterCategory;
	
	public static String version = "0.0";
	public static String currentVersion = "1.1";
	
	public static File configDirectory;
	
	public static void preInit(FMLPreInitializationEvent event) {
		configFile = event.getSuggestedConfigurationFile();
		configDirectory = event.getModConfigurationDirectory();
		configPreInit();
		config = new Configuration(configFile);
		configInit();
		config.getCategory(config.CATEGORY_GENERAL).remove("timeToDelete"); // Wouldn't work anywhere else for some reason
		config.save();
	}
	
	public static void configPreInit() {
		if (!configFile.exists()) {
			version = "0.0";
		}
	}
	
	public static void configInit() {
		version = config.getString("version", config.CATEGORY_GENERAL, version, "version of config");
		timeToDeleteFromLeft();
		timeToDeleteFromEnter();
		config.get(config.CATEGORY_GENERAL, "version", currentVersion, "version of config").set(currentVersion);;
	}
	
	public static void timeToDeleteFromLeft() {
		timeFromLeftCategory = config.getCategory("timeFromLeft");
		timeFromLeftCategory.setComment("Time that has needed to pass since someone left a chunk for it to be deleted");
		String[] timesString;
		int years;
		int months;
		int days;
		int hours;
		int minutes;
		int seconds;
		if (version == "1.0") {
			String time = config.getString("timeToDelete", config.CATEGORY_GENERAL, "0 0 1 0:0:0", "Time that has needed to pass since a player left the chunk for it to be deleted, format:[year] [month] [day] [hour]:[minute]:[second]");
			Pattern pattern = Pattern.compile("\\d+ \\d+ \\d+ \\d+:\\d+:\\d+");
			if (pattern.matcher(time).matches()) {
				timesString = time.split(" ");
				String[] partOfDay;
				partOfDay = timesString[3].split(":");
				int originalLength = timesString.length;
				timesString = Arrays.copyOf(timesString, 6);
				System.arraycopy(partOfDay, 0, timesString, originalLength - 1, partOfDay.length);
				years = Integer.parseInt(timesString[0]);
				if (years > 100 || years < 0) {
					years = 0;
					ChunkLogger.log(Level.WARN, "Years was above 100 or below 0 so it was set to 0");
				}
				months = Integer.parseInt(timesString[1]);
				if (months > 11 || months < 0) {
					months = 0;
					ChunkLogger.log(Level.WARN, "Months was above 11 or below 0 so it was set to 0");
				}
				days = Integer.parseInt(timesString[2]);
				if (days > 30 || days < 0) {
					days = 1;
					ChunkLogger.log(Level.WARN, "Days was above 30 or below 0 so it was set to 1");
				}
				hours = Integer.parseInt(timesString[3]);
				if (hours > 23 || hours < 0) {
					hours = 0;
					ChunkLogger.log(Level.WARN, "Hours was above 23 or below 0 so it was set to 0");
				}
				minutes = Integer.parseInt(timesString[4]);
				if (minutes > 59 || minutes < 0) {
					minutes = 0;
					ChunkLogger.log(Level.WARN, "Minutes was above 59 or below 0 so it was set to 0");
				}
				seconds = Integer.parseInt(timesString[5]);
				if (seconds > 59 || seconds < 0) {
					seconds = 0;
					ChunkLogger.log(Level.WARN, "Seconds was above 59 or below 0 so it was set to 0");
				}
			} else {
				years = 0;
				months = 0;
				days = 1;
				hours = 0;
				minutes = 0;
				seconds = 0;
				ChunkLogger.log(Level.WARN, "time from left wasnt in the correct format so it was set to the default");
			}
		} else {
			years = 0;
			months = 0;
			days = 1;
			hours = 0;
			minutes = 0;
			seconds = 0;
		}
		years = config.getInt("years", "timeFromLeft", years, 0, 100, "years that have needed to pass");
		months = config.getInt("months", "timeFromLeft", months, 0, 11, "months that have needed to pass");
		days = config.getInt("days", "timeFromLeft", days, 0, 30, "days that have needed to pass");
		hours = config.getInt("hours", "timeFromLeft", hours, 0, 23, "hours that have needed to pass");
		minutes = config.getInt("minutes", "timeFromLeft", minutes, 0, 59, "minutes that have needed to pass");
		seconds = config.getInt("seconds", "timeFromLeft", seconds, 0, 59, "seconds that have needed to pass");
		timeFromLeft = Duration.ofSeconds(seconds);
		timeFromLeft = timeFromLeft.plusMinutes(minutes);
		timeFromLeft = timeFromLeft.plusHours(hours);
		timeFromLeft = timeFromLeft.plusDays(days);
		timeFromLeft = timeFromLeft.plusDays(months * 31);
		timeFromLeft = timeFromLeft.plusDays(Math.round(years * 365.25));
	}
	public static void timeToDeleteFromEnter() {
		timeFromEnterCategory = config.getCategory("timeFromEnter");
		timeFromEnterCategory.setComment("Time that has needed to pass since someone entered a chunk for it to be deleted");
		int years = config.getInt("years", "timeFromEnter", 0, 0, 100, "years that have needed to pass");
		int months = config.getInt("months", "timeFromEnter", 2, 0, 11, "months that have needed to pass");
		int days = config.getInt("days", "timeFromEnter", 0, 0, 30, "days that have needed to pass");
		int hours = config.getInt("hours", "timeFromEnter", 0, 0, 23, "hours that have needed to pass");
		int minutes = config.getInt("minutes", "timeFromEnter", 0, 0, 59, "minutes that have needed to pass");
		int seconds = config.getInt("seconds", "timeFromEnter", 0, 0, 59, "seconds that have needed to pass");
		timeFromEnter = Duration.ofSeconds(seconds);
		timeFromEnter = timeFromEnter.plusMinutes(minutes);
		timeFromEnter = timeFromEnter.plusHours(hours);
		timeFromEnter = timeFromEnter.plusDays(days);
		timeFromEnter = timeFromEnter.plusDays(months * 31);
		timeFromEnter = timeFromEnter.plusDays(Math.round(years * 365.25));
	}
}
