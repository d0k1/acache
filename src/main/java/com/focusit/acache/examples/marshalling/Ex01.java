package com.focusit.acache.examples.marshalling;

import com.focusit.acache.marshalling.CacheMarshall;

/**
 * An example of primitive usage of CacheMarshaller
 * @author Denis V. Kirpichenkov
 *
 */
public class Ex01 {

	public static class example {
		private int i = 123;

		public example() {
		}

		public void setI(int i) {
			this.i = i;
		}

		public int getI() {
			return i;
		}
	}

	public static void main(String[] args) {
		example e = new example();
		e.setI(567);
		byte []data = CacheMarshall.getObjectBytes(e);
		System.out.println("example e is about " + data.length + " bytes");
		example ee = CacheMarshall.getBytesObject(data);	
		System.out.println("Clonned example e has i = "+ee.getI());
	}
}
