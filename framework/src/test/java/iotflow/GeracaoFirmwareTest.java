package iotflow;

import iotflow.comportamento.AcaoDefinirEstado;
import iotflow.comportamento.CondicaoComparacao;
import iotflow.comportamento.OperadorComparacao;
import iotflow.comportamento.Regra;
import iotflow.driver.Driver;
import iotflow.driver.RegistroDrivers;
import iotflow.firmware.ConfiguracaoFirmware;
import iotflow.firmware.GeradorFirmware;
import iotflow.hardware.Atuador;
import iotflow.hardware.Connection;
import iotflow.hardware.Controlador;
import iotflow.hardware.Medicao;
import iotflow.hardware.Porta;
import iotflow.hardware.Sensor;
import iotflow.hardware.TipoValor;
import iotflow.nucleo.ProjetoIoT;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GeracaoFirmwareTest {

   private ProjetoIoT projetoMinimo() {
      Controlador oEsp32 = new Controlador("esp32");
      Porta oPortaD22 = new Porta("D22", 22);
      oEsp32.setPortas(oPortaD22);

      Sensor oSensor = new Sensor("sensor");
      Medicao oMedicaoV = new Medicao("v", TipoValor.INTEIRO);
      oSensor.setMedicoes(oMedicaoV);

      Atuador oLed = new Atuador("led");

      return new ProjetoIoT("p")
            .controlador(oEsp32)
            .componentes(oSensor, oLed)
            .conexoes(new Connection(oLed, oEsp32, oPortaD22))
            .regras(Regra
                  .quando(new CondicaoComparacao(oSensor, oMedicaoV, OperadorComparacao.MENOR, 10))
                  .entao(new AcaoDefinirEstado(oLed, true))
            )
            .build();
   }

   @Test
   void firmwareTemEstruturaArduino() {
      GeradorFirmware oFirmware = new GeradorFirmware(new RegistroDrivers()).gerar(projetoMinimo());
      String sCodigo = oFirmware.getCodigoFonte();

      assertTrue(sCodigo.contains("void setup()"), "deve ter setup()");
      assertTrue(sCodigo.contains("void loop()"), "deve ter loop()");
      assertTrue(sCodigo.contains("Serial.begin(115200)"), "deve iniciar Serial");
   }

   @Test
   void firmwareMapeiaPinoDaConexao() {
      String sCodigo = new GeradorFirmware(new RegistroDrivers()).gerar(projetoMinimo()).getCodigoFonte();

      assertTrue(sCodigo.contains("led_pino = 22"), "o pino do LED deve ser 22");
      assertTrue(sCodigo.contains("pinMode(led_pino, OUTPUT)"), "deve declarar o pino como saida");
   }

   @Test
   void regraViraIfNoLoopComDigitalWrite() {
      String sCodigo = new GeradorFirmware(new RegistroDrivers()).gerar(projetoMinimo()).getCodigoFonte();

      assertTrue(sCodigo.contains("long sensor_v = 0;"), "a leitura do sensor deve virar variavel");
      assertTrue(sCodigo.contains("if (sensor_v < 10)"), "a condicao deve virar um if");
      assertTrue(sCodigo.contains("digitalWrite(led_pino, HIGH)"), "a acao deve virar digitalWrite");
   }

   @Test
   void driverPodeEmitirCodigoDeEscopoGlobal() {
      RegistroDrivers oRegistro = new RegistroDrivers();
      oRegistro.registrar(new Driver() {
         @Override
         public String chave() {
            return "dht22-teste";
         }

         @Override
         public String codigoGlobal(String sComponente, int iPino) {
            return "DHT dht_" + sComponente + "(" + iPino + ", DHT22);";
         }
      });

      Controlador oEsp32 = new Controlador("esp32");
      Porta oPortaD4 = new Porta("D4", 4);
      oEsp32.setPortas(oPortaD4);

      Sensor oAmbiente = new Sensor("ambiente");
      Medicao oMedicaoTemperatura = new Medicao("temperatura", TipoValor.DECIMAL);
      oAmbiente.setMedicoes(oMedicaoTemperatura).driver("dht22-teste");

      ProjetoIoT oProjeto = new ProjetoIoT("p")
            .controlador(oEsp32)
            .componentes(oAmbiente)
            .conexoes(new Connection(oAmbiente, oEsp32, oPortaD4))
            .build();

      String sCodigo = new GeradorFirmware(oRegistro).gerar(oProjeto).getCodigoFonte();

      assertTrue(sCodigo.contains("DHT dht_ambiente(4, DHT22);"),
            "o codigo global do driver deve aparecer no firmware");
      assertTrue(sCodigo.indexOf("DHT dht_ambiente") < sCodigo.indexOf("void setup()"),
            "o codigo global deve vir antes do setup()");
   }

   @Test
   void medicaoDecimalViraFloatNoCpp() {
      Controlador oEsp32 = new Controlador("esp32")
            .setPortas(new Porta("D22", 22));

      Sensor oTemperatura = new Sensor("temperatura")
            .setMedicoes(new Medicao("celsius", TipoValor.DECIMAL));

      ProjetoIoT oProjeto = new ProjetoIoT("p")
            .controlador(oEsp32)
            .componentes(oTemperatura)
            .build();

      String sCodigo = new GeradorFirmware(new RegistroDrivers()).gerar(oProjeto).getCodigoFonte();

      assertTrue(sCodigo.contains("float temperatura_celsius = 0;"),
            "medicao DECIMAL deve virar float, nao long");
   }

   @Test
   void configuracaoPermiteAjustarBaudDelayEIncludes() {
      ConfiguracaoFirmware oConfiguracao = new ConfiguracaoFirmware()
            .baudRate(9600)
            .delayMs(100)
            .addInclude("DHT.h");

      String sCodigo = new GeradorFirmware(new RegistroDrivers(), oConfiguracao)
            .gerar(projetoMinimo())
            .getCodigoFonte();

      assertTrue(sCodigo.contains("Serial.begin(9600)"), "o baud rate deve ser configuravel");
      assertTrue(sCodigo.contains("delay(100)"), "o delay do loop deve ser configuravel");
      assertTrue(sCodigo.contains("#include <Arduino.h>"), "o Arduino.h sempre entra");
      assertTrue(sCodigo.contains("#include <DHT.h>"), "includes extras devem ser possiveis");
   }

   @Test
   void platformioIniUsaOMesmoBaudDoFirmware() throws Exception {
      ConfiguracaoFirmware oConfiguracao = new ConfiguracaoFirmware().baudRate(9600);

      GeradorFirmware oFirmware = new GeradorFirmware(new RegistroDrivers(), oConfiguracao)
            .gerar(projetoMinimo());

      java.nio.file.Path oDiretorio = java.nio.file.Files.createTempDirectory("iotflow-teste");
      oFirmware.salvarProjetoPlatformIo(oDiretorio);
      String sIni = java.nio.file.Files.readString(oDiretorio.resolve("platformio.ini"));

      assertTrue(sIni.contains("monitor_speed = 9600"),
            "o monitor_speed deve acompanhar o baud rate configurado");
      assertTrue(sIni.contains("board = esp32dev"), "a placa padrao deve ser esp32dev");
      assertTrue(!sIni.contains("lib_deps"),
            "sem dependencias declaradas o lib_deps nao deve aparecer");
   }

   @Test
   void platformioIniDeclaraAsLibDeps() throws Exception {
      ConfiguracaoFirmware oConfiguracao = new ConfiguracaoFirmware()
            .addLibDep("adafruit/DHT sensor library@^1.4.6");

      GeradorFirmware oFirmware = new GeradorFirmware(new RegistroDrivers(), oConfiguracao)
            .gerar(projetoMinimo());

      java.nio.file.Path oDiretorio = java.nio.file.Files.createTempDirectory("iotflow-teste");
      oFirmware.salvarProjetoPlatformIo(oDiretorio);
      String sIni = java.nio.file.Files.readString(oDiretorio.resolve("platformio.ini"));

      assertTrue(sIni.contains("lib_deps ="), "deve declarar o bloco lib_deps");
      assertTrue(sIni.contains("adafruit/DHT sensor library@^1.4.6"),
            "a dependencia declarada deve aparecer no ini");
   }
}
