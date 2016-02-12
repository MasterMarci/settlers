package de.hs.settlers.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import de.hs.settlers.ApplicationHolder;
import de.hs.settlers.SettlersApplication;
import de.hs.settlers.util.AssociationUtils;

public class Player extends ApplicationHolder implements Recipient
{

	private SimpleStringProperty name = new SimpleStringProperty();
	private SimpleStringProperty team = new SimpleStringProperty();
	private SimpleStringProperty status = new SimpleStringProperty();
	private SimpleStringProperty id = new SimpleStringProperty();
	private SimpleBooleanProperty visitor = new SimpleBooleanProperty();
	private SimpleBooleanProperty online = new SimpleBooleanProperty();

	public String getId (){
		return this.id.get();
	}

	public String getName (){
		return this.name.get();
	}

	public String getStatus (){
		return this.status.get();
	}

	public String getTeam (){
		return this.team.get();
	}

	public boolean isVisitor () {
		return this.visitor.get();
	}
	
	public boolean isOnline () {
		return this.online.get();
	}

	public void setId (String value) {
		this.id.set(value);
	}

	public void setName (String value) {
		this.name.set(value);
	}

	public void setStatus (String value) {
		this.status.set(value);
	}

	public void setTeam (String value) {
		this.team.set(value);
	}

	public void setVisitor (boolean value) {
		this.visitor.set(value);
	}
	
	public void setOnline (boolean value) {
		this.online.set(value);
	}

	public Player withId (String value) {
		setId (value);
		return this;
	}

	public Player withName (String value) {
		setName(value);
		return this;
	}

	public Player withStatus (String value) {
		setStatus (value);
		return this;
	}

	public Player withTeam (String value) {
		setTeam (value);
		return this;
	}

	public Player withVisitor (boolean value) {
		setVisitor (value);
		return this;
	}

	public StringProperty name() {
		return name;
	}

	public StringProperty team() {
		return team;
	}

	public StringProperty status() {
		return status;
	}

	public StringProperty id() {
		return id;
	}

	public BooleanProperty visitor() {
		return visitor;
	}
	
	public BooleanProperty online() {
		return online;
	}
	/**
	 * <pre>
	 *           0..n     players     0..1
	 * Player ------------------------- SettlersApplication
	 *           players               application
	 * </pre>
	 */
	 {
		 AssociationUtils.iInOne(this, applicationProperty(), "players", SettlersApplication.class);
	 }
	 
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((team == null) ? 0 : team.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Player other = (Player) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (team == null) {
			if (other.team != null)
				return false;
		} else if (!team.equals(other.team))
			return false;
		return true;
	}

	@Override
	public String getRecipientString() {
		return "USER " + this.name.get();
	}

	 /**
	  * <pre>
	  *           0..n     join     0..1
	  * Player ------------------------- Game
	  *           joinPlayers               joinGame
	  * </pre>
	  */

}


