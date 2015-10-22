'''
Created on Nov 20, 2014

@author: ph
'''

try:
    import RPi.GPIO as GPIO, time
except RuntimeError:
    print("Error importing RPi.GPIO!  This is probably because you need superuser privileges.  You can achieve this by using 'sudo' to run your script")

import os
import glob

os.system('modprobe w1-gpio')
os.system('modprobe w1-therm')

base_dir = '/sys/bus/w1/devices/'
device_folder = glob.glob(base_dir + '28*')[0]
device_file = device_folder + '/w1_slave'

GPIO.setmode(GPIO.BCM)

LED_PIN1=16
LED_PIN2=20
LED_PIN3=21
Temperature_PIN=4

def setup():
   # chan_list=[LED_PIN1, LED_PIN2, LED_PIN3]
    #GPIO.setup(chan_list, GPIO.OUT,initial=GPIO.LOW)
    
    GPIO.setup(Temperature_PIN, GPIO.IN)

def cleanup():
    GPIO.cleanup()


def read_temp_raw():
    f = open(device_file, 'r')
    lines = f.readlines()
    f.close()
    return lines
 
def read_temp():
    lines = read_temp_raw()
    while lines[0].strip()[-3:] != 'YES':
        time.sleep(0.2)
        lines = read_temp_raw()
    equals_pos = lines[1].find('t=')
    if equals_pos != -1:
        temp_string = lines[1][equals_pos+2:]
        temp_c = float(temp_string) / 1000.0
        temp_f = temp_c * 9.0 / 5.0 + 32.0
        return temp_c, temp_f
        


if __name__ == '__main__':
    setup()
    
    while True:
        print(read_temp())
        time.sleep(1)
        

    cleanup()
    
    
    