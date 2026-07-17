package iotflow.hardware;

public record Porta(String sNome, int iPino) {

   public Porta {
      if (sNome == null || sNome.isBlank()) {
         throw new IllegalArgumentException("Nome da porta não pode ser nulo");
      }
      if (iPino < 0) {
         throw new IllegalArgumentException("Pino não pode ser negativo");
      }
   }
}
