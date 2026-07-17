package iotflow.hardware;

import java.util.ArrayList;
import java.util.List;

public class Sensor extends ComponenteHardware {

   private final List<Medicao> aMedicoes = new ArrayList<Medicao>();

   public Sensor(String sNome) {
      super(sNome);
   }

   public Sensor setMedicoes(Medicao... aMedicoes) {
      if (aMedicoes == null) {
         throw new IllegalArgumentException("Lista de medições não pode ser nula");
      }
      this.aMedicoes.clear();
      for (Medicao oMedicao : aMedicoes) {
         if (oMedicao == null) {
            throw new IllegalArgumentException("Medição não pode ser nula");
         }
         if (!this.aMedicoes.contains(oMedicao)) {
            this.aMedicoes.add(oMedicao);
         }
      }
      return this;
   }

   public Sensor driver(String sDriver) {
      this.setDriver(sDriver);
      return this;
   }

   public List<Medicao> getMedicoes() {
      return List.copyOf(this.aMedicoes);
   }
}
