package havis.capture;

import java.util.Map;

public interface Converter {

	void init(Map<String, String> properties) throws ConverterException;

	byte[] toByteArray(Object value) throws ConverterException;

	Object valueOf(byte[] bytes) throws ConverterException;
}
