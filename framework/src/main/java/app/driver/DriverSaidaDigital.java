package app.driver;

import iotflow.driver.Driver;

public class DriverSaidaDigital implements Driver {

   public static final String CHAVE = "saida-digital";

   @Override
   public String chave() {
      return CHAVE;
   }
}
