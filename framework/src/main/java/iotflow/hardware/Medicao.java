package iotflow.hardware;

public record Medicao(String sNome, TipoValor oTipo) {

   public Medicao {
      if (sNome == null || sNome.isBlank()) {
         throw new IllegalArgumentException("Nome da medição não pode ser nulo");
      }
      if (oTipo == null) {
         throw new IllegalArgumentException("Tipo da medição não pode ser nulo");
      }
   }
}
