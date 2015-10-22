package edu.rit.csci759.rspi.exercise;

public interface RpiIndicatorInterface {
	/*
	 *  Constant thresholds for ambient light intensity
	 */
	static final int AMBIENT_DARK=35;
	static final int AMBIENT_BRIGHT=80;
	
	/*
	 * Constant thresholds for temperature in F
	 */
	static final int TEMPERATURE_COLD=60;
	static final int TEMPERATURE_HOT=76;
	

	/*
	 * function to turn off all LEDs
	 */
	void led_all_off();
	
	/*
	 * function to turn on all LEDs
	 */
	void led_all_on();
	
	/*
	 * function to indicate error; normally blnking red LED
	 */
	void led_error(int blink_count) throws InterruptedException;

	
	/*
	 * Turn on a LED to indicate the value is low 
	 */
	void led_when_low();
	
	/*
	 * Turn on a LED to indicate the value is mid 
	 */
	void led_when_mid();
	
	/*
	 * Turn on a LED to indicate the value is high 
	 */
	void led_when_high();

	
	
	/*
	 * read light intensity value from the photocell
	 */
	int read_ambient_light_intensity();
	
	
	/*
	 * read temperature value from the TMP36 sensor
	 */
	int read_temperature();

}
