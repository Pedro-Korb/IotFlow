package iotflow.nucleo;

import iotflow.driver.Driver;
import iotflow.driver.RegistroDrivers;
import iotflow.firmware.ConfiguracaoFirmware;
import iotflow.firmware.GeradorFirmware;

import java.nio.file.Path;

public class Principal {

   private static Principal INSTANCIA;

   private final RegistroDrivers oRegistroDrivers = new RegistroDrivers();
   private final ConfiguracaoFirmware oConfiguracao = new ConfiguracaoFirmware();

   private Principal() {
   }

   public static Principal getInstance() {
      if (INSTANCIA == null) {
         INSTANCIA = new Principal();
      }
      return INSTANCIA;
   }

   public Principal registrarDriver(Driver ...aDriver) {
      for (Driver oDriver : aDriver) {
         this.oRegistroDrivers.registrar(oDriver);  
      }
      return this;
   }

   public ConfiguracaoFirmware getConfiguracao() {
      return this.oConfiguracao;
   }

   public GeradorFirmware gerarFirmware(ProjetoIoT oProjeto) {
      return new GeradorFirmware(this.oRegistroDrivers, this.oConfiguracao).gerar(oProjeto);
   }

   public Path getDiretorioSaida() {
      Path oAtual = Path.of("").toAbsolutePath();
      if (oAtual.getFileName() != null && "framework".equals(oAtual.getFileName().toString())) {
         return Path.of("..", "firmware-output");
      }
      return Path.of("firmware-output");
   }
}
