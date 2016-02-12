package de.hs.settlers.net.message.text;

public class LogoutMessage extends BidirectionalMessage
{

	@Override
	public String getData()
	{
		return "LOGOUT";
	}

	@Override
	public void setData(String... lines) {
		// nothing to do
	}

}
