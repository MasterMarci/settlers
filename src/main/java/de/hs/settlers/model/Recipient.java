package de.hs.settlers.model;

/**
 * Recipient of a message (can be a player, a team or all players)
 */
public interface Recipient {
	/**
	 * @return a string that is put in front of the message in the
	 *         SendChatMessage <br>
	 *         Example:
	 *         <ul>
	 *         <li>ALL</li>
	 *         <li>USER narrowtux</li>
	 *         <li>TEAM Team-A</li>
	 *         </ul>
	 */
	public String getRecipientString();
}
