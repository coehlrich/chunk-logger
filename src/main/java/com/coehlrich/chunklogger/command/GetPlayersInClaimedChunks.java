package com.coehlrich.chunklogger.command;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.coehlrich.chunklogger.PlayersInChunkListComparator;
import com.coehlrich.chunklogger.chunk.AllChunks;
import com.coehlrich.chunklogger.chunk.ChunkListOfPlayers;
import com.coehlrich.chunklogger.chunk.PlayerInChunk;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api_impl.Universe;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunk;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunkStorage;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class GetPlayersInClaimedChunks extends CommandBase {

	@Override
	public String getCommandName() {
		return "getplayersinclaimedchunks";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "getplayersinclaimedchunks <Owner>";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 0 || args.length > 1) {
			throw new WrongUsageException("<Owner>");
		}
		
		IForgePlayer forgePlayer = Universe.INSTANCE.getPlayer(args[0]);
		
		if (forgePlayer == null) {
			throw new CommandException(String.format("There is no player called %s that has been on this server", args[0]));
		}
		
		Collection<IClaimedChunk> playerClaimedChunks = ClaimedChunkStorage.INSTANCE.getChunks(forgePlayer);
		
		if (playerClaimedChunks.isEmpty()) {
			throw new CommandException(String.format("player %s has not claimed any chunks", forgePlayer.getName()));
		}
		
		ArrayList<PlayerInChunk> playersInChunks = new ArrayList<PlayerInChunk>();
		for (IClaimedChunk claimedChunk : playerClaimedChunks) {
			ChunkDimPos dimPos = claimedChunk.getPos();
			WorldServer world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(claimedChunk.getPos().dim);
			Chunk chunk = world.getChunkFromChunkCoords(dimPos.posX, dimPos.posZ);
			ChunkListOfPlayers chunkListOfPlayers = AllChunks.getChunk(chunk);
			if (chunkListOfPlayers == null) {
				continue;
			}
			
			playersInChunks.addAll(chunkListOfPlayers.playersInChunk);
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
				sender.addChatMessage(new TextComponentString(String.format("Player %s was in one of the chunks from %s to %s for %s years and %s months and %s days and %s hours and %s minutes and %s seconds.", playerName, enterTime, leaveTime, years, months, days, hours, minutes, seconds)));
			} else {
				sender.addChatMessage(new TextComponentString(String.format("Player %s was in one of the chunks from %s and has not left yet", playerName, enterTime)));
			}
		}
	}

}
