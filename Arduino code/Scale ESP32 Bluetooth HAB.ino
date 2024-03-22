/*
 * Complete project details at https://RandomNerdTutorials.com/esp32-load-cell-hx711/
 *
 * HX711 library for Arduino - example file
 * https://github.com/bogde/HX711
 *
 * MIT License
 * (c) 2018 Bogdan Necula
 *
**/
#include <Arduino.h>
#include "HX711.h"
#include "soc/rtc.h"
#include "BluetoothSerial.h"

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

unsigned long startMillis;  //some global variables available anywhere in the program
unsigned long currentMillis;
const unsigned long period = 100;  //the value is a number of milliseconds

String readString; //main captured String
String Lift; 
String Programm; //data String
String Mode;
String Tara;


int ind1; // , locations
int ind2;
int ind3;
int ind4;

const int valve_fast =  18;
const int valve_slow =  19;
const int valve_exit =  21;

float read_lift;
char r_l;


BluetoothSerial SerialBT;

// HX711 circuit wiring
const int LOADCELL_DOUT_PIN = 16;
const int LOADCELL_SCK_PIN = 4;

HX711 scale;

void setup() {
  Serial.begin(115200);
  rtc_cpu_freq_config_t config;
  rtc_clk_cpu_freq_get_config(&config);
  rtc_clk_cpu_freq_to_config(RTC_CPU_FREQ_80M, &config);
  rtc_clk_cpu_freq_set_config_fast(&config);


  pinMode(valve_fast, OUTPUT);
  pinMode(valve_slow, OUTPUT);
  pinMode(valve_exit, OUTPUT);
   Mode = "man;";
  Serial.println("SCALE Balloon 9A4AM");

  Serial.println("Initializing the scale");
  SerialBT.begin("SCALE Balloon 9A4AM"); //Bluetooth device name
  scale.begin(LOADCELL_DOUT_PIN, LOADCELL_SCK_PIN);

  Serial.println("Before setting up the scale:");
  Serial.print("read: \t\t");
  Serial.println(scale.read());      // print a raw reading from the ADC

  Serial.print("read average: \t\t");
  Serial.println(scale.read_average(20));   // print the average of 20 readings from the ADC

  Serial.print("get value: \t\t");
  Serial.println(scale.get_value(5));   // print the average of 5 readings from the ADC minus the tare weight (not set yet)

  Serial.print("get units: \t\t");
  Serial.println(scale.get_units(5), 1);  // print the average of 5 readings from the ADC minus tare weight (not set) divided
            // by the SCALE parameter (not set yet)
            
  scale.set_scale(1874.35);
  //scale.set_scale(-471.497);                      // this value is obtained by calibrating the scale with known weights; see the README for details
  scale.tare();               // reset the scale to 0

  Serial.println("After setting up the scale:");

  Serial.print("read: \t\t");
  Serial.println(scale.read());                 // print a raw reading from the ADC

  Serial.print("read average: \t\t");
  Serial.println(scale.read_average(20));       // print the average of 20 readings from the ADC

  Serial.print("get value: \t\t");
  Serial.println(scale.get_value(5));   // print the average of 5 readings from the ADC minus the tare weight, set with tare()

  Serial.print("get units: \t\t");
  Serial.println(scale.get_units(5), 1);        // print the average of 5 readings from the ADC minus tare weight, divided
            // by the SCALE parameter set with set_scale

  Serial.println("Ready:");
 
}

void loop() {
  //scale_read_sendBT();
 
  App_read_arry();

  
      if (Mode == "aut;") {
        
      Auto_Mode_ON();
      
        
      }
}




void scale_read_sendBT()  {
   //save current time
  currentMillis = millis();

  //********************************
  //to help show if there is any blocking code, this LED toggles every 500ms
  if (currentMillis - startMillis >= 500)
  {
    //restart this TIMER
    startMillis = currentMillis;
    // Serial.print("one reading:\t");
  Serial.println(scale.get_units(3), 1);
  // Serial.print("\t| average:\t");
  // Serial.println(scale.get_units(10), 5);
  
   
    //SerialBT.write(56);
   
   

  } 


}


void App_read_arry() {
  // units = (scale.get_units(3), 1);
    SerialBT.println(scale.get_units(3), 1);
   read_lift = scale.get_units(3),1;
  
if (SerialBT.available())  {
    char c = SerialBT.read();  //gets one byte from serial buffer
    delay(3);  //small delay to allow input buffer to fill
    if (c == '*') {
      //do stuff
      
      //Serial.println();
      
      //Serial.print("captured String is : "); 
      //Serial.println(readString); //prints string to serial port out
      
      ind1 = readString.indexOf(';');  //finds location of first ,
      Lift = (readString.substring(0, ind1));   //captures first data String
      ind2 = readString.indexOf(';', ind1+1 );   //finds location of second ,
      Programm = readString.substring(ind1+1, ind2+1);   //captures second data String
      ind3 = readString.indexOf(';', ind2+1 );
      Mode = readString.substring(ind2+1, ind3+1);
      ind4 = readString.indexOf(';', ind3+1 );
      Tara = readString.substring(ind3+1); //captures remain part of data after last ,

     

      if (Tara == "TY") {
      scale.tare();
      Tara = "TN" ;
        
      }

      if (Programm == "prg_hyd;") {
       digitalWrite(valve_exit, HIGH);
        
      }  
       else {
       digitalWrite(valve_exit, LOW);
      }  
       if (Mode == "aut;") {
        
      Auto_Mode_ON();
      
        
      }
      readString=""; //clears variable for new input
      //Lift="";
      //Programm="";
      //Mode="";
      //Tara="";
    }  
    else {     
      readString += c; //makes the string readString
     
    }
    
  }

}

void Auto_Mode_ON(){

float Lift_slow;
Lift_slow = (Lift.toInt() - 1);
    if (Programm == "prg_hel;") {
      
    if (read_lift < Lift_slow and (read_lift < (Lift.toInt()))) { 
   digitalWrite(valve_fast, HIGH);
   digitalWrite(valve_slow, HIGH);
   digitalWrite(valve_exit, LOW);
   Mode = "aut;";
      }
   
   if (read_lift > Lift_slow and (read_lift < (Lift.toInt()))) {
    digitalWrite(valve_fast, LOW);
    digitalWrite(valve_slow, HIGH);
    digitalWrite(valve_exit, LOW);
    Mode = "aut;";
   
   }
   if (read_lift >= (Lift.toInt())) {
    digitalWrite(valve_fast, LOW);
    digitalWrite(valve_slow, LOW);
    digitalWrite(valve_exit, LOW);
    Mode = "man;"; 
   }


   

}

     if (Programm == "prg_hyd;") {

      if (read_lift < Lift_slow and (read_lift < Lift_slow)) {
   digitalWrite(valve_fast, HIGH);
   digitalWrite(valve_slow, HIGH);
   digitalWrite(valve_exit, LOW);
   Mode = "aut;"; 
    }
   if (read_lift > Lift_slow and (read_lift < (Lift.toInt()))) {
    digitalWrite(valve_fast, LOW);
    digitalWrite(valve_slow, HIGH);
    digitalWrite(valve_exit, LOW);
    Mode = "aut;"; 
   
   }
   if (read_lift >= (Lift.toInt())) {
    digitalWrite(valve_fast, LOW);
    digitalWrite(valve_slow, LOW);
    digitalWrite(valve_exit, HIGH);
    Mode = "man;"; 
   }
   



}








}

