package app.main;

import java.io.IOException;
import java.nio.file.Path;

import app.driver.DriverPotenciometro;
import app.driver.DriverSeletor;
import iotflow.hardware.Controlador;
import iotflow.hardware.Medicao;
import iotflow.hardware.Porta;
import iotflow.nucleo.Principal;
import iotflow.nucleo.ProjetoIoT;
import iotflow.firmware.GeradorFirmware;
import iotflow.hardware.Atuador;
import iotflow.hardware.Connection;
import iotflow.hardware.Sensor;
import iotflow.hardware.TipoValor;

public class MainPotenciometroSeletor {
   public static void main(String[] args) throws IOException {

      Principal oInstancia = Principal.getInstance();

      oInstancia.registrarDriver(
         new DriverPotenciometro(),
         new DriverSeletor()
      );

      oInstancia.getConfiguracao()
         .baudRate(115200)
         .delayMs(50);

      Controlador oEsp32 = new Controlador("esp32");

      Porta oGpio34 = new Porta("GPIO34", 34);
      Porta oGpio02 = new Porta("GPIO02", 2);
      Porta oGpio04 = new Porta("GPIO04", 4);
      Porta oGpio27 = new Porta("GPIO27", 27);
      Porta oGpio18 = new Porta("GPIO18", 18);
      Porta oGpio19 = new Porta("GPIO19", 19);
      Porta oGpio21 = new Porta("GPIO21", 21);
      Porta oGpio22 = new Porta("GPIO22", 22);
      Porta oGpio23 = new Porta("GPIO23", 23);

      oEsp32.setPortas(
         oGpio34, oGpio02, oGpio04, oGpio27,
         oGpio18, oGpio19, oGpio21, oGpio22, oGpio23
      );

      Medicao oMedicaoPotenciometro = new Medicao("nivel", TipoValor.INTEIRO);

      Sensor oPotenciometro = new Sensor("potenciometro")
         .setMedicoes(oMedicaoPotenciometro)
         .driver(DriverPotenciometro.CHAVE);

      Atuador oLed1 = new Atuador("led1").driver(DriverSeletor.CHAVE);
      Atuador oLed2 = new Atuador("led2").driver(DriverSeletor.CHAVE);
      Atuador oLed3 = new Atuador("led3").driver(DriverSeletor.CHAVE);
      Atuador oLed4 = new Atuador("led4").driver(DriverSeletor.CHAVE);
      Atuador oLed5 = new Atuador("led5").driver(DriverSeletor.CHAVE);
      Atuador oLed6 = new Atuador("led6").driver(DriverSeletor.CHAVE);
      Atuador oLed7 = new Atuador("led7").driver(DriverSeletor.CHAVE);
      Atuador oLed8 = new Atuador("led8").driver(DriverSeletor.CHAVE);

      ProjetoIoT oTesteSeletor = new ProjetoIoT("potenciometro-seletor-leds")
            .controlador(oEsp32)
            .componentes(oPotenciometro, oLed1, oLed2, oLed3, oLed4, oLed5, oLed6, oLed7, oLed8)
            .conexoes(
                  new Connection(oPotenciometro, oEsp32, oGpio34),
                  new Connection(oLed1, oEsp32, oGpio02),
                  new Connection(oLed2, oEsp32, oGpio04),
                  new Connection(oLed3, oEsp32, oGpio27),
                  new Connection(oLed4, oEsp32, oGpio18),
                  new Connection(oLed5, oEsp32, oGpio19),
                  new Connection(oLed6, oEsp32, oGpio21),
                  new Connection(oLed7, oEsp32, oGpio22),
                  new Connection(oLed8, oEsp32, oGpio23)
            )
            .regras()
            .build();

      GeradorFirmware oFirmware = oInstancia.gerarFirmware(oTesteSeletor);
      Path oSaida = oInstancia.getDiretorioSaida();
      String sNomePasta = GeradorFirmware.sanitizar(oTesteSeletor.getNome().toLowerCase());
      oFirmware.salvarProjetoPlatformIo(oSaida.resolve(sNomePasta));
   }
}
