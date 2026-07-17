package app.main;

import java.io.IOException;
import java.nio.file.Path;

import app.driver.DriverPotenciometro;
import app.driver.DriverSequenciador;
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

public class MainPotenciometroSequencial {
   public static void main(String[] args) throws IOException {

      Principal oInstancia = Principal.getInstance();

      oInstancia.registrarDriver(
         new DriverPotenciometro(),
         new DriverSequenciador(1, 3000)
      );

      Medicao oMedicaoPotenciometro = new Medicao("nivel", TipoValor.INTEIRO);

      oInstancia.getConfiguracao()
         .baudRate(115200);

      Sensor oPotenciometro = new Sensor("potenciometro")
         .setMedicoes(oMedicaoPotenciometro)
         .driver(DriverPotenciometro.CHAVE);

      oInstancia.getConfiguracao()
         .delayPorLeitura(
            GeradorFirmware.variavelLeitura(oPotenciometro.getNome(), oMedicaoPotenciometro.sNome()),
            1,
            3000
         );

      Controlador oEsp32 = new Controlador("esp32");

      Porta oGpio21 = new Porta("GPIO21", 21);
      Porta oGpio22 = new Porta("GPIO22", 22);
      Porta oGpio23 = new Porta("GPIO23", 23);
      Porta oGpio19 = new Porta("GPIO19", 19);

      Porta oGpio02 = new Porta("GPIO02", 2);
      Porta oGpio04 = new Porta("GPIO04", 4);
      Porta oGpio27 = new Porta("GPIO27", 27);
      Porta oGpio18 = new Porta("GPIO18", 18);
      Porta oGpio34 = new Porta("GPIO34", 34);

      oEsp32.setPortas(
         oGpio21, 
         oGpio22, 
         oGpio23,
         oGpio19, 
         oGpio02, 
         oGpio04, 
         oGpio27,
         oGpio18, 
         oGpio34 
      );

      Atuador oLed1 = new Atuador("led1").driver(DriverSequenciador.CHAVE);
      Atuador oLed2 = new Atuador("led2").driver(DriverSequenciador.CHAVE);
      Atuador oLed3 = new Atuador("led3").driver(DriverSequenciador.CHAVE);
      Atuador oLed4 = new Atuador("led4").driver(DriverSequenciador.CHAVE);
      Atuador oLed5 = new Atuador("led5").driver(DriverSequenciador.CHAVE);
      Atuador oLed6 = new Atuador("led6").driver(DriverSequenciador.CHAVE);
      Atuador oLed7 = new Atuador("led7").driver(DriverSequenciador.CHAVE);
      Atuador oLed8 = new Atuador("led8").driver(DriverSequenciador.CHAVE);

      ProjetoIoT oTesteSequencial = new ProjetoIoT("potenciometro-sequencial-leds")
            .controlador(oEsp32)
            .componentes(oPotenciometro, oLed1, oLed2, oLed3, oLed4, oLed5, oLed6, oLed7, oLed8)
            .conexoes(
                  new Connection(oPotenciometro, oEsp32, oGpio34),
                  new Connection(oLed1, oEsp32, oGpio21),
                  new Connection(oLed2, oEsp32, oGpio22),
                  new Connection(oLed3, oEsp32, oGpio23),
                  new Connection(oLed4, oEsp32, oGpio19),
                  new Connection(oLed5, oEsp32, oGpio02),
                  new Connection(oLed6, oEsp32, oGpio04),
                  new Connection(oLed7, oEsp32, oGpio27),
                  new Connection(oLed8, oEsp32, oGpio18)
            )
            .regras()
            .build();

      GeradorFirmware oFirmware = oInstancia.gerarFirmware(oTesteSequencial);
      Path oSaida = oInstancia.getDiretorioSaida();
      String sNomePasta = GeradorFirmware.sanitizar(oTesteSequencial.getNome().toLowerCase());
      oFirmware.salvarProjetoPlatformIo(oSaida.resolve(sNomePasta));
   }
}
