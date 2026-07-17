package app.driver;

import iotflow.driver.Driver;

public class DriverSequenciador implements Driver {
	public static final String CHAVE = "sequenciador";
	private static boolean primeiraVez = true;
	private int iMinIntervalo = 200;
	private int iMaxIntervalo = 2000;

	public DriverSequenciador() {
	}

	public DriverSequenciador(int iMinimo, int iMaximo) {
		this.iMinIntervalo = iMinimo;
		this.iMaxIntervalo = iMaximo;
	}

	@Override
	public String chave() {
		return CHAVE;
	}

	@Override
	public String codigoGlobal(String sComponente, int iPino) {
		if (primeiraVez) {
			primeiraVez = false;
			return "int sequencia_leds[] = {21, 22, 23, 19, 2, 4, 27, 18};\n" +
				   "int total_leds = 8;\n" +
				   "int led_atual = 0;\n" +
				   "unsigned long ultima_mudanca = 0;\n" +
				   "unsigned long intervalo_ms = 500;";
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
			return String.format(
				"intervalo_ms = map(potenciometro_nivel, 0, 4095, %d, %d);\n" +
				"  unsigned long agora = millis();\n" +
				"  if (agora - ultima_mudanca >= intervalo_ms) {\n" +
				"    digitalWrite(sequencia_leds[led_atual], LOW);\n" +
				"    led_atual = (led_atual + 1) %% total_leds;\n" +
				"    digitalWrite(sequencia_leds[led_atual], HIGH);\n" +
				"    ultima_mudanca = agora;\n" +
				"  }",
				iMinIntervalo, iMaxIntervalo
			);
		}
		return "";
	}
}
