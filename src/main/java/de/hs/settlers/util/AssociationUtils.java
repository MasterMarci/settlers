package de.hs.settlers.util;

import java.lang.reflect.Field;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

/**
 * Contains methods and classes to automate bidirectional associations using
 * JavaFX' property beans system
 * 
 */
public class AssociationUtils {

	/*
	 * ONE-TO-MANY
	 */

	/**
	 * Prepares this object for a bidirectional one-to-many association.<br>
	 * Call this method on the many-side
	 * 
	 * @param thisObject
	 *            the current object instance, usually this
	 * @param otherProperty
	 *            the property in this object that has to be linked to Other
	 * @param iInPropertyFieldName
	 *            the field name where the ObservableSet is stored in the other
	 *            object
	 * @param otherType
	 *            the class of the Other type
	 */
	public static <Me, Other> void iInOne(Me thisObject,
			Property<Other> otherProperty, String iInPropertyFieldName,
			Class<Other> otherType) {
		try {
			otherProperty.addListener(new IInOne<Me, Other>(thisObject,
					getField(iInPropertyFieldName, otherType)));
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prepares this object for a bidirectional one-to-many association. <br>
	 * Call this method on the one-side
	 * 
	 * @param thisObject
	 *            the current object instance, usually this
	 * @param otherProperty
	 *            the set property that contains Other objects
	 * @param manyOfMePropertyFieldName
	 *            the field name of the Property in the other object
	 * @param otherType
	 *            the class of the Other type
	 */
	public static <Me, Other> void manyInMe(Me thisObject,
			ObservableSet<Other> otherProperty,
			String manyOfMePropertyFieldName, Class<Other> otherType) {
		try {
			otherProperty.addListener(new ManyInMe<Me, Other>(thisObject,
					getField(manyOfMePropertyFieldName, otherType)));
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	/**
	 * I am one of many objects in Other
	 */
	private static class IInOne<Me, Other> implements ChangeListener<Other> {
		Field fieldIn;
		Me me;

		public IInOne(Me me, Field fieldIn) {
			super();
			this.fieldIn = fieldIn;
			this.me = me;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void changed(ObservableValue<? extends Other> object,
				Other oldValue, Other newValue) {
			if (oldValue != null) {
				try {
					ObservableSet<Me> in = (ObservableSet<Me>) fieldIn
							.get(oldValue);
					in.remove(me);
				} catch (IllegalArgumentException | IllegalAccessException
						| ClassCastException e) {
					e.printStackTrace();
				}
			}
			if (newValue != null) {
				try {
					ObservableSet<Me> in = (ObservableSet<Me>) fieldIn
							.get(newValue);
					in.add(me);
				} catch (IllegalArgumentException | IllegalAccessException
						| ClassCastException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Many objects of Other are in a property of Me
	 * 
	 * @param <Me>
	 * @param <Other>
	 */
	private static class ManyInMe<Me, Other> implements
			SetChangeListener<Other> {
		Field fieldIn;
		Me me;

		public ManyInMe(Me me, Field fieldIn) {
			super();
			this.me = me;
			this.fieldIn = fieldIn;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onChanged(
				javafx.collections.SetChangeListener.Change<? extends Other> change) {
			if (change.wasAdded()) {
				try {
					Property<Me> in = (Property<Me>) fieldIn.get(change
							.getElementAdded());
					in.setValue(me);
				} catch (IllegalArgumentException | IllegalAccessException
						| ClassCastException e) {
					e.printStackTrace();
				}
			}
			if (change.wasRemoved()) {
				try {
					Property<Me> in = (Property<Me>) fieldIn.get(change
							.getElementRemoved());
					in.setValue(null);
				} catch (IllegalArgumentException | IllegalAccessException
						| ClassCastException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * ONE-TO-ONE
	 */

	/**
	 * Prepares the given thisObject for a bidirectional one-to-one association. <br>
	 * Call this methods on both sides
	 * 
	 * @param thisObject
	 *            the current object instance, usually this
	 * @param otherProperty
	 *            the property that contains the Other object
	 * @param meInPropertyFieldName
	 *            the field name of the Property in the Other object
	 * @param type
	 *            the class of the Other type
	 */
	public static <Me, Other> void oneToOne(Me thisObject,
			Property<Other> otherProperty, String meInPropertyFieldName,
			Class<Other> type) {
		try {
			otherProperty.addListener(new OneInOne(thisObject, getField(meInPropertyFieldName, type)));
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
	
	private static class OneInOne<Me, Other> implements ChangeListener<Other> {
		Me me;
		Field fieldOther;

		public OneInOne(Me me, Field fieldOther) {
			super();
			this.me = me;
			this.fieldOther = fieldOther;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void changed(ObservableValue<? extends Other> object,
				Other oldValue, Other newValue) {
			if (oldValue != null) {
				try {
					Property<Me> other = (Property<Me>) fieldOther
							.get(oldValue);
					other.setValue(null);
				} catch (IllegalArgumentException | IllegalAccessException
						| ClassCastException e) {
					e.printStackTrace();
				}
			}
			if (newValue != null) {
				try {
					Property<Me> other = (Property<Me>) fieldOther
							.get(newValue);
					other.setValue(me);
				} catch (IllegalArgumentException | IllegalAccessException
						| ClassCastException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private static Field getField(String name, Class<?> in) {
		Field ret = null;
		while (true) {
			try {
				ret = in.getDeclaredField(name);
				break;
			} catch (NoSuchFieldException | SecurityException e) {
				in = in.getSuperclass();
				if (in == null || in.equals(Object.class)) {
					return null;
				}
			}
		}
		ret.setAccessible(true);
		return ret;
	}
}
