package de.hs.settlers.util;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

public class PropertyUtils {
	public static <T> void link(ObservableList<T> master, final ObservableList<T> slave) {
		slave.clear();
		slave.addAll(master);
		master.addListener(new ListChangeListener<T>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends T> change) {
				if (change.wasAdded()) {
					slave.addAll(change.getFrom(), change.getAddedSubList());
				}
				if (change.wasRemoved()) {
					slave.removeAll(change.getRemoved());
				}
			}
		});
	}

	public static <T> void link(ObservableSet<T> master, final ObservableList<T> slave) {
		slave.clear();
		slave.addAll(master);
		master.addListener(new SetChangeListener<T>() {

			@Override
			public void onChanged(javafx.collections.SetChangeListener.Change<? extends T> change) {
				if (change.wasAdded()) {
					slave.add(change.getElementAdded());
				}
				if (change.wasRemoved()) {
					slave.remove(change.getElementRemoved());
				}
			}

		});
	}

	@SuppressWarnings("unchecked")
	public static <T> Property getDesiredProperty(Class<T> type) {
		if (type.equals(Integer.class)) {
			return new SimpleIntegerProperty();
		} else if (type.equals(String.class)) {
			return new SimpleStringProperty();
		} else if (type.equals(Double.class)) {
			return new SimpleDoubleProperty();
		} else if (type.equals(Boolean.class)) {
			return new SimpleBooleanProperty();
		} else if (type.equals(List.class)) {
			return new SimpleListProperty(FXCollections.observableArrayList());
		} else if (type.equals(Map.class)) {
			return new SimpleMapProperty(FXCollections.observableHashMap());
		} else if (type.equals(Set.class)) {
			return new SimpleSetProperty(FXCollections.observableSet(new HashSet()));
		}

		return new SimpleObjectProperty<T>();
	}
}
