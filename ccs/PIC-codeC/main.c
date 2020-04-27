#include <main.h>
#include "LCD420.c"
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
int j=0;
int8 c,d,u;

#int_RDA
void RDA_isr(void) {
  buffer[j]=getc();
  if(buffer[0]=='!' && flag==0) {
    j++;
    if(j>=4) {
      j=0;
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

   setup_spi(FALSE);
   setup_wdt(WDT_OFF);
   setup_timer_1( T1_INTERNAL | T1_DIV_BY_1  );
   setup_comparator(NC_NC_NC_NC);
   setup_vref(FALSE);
   
   enable_interrupts(INT_RDA);
   enable_interrupts(GLOBAL);
   setup_oscillator(False);

   while(true)
   {

   // setup des valeurs
   time = 0;

   // recuperation minValue envoye par JAVA
   minVal = 100;// (int16) getc();

   // d�clanchement de la sonde
   triggerSonde();

   // recuperation valeur temps de la sonde
   while(input(echo) == 0){} // attente debut
   set_timer1(0);
   while(input(echo) == 1){} // attente fin ou overflow
   time = get_timer1();

   // temps => distance
   distance = parseDist(time);

   // v�rification borne minVal
   if (distance < minVal){
      // allumer red => trop proche
      printf("1\n");
      output_high(RED);
      output_low(GREEN);
   }
   else {
      // allumer green => OK
      printf("2\n");
      output_high(GREEN);
      output_low(RED);
   }

   // gestion du point si > que 100 !> cm -> m
   if(distance > 99){
      distance = distance / 10;
      output_high(dot);
   }
   else {
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
   
   /*
   lcd_gotoxy(1,1);
   printf(lcd_putc, "Limie : ");
   printf(lcd_putc, "%d", c);
   printf(lcd_putc, "%d", d);
   printf(lcd_putc, "%d", u);
   */

   // affichage sur 7seg de la distance
   output_b(outputValueParser(distance));

   // envoie distance � java
   printf("%ld", distance);
   printf("\n");

   // attente pour eviter spam
   delay_ms(500);
   }

}
