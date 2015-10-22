package edu.rit.csci759.rspi;


import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

import edu.rit.csci759.rspi.utils.MCP3008ADCReader;

public class RpiSensorViaMCP3008 {

	private final static boolean DEBUG         = false;
	private static boolean keepRunning = true;

	public static void main(String[] args)
	{
		GpioController gpio = GpioFactory.getInstance();
		MCP3008ADCReader.initSPI(gpio);

		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			public void run()
			{
				System.out.println("Shutting down.");
				keepRunning = false;
			}
		});
		
		while (keepRunning)
		{
			/*
			 * Reading ambient light from the photocell sensor using the MCP3008 ADC 
			 */
			int adc_ambient = MCP3008ADCReader.readAdc(MCP3008ADCReader.MCP3008_input_channels.CH1.ch());
			// [0, 1023] ~ [0x0000, 0x03FF] ~ [0&0, 0&1111111111]
			// convert in the range of 1-100
			int ambient = (int)(adc_ambient / 10.24); 
			
			if (DEBUG){
				System.out.println("readAdc:" + Integer.toString(adc_ambient) + 
						" (0x" + MCP3008ADCReader.lpad(Integer.toString(adc_ambient, 16).toUpperCase(), "0", 2) + 
						", 0&" + MCP3008ADCReader.lpad(Integer.toString(adc_ambient, 2), "0", 8) + ")");        
				System.out.println("Ambient:" + ambient + "/100 (" + adc_ambient + "/1024)");
			}
			
			
			/*
			 * Reading temperature from the TMP36 sensor using the MCP3008 ADC 
			 */
			int adc_temperature = MCP3008ADCReader.readAdc(MCP3008ADCReader.MCP3008_input_channels.CH0.ch());
			// [0, 1023] ~ [0x0000, 0x03FF] ~ [0&0, 0&1111111111]
			// convert in the range of 1-100
			int temperature = (int)(adc_temperature / 10.24); 
			
			if (DEBUG){
				System.out.println("readAdc:" + Integer.toString(adc_temperature) + 
						" (0x" + MCP3008ADCReader.lpad(Integer.toString(adc_temperature, 16).toUpperCase(), "0", 2) + 
						", 0&" + MCP3008ADCReader.lpad(Integer.toString(adc_temperature, 2), "0", 8) + ")");        
				System.out.println("Temperature:" + temperature + "/100 (" + adc_temperature + "/1024)");
			}
			
			float tmp36_mVolts =(float) (adc_temperature * (3300.0/1024.0));
			// 10 mv per degree
	        float temp_C = (float) (((tmp36_mVolts - 100.0) / 10.0) - 40.0);
	        // convert celsius to fahrenheit
	        float temp_F = (float) ((temp_C * 9.0 / 5.0) + 32);
	        
	        System.out.println("Ambient:" + ambient + "/100; Temperature:"+temperature+"/100 => "+String.valueOf(temp_C)+"C => "+String.valueOf(temp_F)+"F");
			

			try { Thread.sleep(500L); } catch (InterruptedException ie) { ie.printStackTrace(); }
		}
		System.out.println("Bye...");
		gpio.shutdown();
	}   



}
