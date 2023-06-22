/**
 * 2023 Maksymilian Mruszczak
 * 
 * ADC wrapper
 */ 

#include <stdio.h>
#include <hal_adc.h>
#include "adc.h"

/* Only these GPIOs are supported: 4, 5, 6, 9, 10, 11, 12, 13, 14, 15 */
#define ADC_GPIO 11 /* input pin */

int
init_adc(unsigned int sampling_rate, unsigned int nsamples)
{

	int r = hal_adc_init(
			1, /* Single-Channel Conversion Mode */
			sampling_rate, /* Samples per second */
			nsamples, /* # of samples to take per call */
			ADC_GPIO  /* GPIO Pin Number */
			);
	return r;
}

float
get_adc(void) {
	float val;
    int ival = hal_adc_get_data(ADC_GPIO, 0);
	/*                                    ^
	 * 1 - scale value to get mV
	 * we prefer 0-1 range so this param remains 0
	 */
	val = (float)ival / 65535.f;
	return val;
}
