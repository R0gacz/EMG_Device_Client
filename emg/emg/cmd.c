/**
 * 2023 Maksymilian Mruszczak
 * 
 * Bindings for testing in RTOS cli testing
 */ 

#include <stdio.h>
#include <string.h>
#include <assert.h>
#include <cli.h>
#include "adc.h"

static void cmd_init_adc(char *, int, int, char **);
static void cmd_read_adc(char *, int, int, char **);

const static struct cli_command cmds_user[] STATIC_CLI_CMD_ATTRIBUTE = {
    {"init_adc",        "Init ADC Channel",          cmd_init_adc},
    {"read_adc",        "Read ADC Channel",          cmd_read_adc},
};

static void
cmd_init_adc(char *buf, int len, int argc, char **argv)
{
	assert(init_adc(10000, 1000) == 0);
}

static void
cmd_read_adc(char *buf, int len, int argc, char **argv)
{
	printf("ADC value = %f\n", get_adc());
}

int
cli_init(void)
{
	return 0;
}

/*************************************
 * handlers from bouffalo/pine example
 */

void
__assert_func(const char *file, int line, const char *func, const char *failedexpr)
{
	//  Show the assertion failure, file, line, function name
	printf("Assertion Failed \"%s\": file \"%s\", line %d%s%s\r\n",
			failedexpr, file, line, func ? ", function: " : "",
			func ? func : "");
	//  Loop forever, do not pass go, do not collect $200
	for (;;) {}
}

///////////////////////////////////////////////////////////////////////////////
//  Dump Stack

/// Dump the current stack
void
dump_stack(void)
{
	//  For getting the Stack Frame Pointer. Must be first line of function.
	uintptr_t *fp;

	//  Fetch the Stack Frame Pointer. Based on backtrace_riscv from
	//  https://github.com/bouffalolab/bl_iot_sdk/blob/master/components/bl602/freertos_riscv_ram/panic/panic_c.c#L76-L99
	__asm__("add %0, x0, fp" : "=r"(fp));
	printf("dump_stack: frame pointer=%p\r\n", fp);

	//  Dump the stack, starting at Stack Frame Pointer - 1
	printf("=== stack start ===\r\n");
	for (int i = 0; i < 128; i++) {
		uintptr_t *ra = (uintptr_t *)*(unsigned long *)(fp - 1);
		printf("@ %p: %p\r\n", fp - 1, ra);
		fp++;
	}
	printf("=== stack end ===\r\n\r\n");
}
