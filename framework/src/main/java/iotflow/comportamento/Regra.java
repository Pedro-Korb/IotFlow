package iotflow.comportamento;

import java.util.ArrayList;
import java.util.List;

public class Regra {

   private final Condicao oCondicao;
   private final List<Acao> aAcoes = new ArrayList<Acao>();

   private Regra(Condicao oCondicao) {
      if (oCondicao == null) {
         throw new IllegalArgumentException("Condição não pode ser nula");
      }
      this.oCondicao = oCondicao;
   }

   public static Regra quando(Condicao oCondicao) {
      return new Regra(oCondicao);
   }

   public Regra entao(Acao oAcao) {
      return this.e(oAcao);
   }

   public Regra e(Acao oAcao) {
      if (oAcao == null) {
         throw new IllegalArgumentException("Ação não pode ser nula");
      }
      this.aAcoes.add(oAcao);
      return this;
   }

   public Condicao getCondicao() {
      return this.oCondicao;
   }

   public List<Acao> getAcoes() {
      return List.copyOf(this.aAcoes);
   }
}
