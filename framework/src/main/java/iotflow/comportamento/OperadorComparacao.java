package iotflow.comportamento;

public enum OperadorComparacao {
   MENOR("<"),
   MENOR_IGUAL("<="),
   MAIOR(">"),
   MAIOR_IGUAL(">="),
   IGUAL("=="),
   DIFERENTE("!=");

   private final String sCpp;

   OperadorComparacao(String sCpp) {
      this.sCpp = sCpp;
   }

   public String toCpp() {
      return this.sCpp;
   }
}
