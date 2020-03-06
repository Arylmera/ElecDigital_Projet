#include <main.h>

int b_value;

int outputValueParser(int value){
   int output = 0;
   if (b_value < 10) { output = value;}
   else {
    int x = value;
    x = x%10;
    int i = value;
    i = i/10;
    
    output = x + i*16;
   }
   return output
}

void main()
{
   setup_low_volt_detect(FALSE);
   b_value = 0;

    
   while(true)
   {
   output_b( outputValueParser(b_value) );
   output_high(PIN_E1);
   delay_ms(500);
   output_low(PIN_E1);
   b_value++;
   
   }

}

