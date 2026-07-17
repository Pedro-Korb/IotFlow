package app.driver;

import iotflow.driver.Driver;
import iotflow.firmware.GeradorFirmware;

public class DriverRelogio implements Driver {

   public static final String CHAVE = "relogio-fase";

   @Override
   public String chave() {
      return CHAVE;
   }

   @Override
   public String codigoLeituraLoop(String sComponente, int iPino) {
      String sVariavel = GeradorFirmware.variavelLeitura(sComponente, "fase");
      return sVariavel + " = (millis() / 1000) % 2;";
   }
}
