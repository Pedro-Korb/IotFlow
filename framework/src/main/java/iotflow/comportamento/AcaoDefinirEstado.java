package iotflow.comportamento;

import iotflow.firmware.GeradorFirmware;
import iotflow.hardware.Atuador;

public class AcaoDefinirEstado implements Acao {

   private final Atuador oAtuador;
   private final boolean bLigado;

   public AcaoDefinirEstado(Atuador oAtuador, boolean bLigado) {
      if (oAtuador == null) {
         throw new IllegalArgumentException("Atuador não pode ser nulo");
      }
      this.oAtuador = oAtuador;
      this.bLigado = bLigado;
   }

   @Override
   public String toCpp() {
      String sVariavelPino = GeradorFirmware.variavelPino(this.oAtuador.getNome());
      return "digitalWrite(" + sVariavelPino + ", " + (this.bLigado ? "HIGH" : "LOW") + ");";
   }
}
