package app.driver;

import iotflow.driver.Driver;
import iotflow.firmware.GeradorFirmware;

public class DriverDHT22 implements Driver {

   public static final String CHAVE = "dht22";

   @Override
   public String chave() {
      return CHAVE;
   }

   @Override
   public String codigoGlobal(String sComponente, int iPino) {
      return "DHT " + this.objeto(sComponente) + "(" + iPino + ", DHT22);";
   }

   @Override
   public String codigoSetup(String sComponente, int iPino) {
      return this.objeto(sComponente) + ".begin();";
   }

   @Override
   public String codigoLeituraLoop(String sComponente, int iPino) {
      String sTemperatura = GeradorFirmware.variavelLeitura(sComponente, "temperatura");
      String sUmidade = GeradorFirmware.variavelLeitura(sComponente, "umidade");
      return sTemperatura + " = " + this.objeto(sComponente) + ".readTemperature();\n  "
           + sUmidade + " = " + this.objeto(sComponente) + ".readHumidity();";
   }

   private String objeto(String sComponente) {
      return "dht_" + GeradorFirmware.sanitizar(sComponente);
   }
}
