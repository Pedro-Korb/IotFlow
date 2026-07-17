package iotflow.comportamento;

import iotflow.firmware.GeradorFirmware;
import iotflow.hardware.Atuador;

public class AcaoAlternarEstado implements Acao {

   private final Atuador oAtuador;

   public AcaoAlternarEstado(Atuador oAtuador) {
      if (oAtuador == null) {
         throw new IllegalArgumentException("Atuador não pode ser nulo");
      }
      this.oAtuador = oAtuador;
   }

   @Override
   public String toCpp() {
      String sVariavelPino = GeradorFirmware.variavelPino(this.oAtuador.getNome());
      return "digitalWrite(" + sVariavelPino + ",  !digitalRead(" + sVariavelPino + "));";
   }
}
