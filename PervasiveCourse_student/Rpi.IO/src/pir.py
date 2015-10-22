'''
Created on Nov 20, 2014

@author: ph
'''

try:
    import RPi.GPIO as GPIO, time 
except RuntimeError:
    print("Error importing RPIO!  This is probably because you need superuser privileges.  You can achieve this by using 'sudo' to run your script")

GPIO.setmode(GPIO.BCM)

PIR_PIN = 26
LED_PIN1=16
LED_PIN2=20
LED_PIN3=21
MOTOR_PIN=12


def setup():
    GPIO.setup(PIR_PIN, GPIO.IN)

    chan_list=[LED_PIN1, LED_PIN2, LED_PIN3]
    GPIO.setup(chan_list, GPIO.OUT, initial=GPIO.LOW)
   
    #GPIO.setup(MOTOR_PIN, GPIO.OUT, initial=GPIO.LOW)
    

def cleanup():
    GPIO.cleanup()



def check_alarm():
    while True:
        if GPIO.input(PIR_PIN):
            GPIO.output(LED_PIN3, GPIO.LOW)
            GPIO.output(LED_PIN2, GPIO.HIGH)
            #GPIO.output(MOTOR_PIN, GPIO.HIGH)
            #time.sleep(0.5)
            #GPIO.output(MOTOR_PIN, GPIO.LOW)
        else:
            print "PIR alarm!"
            GPIO.output(LED_PIN2, GPIO.LOW)
            GPIO.output(LED_PIN3, GPIO.HIGH)
        time.sleep(0.5)
    

if __name__ == '__main__':
    setup()
    
    check_alarm()
    
    
    cleanup()