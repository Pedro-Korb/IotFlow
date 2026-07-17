package iotflow.hardware;

import java.util.ArrayList;
import java.util.List;

public class Controlador extends ComponenteHardware {

   private final List<Porta> aPortas = new ArrayList<Porta>();

   public Controlador(String sNome) {
      super(sNome);
   }

   public Controlador setPortas(Porta... aPortas) {
      if (aPortas == null) {
         throw new IllegalArgumentException("Lista de portas não pode ser nula");
      }
      this.aPortas.clear();
      for (Porta oPorta : aPortas) {
         if (oPorta == null) {
            throw new IllegalArgumentException("Porta não pode ser nula");
         }
         if (!this.aPortas.contains(oPorta)) {
            this.aPortas.add(oPorta);
         }
      }
      return this;
   }

   public List<Porta> getPortas() {
      return List.copyOf(this.aPortas);
   }
}
