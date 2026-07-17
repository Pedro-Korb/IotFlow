package iotflow.comportamento;

public enum OperadorLogico {
   E("&&"),
   OU("||");

   private final String sCpp;

   OperadorLogico(String sCpp) {
      this.sCpp = sCpp;
   }

   public String toCpp() {
      return this.sCpp;
   }
}
