package com.coehlrich.chunklogger;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.LogManager;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Config {
	public static String timeToDelete = "0 0 1 0:0:0";
	public static Long millisecondsToDelete;
	
	public static File configfile;
	public static Configuration config;
	
	public static void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new Config());
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		configInit();
		config.save();
	}
	
	public static void configInit() {
		timeToDelete = config.getString("timeToDelete", config.CATEGORY_GENERAL, timeToDelete, "Time that has needed to pass since a player left the chunk for it to be deleted, format:[year] [month] [day] [hour]:[minute]:[second]");
		Date timeToDeleteDate;
		try {
		timeToDeleteDate = new SimpleDateFormat("Y MM dd HH:mm:ss").parse(timeToDelete);
		} catch (ParseException e) {
			LogManager.getLogger("Chunk Logger").warn("Date in config is not a valid date, using default");
			Calendar calendar = Calendar.getInstance();
			calendar.set(0, 0, 1, 0, 0, 0);
			timeToDeleteDate = calendar.getTime();
		}
		Calendar timeToDeleteCalendar = Calendar.getInstance();
		timeToDeleteCalendar.setTime(timeToDeleteDate);
		timeToDeleteCalendar.set(timeToDeleteCalendar.YEAR + 1970, timeToDeleteCalendar.MONTH, timeToDeleteCalendar.DAY_OF_MONTH);
		millisecondsToDelete = timeToDeleteCalendar.getTimeInMillis();
	}
}
