package de.hs.settlers.state;

import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import javafx.embed.swing.JFXPanel;

import org.junit.BeforeClass;
import org.junit.Test;

import de.hs.settlers.SettlersApplication;

public class StateTest {
	@BeforeClass
	public static void initClass() {
		new JFXPanel(); // using this hack to init the javafx toolkit
	}
	
	@Test
	public void testStates() {
		SettlersApplication app = new SettlersApplication();
		AbstractState state1 = createMockBuilder(AbstractState.class).withConstructor("State 1").createMock();
		AbstractState state2 = createMockBuilder(AbstractState.class).withConstructor("State 2").createMock();

		// check for some basic assertions
		assertEquals("State 1", state1.getName());
		assertEquals("State 2", state2.nameProperty().get());

		// tell easy mock which method calls we expect
		state1.start();
		state1.end();
		state2.start();
		state2.end();

		// set to replay state
		replay(state1);
		replay(state2);

		// use our source
		app.setCurrentState(state1);
		app.setCurrentState(state2);
		app.setCurrentState(null);

		// verify the aforementioned conditions were met
		verify(state1);
		verify(state2);
	}
}
