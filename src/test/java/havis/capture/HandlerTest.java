package havis.capture;

import java.util.Map;

public class HandlerTest {

	AdapterListener listener;
	
	public void test() throws AdapterException{

		
		Adapter adapter;
		
		AdapterManager manager = new AdapterManager(adapter = new Adapter() {

			@Override
			public void unsubscribe(String device, String field) throws AdapterException {
			}
			
			@Override
			public void subscribe(String device, String field) throws AdapterException {
			}
			
			@Override
			public void setValue(String device, String field, Object value) throws AdapterException {
			}
			
			@Override
			public void setProperty(String device, String field, String name, String value) throws AdapterException {
			}
			
			@Override
			public void setProperty(String device, String name, String value) throws AdapterException {
			}
			
			@Override
			public void setProperty(String name, String value) throws AdapterException {
			}
			
			@Override
			public void setProperties(Map<String, String> properties) throws AdapterException {
			}
			
			@Override
			public void setLabel(String device, String field, String label) throws AdapterException {
			}
			
			@Override
			public void setLabel(String device, String label) throws AdapterException {
			}
			
			@Override
			public void remove(String device) throws AdapterException {
			}
			
			@Override
			public void open(AdapterListener listener) throws AdapterException {
				HandlerTest.this.listener = listener;
			}
			
			@Override
			public Object getValue(String device, String field) throws AdapterException {
				return null;
			}
			
			@Override
			public Map<String, String> getProperties() throws AdapterException {
				return null;
			}
			
			@Override
			public String getLabel(String device, String field) throws AdapterException {
				return null;
			}
			
			@Override
			public String getLabel(String device) throws AdapterException {
				return null;
			}
			
			@Override
			public Map<String, Device> getDevices() throws AdapterException {
				return null;
			}
			
			@Override
			public void close() throws AdapterException {
			}
			
			@Override
			public String add(Device device) throws AdapterException {
				return null;
			}
		});
		
		AdapterHandler handler1 = manager.createInstance();
		handler1.setListener(new AdapterListener() {
			
			@Override
			public void valueChanged(Adapter source, FieldValueChangedEvent event) {
				System.out.println("1: "+event);
			}
			
			@Override
			public void usabilityChanged(Adapter source, FieldUsabilityChangedEvent event) {
				System.out.println("1: "+event);
			}
			
			@Override
			public void usabilityChanged(Adapter source, DeviceUsabilityChangedEvent event) {
				System.out.println("1: "+event);
			}
		});
		AdapterHandler handler2 = manager.createInstance();
		handler2.setListener(new AdapterListener() {
			
			@Override
			public void valueChanged(Adapter source, FieldValueChangedEvent event) {
				System.out.println("2: "+event);
			}
			
			@Override
			public void usabilityChanged(Adapter source, FieldUsabilityChangedEvent event) {
				System.out.println("2: "+event);
			}
			
			@Override
			public void usabilityChanged(Adapter source, DeviceUsabilityChangedEvent event) {
				System.out.println("2: "+event);
			}
		});
		handler1.subscribe("device1", "field1");
		handler2.subscribe("device1", "field1");
		listener.usabilityChanged(adapter, new DeviceUsabilityChangedEvent("device1", true));
		listener.usabilityChanged(adapter, new FieldUsabilityChangedEvent("device1", "field1", true));
		listener.valueChanged(adapter, new FieldValueChangedEvent("device1", "field1", "value"));
	}
}