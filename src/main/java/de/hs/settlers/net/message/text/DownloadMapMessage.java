package de.hs.settlers.net.message.text;

public class DownloadMapMessage extends BidirectionalMessage {

	String mapName;

	public DownloadMapMessage() {
	}

	public DownloadMapMessage(String mapName) {
		this.mapName = mapName;
	}

	@Override
	public String getData() {
		return "DOWNLOAD MAP " + mapName;
	}

	String lines[];

	@Override
	public void setData(String... lines) {
		this.lines = lines;
	}

	public String[] getLines() {
		return lines;
	}

	public String getMapName() {
		return mapName;
	}

}
