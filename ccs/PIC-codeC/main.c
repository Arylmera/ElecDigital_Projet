#include <main.h>
#use fast_io(C)

#define trigger pin_C0
#define echo pin_C1
#define dot pin_E2

#int_TIMER1

int8 b = 0;
int time;
double distance;

/*
* transofmation de la valeur en valeur base 16
*/
int8 outputValueParser(int8 value){
   int8 output = 0;
   if (b < 10) { output = value;}
   else {
    long long x = value;
    x = x % 10;
    int8 i = (int8) value;
    i = i/10;
    
    output = x + i*16;
   }
   return output;
}

double parseDist(double time){
  //return time / (343);
  return time / 58.82;
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
   
   setup_timer_1( T1_INTERNAL | T1_DIV_BY_1  );
   
   while(true)
   {
   
   time = 0;
   triggerSonde();
   
   while(input(echo) == 0){} // attente d�but
   set_timer1(0);
   while(input(echo) == 1){} // attente fin ou overflow
   time = get_timer1();
   
   distance = parseDist(time);
   b = (int) (distance);
   
   if(b > 99){
      b = b / 10;
      output_high(dot);
   }
   else {
      output_low(dot);
   }
   
   output_b(outputValueParser(b));
   
   delay_ms(400);
   }

}

