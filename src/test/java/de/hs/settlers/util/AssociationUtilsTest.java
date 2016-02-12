package de.hs.settlers.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashSet;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import org.junit.Test;

public class AssociationUtilsTest {
	@Test
	public void testOneToManyAssoc() {
		OneToManyA a = new OneToManyA();
		OneToManyB b = new OneToManyB();
		OneToManyB b2 = new OneToManyB();

		assertNull(a.getMyB());
		assertFalse(b.getAllAs().contains(a));

		a.setMyB(b);

		assertTrue(b.getAllAs().contains(a));

		b.getAllAs().remove(a);

		assertNull(a.getMyB());

		a = new OneToManyA();
		b = new OneToManyB();

		b.getAllAs().add(a);

		assertEquals(b, a.getMyB());

		a.setMyB(null);

		assertFalse(b.getAllAs().contains(a));

		a.setMyB(b);

		assertTrue(b.getAllAs().contains(a));

		a.setMyB(b2);

		assertFalse(b.getAllAs().contains(a));
		assertTrue(b2.getAllAs().contains(a));
	}

	private static class OneToManyA {
		private SimpleObjectProperty<OneToManyB> myB = new SimpleObjectProperty<>();

		{
			AssociationUtils.iInOne(this, myB, "allAs", OneToManyB.class);
		}

		public OneToManyB getMyB() {
			return myB.get();
		}

		public void setMyB(OneToManyB b) {
			myB.setValue(b);
		}

		public ObjectProperty<OneToManyB> myBProperty() {
			return myB;
		}
	}

	private static class OneToManyB {
		private ObservableSet<OneToManyA> allAs = FXCollections.observableSet(new LinkedHashSet<OneToManyA>());

		{
			AssociationUtils.manyInMe(this, allAs, "myB", OneToManyA.class);
		}

		public ObservableSet<OneToManyA> getAllAs() {
			return allAs;
		}
	}

	@Test
	public void testOneToOneAssoc() {
		OneToOneA a = new OneToOneA();
		OneToOneA a2 = new OneToOneA();
		OneToOneB b = new OneToOneB();

		a.setB(b);

		assertEquals(a, b.getA());

		a.setB(null);

		assertNull(b.getA());

		a = new OneToOneA();
		b = new OneToOneB();

		b.setA(a);

		assertEquals(b, a.getB());

		b.setA(null);

		assertNull(a.getB());

		b.setA(a);

		assertEquals(b, a.getB());

		b.setA(a2);

		assertNull(a.getB());
		assertEquals(b, a2.getB());
	}

	private static class OneToOneA {
		private SimpleObjectProperty<OneToOneB> b = new SimpleObjectProperty<>();

		{
			AssociationUtils.oneToOne(this, b, "a", OneToOneB.class);
		}

		public void setB(OneToOneB b) {
			this.b.set(b);
		}

		public OneToOneB getB() {
			return b.get();
		}
	}

	private static class OneToOneB {
		private SimpleObjectProperty<OneToOneA> a = new SimpleObjectProperty<>();

		{
			AssociationUtils.oneToOne(this, a, "b", OneToOneA.class);
		}

		public void setA(OneToOneA a) {
			this.a.set(a);
		}

		public OneToOneA getA() {
			return a.get();
		}
	}
}
