#define LEDC_TIMER_12_BIT 12

// use 5000 Hz as a LEDC base frequency
#define LEDC_BASE_FREQ 5000

#define LED_PIN 4

#define LEDC_START_DUTY  (0)
#define LEDC_TARGET_DUTY (4095)
#define LEDC_FADE_TIME   (3000)

bool fade_ended = false;  // status of LED fade
bool fade_in = true;

void ARDUINO_ISR_ATTR LED_FADE_ISR() {
  fade_ended = true;
}

void setup() {
  Serial.begin(115200);
  ledcAttach(LED_PIN, LEDC_BASE_FREQ, LEDC_TIMER_12_BIT);
  ledcFade(LED_PIN, LEDC_START_DUTY, LEDC_TARGET_DUTY, LEDC_FADE_TIME);
  Serial.println("LED Fade on started.");
  delay(LEDC_FADE_TIME);
  ledcFadeWithInterrupt(LED_PIN, LEDC_TARGET_DUTY, LEDC_START_DUTY, LEDC_FADE_TIME, LED_FADE_ISR);
  Serial.println("LED Fade off started.");
}

void loop() {
  if (fade_ended) {
    Serial.println("LED fade ended");
    fade_ended = false;
    if (fade_in) {
      ledcFadeWithInterrupt(LED_PIN, LEDC_START_DUTY, LEDC_TARGET_DUTY, LEDC_FADE_TIME, LED_FADE_ISR);
      Serial.println("LED Fade in started.");
      fade_in = false;
    } else {
      ledcFadeWithInterrupt(LED_PIN, LEDC_TARGET_DUTY, LEDC_START_DUTY, LEDC_FADE_TIME, LED_FADE_ISR);
      Serial.println("LED Fade out started.");
      fade_in = true;
    }
  }
}
