package iotflow.comportamento;

import iotflow.firmware.GeradorFirmware;
import iotflow.hardware.Medicao;
import iotflow.hardware.Sensor;

public class CondicaoComparacao implements Condicao {

   private final Sensor oSensor;
   private final Medicao oMedicao;
   private final OperadorComparacao oOperador;
   private final double dValor;

   public CondicaoComparacao(Sensor oSensor, Medicao oMedicao, OperadorComparacao oOperador, double dValor) {
      if (oSensor == null) {
         throw new IllegalArgumentException("Sensor não pode ser nulo");
      }
      if (oMedicao == null) {
         throw new IllegalArgumentException("Medição não pode ser nula");
      }
      if (oOperador == null) {
         throw new IllegalArgumentException("Operador não pode ser nulo");
      }
      this.oSensor = oSensor;
      this.oMedicao = oMedicao;
      this.oOperador = oOperador;
      this.dValor = dValor;
   }

   @Override
   public String toCpp() {
      String sVariavel = GeradorFirmware.variavelLeitura(this.oSensor.getNome(), this.oMedicao.sNome());
      return sVariavel + " " + this.oOperador.toCpp() + " " + this.formatarValor();
   }

   private String formatarValor() {
      if (this.dValor == Math.floor(this.dValor) && !Double.isInfinite(this.dValor)) {
         return Long.toString((long) this.dValor);
      }
      return Double.toString(this.dValor);
   }
}
