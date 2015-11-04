package com.focusit.acache.marshalling;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.ByteBufferOutput;

public class CacheMarshall {
	
	static final Kryo kryo = new Kryo();
	
	public static byte[] getObjectBytes(Object o){
		ByteBufferOutput output = new ByteBufferOutput(100, -1);
		kryo.writeClassAndObject(output, o);
		output.close();
		return output.toBytes();
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBytesObject(byte []bytes){
		ByteBufferInput input = new ByteBufferInput(bytes);
		Object o =kryo.readClassAndObject(input);
		return (T)o;
	}
}
