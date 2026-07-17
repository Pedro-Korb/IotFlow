package iotflow.hardware;

public abstract class ComponenteHardware {

   private final String sNome;
   private String sDriver;

   public ComponenteHardware(String sNome) {
      if (sNome == null || sNome.isBlank()) {
         throw new IllegalArgumentException("O nome do componente não pode ser nulo");
      }
      this.sNome = sNome;
   }

   public String getNome() {
      return this.sNome;
   }

   public String getDriver() {
      return this.sDriver;
   }

   protected void setDriver(String sDriver) {
      this.sDriver = sDriver;
   }
}
