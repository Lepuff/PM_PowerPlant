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
struct userInfo {
  String tagID = "";
  bool loggedIn = false;
};


userInfo user;

MFRC522 mfrc522(SS_PIN, RST_PIN);
LiquidCrystal lcd(7, 6, 5, 4, 3, 2); //Parameters: (rs, enable, d4, d5, d6, d7)

void setup() {
  SPI.begin(); //SPI bus
  mfrc522.PCD_Init(); //MFRC522 (RFID)

  //userInfo user;
  //analogWrite(A5, contrast);
  lcd.begin(16, 2); //LCD screen

  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print(" Scan Your Card ");
  
  Serial.begin(9600);
  BTserial.begin(9600);
}

void loop() {
  
  //Wait until new tag is available
  while (getID()){
    lcd.clear();
    lcd.setCursor(0, 0);

    if (user.tagID
    if (!user.loggedIn){
      //Serial.println("Logged in\n");
      lcd.print("Logged in");
      user.loggedIn = true;
      Serial.println(user.tagID);
    }
    else{
      //Serial.println("Logged out\n");
      lcd.print("Logged out");
      user.loggedIn = false;
    }
  }

  delay (1500);
  lcd.clear();
  lcd.print(" Scan Your Card ");
  
  lcd.setCursor(0, 0);
  lcd.print(" Scan Your Card ");  
  // Keep reading from BT-unit and send to Arduino Serial Monitor
  if (BTserial.available()) {
    Serial.write(BTserial.read());
  }

  // Keep reading from Arduino Serial Monitor and send to BT-unit
  if (Serial.available()) {
    BTserial.write(Serial.read());
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
  user.tagID = "";
  for ( uint8_t i = 0; i < 4; i++) { // The MIFARE PICCs that we use have 4 byte UID
  //readCard[i] = mfrc522.uid.uidByte[i];
  user.tagID.concat(String(mfrc522.uid.uidByte[i], HEX)); // Adds the 4 bytes in a single String variable
  }
  user.tagID.toUpperCase();
  mfrc522.PICC_HaltA(); // Stop reading
  return true;
}
