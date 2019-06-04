package com.tamboot.common.utils;

import java.util.List;

public class ArrayUtils {
	public static Object toPrimitiveArray(Class<?> type, List<Object> list) {
		if (type == null || list == null) {
			return null;
		}
		
		int elementSize = list.size();
		if (type == String.class) { 
			return list.toArray(new String[elementSize]);
		} else if (type == Boolean.class) {
			return list.toArray(new Boolean[elementSize]);
		} else if (type == Byte.class) {
			return list.toArray(new Byte[elementSize]);
		} else if (type == Character.class) {
			return list.toArray(new Character[elementSize]);
		} else if (type == Short.class) {
			return list.toArray(new Short[elementSize]);
		} else if (type == Integer.class) {
			return list.toArray(new Integer[elementSize]);
		} else if (type == Long.class) {
			return list.toArray(new Long[elementSize]);
		} else if (type == Float.class) {
			return list.toArray(new Float[elementSize]);
		} else if (type == Double.class) {
			return list.toArray(new Double[elementSize]);
		} else if (type == boolean.class) {
			boolean[] array = new boolean[elementSize];
			for (int i=0; i<elementSize; i++) {
				array[i] = (boolean) list.get(i);
			}
			return array;
		} else if (type == byte.class) {
			byte[] array = new byte[elementSize];
			for (int i=0; i<elementSize; i++) {
				array[i] = (byte) list.get(i);
			}
			return array;
		} else if (type == char.class) {
			char[] array = new char[elementSize];
			for (int i=0; i<elementSize; i++) {
				array[i] = (char) list.get(i);
			}
			return array;
		} else if (type == short.class) {
			short[] array = new short[elementSize];
			for (int i=0; i<elementSize; i++) {
				array[i] = (short) list.get(i);
			}
			return array;
		} else if (type == int.class) {
			int[] array = new int[elementSize];
			for (int i=0; i<elementSize; i++) {
				array[i] = (int) list.get(i);
			}
			return array;
		} else if (type == long.class) {
			long[] array = new long[elementSize];
			for (int i=0; i<elementSize; i++) {
				array[i] = (long) list.get(i);
			}
			return array;
		} else if (type == float.class) {
			float[] array = new float[elementSize];
			for (int i=0; i<elementSize; i++) {
				array[i] = (float) list.get(i);
			}
			return array;
		} else if (type == double.class) {
			double[] array = new double[elementSize];
			for (int i=0; i<elementSize; i++) {
				array[i] = (double) list.get(i);
			}
			return array;
		}
		
		return null;
	}
}
