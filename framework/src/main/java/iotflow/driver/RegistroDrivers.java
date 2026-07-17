package iotflow.driver;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class RegistroDrivers {

   private final Map<String, Driver> aDrivers = new LinkedHashMap<String, Driver>();

   public RegistroDrivers registrar(Driver oDriver) {
      if (oDriver == null) {
         throw new IllegalArgumentException("Driver não pode ser nulo");
      }
      this.aDrivers.put(oDriver.chave(), oDriver);
      return this;
   }

   public Optional<Driver> buscar(String sChave) {
      return Optional.ofNullable(this.aDrivers.get(sChave));
   }
}
