'''
Created on Nov 20, 2014

@author: ph
'''

# example of using MCP3008 ADC to read value from photocell and tmp36

try:
    import RPi.GPIO as GPIO, time
except RuntimeError:
    print("Error importing RPi.GPIO!  This is probably because you need superuser privileges.  You can achieve this by using 'sudo' to run your script")

import plotly.plotly as py
import json
import datetime
import readadc

GPIO.setmode(GPIO.BCM)

LED_RED=21
LED_YELLOW=20
LED_GREEN=16
chan_list=[LED_RED, LED_YELLOW, LED_GREEN]

AMBIENT_DARK=35;
AMBIENT_BRIGHT=80;


def setup():
    
    GPIO.setup(chan_list, GPIO.OUT,initial=GPIO.LOW)
    
    readadc.initialize()


def cleanup():
    GPIO.cleanup()


def led_all_off():
    GPIO.output(chan_list, GPIO.LOW)

def led_all_on():
    GPIO.output(chan_list, GPIO.HIGH)

def led_when_low():
    GPIO.output(LED_RED, GPIO.HIGH)
    GPIO.output(LED_YELLOW, GPIO.LOW)
    GPIO.output(LED_GREEN, GPIO.LOW)

def led_when_mid():
    GPIO.output(LED_YELLOW, GPIO.HIGH)
    GPIO.output(LED_RED, GPIO.LOW)
    GPIO.output(LED_GREEN, GPIO.LOW)

def led_when_high():
    GPIO.output(LED_GREEN, GPIO.HIGH)
    GPIO.output(LED_RED, GPIO.LOW)
    GPIO.output(LED_YELLOW, GPIO.LOW)
    
def led_error(blink_count):
    bc=0
    while bc<blink_count:
        GPIO.output(LED_RED, GPIO.HIGH)
        time.sleep(1)
        GPIO.output(LED_RED, GPIO.LOW) 
        time.sleep(1)
        bc+=1

def get_temp(adc_pin):
    tmp36_data = readadc.readadc(adc_pin,
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
    
    return temp_F

def get_intensity(adc_pin):
    photo_data = readadc.readadc(adc_pin,
                            readadc.PINS.SPICLK,
                            readadc.PINS.SPIMOSI,
                            readadc.PINS.SPIMISO,
                            readadc.PINS.SPICS)
        
    # convert light intensity value to 1-100
    photo_millivolts = photo_data * (3300.0/1024.0)
    light_value = (photo_millivolts * (100.0/3300.0))
    return light_value






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
    
    
    
    while True:
        light_value = get_intensity(1)
        print light_value
        if(light_value<=AMBIENT_DARK and light_value>=0):
            led_when_low();
        elif (light_value>AMBIENT_DARK and light_value<AMBIENT_BRIGHT):
            led_when_mid();
        elif(light_value>=AMBIENT_BRIGHT and light_value<=100):
            led_when_high();
        else:
            led_error(3);
            
        stream.write({'x': datetime.datetime.now(), 'y': ("%.1f" % light_value)})
        stream1.write({'x': datetime.datetime.now(), 'y': get_temp(0)})
        #stream2.write({'x': datetime.datetime.now(), 'y': read_temp(sensor2_file)[0]})
        time.sleep(1)






if __name__ == '__main__':
    setup()
    
    
    stream_to_plotly()
        
    
    cleanup()
    
    