package com.coehlrich.chunklogger;

import java.util.Comparator;

import com.coehlrich.chunklogger.chunk.PlayerInChunk;

public class PlayersInChunkListComparator implements Comparator<PlayerInChunk> {

	@Override
	public int compare(PlayerInChunk firstPlayer, PlayerInChunk secondPlayer) {
		return firstPlayer.getEnterTimeCalendar().compareTo(secondPlayer.getEnterTimeCalendar());
	}

}
