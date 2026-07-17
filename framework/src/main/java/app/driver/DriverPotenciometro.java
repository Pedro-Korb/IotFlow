package app.driver;

import iotflow.driver.Driver;
import iotflow.firmware.GeradorFirmware;

public class DriverPotenciometro implements Driver {

   public static final String CHAVE = "nivel-potenciometro";

   @Override
   public String chave() {
      return CHAVE;
   }

   @Override
   public String codigoLeituraLoop(String sComponente, int iPino) {
      String sVariavel = GeradorFirmware.variavelLeitura(sComponente, "nivel");
      return sVariavel + " = analogRead(" + GeradorFirmware.variavelPino(sComponente) + ");";
   }
}
