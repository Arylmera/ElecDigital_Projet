#include <main.h>

void main()
{
   setup_low_volt_detect(FALSE);

   //Example
   
   int b_value = 0x00;
   
   while(true)
   {
      output_high(PIN_E0);
      output_high(PIN_E1);
      delay_ms(1000);
      output_low(PIN_E0);
      output_low(PIN_E1);
      output_b(b_value);
      b_value++;
   }

}
