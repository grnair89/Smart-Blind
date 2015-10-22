'''
Created on Nov 20, 2014

@author: ph
'''
import readadc
import time

try:
    import RPi.GPIO as GPIO
except RuntimeError:
    print("Error importing RPi.GPIO!  This is probably because you need superuser privileges.  You can achieve this by using 'sudo' to run your script")

GPIO.setmode(GPIO.BCM)
DEBUG=True
LOGGER=True



# temperature sensor connected channel 0 of mcp3008
tmp36_adc = 0
photo_adc = 1

def setup():
    readadc.initialize()

def loop():
    while True:
        tmp36_data = readadc.readadc(tmp36_adc,
                              readadc.PINS.SPICLK,
                              readadc.PINS.SPIMOSI,
                              readadc.PINS.SPIMISO,
                              readadc.PINS.SPICS)
        
        tmp36_millivolts = tmp36_data * (3300.0/1024.0)
        
        # 10 mv per degree
        temp_C = ((tmp36_millivolts - 100.0) / 10.0) - 40.0
        # convert celsius to fahrenheit
        temp_F = (temp_C * 9.0 / 5.0) + 32
        # remove decimal point from millivolts
        tmp36_millivolts = "%d" % tmp36_millivolts
        # show only one decimal place for temprature and voltage readings
        temp_C = "%.1f" % temp_C
        temp_F = "%.1f" % temp_F
        
        photo_data = readadc.readadc(photo_adc,
                            readadc.PINS.SPICLK,
                            readadc.PINS.SPIMOSI,
                            readadc.PINS.SPIMISO,
                            readadc.PINS.SPICS)
        
        photo_millivolts = photo_data * (100.0/1024.0)
        
        
        print "Current temperature: %sC, %sF; light: %f" % (temp_C, temp_F, photo_millivolts)
        
        time.sleep(0.5)




def cleanup():
    # initial values of variables etc...  
    counter = 0  
  
    try:  
        # here you put your main loop or block of code  
        while counter < 9000000:  
            # count up to 9000000 - takes ~20s  
            counter += 1  
            print "Target reached: %d" % counter  
  
    except KeyboardInterrupt:  
        # here you put any code you want to run before the program   
        # exits when you press CTRL+C  
        print "\n", counter # print value of counter  
  
    except:  
        # this catches ALL other exceptions including errors.  
        # You won't get any error messages for debugging  
        # so only use it once your code is working  
        print "Other error or exception occurred!"  
  
    finally:  
        GPIO.cleanup() # this ensures a clean exit  




if __name__ == '__main__':
    setup()
    
    loop()
    
    
    cleanup()