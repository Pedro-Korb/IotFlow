package iotflow.hardware;

public class Connection {

   private final ComponenteHardware oComponente;
   private final Controlador oControlador;
   private final Porta oPorta;

   public Connection(ComponenteHardware oComponente, Controlador oControlador, Porta oPorta) {
      if (oComponente == null) {
         throw new IllegalArgumentException("Componente não pode ser nulo");
      }
      if (oControlador == null) {
         throw new IllegalArgumentException("Controlador não pode ser nulo");
      }
      if (oPorta == null) {
         throw new IllegalArgumentException("Porta não pode ser nula");
      }
      this.oComponente = oComponente;
      this.oControlador = oControlador;
      this.oPorta = oPorta;
   }

   public String sComponente() {
      return this.oComponente.getNome();
   }

   public String sControlador() {
      return this.oControlador.getNome();
   }

   public String sPorta() {
      return this.oPorta.sNome();
   }

   public ComponenteHardware getComponente() {
      return this.oComponente;
   }

   public Controlador getControlador() {
      return this.oControlador;
   }

   public Porta getPorta() {
      return this.oPorta;
   }
}
