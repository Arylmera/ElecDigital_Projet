#include <main.h>
#use fast_io(C)

#use rs232(baud=9600, parity=N, xmit=PIN_C6, rcv=PIN_C7)

#define trigger pin_C0
#define echo pin_C1
#define dot pin_E2
#define RX pin_C7
#define TX pin_C6
#define GREEN pin_E0
#define RED pin_E1

#int_TIMER1

int16 time, distance, x, i, minVal;
boolean flag=0;
char buffer[4];
int i=0;
int8 c,d,u;

#int_RDA
void RDA_isr(void) {
  buffer[i]=getc();
  if(buffer[0]=='!' && flag==0) {
    i++;
    if(i>=4) {
      i=0;
      flag=1;
    }
  }
}

/*
* transofmation de la valeur en valeur base 16
*/
int16 outputValueParser(int16 value){
   int output = 0;
   if (value < 10) { output = value;}
   else {
    x = value;
    x = x % 10;
    i = value;
    i = i/10;

    output = x + i*16;
   }
   return output;
}

/*
* parsing temps => distance
*/
int16 parseDist(int16 time){
  //return time / (285) ; // theoriquement 343 m/s
  return time/100;
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
   set_tris_c(0b01000010); // set RC1 as input (ECHO)

   setup_timer_1( T1_INTERNAL | T1_DIV_BY_1  );

   while(true)
   {

   // setup des valeurs
   time = 0;

   // r�cup�ration minValue envoy� par JAVA
   minVal = 100;// (int16) getc();

   // d�clanchement de la sonde
   triggerSonde();

   // r�cup�ration valeur temps de la sonde
   while(input(echo) == 0){} // attente d�but
   set_timer1(0);
   while(input(echo) == 1){} // attente fin ou overflow
   time = get_timer1();

   // temps => distance
   distance = parseDist(time);

   // v�rification borne minVal
   if (distance < minVal){
      // allumer red => trop proche
      output_high(RED);
      output_low(GREEN);
   }
   else {
      // allumer green => OK
      output_high(GREEN);
      output_low(RED);
   }

   // gestion du point si > que 100 !> cm -> m
   if(distance > 99){
      distance = distance / 10;
      printf("1\n");
      output_high(dot);
   }
   else {
     printf("2\n");
      output_low(dot);
   }

   if(flag==1){
     flag=0;
     c=buffer[1]-48;
     d=buffer[2]-48;
     u=buffer[3]-48;
     minVal=(int16) (c*100+d*10+u);
   }

   c=minVal/100;
   d=(minVal-(c*100))/10;
   u=(minVal-(c*100))-(d*10);


   // afichage sur 7seg de la distance
   output_b(outputValueParser(distance));

   printf("%ld", distance);
   printf("\n");

   // attente pour �viter spam
   delay_ms(500);
   }

}
