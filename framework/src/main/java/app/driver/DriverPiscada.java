package app.driver;

import iotflow.driver.Driver;

public class DriverPiscada implements Driver {
	public static final String CHAVE = "piscada";

	@Override
	public String chave() {
		return CHAVE;
	}

	@Override
	public String codigoGlobal(String sComponente, int iPino) {
		return String.format(
			"const unsigned long %s_intervalo = 500;\n" +
			"struct BlinkTimer_%s {\n" +
			"  int pino;\n" +
			"  unsigned long intervalMs;\n" +
			"  unsigned long ultimaTroca;\n" +
			"  bool estado;\n" +
			"  \n" +
			"  BlinkTimer_%s(int p, unsigned long interval) : pino(p), intervalMs(interval), ultimaTroca(0), estado(false) {}\n" +
			"  \n" +
			"  void atualizar() {\n" +
			"    unsigned long agora = millis();\n" +
			"    if (agora - ultimaTroca >= intervalMs) {\n" +
			"      estado = !estado;\n" +
			"      digitalWrite(pino, estado ? HIGH : LOW);\n" +
			"      ultimaTroca = agora;\n" +
			"    }\n" +
			"  }\n" +
			"};\n" +
			"BlinkTimer_%s %s_piscador(%d, %s_intervalo);",
			sComponente, sComponente, sComponente, sComponente, sComponente, iPino, sComponente
		);
	}

	@Override
	public String codigoSetup(String sComponente, int iPino) {
		return String.format("pinMode(%d, OUTPUT);", iPino);
	}

	@Override
	public String codigoLeituraLoop(String sComponente, int iPino) {
		return String.format("%s_piscador.atualizar();", sComponente);
	}
}
