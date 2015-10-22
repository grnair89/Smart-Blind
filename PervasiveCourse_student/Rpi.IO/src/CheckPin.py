#!/bin/python

'''
Created on Nov 18, 2014

@author: ph
'''

try:
    import RPi.GPIO as GPIO
except RuntimeError:
    print("Error importing RPi.GPIO!  This is probably because you need superuser privileges.  You can achieve this by using 'sudo' to run your script")


pin = input('Enter pin: ')


GPIO.setmode(GPIO.BCM)
func = GPIO.gpio_function(pin)

print ("Pin %d in mode: " % pin)
if func == GPIO.IN:
    print "INPUT"
elif func == GPIO.OUT:
    print "OUTPUT"
elif func == GPIO.SPI:
    print "SPI"
elif func == GPIO.I2C:
    print "I2C"
elif func == GPIO.HARD_PWM:
    print "Hard_PWN"
elif func == GPIO.SERIAL:
    print "Serial"
elif func == GPIO.UNKNOWN:
    print "Unknown"
    
        





if __name__ == '__main__':
    pass