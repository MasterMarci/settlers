package de.hs.settlers.model;

public class Team implements Recipient {

	String team;
	
	public Team(String team) {
		this.team = team;
	}
	
	@Override
	public String getRecipientString() {
		return "TEAM " + team;
	}

}
