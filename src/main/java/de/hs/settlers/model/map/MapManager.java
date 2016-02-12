package de.hs.settlers.model.map;

import java.util.LinkedHashSet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import de.hs.settlers.ApplicationHolder;
import de.hs.settlers.SettlersApplication;
import de.hs.settlers.net.event.NetworkEventListener;
import de.hs.settlers.net.message.text.DownloadMapMessage;
import de.hs.settlers.net.message.text.ListMapsMessage;
import de.hs.settlers.util.AssociationUtils;
import de.hs.settlers.util.ParseUtils.ParseResult;

public class MapManager extends ApplicationHolder {

	private ObservableSet<Map> maps = FXCollections.observableSet(new LinkedHashSet<Map>());

	{
		AssociationUtils.manyInMe(this, maps, "mapManager", Map.class);
	}

	private NetworkEventListener<ListMapsMessage> mapListListener = new NetworkEventListener<ListMapsMessage>(ListMapsMessage.class) {
		@Override
		public void onMessage(ListMapsMessage message) {
			for (ParseResult result : message.getMaps()) {
				if (result.getObjectName().equals("MAP")) {
					String name = result.get("NAME");
					int version = Integer.parseInt(result.get("VERSION"));
					String creator = result.get("CREATOR");

					Map existing = getMapWithName(name);
					if (existing == null) {
						existing = new Map();
						existing.setMapManager(MapManager.this);
					}

					existing.setMapTitle(name);
					existing.setCreator(creator);

					SettlersApplication.LOGGER.info("Got map " + existing);

					if (existing.getVersion() < version) {
						SettlersApplication.LOGGER.info("Server has version " + version + " which is higher than ours (" + existing.getVersion() + "). Downloading new version...");
						downloadMap(existing);
					}
				}
			}
		}
	};

	private NetworkEventListener<DownloadMapMessage> downloadMapListener = new NetworkEventListener<DownloadMapMessage>(DownloadMapMessage.class) {

		@Override
		public void onMessage(DownloadMapMessage message) {
			String mapName = ((DownloadMapMessage) message.getAnswerTo()).getMapName();
			Map map = getMapWithName(mapName);
			if (map != null) {
				StringBuilder builder = new StringBuilder();
				for (String line : message.getLines()) {
					builder.append(line).append("\n");
				}
				map.setMapData(builder.toString());
				map.parseMap();
				SettlersApplication.LOGGER.info("Received data for map " + map.toString());
			} else {
				SettlersApplication.LOGGER.warning("Received map data for unknown map (" + mapName + ") - ignoring");
			}
		}
	};

	private boolean listenersRegistered = false;

	public ObservableSet<Map> getMaps() {
		return maps;
	}

	public Map getMapWithName(String name) {
		for (Map map : maps) {
			if (map.getMapTitle().equals(name)) {
				return map;
			}
		}
		return null;
	}

	public void updateMaps() {
		registerListeners();
		getApplication().getTextCommunicationProtocol().sendMessage(new ListMapsMessage());
	}

	private void downloadMap(Map map) {
		registerListeners();
		if (getApplication() != null) {
			getApplication().getTextCommunicationProtocol().sendMessage(new DownloadMapMessage(map.getMapTitle()));
		}
	}

	private void registerListeners() {
		if (!listenersRegistered && getApplication() != null) {
			getApplication().getTextCommunicationProtocol().getEventManager().registerListener(mapListListener);
			getApplication().getTextCommunicationProtocol().getEventManager().registerListener(downloadMapListener);

			listenersRegistered = true;
		}
	}

	public NetworkEventListener<DownloadMapMessage> getDownloadMapListener() {
		return downloadMapListener;
	}

	public NetworkEventListener<ListMapsMessage> getMapListListener() {
		return mapListListener;
	}
}
