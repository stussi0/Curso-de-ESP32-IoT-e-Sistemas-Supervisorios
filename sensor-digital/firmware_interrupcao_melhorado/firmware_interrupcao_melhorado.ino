#include <Arduino.h>

static uint32_t lastMillis = 0;

struct Button {
  const uint8_t PIN;
  uint32_t numberKeyPresses;
  bool pressed;
};

Button button2 = {2, 0, false};

void ARDUINO_ISR_ATTR isr() {
  detachInterrupt(button2.PIN);
  button2.numberKeyPresses += 1;
  button2.pressed = true;
  lastMillis = millis();
}

void setup() {
  Serial.begin(115200);
  pinMode(button2.PIN, INPUT_PULLUP);
  attachInterrupt(button2.PIN, isr, FALLING);
}

void loop() {
  if (button2.pressed) {
    if(millis() - lastMillis > 500){
      Serial.printf("Button 2 has been pressed %lu times\n", button2.numberKeyPresses);
      button2.pressed = false;
      attachInterrupt(button2.PIN, isr, FALLING);
    }
  }
}
