#include <main.h>

#use fast_io(C)

#define trigger pin_C0
#define echo pin_C1

int b;
int time;

/*
* transofmation de la valeur en valeur base 16
*/
int outputValueParser(int value){
   int output = 0;
   if (b < 10) { output = value;}
   else {
    int x = value;
    x = x%10;
    int i = value;
    i = i/10;
    
    output = x + i*16;
   }
   return output;
}

/*
* récupération distance
*/
int parseDist(int time){
  return time * 0.034;
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
   
   while(true)
   {
   b = 0;
   time = 0;
   
   triggerSonde();
   
   while(input(echo) == 0){}
   while(input(echo) == 1){
      time = time + 1;
      delay_us(1);
   }
   
   b = parseDist(time);
   output_b(outputValueParser(b));
   delay_ms(100);
   }

}

