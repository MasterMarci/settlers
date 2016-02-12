package de.hs.settlers.net.message.text;


public class LoginMessage extends BasicClientTextMessage {
	private String username, password;

	public LoginMessage(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String getData() {
		StringBuilder sb = new StringBuilder();
		sb.append("LOGIN ")
				.append(username)
				.append(' ')
				.append(password);
		return sb.toString();
	}

	@Override
	public Class<? extends ServerTextMessage> getAnswerType() {
		return IdMessage.class;
	}

}
