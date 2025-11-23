#include "BluetoothSerial.h"

bool flag1;
bool flag2;
int count;
char msg[3];

String device_name = "ESP32-BT-Seu-Nome";

// Check if Bluetooth is available
#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

// Check Serial Port Profile
#if !defined(CONFIG_BT_SPP_ENABLED)
#error Serial Port Profile for Bluetooth is not available or not enabled. It is only available for the ESP32 chip.
#endif

BluetoothSerial SerialBT;

void setup() {
  Serial.begin(115200);
  SerialBT.begin(device_name);  //Bluetooth device name
  analogReadResolution(12);
  pinMode(2, OUTPUT);
  flag1 = false;
  flag2 = false;
  Serial.printf("The device with name \"%s\" is started.\nNow you can pair it with Bluetooth!\n", device_name.c_str());
}

void loop() {
  if (SerialBT.available()) {
    char carac = (char)SerialBT.read();
    if(flag1){
      msg[count] = carac;
      count++;
      if(count > 2){
        flag1 = false;
        Serial.print(msg[0]);
        Serial.print(msg[1]);
        Serial.print(msg[2]);
        if(msg[0] == 'L' && msg[1] == 'I' && msg[2] == 'G'){
          digitalWrite(2, HIGH);
        } else if(msg[0] == 'D' && msg[1] == 'S' && msg[2] == 'L'){
          digitalWrite(2, LOW);
        }
      }
    }
    if(carac == '@'){
      flag1 = true;
      count = 0;
    }
  }
}
