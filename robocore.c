#include <hFramework.h>
#include <hMotor.h>
#include "Lego_Ultrasonic.h"
#include <DistanceSensor.h>
#include <stdio.h>
#include <hExt.h>
#include <stdlib.h>

#define MotorR hMot4
#define MotorL hMot3

using namespace hFramework;
using namespace hSensors;
using namespace hModules;

               //lista znakow do komunikacji bluetooth
const char Sprzod[1] = {'F'};
const char Sprawo[1] = {'R'};
const char Styl[1] = {'B'};
const char Slewo[1] = {'L'};
const char Sstop[1] = {'S'};
const char Sdance[1] = {'D'};
const char SprzelacznikOn[1] = {'N'};
const char SprzelacznikOff[1] = {'O'};
char bluedata[1] = {'S'};

const int power=700;		//moc silnikow
const unsigned int minDist = 30;	//minimalny dystans przed Stop

unsigned int distFR, distFL, distFC, distBL;           //Odczyty z Sensorow

               //deklaracja distanceSensor
DistanceSensor  sensorFR(hSens2);
DistanceSensor  sensorFC(hSens4);
DistanceSensor  sensorFL(hSens3);

void sensors(){        //odczytywanie dystansow z sensorow
        while(true){
        distFR = sensorFR.getDistance();
        distFL = sensorFL.getDistance();
        distFC = sensorFC.getDistance();
        sys.delay(10);
    }
}
void GoForward(int power){
    MotorR.setPower(power);
    MotorL.setPower(power);
}
void GoBackward(int power){
    MotorR.setPower(-power);
    MotorL.setPower(-power);
}
void TurnRight(int power){
    MotorR.setPower(-power);
    MotorL.setPower(power);
}
void TurnLeft(int power){
    MotorR.setPower(power);
    MotorL.setPower(-power);
}
void Stop(){   //Zatrzymaj
    MotorR.setPower(0);//.stop();
    MotorL.setPower(0);//.stop();
}
void Dance(){
        //moveState = 5;
          switch (rand()%5){
        case 0: Stop(); sys.delay(200); break;
        case 1: GoForward(power); sys.delay(200); break;
        case 2: GoBackward(power); sys.delay(200); break;
        case 3: TurnLeft(power); sys.delay(200); break;
        case 4: TurnRight(power); sys.delay(200); break;
     }
}
void bluetooth(){	//nasluchiwanie Bluetooth
      hExt1.serial.init(9600, ODD, TWO);
      while(true){
        if (Serial.available()>0){
            hExt1.serial.read(bluedata,1,0xffffffff);
        }
    }
}
void tellme(){
    while(true){
         printf("bluetooth: %s\n\r", bluedata);
         sys.delay(100);
    }
}
void Drive(int power){
       if (bluedata[0]==Sstop[0]){
        Stop();                         //Stop
    }else if(bluedata[0]==Sprzod[0]){
        if (distFC<=minDist){
            bluedata[0]=Sstop[0];
        }else{
            GoForward(power);           //W przod
        }
    }else if(bluedata[0]==Sprawo[0]){
        if (distFR<=minDist){
            bluedata[0]=Sstop[0];
        }else{
            TurnRight(power);            //Prawo
        }
    }else if(bluedata[0]==Styl[0]){
            GoBackward(power);          //W tyl
    }else if(bluedata[0]==Slewo[0]){
        if (distFL<=minDist){
            bluedata[0]=Sstop[0];
        }else{
            TurnLeft(power);            //Lewo
        }
    }else if(bluedata[0]==Sdance[0]){
        Dance();                        //Dance
    }else{
        Stop();                         //Defult Stop
    }
}
void Autonomic(int power){

            if(distFR<=minDist && distFC>minDist && distFL>minDist){
                TurnLeft(power);

            }else if(distFL<=minDist && distFC>minDist && distFR>minDist){
                TurnRight(power);

            }else if( (distFR<=minDist && distFL<=minDist)  || distFC<=minDist){
                TurnLeft(power);

            }else if(distFL>minDist && distFR>minDist && distFC>minDist){
                GoForward(power);
            }
}
void hMain()
{
    bool autonomic = false;
    sys.taskCreate(&sensors);       //stworzenie watku zczytywania z sensorow
    sys.taskCreate(&bluetooth);     //stworzenie watku do czytania danych z bluetooth
    sys.taskCreate(&tellme);	    //stworzenie watku do wypisywania stanu przez USB
     while (true)
    {
        if(bluedata[0]==SprzelacznikOn[0]) autonomic=true;	//Dziala autonomicznie
        if(bluedata[0]==SprzelacznikOff[0]) autonomic=false;
        if(autonomic){
            Autonomic(power);
        } else {
            Drive(power);
        }
    }
}
