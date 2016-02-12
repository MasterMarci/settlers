package de.hs.settlers.model.dynamic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.junit.Test;

public class DynamicModelTest {
	protected boolean eventFired = false;

	@Test
	public void testModel() {
		DynamicModel model = new DynamicModel();

		model.getCollection("Player").getObject("a3b2f").getProperty("name", String.class).addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				eventFired = true;
			}
		});

		model.getCollection("Player").getObject("a3b2f").getProperty("name", String.class).setValue("narrowtux");

		assertTrue("no update has been received", eventFired);

		try {
			model.getCollection("Player").getObject("a3b2f").getProperty("name", Integer.class);
			fail("getProperty didn't throw an exception");
		} catch (Exception e) {
			// exception was desired
		}

		model.getCollection("Player").getObject("a3b2f").getProperty("name", String.class).setValue("somebody");

		assertEquals("somebody", model.getCollection("Player").getObject("a3b2f").getProperty("name", String.class).getValue());

		assertEquals("a3b2f", model.getCollection("Player").getObject("a3b2f").getId());
		assertEquals(model.getCollection("Player"), model.getCollection("Player").getObject("a3b2f").getCollection());
		assertEquals("Player", model.getCollection("Player").getType());
	}
}
