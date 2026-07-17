package iotflow.comportamento;

public interface Condicao {

   String toCpp();

   default Condicao e(Condicao oOutra) {
      return new CondicaoComposta(OperadorLogico.E, this, oOutra);
   }

   default Condicao ou(Condicao oOutra) {
      return new CondicaoComposta(OperadorLogico.OU, this, oOutra);
   }
}
