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
    }], filename='Raspberry Pi Light Intensity Streaming')
    
    print "View your streaming graph here: ", url
    
    stream = py.Stream(plotly_user_config['plotly_streaming_tokens'][0])
    stream.open()
    
    stream1 = py.Stream(plotly_user_config['plotly_streaming_tokens'][1])
    stream1.open()
        
#         sensor_data = readadc.readadc(Photocell_PIN,
#                                   readadc.PINS.SPICLK,
#                                   readadc.PINS.SPIMOSI,
#                                   readadc.PINS.SPIMISO,
#                                   readadc.PINS.SPICS)
    while True:
        stream.write({'x': datetime.datetime.now(), 'y': RCtime()})
        stream1.write({'x': datetime.datetime.now(), 'y': RCtime()+1})
        time.sleep(0.25)


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
    
    stream_to_plotly()
    
    
    
    
    #while True:
       # print RCtime()

        
        
    
    
    cleanup()