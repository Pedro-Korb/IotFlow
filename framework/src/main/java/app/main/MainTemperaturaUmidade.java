package app.main;

import java.io.IOException;
import java.nio.file.Path;

import app.driver.DriverDHT22;
import app.driver.DriverSaidaDigital;
import iotflow.comportamento.CondicaoComparacao;
import iotflow.comportamento.OperadorComparacao;
import iotflow.comportamento.Regra;
import iotflow.firmware.GeradorFirmware;
import iotflow.hardware.Atuador;
import iotflow.hardware.Controlador;
import iotflow.nucleo.Principal;
import iotflow.nucleo.ProjetoIoT;
import iotflow.hardware.Porta;
import iotflow.hardware.Sensor;
import iotflow.hardware.TipoValor;
import iotflow.hardware.Medicao;
import iotflow.hardware.Connection;
import iotflow.comportamento.AcaoDefinirEstado;
import iotflow.comportamento.Condicao;

public class MainTemperaturaUmidade {

   public static void main(String[] args)  throws IOException {

      // led branco:  UMIDADE
      // led vermelho: alto
      // led azul:     baixo
      // led verde:    ok

      // led amarelo:  TEMPERATURA
      // led vermelho: alto
      // led azul:     baixo
      // led verde:    ok


      Principal oPrincipal = Principal.getInstance();

      oPrincipal.registrarDriver(
         new DriverSaidaDigital(),
         new DriverDHT22()
      );

      oPrincipal.getConfiguracao()
         .delayMs(1000)
         .baudRate(115200)
         .addInclude("DHT.h")
         .habilitarDebug()
         .addLibDep("adafruit/DHT sensor library@^1.4.6");

      Controlador oEsp32 = new Controlador("esp32");

      Porta oGpio15 = new Porta("GPIO15", 15);

      Porta oGpio18 = new Porta("GPIO18", 18);
      Porta oGpio02 = new Porta("GPIO02",  2);
      Porta oGpio04 = new Porta("GPIO04",  4);
      Porta oGpio05 = new Porta("GPIO05",  5);

      Porta oGpio19 = new Porta("GPIO19", 19);
      Porta oGpio21 = new Porta("GPIO21", 21);
      Porta oGpio22 = new Porta("GPIO22", 22);
      Porta oGpio23 = new Porta("GPIO23", 23);

      oEsp32.setPortas(
         oGpio15,
         oGpio18,
         oGpio02,
         oGpio04,
         oGpio05,
         oGpio19,
         oGpio21,
         oGpio22,
         oGpio23        
      );

      Atuador oLedTemperatura       = new Atuador("LedTemperatura");
      Atuador oLedTemperaturaBaixa  = new Atuador("ledAzulTemperatura");
      Atuador oLedTemperaturaNormal = new Atuador("ledVerdeTemperatura");
      Atuador oLedTemperaturaAlta   = new Atuador("ledVermelhoTemperatura");

      Atuador oLedUmidade        = new Atuador("ledUmidade");
      Atuador oLedUmidadeBaixa   = new Atuador("ledAzulUmidade");
      Atuador oLedUmidadeNormal  = new Atuador("ledVerdeUmidade");
      Atuador oLedUmidadeAlta    = new Atuador("ledVermelhoUmidade");

      Medicao oMedicaoUmidade     = new Medicao("umidade",     TipoValor.DECIMAL);
      Medicao oMedicaoTemperatura = new Medicao("temperatura", TipoValor.DECIMAL);

      Sensor oDht22 = new Sensor("dht22")
         .setMedicoes(
            oMedicaoTemperatura,
            oMedicaoUmidade
         )
         .driver(DriverDHT22.CHAVE);


      Condicao oTemperaturaExiste = new CondicaoComparacao(
         oDht22, 
         oMedicaoTemperatura, 
         OperadorComparacao.MAIOR, 
         0
      );

      Condicao oUmidadeExiste = new CondicaoComparacao(
         oDht22, 
         oMedicaoUmidade, 
         OperadorComparacao.MAIOR,
         0
      );

      Condicao oTemperaturaBaixa = new CondicaoComparacao(
         oDht22, 
         oMedicaoTemperatura, 
         OperadorComparacao.MENOR, 
         20
      );
      
      Condicao oTemperaturaNormal = new CondicaoComparacao(
         oDht22, 
         oMedicaoTemperatura, 
         OperadorComparacao.MAIOR_IGUAL, 
         20
      )
      .e(new CondicaoComparacao(
         oDht22, 
         oMedicaoTemperatura, 
         OperadorComparacao.MENOR_IGUAL, 
         35
      ));
      
      Condicao oTemperaturaAlta = new CondicaoComparacao(
         oDht22, 
         oMedicaoTemperatura, 
         OperadorComparacao.MAIOR, 
         35
      );

      Condicao oUmidadeBaixa = new CondicaoComparacao(
            oDht22, 
            oMedicaoUmidade, 
            OperadorComparacao.MENOR, 
            30
         );
      
      Condicao oUmidadeNormal = new CondicaoComparacao(
         oDht22, 
         oMedicaoUmidade, 
         OperadorComparacao.MAIOR_IGUAL, 
         30
      )
      .e(new CondicaoComparacao(
         oDht22, 
         oMedicaoUmidade, 
         OperadorComparacao.MENOR_IGUAL, 
         60
      ));
      
      Condicao oUmidadeAlta = new CondicaoComparacao(
         oDht22, 
         oMedicaoUmidade, 
         OperadorComparacao.MAIOR, 
         60
      );

      ProjetoIoT oTesteTemperaturaUmidade = new ProjetoIoT("teste-temperatura-umidade")
         .controlador(oEsp32)
         .componentes(
            oDht22,
            oLedTemperatura,
            oLedTemperaturaBaixa,
            oLedTemperaturaNormal,
            oLedTemperaturaAlta,
            oLedUmidade,
            oLedUmidadeBaixa,
            oLedUmidadeNormal,
            oLedUmidadeAlta
         )
         .conexoes(
            new Connection(oDht22,                oEsp32, oGpio15),
            new Connection(oLedTemperatura,       oEsp32, oGpio18),
            new Connection(oLedTemperaturaBaixa,  oEsp32, oGpio02),
            new Connection(oLedTemperaturaNormal, oEsp32, oGpio04),
            new Connection(oLedTemperaturaAlta,   oEsp32, oGpio05),
            new Connection(oLedUmidade,           oEsp32, oGpio19),
            new Connection(oLedUmidadeBaixa,      oEsp32, oGpio21),
            new Connection(oLedUmidadeNormal,     oEsp32, oGpio22),
            new Connection(oLedUmidadeAlta,       oEsp32, oGpio23)
         )
         .regras(
            Regra.quando(oTemperaturaExiste).entao(new AcaoDefinirEstado(oLedTemperatura, true)),
            Regra.quando(oUmidadeExiste).entao(new AcaoDefinirEstado(oLedUmidade, true)),
            Regra.quando(oTemperaturaBaixa).entao(new AcaoDefinirEstado(oLedTemperaturaBaixa, true)).e(new AcaoDefinirEstado(oLedTemperaturaNormal, false)).e(new AcaoDefinirEstado(oLedTemperaturaAlta, false)),
            Regra.quando(oTemperaturaNormal).entao(new AcaoDefinirEstado(oLedTemperaturaBaixa, false)).e(new AcaoDefinirEstado(oLedTemperaturaNormal, true)).e(new AcaoDefinirEstado(oLedTemperaturaAlta, false)),
            Regra.quando(oTemperaturaAlta).entao(new AcaoDefinirEstado(oLedTemperaturaBaixa, false)).e(new AcaoDefinirEstado(oLedTemperaturaNormal, false)).e(new AcaoDefinirEstado(oLedTemperaturaAlta, true)),
            Regra.quando(oUmidadeBaixa).entao(new AcaoDefinirEstado(oLedUmidadeBaixa, true)).e(new AcaoDefinirEstado(oLedUmidadeNormal, false)).e(new AcaoDefinirEstado(oLedUmidadeAlta, false)),
            Regra.quando(oUmidadeNormal).entao(new AcaoDefinirEstado(oLedUmidadeBaixa, false)).e(new AcaoDefinirEstado(oLedUmidadeNormal, true)).e(new AcaoDefinirEstado(oLedUmidadeAlta, false)),
            Regra.quando(oUmidadeAlta).entao(new AcaoDefinirEstado(oLedUmidadeBaixa, false)).e(new AcaoDefinirEstado(oLedUmidadeNormal, false)).e(new AcaoDefinirEstado(oLedUmidadeAlta, true))
         )
         .build();

      GeradorFirmware oFirmware = oPrincipal.gerarFirmware(oTesteTemperaturaUmidade);

      Path oSaida = oPrincipal.getDiretorioSaida();
      String sNomePasta = GeradorFirmware.sanitizar(oTesteTemperaturaUmidade.getNome().toLowerCase());
      oFirmware.salvarProjetoPlatformIo(oSaida.resolve(sNomePasta));
   }  
}
