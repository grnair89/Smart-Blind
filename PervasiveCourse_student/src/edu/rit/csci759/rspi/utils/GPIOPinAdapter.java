package edu.rit.csci759.rspi.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinShutdown;
import com.pi4j.io.gpio.GpioProvider;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinListener;

public class GPIOPinAdapter implements GpioPinDigitalOutput
{
	protected final GpioController gpio = GpioFactory.getInstance();
	protected final GpioPinDigitalOutput pin;  

	public GPIOPinAdapter(Pin p, String name, PinState originalState)
	{
		super();
		pin = gpio.provisionDigitalOutputPin(p, name, originalState);  
	}

	@Override
	public void high()
	{
		pin.high();
	}

	@Override
	public void low()
	{
		pin.low();
	}

	@Override
	public void toggle()
	{
		pin.toggle();
	}

	@Override
	public Future<?> blink(long delay)
	{
		return pin.blink(delay);
	}

	@Override
	public Future<?> blink(long delay, PinState blinkState)
	{
		return pin.blink(delay, blinkState);
	}

	@Override
	public Future<?> blink(long delay, long duration)
	{
		return pin.blink(delay, duration);
	}

	@Override
	public Future<?> blink(long delay, long duration, PinState blinkState)
	{
		return pin.blink(delay, duration, blinkState);
	}

	@Override
	public Future<?> pulse(long duration)
	{
		return pin.pulse(duration);
	}

	@Override
	public Future<?> pulse(long duration, boolean blocking)
	{
		return pin.pulse(duration, blocking);
	}

	@Override
	public Future<?> pulse(long duration, PinState pulseState)
	{
		return pin.pulse(duration, pulseState);
	}

	@Override
	public Future<?> pulse(long duration, PinState pulseState, boolean blocking)
	{
		return pin.pulse(duration, pulseState, blocking);
	}

	@Override
	public void setState(PinState state)
	{
		pin.setState(state);
	}

	@Override
	public void setState(boolean state)
	{
		pin.setState(state);
	}

	@Override
	public boolean isHigh()
	{
		return pin.isHigh();
	}

	@Override
	public boolean isLow()
	{
		return pin.isLow();
	}

	@Override
	public PinState getState()
	{
		return pin.getState();
	}

	@Override
	public boolean isState(PinState state)
	{
		return pin.isState(state);
	}

	@Override
	public GpioProvider getProvider()
	{
		return pin.getProvider();
	}

	@Override
	public Pin getPin()
	{
		return pin.getPin();
	}

	@Override
	public void setName(String name)
	{
		pin.setName(name);
	}

	@Override
	public String getName()
	{
		return pin.getName();
	}

	@Override
	public void setTag(Object tag)
	{
		pin.setTag(tag);
	}

	@Override
	public Object getTag()
	{
		return pin.getTag();
	}

	@Override
	public void setProperty(String key, String value)
	{
		pin.setProperty(key, value);
	}

	@Override
	public boolean hasProperty(String key)
	{
		return pin.hasProperty(key);
	}

	@Override
	public String getProperty(String key)
	{
		return pin.getProperty(key);
	}

	@Override
	public String getProperty(String key, String defaultValue)
	{
		return pin.getProperty(key, defaultValue);
	}

	@Override
	public Map<String, String> getProperties()
	{
		//  return Collections.emptyMap();
		return pin.getProperties();
	}

	@Override
	public void removeProperty(String key)
	{
		pin.removeProperty(key);
	}

	@Override
	public void clearProperties()
	{
		pin.clearProperties();
	}

	@Override
	public void export(PinMode mode)
	{
		pin.export(mode);
	}

	@Override
	public void unexport()
	{
		pin.unexport();
	}

	@Override
	public boolean isExported()
	{
		return pin.isExported();
	}

	@Override
	public void setMode(PinMode mode)
	{
		pin.setMode(mode);
	}

	@Override
	public PinMode getMode()
	{
		return pin.getMode();
	}

	@Override
	public boolean isMode(PinMode mode)
	{
		return pin.isMode(mode);
	}

	@Override
	public void setPullResistance(PinPullResistance resistance)
	{
		pin.setPullResistance(resistance);
	}

	@Override
	public PinPullResistance getPullResistance()
	{
		return pin.getPullResistance();
	}

	@Override
	public boolean isPullResistance(PinPullResistance resistance)
	{
		return pin.isPullResistance(resistance);
	}

	@Override
	public GpioPinShutdown getShutdownOptions()
	{
		return pin.getShutdownOptions();
	}

	@Override
	public void setShutdownOptions(GpioPinShutdown options)
	{
		pin.setShutdownOptions(options);
	}

	@Override
	public void setShutdownOptions(Boolean unexport)
	{
		pin.setShutdownOptions(unexport);
	}

	@Override
	public void setShutdownOptions(Boolean unexport, PinState state)
	{
		pin.setShutdownOptions(unexport, state);
	}

	@Override
	public void setShutdownOptions(Boolean unexport, PinState state, PinPullResistance resistance)
	{
		pin.setShutdownOptions(unexport, state, resistance);
	}

	@Override
	public void setShutdownOptions(Boolean unexport, PinState state, PinPullResistance resistance, PinMode mode)
	{
		pin.setShutdownOptions(unexport, state, resistance, mode);
	}

	@Override
	public void addListener(GpioPinListener... arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addListener(List<? extends GpioPinListener> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<GpioPinListener> getListeners() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasListener(GpioPinListener... arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeAllListeners() {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeListener(GpioPinListener... arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeListener(List<? extends GpioPinListener> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public Future<?> pulse(long arg0, Callable<Void> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> pulse(long arg0, boolean arg1, Callable<Void> arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> pulse(long arg0, PinState arg1, Callable<Void> arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> pulse(long arg0, PinState arg1, boolean arg2,
			Callable<Void> arg3) {
		// TODO Auto-generated method stub
		return null;
	}
}