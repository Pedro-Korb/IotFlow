package app.driver;

import iotflow.driver.Driver;
import iotflow.firmware.GeradorFirmware;

public class DriverDS18B20 implements Driver {

   public static final String CHAVE = "ds18b20";

   @Override
   public String chave() {
      return CHAVE;
   }

   @Override
   public String codigoGlobal(String sComponente, int iPino) {
      String sNome = GeradorFirmware.sanitizar(sComponente);
      return "OneWire oneWire_" + sNome + "(" + iPino + ");\n"
           + "DallasTemperature " + this.objeto(sComponente) + "(&oneWire_" + sNome + ");";
   }

   @Override
   public String codigoSetup(String sComponente, int iPino) {
      return this.objeto(sComponente) + ".begin();";
   }

   @Override
   public String codigoLeituraLoop(String sComponente, int iPino) {
      String sTemperatura = GeradorFirmware.variavelLeitura(sComponente, "temperatura");
      return this.objeto(sComponente) + ".requestTemperatures();\n  "
           + sTemperatura + " = " + this.objeto(sComponente) + ".getTempCByIndex(0);";
   }

   private String objeto(String sComponente) {
      return "ds18b20_" + GeradorFirmware.sanitizar(sComponente);
   }
}
