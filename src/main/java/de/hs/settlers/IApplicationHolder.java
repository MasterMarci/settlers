package de.hs.settlers;

import javafx.beans.property.ObjectProperty;

public interface IApplicationHolder {

	public abstract SettlersApplication getApplication();

	public abstract void setApplication(SettlersApplication application);

	ObjectProperty<SettlersApplication> applicationProperty();

}
