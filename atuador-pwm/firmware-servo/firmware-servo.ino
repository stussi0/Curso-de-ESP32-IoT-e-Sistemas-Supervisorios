
// Include the ESP32 Arduino Servo Library instead of the original Arduino Servo Library
#include <ESP32Servo.h> 

Servo myservo;  // create servo object to control a servo

// Possible PWM GPIO pins on the ESP32: 0(used by on-board button),2,4,5(used by on-board LED),12-19,21-23,25-27,32-33 
// Possible PWM GPIO pins on the ESP32-S2: 0(used by on-board button),1-17,18(used by on-board LED),19-21,26,33-42
// Possible PWM GPIO pins on the ESP32-S3: 0(used by on-board button),1-21,35-45,47,48(used by on-board LED)
// Possible PWM GPIO pins on the ESP32-C3: 0(used by on-board button),1-7,8(used by on-board LED),9-10,18-21
int servoPin = 16;      // GPIO pin used to connect the servo control (digital out)
// Possible ADC pins on the ESP32: 0,2,4,12-15,32-39; 34-39 are recommended for analog input
// Possible ADC pins on the ESP32-S2: 1-20 are recommended for analog input
#if defined(CONFIG_IDF_TARGET_ESP32S2) || defined(CONFIG_IDF_TARGET_ESP32S3)
int potPin = 10;        // GPIO pin used to connect the potentiometer (analog in)
#elif defined(CONFIG_IDF_TARGET_ESP32C3)
int potPin = 4;         // GPIO pin used to connect the potentiometer (analog in)
#else
int potPin = 34;        // GPIO pin used to connect the potentiometer (analog in)
#endif
int ADC_Max = 4096;     // This is the default ADC max value on the ESP32 (12 bit ADC width);
                        // this width can be set (in low-level oode) from 9-12 bits, for a
                        // a range of max values of 512-4096
  
int val;    // variable to read the value from the analog pin

void setup()
{
	// Allow allocation of all timers
	ESP32PWM::allocateTimer(0);
	ESP32PWM::allocateTimer(1);
	ESP32PWM::allocateTimer(2);
	ESP32PWM::allocateTimer(3);
  myservo.setPeriodHertz(50);// Standard 50hz servo
  myservo.attach(servoPin, 500, 2400);   // attaches the servo on pin 18 to the servo object
                                         // using SG90 servo min/max of 500us and 2400us
                                         // for MG995 large servo, use 1000us and 2000us,
                                         // which are the defaults, so this line could be
                                         // "myservo.attach(servoPin);"
}

void loop() {
  //val = analogRead(potPin);            // read the value of the potentiometer (value between 0 and 1023)
  //val = map(val, 0, ADC_Max, 0, 180);     // scale it to use it with the servo (value between 0 and 180)
  for(int i = 0; i < 179; i++){
    myservo.write(i);                  // set the servo position according to the scaled value
    delay(50);                          // wait for the servo to get there
  }
  delay(2000);
  for(int i = 179; i >= 0; i--){
    myservo.write(i);                  // set the servo position according to the scaled value
    delay(50);                          // wait for the servo to get there
  }
}

