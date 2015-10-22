package edu.rit.csci759.rspi.utils;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class MCP3008ADCReader {
	private final static boolean DISPLAY_DIGIT = false;
	// Note: "Mismatch" 23-24. The wiring says DOUT->#23, DIN->#24
	// 23: DOUT on the ADC is IN on the GPIO. ADC:Slave, GPIO:Master
	// 24: DIN on the ADC, OUT on the GPIO. Same reason as above.
	// SPI: Serial Peripheral Interface
	private static Pin spiClk  = RaspiPin.GPIO_01; // Pin #18, clock
	private static Pin spiMiso = RaspiPin.GPIO_04; // Pin #23, data in.  MISO: Master In Slave Out
	private static Pin spiMosi = RaspiPin.GPIO_05; // Pin #24, data out. MOSI: Master Out Slave In
	private static Pin spiCs   = RaspiPin.GPIO_06; // Pin #25, Chip Select

	public enum MCP3008_input_channels
	{
		CH0(0),
		CH1(1),
		CH2(2),
		CH3(3),
		CH4(4),
		CH5(5),
		CH6(6),
		CH7(7);

		private int ch;

		MCP3008_input_channels(int chNum)
		{
			this.ch = chNum;
		}

		public int ch() { return this.ch; }
	}

	private static GpioPinDigitalInput  misoInput        = null;
	private static GpioPinDigitalOutput mosiOutput       = null;
	private static GpioPinDigitalOutput clockOutput      = null;
	private static GpioPinDigitalOutput chipSelectOutput = null;



	public static void initSPI(GpioController gpio){
		mosiOutput       = gpio.provisionDigitalOutputPin(spiMosi, "MOSI", PinState.LOW);
		clockOutput      = gpio.provisionDigitalOutputPin(spiClk,  "CLK",  PinState.LOW);
		chipSelectOutput = gpio.provisionDigitalOutputPin(spiCs,   "CS",   PinState.LOW);

		misoInput        = gpio.provisionDigitalInputPin(spiMiso, "MISO");
	}

	public static int readAdc(int channel)
	{
		chipSelectOutput.high();
		clockOutput.low();
		chipSelectOutput.low();

		int adccommand = channel;
		adccommand |= 0x18; // 0x18: 00011000
		adccommand <<= 3;
		// Send 5 bits: 8 - 3. 8 input channels on the MCP3008.
		for (int i=0; i<5; i++) //
		{
			if ((adccommand & 0x80) != 0x0) // 0x80 = 0&10000000
				mosiOutput.high();
			else
				mosiOutput.low();
			adccommand <<= 1;      
			clockOutput.high();
			clockOutput.low();      
		}

		int adcOut = 0;
		for (int i=0; i<12; i++) // Read in one empty bit, one null bit and 10 ADC bits
		{
			clockOutput.high();
			clockOutput.low();      
			adcOut <<= 1;

			if (misoInput.isHigh())
			{
				//	      System.out.println("    " + misoInput.getName() + " is high (i:" + i + ")");
				// Shift one bit on the adcOut
				adcOut |= 0x1;
			}
			if (DISPLAY_DIGIT)
				System.out.println("ADCOUT: 0x" + Integer.toString(adcOut, 16).toUpperCase() + 
						", 0&" + Integer.toString(adcOut, 2).toUpperCase());
		}
		chipSelectOutput.high();

		adcOut >>= 1; // Drop first bit
		return adcOut;
	}

	public static String lpad(String str, String with, int len)
	{
		String s = str;
		while (s.length() < len)
			s = with + s;
		return s;
	}

}
