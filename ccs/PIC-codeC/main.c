#include <main.h>

#use fast_io(C)

#define trigger pin_C0
#define echo pin_C1
#define dot pin_E2

float b;
float time;

/*
* transofmation de la valeur en valeur base 16
*/
float outputValueParser(float value){
   float output = 0;
   if (b < 10) { output = value;}
   else {
    long long x = value;
    x = x % 10;
    float i = (float) value;
    i = i/10;
    
    output = x + i*16;
   }
   return output;
}

/*
* r�cup�ration distance
*/
float parseDist(float time){
  return time / (343);
}

/*
* trigger de la sonde
*/
void triggerSonde(){
   output_high(trigger);
   delay_us(10);
   output_low(trigger);
}

/*
* fonction principale
*/
void main()
{
   setup_low_volt_detect(FALSE);
   set_tris_c(2); // set RC1 as input (ECHO)
   
   T0CON = 0x08;       
   TMR0IF = 0;         // overflow flag = 0
   TMR0=0;

   
   while(true)
   {
   b = 0.0;
   time = 0.0;
   
   triggerSonde();
   
   while(input(echo) == 0); // attente d�but
   TMR0 = 0;
   T0CON = 1;
   while(input(echo) == 1 && !TMR0IF); // attente fin ou overflow
   time = TMR0;
   T0CON = 0;
   
   b = parseDist(time);
   printf(b);
   if (b < 10) {
    b *= 10;
    output_high(dot);
   }
   else {
    output_low(dot);
   }
   
   output_b(outputValueParser(b));
   delay_ms(100);
   }

}

