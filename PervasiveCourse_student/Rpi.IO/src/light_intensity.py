'''
Created on Nov 20, 2014

@author: ph
'''

try:
    import RPi.GPIO as GPIO, time, os
except RuntimeError:
    print("Error importing RPi.GPIO!  This is probably because you need superuser privileges.  You can achieve this by using 'sudo' to run your script")

GPIO.setmode(GPIO.BCM)

LED_PIN1=16
LED_PIN2=20
LED_PIN3=21
Photocell_PIN=13

def setup():
    chan_list=[LED_PIN1, LED_PIN2, LED_PIN3]
    GPIO.setup(chan_list, GPIO.OUT,initial=GPIO.LOW)
    
    GPIO.setup(Photocell_PIN, GPIO.IN)

def cleanup():
    GPIO.cleanup()


# code for getting a range of value by measuring the time it takes
# to charge a capacitor
def RCtime():
    reading=0;
    GPIO.setup(Photocell_PIN, GPIO.OUT)
    GPIO.output(Photocell_PIN, GPIO.LOW)
    time.sleep(0.1)
    
    GPIO.setup(Photocell_PIN, GPIO.IN)
    while (GPIO.input(Photocell_PIN) == GPIO.LOW):
        reading+=1
    return reading


# a digital read function; it output 0 or 1 depending on whether the
# resistance is above or below the threshold
def digital_read():
    while True:
        if GPIO.input(Photocell_PIN):
            GPIO.output(LED_PIN2, GPIO.LOW)
            GPIO.output(LED_PIN1, GPIO.HIGH)
        else:
            GPIO.output(LED_PIN2, GPIO.HIGH)
            GPIO.output(LED_PIN1, GPIO.LOW)
        time.sleep(0.5)
            
        


if __name__ == '__main__':
    setup()
    
    #digital_read()
    
    while True:
        print RCtime()
        
    
    
    
    cleanup()