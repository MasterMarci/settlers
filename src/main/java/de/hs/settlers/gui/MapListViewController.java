package de.hs.settlers.gui;

import de.hs.settlers.model.map.Map;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class MapListViewController extends ViewController {

	@FXML
	VBox root;
	
	@FXML
	TableView<Map> mapTable;
	
	@FXML
	TableColumn<Map, String> mapNameColumn;
	
	@FXML
	TableColumn<Map, String> mapVersionColumn;
	
	@FXML
	TableColumn<Map, String> mapOwnerColumn;
	
	@FXML
	TableColumn<Map, String> mapPublishedColumn;
	
	@Override
	public Node getRootNode() {
		// TODO Auto-generated method stub
		return root;
	}

	@Override
	public void displayError(String error) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void init() {
		mapTable.setItems(FXCollections.observableArrayList(getApplication().getMapManager().getMaps()));
		mapNameColumn.setCellValueFactory(new PropertyValueFactory<Map,String>("mapTitle"));
		mapOwnerColumn.setCellValueFactory(new PropertyValueFactory<Map,String>("creator"));
		mapVersionColumn.setCellValueFactory(new PropertyValueFactory<Map,String>("version"));
		mapPublishedColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map,String>, ObservableValue<String>>() {
			
			@Override
			public ObservableValue<String> call(CellDataFeatures<Map, String> arg0) {
				return new ObservableValue<String>() {
					
					@Override
					public void removeListener(InvalidationListener arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void addListener(InvalidationListener arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void removeListener(ChangeListener<? super String> arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public String getValue() {
						return "Yes";
					}
					
					@Override
					public void addListener(ChangeListener<? super String> arg0) {
						// TODO Auto-generated method stub
						
					}
				};
			}
		});
	}

}
