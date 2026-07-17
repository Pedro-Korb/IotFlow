package iotflow.firmware;

import iotflow.comportamento.Acao;
import iotflow.comportamento.Regra;
import iotflow.driver.Driver;
import iotflow.driver.RegistroDrivers;
import iotflow.hardware.Atuador;
import iotflow.hardware.ComponenteHardware;
import iotflow.hardware.Medicao;
import iotflow.hardware.Sensor;
import iotflow.hardware.TipoValor;
import iotflow.nucleo.ProjetoIoT;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class GeradorFirmware {

   private final RegistroDrivers oRegistro;
   private final ConfiguracaoFirmware oConfiguracao;
   private String sCodigoFonte;

   public GeradorFirmware(RegistroDrivers oRegistro) {
      this(oRegistro, new ConfiguracaoFirmware());
   }

   public GeradorFirmware(RegistroDrivers oRegistro, ConfiguracaoFirmware oConfiguracao) {
      if (oRegistro == null) {
         throw new IllegalArgumentException("Registro não pode ser nulo");
      }
      if (oConfiguracao == null) {
         throw new IllegalArgumentException("Configuração não pode ser nula");
      }
      this.oRegistro = oRegistro;
      this.oConfiguracao = oConfiguracao;
   }

   public static String variavelLeitura(String sComponente, String sMedicao) {
      return sanitizar(sComponente) + "_" + sanitizar(sMedicao);
   }

   public static String variavelPino(String sComponente) {
      return sanitizar(sComponente) + "_pino";
   }

   public static String sanitizar(String sNome) {
      return sNome.replaceAll("[^a-zA-Z0-9]", "_");
   }

   public GeradorFirmware gerar(ProjetoIoT oProjeto) {
      if (oProjeto == null) {
         throw new IllegalArgumentException("Projeto não pode ser nulo");
      }
      StringBuilder oCodigo = new StringBuilder();
      for (String sInclude : this.oConfiguracao.getIncludes()) {
         oCodigo.append("#include <").append(sInclude).append(">\n");
      }
      oCodigo.append("\n");
      this.constantesDePino(oCodigo, oProjeto);
      this.declaracoesGlobais(oCodigo, oProjeto);
      this.variaveisDeLeitura(oCodigo, oProjeto);
      this.setup(oCodigo, oProjeto);
      this.loop(oCodigo, oProjeto);
      this.sCodigoFonte = oCodigo.toString();
      return this;
   }

   public String getCodigoFonte() {
      if (this.sCodigoFonte == null) {
         throw new IllegalStateException("Nenhum firmware gerado ainda: chame gerar(oProjeto) antes");
      }
      return this.sCodigoFonte;
   }

   public Path salvarProjetoPlatformIo(Path oDiretorio) throws IOException {
      Path oSrc = oDiretorio.resolve("src");
      Files.createDirectories(oSrc);
      Files.writeString(oDiretorio.resolve("platformio.ini"), this.platformioIni());
      Files.writeString(oSrc.resolve("main.cpp"), this.getCodigoFonte());
      return oDiretorio;
   }

   private String platformioIni() {
      StringBuilder oIni = new StringBuilder();
      oIni.append("[env:").append(this.oConfiguracao.getPlaca()).append("]\n");
      oIni.append("platform = ").append(this.oConfiguracao.getPlataforma()).append("\n");
      oIni.append("board = ").append(this.oConfiguracao.getPlaca()).append("\n");
      oIni.append("framework = arduino\n");
      oIni.append("monitor_speed = ").append(this.oConfiguracao.getBaudRate()).append("\n");
      if (!this.oConfiguracao.getLibDeps().isEmpty()) {
         oIni.append("lib_deps =\n");
         for (String sLibDep : this.oConfiguracao.getLibDeps()) {
            oIni.append("  ").append(sLibDep).append("\n");
         }
      }
      return oIni.toString();
   }

   private void constantesDePino(StringBuilder oCodigo, ProjetoIoT oProjeto) {
      for (ComponenteHardware oComponente : oProjeto.getComponentes()) {
         Optional<Integer> oPino = oProjeto.pinoDe(oComponente.getNome());
         if (oPino.isPresent()) {
            String sVariavel = variavelPino(oComponente.getNome());
            oCodigo.append("const int ").append(sVariavel)
                   .append(" = ").append(oPino.get()).append(";\n");
         }
      }
      oCodigo.append("\n");
   }

   private void declaracoesGlobais(StringBuilder oCodigo, ProjetoIoT oProjeto) {
      boolean bEmitiu = false;
      for (ComponenteHardware oComponente : oProjeto.getComponentes()) {
         String sGlobal = this.codigoDriver(oComponente, oProjeto, TrechoDriver.GLOBAL);
         if (!sGlobal.isBlank()) {
            oCodigo.append(sGlobal).append("\n");
            bEmitiu = true;
         }
      }
      if (bEmitiu) {
         oCodigo.append("\n");
      }
   }

   private void variaveisDeLeitura(StringBuilder oCodigo, ProjetoIoT oProjeto) {
      for (ComponenteHardware oComponente : oProjeto.getComponentes()) {
         if (oComponente instanceof Sensor oSensor) {
            for (Medicao oMedicao : oSensor.getMedicoes()) {
               String sTipoCpp = oMedicao.oTipo() == TipoValor.DECIMAL ? "float" : "long";
               String sVariavel = variavelLeitura(oSensor.getNome(), oMedicao.sNome());
               oCodigo.append(sTipoCpp).append(" ").append(sVariavel).append(" = 0;\n");
            }
         }
      }
      oCodigo.append("\n");
   }

   private void setup(StringBuilder oCodigo, ProjetoIoT oProjeto) {
      oCodigo.append("void setup() {\n");
      oCodigo.append("  Serial.begin(").append(this.oConfiguracao.getBaudRate()).append(");\n");
      for (ComponenteHardware oComponente : oProjeto.getComponentes()) {
         if (oComponente instanceof Atuador && oProjeto.pinoDe(oComponente.getNome()).isPresent()) {
            String sVariavel = variavelPino(oComponente.getNome());
            oCodigo.append("  pinMode(").append(sVariavel).append(", OUTPUT);\n");
         }
      }
      for (ComponenteHardware oComponente : oProjeto.getComponentes()) {
         String sSetup = this.codigoDriver(oComponente, oProjeto, TrechoDriver.SETUP);
         if (!sSetup.isBlank()) {
            oCodigo.append("  ").append(sSetup).append("\n");
         }
      }
      oCodigo.append("}\n\n");
   }

   private void loop(StringBuilder oCodigo, ProjetoIoT oProjeto) {
      oCodigo.append("void loop() {\n");
      for (ComponenteHardware oComponente : oProjeto.getComponentes()) {
         String sLeitura = this.codigoDriver(oComponente, oProjeto, TrechoDriver.LEITURA);
         if (!sLeitura.isBlank()) {
            oCodigo.append("  ").append(sLeitura).append("\n");
         }
      }
      oCodigo.append("\n");
      for (Regra oRegra : oProjeto.getRegras()) {
         oCodigo.append("  if (").append(oRegra.getCondicao().toCpp()).append(") {\n");
         for (Acao oAcao : oRegra.getAcoes()) {
            oCodigo.append("    ").append(oAcao.toCpp()).append("\n");
         }
         oCodigo.append("  }\n");
      }

      if (this.oConfiguracao.isDebug()) {
         for (ComponenteHardware oComponente : oProjeto.getComponentes()) {
            if (oComponente instanceof Sensor oSensor) {
               for (Medicao oMedicao : oSensor.getMedicoes()) {
                  String sVariavel = variavelLeitura(oSensor.getNome(), oMedicao.sNome());
                  oCodigo.append("  Serial.print(\"").append(sVariavel).append(": \");\n");
                  oCodigo.append("  Serial.println(").append(sVariavel).append(");\n");
               }
            }
         }
      }

      this.getCodigoDelay();

      oCodigo.append(this.getCodigoDelay());
      oCodigo.append("}\n");
   }

   private String getCodigoDelay() {
      StringBuilder oStringBuilder = new StringBuilder();
      if(this.oConfiguracao.getVariavelLeituraDelay() != null && this.oConfiguracao.getMinDelayVariavel() > 0 && this.oConfiguracao.getMaxDelayVariavel() > 0) {
         return oStringBuilder
            .append("\n  delay(map(")
            .append(this.oConfiguracao.getVariavelLeituraDelay())
            .append(", 0, 4095, ")
            .append(this.oConfiguracao.getMinDelayVariavel())
            .append(", ")
            .append(this.oConfiguracao.getMaxDelayVariavel())
            .append("));\n")
            .toString();
      }

      return oStringBuilder
         .append("\n  delay(")
         .append(this.oConfiguracao.getDelayMs())
         .append(");\n").toString();
   }

   private enum TrechoDriver {
      GLOBAL,
      SETUP,
      LEITURA
   }

   private String codigoDriver(ComponenteHardware oComponente, ProjetoIoT oProjeto, TrechoDriver oTrecho) {
      if (oComponente.getDriver() == null) {
         return "";
      }
      Optional<Driver> oDriver = this.oRegistro.buscar(oComponente.getDriver());
      if (oDriver.isEmpty()) {
         return "";
      }
      int iPino = oProjeto.pinoDe(oComponente.getNome()).orElse(-1);
      return switch (oTrecho) {
         case GLOBAL  -> oDriver.get().codigoGlobal(oComponente.getNome(), iPino);
         case SETUP   -> oDriver.get().codigoSetup(oComponente.getNome(), iPino);
         case LEITURA -> oDriver.get().codigoLeituraLoop(oComponente.getNome(), iPino);
      };
   }
}
