package edu.rit.csci759.rspi;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/* Color scheme if using tri-color LED
 * yellow  = R+G
 * cyan    = G+B
 * magenta = R+B
 * white   = R+G+B
 */

public class Rpi3ColorLED
{
	private static final BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

	// create gpio controller
	final static GpioController gpio = GpioFactory.getInstance();

	final static GpioPinDigitalOutput greenPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27, "green", PinState.LOW);
	final static GpioPinDigitalOutput yellowPin  = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28, "yellow",  PinState.LOW);
	final static GpioPinDigitalOutput redPin   = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_29, "red",   PinState.LOW);

	public static String userInput(String prompt)
	{
		String retString = "";
		System.out.print(prompt);
		try{
			retString = stdin.readLine();
		}catch(Exception e){
			System.out.println(e);
			String s;
			try{
				s = userInput("<Oooch/>");
			}catch(Exception exception){
				exception.printStackTrace();
			}
		}
		return retString;
	}

	private static void blink3LED(int bc) throws InterruptedException{
		/*
		 * the following code for blink them in sequence
		 */
		System.out.println("Blinking all LED for "+bc+" times...");
		int count=0;
		while (count<bc){
			greenPin.toggle();
			Thread.sleep(300);
			yellowPin.toggle();
			Thread.sleep(300);
			redPin.toggle();
			Thread.sleep(300);
			count++;
		}
		LEDsOff();
	}

	private static void LEDsOff(){
		// Switch them off
		redPin.low();
		greenPin.low();
		yellowPin.low();
	}

	public static void main(String[] args) throws InterruptedException {
		System.out.println("GPIO Control - pin 27, 28 & 29 ... started.");

		blink3LED(5);
		

		boolean continue_run = true;
		while (continue_run){
			String s = userInput("R(ed), G(reen), Y(ellow), L(oop), or Q(uit) > ");
			if ("R".equals(s.toUpperCase()))
				redPin.toggle();
			else if ("G".equals(s.toUpperCase()))
				greenPin.toggle();
			else if ("Y".equals(s.toUpperCase()))
				yellowPin.toggle();
			else if ("L".equals(s.toUpperCase()))
				blink3LED(5);
			else if ("QUIT".equals(s.toUpperCase()) || "Q".equals(s.toUpperCase()))
				continue_run = false;
			else
				System.out.println("Unknown command [" + s + "]");
		}


		// Switch them off
		LEDsOff();
		// stop all GPIO activity/threads by shutting down the GPIO controller
		// (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
		gpio.shutdown();
	}
}
