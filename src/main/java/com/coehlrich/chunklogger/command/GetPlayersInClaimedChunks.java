package com.coehlrich.chunklogger.command;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import com.coehlrich.chunklogger.PlayersInChunkListComparator;
import com.coehlrich.chunklogger.chunk.AllChunks;
import com.coehlrich.chunklogger.chunk.ChunkListOfPlayers;
import com.coehlrich.chunklogger.chunk.PlayerInChunk;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbutilities.data.ClaimedChunk;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;

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
	public String getName() {
		return "getplayersinclaimedchunks";
	}

	@Override
	public String getUsage(ICommandSender sender) {
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
		
		ForgePlayer forgePlayer = Universe.get().getPlayer(args[0]);
		
		if (forgePlayer == null) {
			throw new CommandException(String.format("There is no player called %s that has been on this server", args[0]));
		}
		
		ForgeTeam team = forgePlayer.getTeam();
		
		if (team == null) {
			throw new CommandException(String.format("Player %s is not in a team", forgePlayer.getName()));
		}
		
		Set<ClaimedChunk> teamClaimedChunks = ClaimedChunks.get().getTeamChunks(team);
		
		if (teamClaimedChunks.isEmpty()) {
			throw new CommandException(String.format("team %s has not claimed any chunks", team.getName()));
		}
		
		ArrayList<PlayerInChunk> playersInChunks = new ArrayList<PlayerInChunk>();
		for (ClaimedChunk claimedChunk : teamClaimedChunks) {
			ChunkDimPos dimPos = claimedChunk.getPos();
			WorldServer world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(dimPos.dim);
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
				sender.sendMessage(new TextComponentString(String.format("Player %s was in one of the chunks from %s to %s for %s years and %s months and %s days and %s hours and %s minutes and %s seconds.", playerName, enterTime, leaveTime, years, months, days, hours, minutes, seconds)));
			} else {
				sender.sendMessage(new TextComponentString(String.format("Player %s was in one of the chunks from %s and has not left yet", playerName, enterTime)));
			}
		}
	}
}
