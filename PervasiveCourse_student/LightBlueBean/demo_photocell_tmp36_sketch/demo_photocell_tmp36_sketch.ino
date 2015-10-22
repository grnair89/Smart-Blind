#include <Servo.h>

#define MAXLEVEL 255
#define MINLEVEL 0
#define LOWLEVEL 85
#define HIGHLEVEL 170

// Some servo can go from 0 to 180 degree, so we need to limit the angle
#define SERVO_LOWER_BOUND 0
#define SERVO_UPPER_BOUND 160

#define button1  13
#define button2  14
#define button3  15
#define scroller 29
 
static int d0 = 0;
static int d1 = 1;
static int d2 = 2;
static int d3 = 3;
static int d4 = 4;
static int d5 = 5;

int motorPin1 =  1;    // One motor wire connected to digital pin 4
int motorPin2 =  2;    // One motor wire connected to digital pin 5

Servo myservo;
int previousLightIntensity=0;


void setup()
{
    // initialize serial communication
    Serial.begin(57600);
    Serial.setTimeout(25);
    
    // Digital pins
    pinMode(d0, INPUT_PULLUP);  
    //pinMode(d1, INPUT_PULLUP);  
    //pinMode(d2, INPUT_PULLUP);  
    pinMode(motorPin1, OUTPUT); 
    pinMode(motorPin2, OUTPUT);
    //pinMode(d3, OUTPUT); 
    pinMode(d4, INPUT_PULLUP);
    pinMode(d5, INPUT_PULLUP);
    // initialize the digital pins as an output:
    
    
    // attaches the servo on pin 3 to the servo object
    myservo.attach(3);
    //myservo.write(0); //set servo to init point
}

void all_on()
{
  Bean.setLed(255, 255, 255);
}

void all_off()
{
  Bean.setLed(0, 0, 0); 
}

void blink_red()
{
   Bean.setLed(255, 0, 0);
   Bean.sleep(500);
   Bean.setLed(0, 0, 0);
}

void blink_green()
{
   Bean.setLed(0, 255, 0);
   Bean.sleep(500);
   Bean.setLed(0, 0, 0);
}

void blink_blue()
{
   Bean.setLed(0, 0, 255);
   Bean.sleep(500);
   Bean.setLed(0, 0, 0);
}

void blink_3_color()
{
  Bean.setLed(255, 0, 0);
  Bean.sleep(500);
  Bean.setLed(0, 255, 0);
  Bean.sleep(500);
  Bean.setLed(0, 0, 255);
  Bean.sleep(500);
}

void fade_3_color()
{
  int r,g,b;
  for(int i=0;i<=30;i++){
    r=random(0,255);
    g=random(0,255);
    b=random(0,255);
    
    Bean.setLed(r, g, b);
    Bean.sleep(10);
  }
}

void blink_two_loop()
{
  Bean.setLed(255, 0, 0);
  Bean.sleep(500);
  Bean.setLed(0, 255, 0);
  Bean.sleep(500);
  Bean.setLed(0, 0, 255);
  Bean.sleep(500);
  Bean.setLed(0, 255, 0);
  Bean.sleep(500);
  Bean.setLed(255, 0, 0);
  Bean.sleep(500);
}

//void rotateLeft(int speedOfRotate, int length){
//  analogWrite(motorPin1, speedOfRotate); //rotates motor
//  digitalWrite(motorPin2, LOW);    // set the Pin motorPin2 LOW
//  delay(length); //waits
//  digitalWrite(motorPin1, LOW);    // set the Pin motorPin1 LOW
//}
//
//void rotateRight(int speedOfRotate, int length){
//  analogWrite(motorPin2, speedOfRotate); //rotates motor
//  digitalWrite(motorPin1, LOW);    // set the Pin motorPin1 LOW
//  delay(length); //waits
//  digitalWrite(motorPin2, LOW);    // set the Pin motorPin2 LOW
//}
//
//void rotateLeftFull(int length){
//  digitalWrite(motorPin1, HIGH); //rotates motor
//  digitalWrite(motorPin2, LOW);    // set the Pin motorPin2 LOW
//  delay(length); //waits
//  digitalWrite(motorPin1, LOW);    // set the Pin motorPin1 LOW
//}
//
//void rotateRightFull(int length){
//  digitalWrite(motorPin2, HIGH); //rotates motor
//  digitalWrite(motorPin1, LOW);    // set the Pin motorPin1 LOW
//  delay(length); //waits
//  digitalWrite(motorPin2, LOW);    // set the Pin motorPin2 LOW
//}

void turn_servo(int degree, int speedOfTurn)
{
   myservo.write(degree);                  // sets the servo position according to the scaled value 
   delay(speedOfTurn); 
}

void servo_sweep(){
 for(int pos = SERVO_LOWER_BOUND; pos < SERVO_UPPER_BOUND; pos += 1)  // goes from 0 degrees to 180 degrees 
  {                                  // in steps of 1 degree 
    myservo.write(pos);              // tell servo to go to position in variable 'pos' 
    delay(5);                       // waits 15ms for the servo to reach the position 
  } 
  for(int pos = SERVO_UPPER_BOUND; pos>=SERVO_LOWER_BOUND; pos-=1)     // goes from 180 degrees to 0 degrees 
  {                                
    myservo.write(pos);              // tell servo to go to position in variable 'pos' 
    delay(5);                       // waits 15ms for the servo to reach the position 
  }  
}

