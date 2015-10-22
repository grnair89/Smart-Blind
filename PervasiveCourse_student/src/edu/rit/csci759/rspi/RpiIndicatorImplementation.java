/**
 * @author Harsh Patil
 * @author Shikha Soni
 */
package edu.rit.csci759.rspi;


import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import edu.rit.csci759.rspi.utils.MCP3008ADCReader;

public class RpiIndicatorImplementation implements RpiIndicatorInterface {
	 final static GpioController gpio  = GpioFactory.getInstance();;
	 final static GpioPinDigitalOutput pin27 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27, "MyLED", PinState.LOW);
	 final static GpioPinDigitalOutput pin28 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28, "MyLED", PinState.LOW);
	 final static GpioPinDigitalOutput pin29 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_29, "MyLED", PinState.LOW);  
	
	 static {
		 MCP3008ADCReader.initSPI(gpio);
	 }
	 public RpiIndicatorImplementation(){
		System.out.println("<--Pi4J--> GPIO Control Example ... started.");
		
        // provision gpio pin #01 as an output pin and turn on by default
       
	}
	
	@Override
	public void led_all_off() {
		// TODO Auto-generated method stub
		pin27.low();
		pin28.low();
		pin29.low();
	}

	@Override
	public void led_all_on() {
		// TODO Auto-generated method stub
		pin27.high();
		pin28.high();
		pin29.high();
	}

	@Override
	public void led_error(int blink_count) throws InterruptedException {
		// TODO Auto-generated method stub
		for(int i = 0; i<=blink_count; i++){
			pin29.toggle();
			Thread.sleep(1000);
			pin29.toggle();
		}
		pin29.low();
	}

	@Override
	public void led_when_low() {
		// TODO Auto-generated method stub
		System.out.println("LED low");
		pin29.high();
	}

	@Override
	public void led_when_mid() {
		System.out.println("LED mid");
		// TODO Auto-generated method stub
		pin28.high();
	}

	@Override
	public void led_when_high() {
		System.out.println("LED high");
		// TODO Auto-generated method stub
		pin27.high();
	}

	@Override
	public synchronized int read_ambient_light_intensity() {
		/*
		 * Reading ambient light from the photocell sensor using the MCP3008 ADC 
		 */
		int adc_ambient = MCP3008ADCReader.readAdc(MCP3008ADCReader.MCP3008_input_channels.CH1.ch());
		// [0, 1023] ~ [0x0000, 0x03FF] ~ [0&0, 0&1111111111]
		// convert in the range of 1-100
		int ambient = (int)(adc_ambient / 10.24); 
		//System.out.println(ambient);
		gpio.shutdown();
		return ambient;
	}

	@Override
	public synchronized int read_temperature() {
		/*
		 * Reading temperature from the TMP36 sensor using the MCP3008 ADC 
		 */
		int adc_temperature = MCP3008ADCReader.readAdc(MCP3008ADCReader.MCP3008_input_channels.CH0.ch());
		// [0, 1023] ~ [0x0000, 0x03FF] ~ [0&0, 0&1111111111]
		// convert in the range of 1-100
		int temperature = (int)(adc_temperature / 10.24); 
		
		float tmp36_mVolts =(float) (adc_temperature * (3300.0/1024.0));
		// 10 mv per degree
		float temp_C = (float) (((tmp36_mVolts - 100.0) / 10.0) - 40.0);
		// convert celsius to fahrenheit
		int temp_F = (int) ((temp_C * 9.0 / 5.0) + 32);
			
		gpio.shutdown();
		return temp_F;
	}
}
