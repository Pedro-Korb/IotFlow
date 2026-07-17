#include <Arduino.h>
#include <DHT.h>

const int dht22_pino = 15;
const int LedTemperatura_pino = 18;
const int ledAzulTemperatura_pino = 2;
const int ledVerdeTemperatura_pino = 4;
const int ledVermelhoTemperatura_pino = 5;
const int ledUmidade_pino = 19;
const int ledAzulUmidade_pino = 21;
const int ledVerdeUmidade_pino = 22;
const int ledVermelhoUmidade_pino = 23;

DHT dht_dht22(15, DHT22);

float dht22_temperatura = 0;
float dht22_umidade = 0;

void setup() {
  Serial.begin(115200);
  pinMode(LedTemperatura_pino, OUTPUT);
  pinMode(ledAzulTemperatura_pino, OUTPUT);
  pinMode(ledVerdeTemperatura_pino, OUTPUT);
  pinMode(ledVermelhoTemperatura_pino, OUTPUT);
  pinMode(ledUmidade_pino, OUTPUT);
  pinMode(ledAzulUmidade_pino, OUTPUT);
  pinMode(ledVerdeUmidade_pino, OUTPUT);
  pinMode(ledVermelhoUmidade_pino, OUTPUT);
  dht_dht22.begin();
}

void loop() {
  dht22_temperatura = dht_dht22.readTemperature();
  dht22_umidade = dht_dht22.readHumidity();

  if (dht22_temperatura > 0) {
    digitalWrite(LedTemperatura_pino, HIGH);
  }
  if (dht22_umidade > 0) {
    digitalWrite(ledUmidade_pino, HIGH);
  }
  if (dht22_temperatura < 20) {
    digitalWrite(ledAzulTemperatura_pino, HIGH);
    digitalWrite(ledVerdeTemperatura_pino, LOW);
    digitalWrite(ledVermelhoTemperatura_pino, LOW);
  }
  if ((dht22_temperatura >= 20) && (dht22_temperatura <= 35)) {
    digitalWrite(ledAzulTemperatura_pino, LOW);
    digitalWrite(ledVerdeTemperatura_pino, HIGH);
    digitalWrite(ledVermelhoTemperatura_pino, LOW);
  }
  if (dht22_temperatura > 35) {
    digitalWrite(ledAzulTemperatura_pino, LOW);
    digitalWrite(ledVerdeTemperatura_pino, LOW);
    digitalWrite(ledVermelhoTemperatura_pino, HIGH);
  }
  if (dht22_umidade < 30) {
    digitalWrite(ledAzulUmidade_pino, HIGH);
    digitalWrite(ledVerdeUmidade_pino, LOW);
    digitalWrite(ledVermelhoUmidade_pino, LOW);
  }
  if ((dht22_umidade >= 30) && (dht22_umidade <= 60)) {
    digitalWrite(ledAzulUmidade_pino, LOW);
    digitalWrite(ledVerdeUmidade_pino, HIGH);
    digitalWrite(ledVermelhoUmidade_pino, LOW);
  }
  if (dht22_umidade > 60) {
    digitalWrite(ledAzulUmidade_pino, LOW);
    digitalWrite(ledVerdeUmidade_pino, LOW);
    digitalWrite(ledVermelhoUmidade_pino, HIGH);
  }
  Serial.print("dht22_temperatura: ");
  Serial.println(dht22_temperatura);
  Serial.print("dht22_umidade: ");
  Serial.println(dht22_umidade);

  delay(1000);
}