// the loop routine runs over and over again forever:
void loop()
{ 
    char buffer[64];
    size_t readLength = 64;
    uint8_t length = 0;
  
    int analog0 = analogRead(A0);
    int lightIntensity = map(analog0, 0, 1023, 0, 254);
    int analog1 = analogRead(A1);
    
    // intensity indicator
    if (lightIntensity > MAXLEVEL) {
      blink_red();
    } else if(lightIntensity < MINLEVEL) {
      blink_green();
    } else if (lightIntensity >= HIGHLEVEL) {
        Bean.setLed(0, 255, 0);
    } else if (lightIntensity <= LOWLEVEL) {
        Bean.setLed(255, 0, 0);
    } else if (lightIntensity > LOWLEVEL && lightIntensity< HIGHLEVEL) {
        Bean.setLed(250, 129, 0);
    } else {
      blink_blue();
    }
    
    // servo_sweep();
    
    if(lightIntensity < MAXLEVEL && lightIntensity > MINLEVEL){
      if(abs(lightIntensity-previousLightIntensity)>10){
        int servoPos=map(lightIntensity, 0, 254, SERVO_LOWER_BOUND, SERVO_UPPER_BOUND);
        turn_servo(servoPos, 1);
      }
      previousLightIntensity=lightIntensity;
    }
   
    
    length = Serial.readBytes(buffer, readLength);    
  
    // Return all the serial pins
    // All of these features other than the digital/analog pin inputs are performed 
    // behind-the-scenes using our Apple Bean SDK.  The digital/analog pin inputs are 
    // read from a sketch that’s uploaded to the Bean.  These pins use a simple command/response 
    // structure.  The client (iPhone) sends the decimal value ‘2’ to request the pin states.  
    // After receiving this command, the Arduino probes the pins and sends the following info back to the client:
    // byte 0: 0x82 – Response to 0x02 (decimal value ‘2’ in hexadecimal)
    // byte 1: the digital pin values as bits b0 to b5 in the order d0 to d5
    // byte 2: the low-byte of analog pin 1
    // byte 3: the high-byte of analog pin 1
    // byte 4: the low-byte of analog pin 2
    // byte 5: the high-byte of analog pin 2
    if ( 0x02 == buffer[0] && 1 == length)
    {  
      //pinMode(d0, INPUT_PULLUP);  //PD6
      //pinMode(d1, INPUT_PULLUP);  //PB1
      //pinMode(d2, INPUT_PULLUP);  //PB2
      //pinMode(d3, INPUT_PULLUP); 
      pinMode(d4, INPUT_PULLUP);
      pinMode(d5, INPUT_PULLUP);
      
      int digital0 = 0;//digitalRead(d0);
      int digital1 = 0; //digitalRead(d1);
      int digital2 = 0; // digitalRead(d2);
      int digital3 = 0; //digitalRead(d3);
      int digital4 =  digitalRead(d4);
      int digital5 =  digitalRead(d5);
    
      uint8_t digitalAll = digital0 | ( digital1 << 1 ) | ( digital2 << 2 ) | ( digital3 << 3);
      digitalAll |= ( digital4 << 4 ) | ( digital5 << 5 );
      
      buffer[0] = 0x82;
      buffer[1] = digitalAll;
      buffer[2] = analog0 & 0xFF;
      buffer[3] = analog0 >> 8;
      buffer[4] = analog1 & 0xFF;
      buffer[5] = analog1 >> 8;
    
      Serial.write((uint8_t*)buffer, 6); 
    }
    
    
    boolean led_displayed=false;
    // Controlling from iphone app
    if ( length > 0 ) {
      for (int i = 0; i < length - 1; i += 2 ) {
        // Check if button1 has been pressed or released...
        if ( buffer[i] == button1 ) {
          // If the button is held down, buffer[i+1] will be 0
          // If it's released, buffer[i+1] is 1
          // Set pin to 1 when the button is held down
          // and to 0 when released
          //analogWrite(3,255);
          if(!led_displayed && 1-buffer[i+1] == 1) {
            blink_3_color();
          }
        } else if ( buffer[i] == button2 ){
          digitalWrite(4,1-buffer[i+1]);
          if(!led_displayed && 1-buffer[i+1] == 1) {
            fade_3_color();
          }
        } else if ( buffer[i] == button3 ) {
          digitalWrite(5,1-buffer[i+1]);
          if(!led_displayed && 1-buffer[i+1] == 1) {
            blink_two_loop();
          }
        } else if (buffer[i] == scroller) {
          //controlling a servo
          //turn_servo(buffer[i+1]);
          
          //rotating two-way motor
//          if(buffer[i+1]<=255/2)
//            rotateLeft(buffer[i+1], 1000);
//          else
//            rotateRight(buffer[i+1]-255/2, 1000);
        }
        led_displayed=true;
      }
    }
        
    Bean.sleep(300);          // sleep for a bit
}
