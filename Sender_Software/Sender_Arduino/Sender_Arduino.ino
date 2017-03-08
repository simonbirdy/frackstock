/******************
 * 
 * Frackstock 2017
 * 
 * Author: Simon Vogel
 * 
 * 
 */


#include <ESP8266WiFi.h>
#include <WiFiUdp.h>

WiFiUDP Udp;
char incomingPacket[10];
char  replyPacekt[] = "Hi there! Got the message :-)";



const char* ssid = "ESP-Accesspoint";
const char* password = "12345678";  // set to "" for open access point w/o passwortd

char session;


void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  session = 0;
    // setup globals
  
  // prepare GPIO2
  pinMode(2, OUTPUT);
  digitalWrite(2, 1);
  
  // start serial
  Serial.begin(9600);
  delay(1);
  
  // AP mode
  WiFi.mode(WIFI_AP);
  WiFi.softAP(ssid, password);

  Udp.begin(30000);
}


void loop() {
  // put your main code here, to run repeatedly:
      int packetSize = Udp.parsePacket();
  if (packetSize)
  {
    Serial.printf("Received %d bytes from %s, port %d\n", packetSize, Udp.remoteIP().toString().c_str(), Udp.remotePort());
    int len = Udp.read(incomingPacket, 10);
    if (len > 0)
    {
      char checksum = incomingPacket[0] + incomingPacket[1] + incomingPacket[2] + incomingPacket[3] + incomingPacket[4] + session;
      Serial.write(0xAA);
      Serial.write(session);
      Serial.write(incomingPacket[0]);
      Serial.write(incomingPacket[1]);
      Serial.write(incomingPacket[2]);
      Serial.write(incomingPacket[3]);
      Serial.write(incomingPacket[4]);
      Serial.write(checksum);
      delay(10);
      Serial.write(0xAA);
      Serial.write(session);
      Serial.write(incomingPacket[0]);
      Serial.write(incomingPacket[1]);
      Serial.write(incomingPacket[2]);
      Serial.write(incomingPacket[3]);
      Serial.write(incomingPacket[4]);
      Serial.write(checksum);
      session++;
    }
    else{
    }
  }



}
