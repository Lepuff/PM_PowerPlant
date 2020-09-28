#include <SPI.h>
#include <MFRC522.h>
#include <LiquidCrystal.h>
#include <SoftwareSerial.h>

const int RST_PIN = 9;
const int SS_PIN = 10;
const int  buttonPin = 1;
const char LOGGEDOUT = '0';
const char LOGGEDIN = '1';
const char WARNING = '2';

int buttonState;         // current state of the button
int lastButtonState = 0;     // previous state of the button
int roomNumber = 0;
int radiationLevel = 30;
bool loginStatus = false;
int firstChar;
//#define RST_PIN 9
//#define SS_PIN 10


SoftwareSerial BTserial(A4, A3); //RX | TX
byte readCard[4];
String masterTag = "565EE2F7";
String tagID = "";
bool loggedIn;

//userInfo user;

MFRC522 mfrc522(SS_PIN, RST_PIN);
LiquidCrystal lcd(7, 6, 5, 4, 3, 2); //Parameters: (rs, enable, d4, d5, d6, d7)

void setup() {
  SPI.begin(); //SPI bus
  mfrc522.PCD_Init(); //MFRC522 (RFID)

  // initialize the button pin as a input:
  pinMode(buttonPin, INPUT);

  lcd.begin(16, 2); //LCD screen

  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print(" Scan Your Card ");

  Serial.begin(9600);
  BTserial.begin(9600);
}

void loop() {

  lcd.clear();
  lcd.print(" Scan Your Card ");
  //firstChar = BTserial.readString().charAt(0);

  //Wait until new tag is available
  while (getID()) {
    lcd.clear();
    lcd.setCursor(0, 0);

    BTserial.print(tagID);
    Serial.print(tagID);
    BTserial.read();
  }

  if (BTserial.available()) {
    lcd.clear();
    lcd.setCursor(0, 0);
    firstChar = BTserial.readString().charAt(0);
    switch (firstChar) {
      case LOGGEDOUT: //user not logged in -> login user
        lcd.print(" Logged in");
        Serial.println(BTserial.readString().charAt(0)); //for debugging
        delay(1000);
        break;

      case LOGGEDIN: //user logged in -> logout user
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
        break;
    }
  }

  /*if (BTserial.available()) {
    lcd.clear();
    lcd.setCursor(0, 0);
    firstChar = BTserial.readString().charAt(0);

    if (firstChar == LOGGEDOUT) { //user not logged in -> login user
      lcd.print(" Logged in");
      Serial.println(BTserial.readString().charAt(0));
    }
    if (firstChar == LOGGEDIN) { //user logged in -> logout user
      lcd.print(" Logged out");
      Serial.println(BTserial.readString().charAt(0));
    }
    if (firstChar == WARNING){
      lcd.print("WARNING");
      lcd.setCursor(0, 1);
      lcd.print("LIMIT REACHED");
    }
    delay(1000);
    }*/

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
  tagID = "1";
  for ( uint8_t i = 0; i < 4; i++) { // The MIFARE PICCs that we use have 4 byte UID
    //readCard[i] = mfrc522.uid.uidByte[i];
    //Serial.print(readCard[i], HEX);
    //BTserial.print(readCard[i], HEX);
    tagID.concat(String(mfrc522.uid.uidByte[i], HEX)); // Adds the 4 bytes in a single String variable
  }
  tagID.toUpperCase();
  mfrc522.PICC_HaltA(); // Stop reading
  return true;
}
