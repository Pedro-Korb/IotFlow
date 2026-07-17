package iotflow.nucleo;

import iotflow.comportamento.Regra;
import iotflow.hardware.ComponenteHardware;
import iotflow.hardware.Connection;
import iotflow.hardware.Controlador;
import iotflow.hardware.Porta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjetoIoT {

   private final String sNome;
   private Controlador oControlador;
   private List<ComponenteHardware> aComponentes = new ArrayList<>();
   private List<Connection> aConnections = new ArrayList<>();
   private List<Regra> aRegras = new ArrayList<>();

   public ProjetoIoT(String sNome) {
      if (sNome == null || sNome.isBlank()) {
         throw new IllegalArgumentException("Nome do projeto não pode ser nulo");
      }
      this.sNome = sNome;
   }

   public ProjetoIoT controlador(Controlador oControlador) {
      if (oControlador == null) {
         throw new IllegalArgumentException("Controlador não pode ser nulo");
      }
      this.oControlador = oControlador;
      return this;
   }

   public ProjetoIoT componentes(ComponenteHardware... aComponentes) {
      if (aComponentes == null) {
         throw new IllegalArgumentException("Lista de componentes não pode ser nula");
      }
      this.aComponentes.clear();
      for (ComponenteHardware oComponente : aComponentes) {
         if (oComponente == null) {
            throw new IllegalArgumentException("Componente não pode ser nulo");
         }
         this.aComponentes.add(oComponente);
      }
      return this;
   }

   public ProjetoIoT conexoes(Connection... aConnections) {
      if (aConnections == null) {
         throw new IllegalArgumentException("Lista de conexões não pode ser nula");
      }
      this.aConnections.clear();
      for (Connection oConnection : aConnections) {
         if (oConnection == null) {
            throw new IllegalArgumentException("Conexão não pode ser nula");
         }
         this.aConnections.add(oConnection);
      }
      return this;
   }

   public ProjetoIoT regras(Regra... aRegras) {
      if (aRegras == null) {
         throw new IllegalArgumentException("Lista de regras não pode ser nula");
      }
      this.aRegras.clear();
      for (Regra oRegra : aRegras) {
         if (oRegra == null) {
            throw new IllegalArgumentException("Regra não pode ser nula");
         }
         this.aRegras.add(oRegra);
      }
      return this;
   }

   public ProjetoIoT build() {
      if (this.oControlador == null) {
         throw new IllegalStateException("O projeto precisa de um controlador");
      }
      this.validarConnections();
      return this;
   }

   public String getNome() {
      return this.sNome;
   }

   public Controlador getControlador() {
      return this.oControlador;
   }

   public List<ComponenteHardware> getComponentes() {
      return List.copyOf(this.aComponentes);
   }

   public List<Connection> getConnections() {
      return List.copyOf(this.aConnections);
   }

   public List<Regra> getRegras() {
      return List.copyOf(this.aRegras);
   }

   public Optional<Integer> pinoDe(String sComponente) {
      for (Connection oConnection : this.aConnections) {
         if (oConnection.sComponente().equals(sComponente) && this.oControlador != null) {
            for (Porta oPorta : this.oControlador.getPortas()) {
               if (oPorta.sNome().equals(oConnection.sPorta())) {
                  return Optional.of(oPorta.iPino());
               }
            }
         }
      }
      return Optional.empty();
   }

   private void validarConnections() {
      for (Connection oConnection : this.aConnections) {
         if (!this.aComponentes.contains(oConnection.getComponente())) {
            throw new IllegalStateException("Conexão inválida: o componente \""
                  + oConnection.getComponente().getNome() + "\" não foi declarado no projeto");
         }
         if (!this.oControlador.equals(oConnection.getControlador())) {
            throw new IllegalStateException("Conexão inválida: o controlador \""
                  + oConnection.getControlador().getNome() + "\" não coincide com o controlador do projeto");
         }
         if (!this.oControlador.getPortas().contains(oConnection.getPorta())) {
            throw new IllegalStateException("Conexão inválida: o controlador \""
                  + this.oControlador.getNome() + "\" não tem a porta \""
                  + oConnection.getPorta().sNome() + "\"");
         }
      }
   }
}
