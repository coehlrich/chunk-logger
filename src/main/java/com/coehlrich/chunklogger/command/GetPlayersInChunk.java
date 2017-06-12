package com.coehlrich.chunklogger.command;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
import java.util.Map;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class GetPlayersInChunk extends CommandBase {

	@Override
	public String getCommandName() {
		return "getplayersinchunk";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "getplayersinchunk <ChunkX> <ChunkZ> <dimensionID";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 2 || args.length > 3) {
			throw new WrongUsageException("<ChunkX> <ChunkZ> [dimensionID]");
		}
		int chunkX = Integer.parseInt(args[0]);
		int chunkZ = Integer.parseInt(args[1]);
		int dimensionID;
		if (args.length < 3) {
			dimensionID = 0;
		} else {
			dimensionID = Integer.parseInt(args[2]);
		}
		ArrayList<Integer> dimensionids = new ArrayList<Integer>();
		dimensionids.toArray(DimensionManager.getIDs());
		int dimensionid;
		if (args.length == 3) {
			if (!dimensionids.contains(Integer.parseInt(args[2]))) {
				throw new NumberInvalidException("Dimension ID " + args[2] + " does not exist");
			} else {
				dimensionid = Integer.parseInt(args[2]);
			}
		} else {
			dimensionid = 0;
		}
		ChunkListOfPlayers chunk = AllChunks.getChunk(new Chunk(FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(dimensionid), chunkX, chunkZ));
		ArrayList<PlayerInChunk> playersInChunk = (ArrayList<PlayerInChunk>) chunk.getPlayersInChunk().clone();
		for (PlayerInChunk player : playersInChunk) {
			String playerName = server.getPlayerProfileCache().getProfileByUUID(UUID.fromString(player.getPlayer())).getName();
			String enterTime = new SimpleDateFormat("yyyy/mm/dd HH:mm:ss").format(player.getEnterTimeCalendar().getTime());
			if (player.hasLeft()) {
				String leaveTime = new SimpleDateFormat("yyyy/mm/dd HH:mm:ss").format(player.getLeaveTimeCalendar().getTime());
				Map<TimeUnit, Long> stayTime = player.getdifference();
				ArrayList<TimeUnit> timeUnits = new ArrayList<TimeUnit>(stayTime.keySet());
				StringBuilder allTime = new StringBuilder();
				for (TimeUnit timeUnit : timeUnits) {
					if (timeUnits.indexOf(timeUnit) == timeUnits.size()) {
						allTime.delete(allTime.length() - 1, allTime.length());
						allTime.append("and" + stayTime.get(timeUnit).toString() + " " + timeUnit.toString().toLowerCase() + "'s.");
					} else {
						allTime.append(stayTime.get(timeUnit).toString() + " " + timeUnit.toString().toLowerCase() + "'s, ");
					}
				}
				sender.addChatMessage(new TextComponentString(String.format("Player %s was in the chunk from %s to %s for %s", playerName, enterTime, leaveTime, allTime.toString())));
			} else {
				sender.addChatMessage(new TextComponentString(String.format("Player %s was in the chunk from %s and has not left yet", playerName, enterTime)));
			}
		}
	}

}
