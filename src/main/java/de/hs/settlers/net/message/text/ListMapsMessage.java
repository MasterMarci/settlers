package de.hs.settlers.net.message.text;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.hs.settlers.util.ParseUtils;
import de.hs.settlers.util.ParseUtils.ParseResult;

public class ListMapsMessage extends BidirectionalMessage {

	@Override
	public String getData() {
		return "LIST MAPS";
	}

	@Override
	public void setData(String... lines) {
		for (String line : lines) {
			maps.add(ParseUtils.parseKeyValueLine(line));
		}
	}

	private LinkedList<ParseResult> maps = new LinkedList<>();

	public List<ParseResult> getMaps() {
		return Collections.unmodifiableList(maps);
	}
}
