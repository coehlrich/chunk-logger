package com.coehlrich.chunklogger.command;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

import com.coehlrich.chunklogger.PlayersInChunkListComparator;
import com.coehlrich.chunklogger.chunk.AllChunks;
import com.coehlrich.chunklogger.chunk.ChunkListOfPlayers;
import com.coehlrich.chunklogger.chunk.PlayerInChunk;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class GetPlayersInChunks extends CommandBase {

	@Override
	public String getCommandName() {
		return "getplayersinchunks";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "getplayersinchunks <ChunkX> <ChunkZ> [dimensionID]";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 4 || args.length > 5) {
			throw new WrongUsageException("<FromChunkX> <FromChunkZ> <ToChunkX> <ToChunkZ> [dimensionID]");
		}
		int fromChunkX = Integer.parseInt(args[0]);
		int fromChunkZ = Integer.parseInt(args[1]);
		int toChunkX = Integer.parseInt(args[2]);
		int toChunkZ = Integer.parseInt(args[3]);
		int dimensionID;
		if (args.length < 5) {
			dimensionID = 0;
		} else {
			dimensionID = Integer.parseInt(args[4]);
		}
		
		ArrayList<Integer> dimensionIDs = new ArrayList<Integer>();
		dimensionIDs.toArray(DimensionManager.getIDs());
		if (!dimensionIDs.contains(dimensionID)) {
			throw new NumberInvalidException("Dimension ID " + dimensionID + " does not exist");
		}
		
		if (toChunkZ < fromChunkZ) {
			int temp = fromChunkZ;
			fromChunkZ = toChunkZ;
			toChunkZ = temp;
		}
		
		if (toChunkX < fromChunkX) {
			int temp = fromChunkX;
			fromChunkX = toChunkX;
			toChunkX = temp;
		}
		
		ArrayList<PlayerInChunk> playersInChunks = new ArrayList<PlayerInChunk>();
		for (int chunkX = fromChunkX; chunkX < toChunkX + 1; chunkX++) {
			for (int chunkZ = fromChunkZ; chunkZ < toChunkZ + 1; chunkZ++) {
				Chunk chunk = new Chunk(FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(dimensionID), chunkX, chunkZ);
				ChunkListOfPlayers chunkListOfPlayers = AllChunks.getChunk(chunk);
				if (chunkListOfPlayers == null) {
					continue;
				}
				
				playersInChunks.addAll(chunkListOfPlayers.playersInChunk);
			}
		}
		
		Collections.sort(playersInChunks, new PlayersInChunkListComparator());
		for (PlayerInChunk player : playersInChunks) {
			String playerName = server.getPlayerProfileCache().getProfileByUUID(player.getPlayer()).getName();
			String enterTime = player.getEnterTimeCalendar().format(DateTimeFormatter.ofPattern("u/M/d H:m:s"));
			if (player.hasLeft()) {
				String leaveTime = player.getLeaveTimeCalendar().format(DateTimeFormatter.ofPattern("u/M/d H:m:s"));
				Duration stayTime = player.getStayTime();
				Long years = stayTime.toDays() / 365L;
				Long months = stayTime.toDays() / 30L;
				Long days = stayTime.toDays();
				Long hours = stayTime.toHours();
				Long minutes = stayTime.toMinutes();
				Long seconds = stayTime.getSeconds();
				if (months > 11) {
					months = months - (years * 12);
				}
				if (days > 29) {
					days = days - (days / 30 * 30);
				}
				if (hours > 23) {
					hours = hours - (hours / 24 * 24);
				}
				if (minutes > 59) {
					minutes = minutes - (minutes / 60 * 60);
				}
				if (seconds > 59) {
					seconds = seconds - (seconds / 60 * 60);
				}
				sender.addChatMessage(new TextComponentString(String.format("Player %s was in the chunk from %s to %s for %s years and %s months and %s days and %s hours and %s minutes and %s seconds.", playerName, enterTime, leaveTime, years, months, days, hours, minutes, seconds)));
			} else {
				sender.addChatMessage(new TextComponentString(String.format("Player %s was in the chunk from %s and has not left yet", playerName, enterTime)));
			}
		}

	}

}
