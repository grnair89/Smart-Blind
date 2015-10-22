'''
Created on Nov 20, 2014

@author: ph
'''

try:
    import RPi.GPIO as GPIO
except RuntimeError:
    print("Error importing RPi.GPIO!  This is probably because you need superuser privileges.  You can achieve this by using 'sudo' to run your script")

GPIO.setmode(GPIO.BCM)
 

def setup():
    pass



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
    
    
    
    
    cleanup()