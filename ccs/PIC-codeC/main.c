#include <main.h>
#include "LCD420.c"

#use rs232(baud=9600, parity=N, xmit=PIN_C6, rcv=PIN_C7, bits = 8, ERRORS)

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
int8 j=0;
int8 c,d,u;

#INT_RDA
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
* trigger de la sonde
*/
void triggerSonde(){
   output_high(trigger);
   delay_us(10);
   output_low(trigger);
}

/*
* Fonctione d'initalisation du LCD
*/
void init_lcd(){
   delay_ms(500);
   lcd_putc('\f');
   lcd_gotoxy(1,1);
   printf(lcd_putc," Projet Electronique ");
   lcd_gotoxy(1,3);
   printf(lcd_putc," 2020 Groupe 3 ");
   delay_ms(1000);
   lcd_putc('\f');
}


/*
* fonction principale
*/
void main()
{
   setup_low_volt_detect(FALSE);

   setup_spi(FALSE);
   setup_wdt(WDT_OFF);
   setup_timer_1( T1_INTERNAL | T1_DIV_BY_1  );
   setup_comparator(NC_NC_NC_NC);
   setup_vref(FALSE);
   setup_oscillator(False);

   enable_interrupts(INT_RDA); // interuption sur r�ception port RS232
   enable_interrupts(GLOBAL);

   lcd_init();
   init_lcd();
   
   minVal = 100;

   while(true)
   {

   // setup des valeurs
   time = 0;

   // recuperation minValue envoye par JAVA
   //minVal = 100;

   // d�clanchement de la sonde
   triggerSonde();

   // recuperation valeur temps de la sonde
   while(input(echo) == 0){} // attente debut
   set_timer1(0);
   while(input(echo) == 1){} // attente fin
   time = get_timer1();

   // temps => distance
   distance = time/285;

   // envoie distance ici java
   printf(" %ld", distance);
   printf("\n");

   // si donn�es recu depuis java sur RS232 > interuption INT_RSA
   if(flag==1){
     c=buffer[1]-48;
     d=buffer[2]-48;
     u=buffer[3]-48;
     minVal=(int16) (c*100+d*10+u);
     flag=0;
   }

   
   // cr�ation valeurs LCD MINvalue
   c = minVal/100;
   d = (minVal-(c*100))/10;
   u = (minVal-(c*100))-(d*10);
   // Affichage MinValue LCD
   lcd_gotoxy(3,1);
   printf(lcd_putc, " MinVal: ");
   printf(lcd_putc, "%d", c);
   printf(lcd_putc, "%d", d);
   printf(lcd_putc, "%d", u);
   
   // cr�eation valeurs Distance LCD
   c = distance/100;
   d = (distance-(c*100))/10;
   u = (distance-(c*100))-(d*10);
   // affichage distance LCD
   lcd_gotoxy(3,3);
   printf(lcd_putc, " Distance: ");
   printf(lcd_putc, "%d", c);
   printf(lcd_putc, "%d", d);
   printf(lcd_putc, "%d", u);

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

   // affichage sur 7seg de la distance
   output_b(outputValueParser(distance));


   // attente pour eviter spam
   delay_ms(500);
   }

}
