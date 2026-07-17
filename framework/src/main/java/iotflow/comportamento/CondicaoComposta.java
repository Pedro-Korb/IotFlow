package iotflow.comportamento;

import java.util.ArrayList;
import java.util.List;

public class CondicaoComposta implements Condicao {

   private final OperadorLogico oOperador;
   private final List<Condicao> aCondicoes = new ArrayList<Condicao>();

   public CondicaoComposta(OperadorLogico oOperador, Condicao... aCondicoes) {
      if (oOperador == null) {
         throw new IllegalArgumentException("Operador lógico não pode ser nulo");
      }
      if (aCondicoes == null || aCondicoes.length < 2) {
         throw new IllegalArgumentException("Uma composição precisa de ao menos 2 condições");
      }
      for (Condicao oCondicao : aCondicoes) {
         if (oCondicao == null) {
            throw new IllegalArgumentException("Condição não pode ser nula");
         }
         this.aCondicoes.add(oCondicao);
      }
      this.oOperador = oOperador;
   }

   @Override
   public String toCpp() {
      List<String> aPartes = new ArrayList<String>();
      for (Condicao oCondicao : this.aCondicoes) {
         aPartes.add("(" + oCondicao.toCpp() + ")");
      }
      return String.join(" " + this.oOperador.toCpp() + " ", aPartes);
   }
}
