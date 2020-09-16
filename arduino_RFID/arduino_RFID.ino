#include <SPI.h>
#include <MFRC522.h>
#include <LiquidCrystal.h>
#include <SoftwareSerial.h>

#define RST_PIN 9
#define SS_PIN 10


SoftwareSerial BTserial(A4, A3); //RX | TX
byte readCard[4];
String masterTag = "565EE2F7";
String tagID = "";
bool loggedIn;
bool displayRooms;

//userInfo user;

MFRC522 mfrc522(SS_PIN, RST_PIN);
LiquidCrystal lcd(7, 6, 5, 4, 3, 2); //Parameters: (rs, enable, d4, d5, d6, d7)

void setup() {
  SPI.begin(); //SPI bus
  mfrc522.PCD_Init(); //MFRC522 (RFID)

  displayRooms = false;
  lcd.begin(16, 2); //LCD screen

  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print(" Scan Your Card ");

  Serial.begin(9600);
  BTserial.begin(9600);
}

void loop() {

  if (displayRooms) {

  }
  else {
    lcd.clear();
    lcd.print(" Scan Your Card ");
  }
  //Wait until new tag is available
  while (getID()) {
    lcd.clear();
    lcd.setCursor(0, 0);

    lcd.print("ID : ");
    lcd.setCursor(0, 1);
    lcd.print(tagID);
    delay (1000);
  }

  if (BTserial.available()) {
    Serial.println(BTserial.readString());
  }
  if (Serial.available()) {
    BTserial.println(Serial.readString());
  }

}

//Read new tag if available
boolean getID()
{
  // Getting ready for Reading PICCs
  if ( ! mfrc522.PICC_IsNewCardPresent()) { //If a new PICC placed to RFID reader continue
    return false;
  }
  if ( ! mfrc522.PICC_ReadCardSerial()) { //Since a PICC placed get Serial and continue
    return false;
  }
  tagID = "";
  for ( uint8_t i = 0; i < 4; i++) { // The MIFARE PICCs that we use have 4 byte UID
    readCard[i] = mfrc522.uid.uidByte[i];
    Serial.print(readCard[i], HEX);
    BTserial.print(readCard[i], HEX);
    tagID.concat(String(mfrc522.uid.uidByte[i], HEX)); // Adds the 4 bytes in a single String variable
  }
  Serial.println();
  BTserial.println();
  tagID.toUpperCase();
  mfrc522.PICC_HaltA(); // Stop reading
  return true;
}
