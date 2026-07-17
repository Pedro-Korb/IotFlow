package app.driver;

import iotflow.driver.Driver;

public class DriverSeletor implements Driver {
	public static final String CHAVE = "seletor";
	private static boolean primeiraVez = true;

	public DriverSeletor() {
	}

	@Override
	public String chave() {
		return CHAVE;
	}

	@Override
	public String codigoGlobal(String sComponente, int iPino) {
		if (primeiraVez) {
			primeiraVez = false;
			return "int seletor_leds[] = {2, 4, 27, 18, 19, 21, 22, 23};\n" +
				   "int total_seletor = 8;\n" +
				   "int led_selecionado = 0;";
		}
		return "";
	}

	@Override
	public String codigoSetup(String sComponente, int iPino) {
		return String.format("pinMode(%d, OUTPUT); digitalWrite(%d, LOW);", iPino, iPino);
	}

	@Override
	public String codigoLeituraLoop(String sComponente, int iPino) {
		if (sComponente.equals("led1")) {
			return "int novo_led = map(potenciometro_nivel, 0, 4095, 0, total_seletor - 1);\n" +
				   "  if (novo_led != led_selecionado) {\n" +
				   "    digitalWrite(seletor_leds[led_selecionado], LOW);\n" +
				   "    led_selecionado = novo_led;\n" +
				   "    digitalWrite(seletor_leds[led_selecionado], HIGH);\n" +
				   "  }";
		}
		return "";
	}
}
