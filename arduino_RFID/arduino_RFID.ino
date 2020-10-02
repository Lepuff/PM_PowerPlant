#include <SPI.h>
#include <MFRC522.h>
#include <LiquidCrystal.h>
#include <SoftwareSerial.h>

const int RST_PIN = 9;
const int SS_PIN = 10;
const char LOGGEDOUT = '0';
const char LOGGEDIN = '1';
const char WARNING = '2';
const int RADIATION_CHECK_DELAY = 300;
const int NOISE_FILTER_VALUE = 1;

const String ID = "1"; //message identifier for sending tag-ID
const String RADIATION = "2"; //message identifier for sending radiation-level

char firstChar;
int potentioPin = A0;
int potentioVal = 0;
int previousVal = 0;
unsigned long timer = 0;

SoftwareSerial BTserial(A4, A3); //RX | TX
String tagID = ""; // our tags "565EE2F7" && "699FC756"
bool loggedIn;

MFRC522 mfrc522(SS_PIN, RST_PIN);
LiquidCrystal lcd(7, 6, 5, 4, 3, 2); //Parameters: (rs, enable, d4, d5, d6, d7)

void setup() {
  SPI.begin(); //SPI bus
  mfrc522.PCD_Init(); //MFRC522 (RFID)
  pinMode(A0, INPUT);

  lcd.begin(16, 2); //LCD screen
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("Scan Your Card ");

  timer = millis();
  potentioVal = map(analogRead(potentioPin), 0, 1023, 0, 100);
  previousVal = potentioVal;
  Serial.begin(9600);
  BTserial.begin(9600);
}

void loop() {

  if (millis() >= timer + RADIATION_CHECK_DELAY) {
    potentioVal = map(analogRead(potentioPin), 0, 1023, 0, 100);
    if (potentioVal > previousVal + NOISE_FILTER_VALUE || potentioVal < previousVal - NOISE_FILTER_VALUE) {
      String message = RADIATION; // RADIATION == "2" -> identifier for message
      message.concat(potentioVal);
      message.concat("."); // "." is used as end of message for android
      BTserial.print(message);
      Serial.println(message);
      previousVal = potentioVal;
    }
    timer = millis();
  }

  lcd.clear();
  lcd.print("Scan Your Card ");
  lcd.setCursor(0, 1);
  lcd.print("Radiation: ");
  lcd.print(previousVal);

  //Wait until new tag is available
  while (getID()) {
    BTserial.print(tagID);
    Serial.print(tagID);
    delay(500);
  }

  if (BTserial.available()) {
    lcd.clear();
    lcd.setCursor(0, 0);
    firstChar = BTserial.readString().charAt(0);

    switch (firstChar) { //state machine for receiving messages via bluetooth
      case LOGGEDOUT: //user is not logged in -> login user
        lcd.print(" Logged in");
        Serial.println(BTserial.readString().charAt(0)); //for debugging
        delay(1000);
        break;

      case LOGGEDIN: //user is logged in -> logout user
        lcd.print(" Logged out");
        Serial.println(BTserial.readString().charAt(0)); //for debugging
        delay(1000);
        break;

      case WARNING: //check for warning message
        lcd.print("WARNING");
        lcd.setCursor(0, 1);
        lcd.print("LIMIT REACHED");
        delay(1000);
        break;

      default:
        Serial.print("Unknown message in state machine: ");
        Serial.println(firstChar);
        break;
    }
  }
}

//Read new tag if available
boolean getID()
{
  // Getting ready for Reading PICCs
  if (mfrc522.PICC_IsNewCardPresent() && mfrc522.PICC_ReadCardSerial()) {
    tagID = ID; //ID == "1" -> used as identifier for message
    for ( uint8_t i = 0; i < 4; i++) { // The MIFARE PICCs that we use have 4 byte UID
      tagID.concat(String(mfrc522.uid.uidByte[i], HEX)); // Adds the 4 bytes in a single String variable
    }
    tagID.concat(".");
    tagID.toUpperCase();
    mfrc522.PICC_HaltA(); // Stop reading
    return true;
  }
  return false;

}
