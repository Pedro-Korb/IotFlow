#include <Arduino.h>

const int potenciometro_pino = 34;
const int led1_pino = 21;
const int led2_pino = 22;
const int led3_pino = 23;
const int led4_pino = 19;
const int led5_pino = 2;
const int led6_pino = 4;
const int led7_pino = 27;
const int led8_pino = 18;

int sequencia_leds[] = {2, 4, 27, 18, 19, 21, 22, 23};
int total_leds = 8;
int led_atual = 0;
unsigned long ultima_mudanca = 0;
unsigned long intervalo_ms = 500;

long potenciometro_nivel = 0;

void setup() {
  Serial.begin(115200);
  pinMode(led1_pino, OUTPUT);
  pinMode(led2_pino, OUTPUT);
  pinMode(led3_pino, OUTPUT);
  pinMode(led4_pino, OUTPUT);
  pinMode(led5_pino, OUTPUT);
  pinMode(led6_pino, OUTPUT);
  pinMode(led7_pino, OUTPUT);
  pinMode(led8_pino, OUTPUT);
  pinMode(21, OUTPUT); digitalWrite(21, LOW);
  pinMode(22, OUTPUT); digitalWrite(22, LOW);
  pinMode(23, OUTPUT); digitalWrite(23, LOW);
  pinMode(19, OUTPUT); digitalWrite(19, LOW);
  pinMode(2, OUTPUT); digitalWrite(2, LOW);
  pinMode(4, OUTPUT); digitalWrite(4, LOW);
  pinMode(27, OUTPUT); digitalWrite(27, LOW);
  pinMode(18, OUTPUT); digitalWrite(18, LOW);
}

void loop() {
  potenciometro_nivel = analogRead(potenciometro_pino);
  intervalo_ms = map(potenciometro_nivel, 0, 4095, 1, 3000);
  unsigned long agora = millis();
  if (agora - ultima_mudanca >= intervalo_ms) {
    digitalWrite(sequencia_leds[led_atual], LOW);
    led_atual = (led_atual + 1) % total_leds;
    digitalWrite(sequencia_leds[led_atual], HIGH);
    ultima_mudanca = agora;
  }


  delay(map(potenciometro_nivel, 0, 4095, 1, 3000));
}
