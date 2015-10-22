'''
Created on Nov 18, 2014

@author: ph
'''
from time import sleep

try:
    import RPi.GPIO as GPIO
except RuntimeError:
    print("Error importing RPi.GPIO!  This is probably because you need superuser privileges.  You can achieve this by using 'sudo' to run your script")

GPIO.setmode(GPIO.BCM)


LED_PIN1=16
LED_PIN2=20
LED_PIN3=21


blink_time = input('Enter blink time: ')



def setup():
    chan_list=[LED_PIN1, LED_PIN2, LED_PIN3]
    GPIO.setup(chan_list, GPIO.OUT,initial=GPIO.LOW)


def rainbow_blink():
    for i in range(1,blink_time,1):
        for p in (LED_PIN1, LED_PIN2, LED_PIN3):
            GPIO.output(p, GPIO.HIGH)
            sleep(0.5)
            GPIO.output(p, GPIO.LOW)
        sleep(1)


def cleanup():
    GPIO.cleanup()



if __name__ == '__main__':
    setup()
    rainbow_blink()
    
    cleanup()