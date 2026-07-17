package iotflow.hardware;

public class Atuador extends ComponenteHardware {

   public Atuador(String sNome) {
      super(sNome);
   }

   public Atuador driver(String sDriver) {
      this.setDriver(sDriver);
      return this;
   }
}
