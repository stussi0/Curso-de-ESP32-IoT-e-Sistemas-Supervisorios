int filtro = 1024;

void setup() {
  Serial.begin(115200);
  analogReadResolution(12);
}

void loop() {
  long analogValue = analogRead(2);
  long analogVolts = analogReadMilliVolts(2);

  for(int i = 0; i < filtro; i++){
    analogValue += analogRead(2);
    analogVolts += analogReadMilliVolts(2);
  }

  analogValue = analogValue/filtro;
  analogVolts = analogVolts/filtro;

  Serial.printf("ADC analog value = %d\n", analogValue);
  Serial.printf("ADC millivolts value = %d\n", analogVolts);

  delay(1000);
}
