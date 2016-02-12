package de.hs.settlers;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class ApplicationHolder implements IApplicationHolder {

	private SimpleObjectProperty<SettlersApplication> application = new SimpleObjectProperty<>();

	@Override
	public void setApplication(SettlersApplication application) {
		this.application.set(application);
	}

	@Override
	public SettlersApplication getApplication() {
		return application.get();
	}
	
	@Override
	public ObjectProperty<SettlersApplication> applicationProperty() {
		return application;
	}

}
