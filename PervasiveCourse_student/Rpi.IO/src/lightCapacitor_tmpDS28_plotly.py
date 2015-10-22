'''
Created on Nov 20, 2014

@author: ph
'''

try:
    import RPi.GPIO as GPIO, time
except RuntimeError:
    print("Error importing RPi.GPIO!  This is probably because you need superuser privileges.  You can achieve this by using 'sudo' to run your script")

import plotly.plotly as py
import json
import datetime
import os
import glob

os.system('modprobe w1-gpio')
os.system('modprobe w1-therm')

base_dir = '/sys/bus/w1/devices/'
sensor1_folder = glob.glob(base_dir + '28*')[0]
sensor1_file = sensor1_folder + '/w1_slave'

sensor2_folder = glob.glob(base_dir + '28*')[1]
sensor2_file = sensor2_folder + '/w1_slave'


GPIO.setmode(GPIO.BCM)

LED_PIN1=16
LED_PIN2=20
LED_PIN3=21
Photocell_PIN=13
Temperature_PIN=4

def setup():
    chan_list=[LED_PIN1, LED_PIN2, LED_PIN3]
    GPIO.setup(chan_list, GPIO.OUT,initial=GPIO.LOW)
    
    GPIO.setup(Photocell_PIN, GPIO.IN)
    GPIO.setup(Temperature_PIN, GPIO.IN)

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

def translate(value, leftMin, leftMax, rightMin, rightMax):
    print value
    # Figure out how 'wide' each range is
    leftSpan = leftMax - leftMin
    rightSpan = rightMax - rightMin

    # Convert the left range into a 0-1 range (float)
    valueScaled = float(value - leftMin) / float(leftSpan)

    # Convert the 0-1 range into a value in the right range.
    return rightMin + (valueScaled * rightSpan)

def read_temp_raw(sensor_file):
    f = open(sensor_file, 'r')
    lines = f.readlines()
    f.close()
    return lines
 
def read_temp(sensor_file):
    lines = read_temp_raw(sensor_file)
    while lines[0].strip()[-3:] != 'YES':
        time.sleep(0.2)
        lines = read_temp_raw()
    equals_pos = lines[1].find('t=')
    if equals_pos != -1:
        temp_string = lines[1][equals_pos+2:]
        temp_c = float(temp_string) / 1000.0
        temp_f = temp_c * 9.0 / 5.0 + 32.0
        return temp_c, temp_f



def stream_to_plotly():
    with open('./config.json') as config_file:
        plotly_user_config = json.load(config_file)

    py.sign_in(plotly_user_config["plotly_username"], plotly_user_config["plotly_api_key"])
    
    url = py.plot([
    {
        'x': [], 'y': [], 'type': 'scatter',
        'stream': {
            'token': plotly_user_config['plotly_streaming_tokens'][0],
            'maxpoints': 200
        }
    },
    {
        'x': [], 'y': [], 'type': 'scatter',
        'stream': {
            'token': plotly_user_config['plotly_streaming_tokens'][1],
            'maxpoints': 200
        }
    }, 
    {
        'x': [], 'y': [], 'type': 'scatter',
        'stream': {
            'token': plotly_user_config['plotly_streaming_tokens'][2],
            'maxpoints': 200
        }
    }], filename='Raspberry Pi Light Intensity Streaming')
    
    print "View your streaming graph here: ", url
    
    stream = py.Stream(plotly_user_config['plotly_streaming_tokens'][0])
    stream.open()
    
    stream1 = py.Stream(plotly_user_config['plotly_streaming_tokens'][1])
    stream1.open()
    
    stream2 = py.Stream(plotly_user_config['plotly_streaming_tokens'][2])
    stream2.open()
        
#         sensor_data = readadc.readadc(Photocell_PIN,
#                                   readadc.PINS.SPICLK,
#                                   readadc.PINS.SPIMOSI,
#                                   readadc.PINS.SPIMISO,
#                                   readadc.PINS.SPICS)

    LED_ON=False
    while True:
        if LED_ON:
            GPIO.output(LED_PIN1, GPIO.HIGH)
            LED_ON=True
        else:
            GPIO.output(LED_PIN1, GPIO.LOW)
            LED_ON=False
            
        stream.write({'x': datetime.datetime.now(), 'y': translate(RCtime(), 0, 700000, 0, 200)})
        stream1.write({'x': datetime.datetime.now(), 'y': read_temp(sensor1_file)[0]})
        stream2.write({'x': datetime.datetime.now(), 'y': read_temp(sensor2_file)[0]})
        time.sleep(5)






if __name__ == '__main__':
    setup()
    
    
    stream_to_plotly()
        
    
    cleanup()
    
    