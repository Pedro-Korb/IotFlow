package iotflow;

import iotflow.comportamento.Condicao;
import iotflow.comportamento.CondicaoComparacao;
import iotflow.comportamento.CondicaoComposta;
import iotflow.comportamento.OperadorComparacao;
import iotflow.comportamento.OperadorLogico;
import iotflow.hardware.Atuador;
import iotflow.hardware.Connection;
import iotflow.hardware.Controlador;
import iotflow.hardware.Medicao;
import iotflow.hardware.Porta;
import iotflow.hardware.Sensor;
import iotflow.hardware.TipoValor;
import iotflow.nucleo.Principal;
import iotflow.nucleo.ProjetoIoT;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PadroesProjetoTest {

   @Test
   void fluentInterfaceCriaProjetoValido() {
      Controlador oEsp32 = new Controlador("esp32")
            .setPortas(new Porta("D22", 22));

      ProjetoIoT oProjeto = new ProjetoIoT("Teste")
            .controlador(oEsp32)
            .build();

      assertEquals("Teste", oProjeto.getNome());
      assertEquals("esp32", oProjeto.getControlador().getNome());
   }

   @Test
   void buildExigeControlador() {
      assertThrows(IllegalStateException.class, () -> new ProjetoIoT("Teste").build());
   }

   @Test
   void selfTypePreservaTipoConcretoNoEncadeamento() {
      Sensor oSensor = new Sensor("umidade")
            .driver("dht22")
            .setMedicoes(new Medicao("v", TipoValor.DECIMAL));

      assertEquals("umidade", oSensor.getNome());
      assertEquals("dht22", oSensor.getDriver());
      assertEquals(1, oSensor.getMedicoes().size());
   }

   @Test
   void combinadorEMontaArvoreComposite() {
      Sensor oUmidade = new Sensor("umidade");
      Medicao oMedicaoV1 = new Medicao("v", TipoValor.DECIMAL);
      oUmidade.setMedicoes(oMedicaoV1);

      Sensor oTemperatura = new Sensor("temperatura");
      Medicao oMedicaoV2 = new Medicao("v", TipoValor.DECIMAL);
      oTemperatura.setMedicoes(oMedicaoV2);

      Condicao oSeca = new CondicaoComparacao(oUmidade, oMedicaoV1, OperadorComparacao.MENOR, 30);
      Condicao oQuente = new CondicaoComparacao(oTemperatura, oMedicaoV2, OperadorComparacao.MAIOR, 30);

      Condicao oAmbas = oSeca.e(oQuente);

      assertTrue(oAmbas instanceof CondicaoComposta);
      assertEquals("(umidade_v < 30) && (temperatura_v > 30)", oAmbas.toCpp());
   }

   @Test
   void combinadorOuMontaArvoreComposite() {
      Sensor oA = new Sensor("a");
      Medicao oMedicaoVA = new Medicao("v", TipoValor.DECIMAL);
      oA.setMedicoes(oMedicaoVA);

      Sensor oB = new Sensor("b");
      Medicao oMedicaoVB = new Medicao("v", TipoValor.DECIMAL);
      oB.setMedicoes(oMedicaoVB);

      Condicao oC1 = new CondicaoComparacao(oA, oMedicaoVA, OperadorComparacao.MENOR, 10);
      Condicao oC2 = new CondicaoComparacao(oB, oMedicaoVB, OperadorComparacao.MENOR, 10);

      assertEquals("(a_v < 10) || (b_v < 10)", oC1.ou(oC2).toCpp());
   }

   @Test
   void compositeGeraExpressaoCppComOperadorLogico() {
      Sensor oUmidade = new Sensor("umidade");
      Medicao oMedicaoVU = new Medicao("v", TipoValor.DECIMAL);
      oUmidade.setMedicoes(oMedicaoVU);

      Sensor oTemperatura = new Sensor("temperatura");
      Medicao oMedicaoVT = new Medicao("v", TipoValor.DECIMAL);
      oTemperatura.setMedicoes(oMedicaoVT);

      Condicao oC1 = new CondicaoComparacao(oUmidade, oMedicaoVU, OperadorComparacao.MENOR, 35);
      Condicao oC2 = new CondicaoComparacao(oTemperatura, oMedicaoVT, OperadorComparacao.MAIOR, 30);

      String sCpp = new CondicaoComposta(OperadorLogico.E, oC1, oC2).toCpp();

      assertEquals("(umidade_v < 35) && (temperatura_v > 30)", sCpp);
   }

   @Test
   void setPortasDefineTodasDeUmaVez() {
      Controlador oEsp32 = new Controlador("esp32")
            .setPortas(
                  new Porta("D22", 22),
                  new Porta("D23", 23));

      assertEquals(2, oEsp32.getPortas().size());
   }

   @Test
   void setPortasSubstituiAsAnteriores() {
      Controlador oEsp32 = new Controlador("esp32")
            .setPortas(new Porta("D21", 21))
            .setPortas(new Porta("D22", 22));

      assertEquals(1, oEsp32.getPortas().size());
      assertEquals("D22", oEsp32.getPortas().get(0).sNome());
   }

   @Test
   void setComponentesESetMedicoesDefinemTudoDeUmaVez() {
      Sensor oSensor = new Sensor("umidade")
            .setMedicoes(new Medicao("v", TipoValor.DECIMAL));

      ProjetoIoT oProjeto = new ProjetoIoT("Teste")
            .controlador(new Controlador("esp32"))
            .componentes(oSensor, new Atuador("led"))
            .build();

      assertEquals(2, oProjeto.getComponentes().size());
      assertEquals(1, oSensor.getMedicoes().size());
   }

   @Test
   void setConexoesDefineTodasDeUmaVez() {
      Controlador oEsp32 = new Controlador("esp32");
      Porta oPortaD22 = new Porta("D22", 22);
      Porta oPortaD23 = new Porta("D23", 23);
      oEsp32.setPortas(oPortaD22, oPortaD23);

      Atuador oLed1 = new Atuador("led1");
      Atuador oLed2 = new Atuador("led2");

      ProjetoIoT oProjeto = new ProjetoIoT("Teste")
            .controlador(oEsp32)
            .componentes(oLed1, oLed2)
            .conexoes(
                  new Connection(oLed1, oEsp32, oPortaD22),
                  new Connection(oLed2, oEsp32, oPortaD23)
            )
            .build();

      assertEquals(2, oProjeto.getConnections().size());
      assertEquals(22, oProjeto.pinoDe("led1").orElseThrow());
      assertEquals(23, oProjeto.pinoDe("led2").orElseThrow());
   }

   @Test
   void buildRejeitaConexaoComComponenteInexistente() {
      Controlador oEsp32 = new Controlador("esp32");
      Porta oPortaD22 = new Porta("D22", 22);
      oEsp32.setPortas(oPortaD22);

      Atuador oLedFantasma = new Atuador("ledFantasma");

      assertThrows(IllegalStateException.class, () -> new ProjetoIoT("Teste")
            .controlador(oEsp32)
            .conexoes(new Connection(oLedFantasma, oEsp32, oPortaD22))
            .build()
      );
   }

   @Test
   void buildRejeitaConexaoComPortaInexistente() {
      Controlador oEsp32 = new Controlador("esp32");
      Porta oPortaD22 = new Porta("D22", 22);
      oEsp32.setPortas(oPortaD22);

      Atuador oLed = new Atuador("led");
      Porta oPortaD99 = new Porta("D99", 99);

      assertThrows(IllegalStateException.class, () -> new ProjetoIoT("Teste")
            .controlador(oEsp32)
            .componentes(oLed)
            .conexoes(new Connection(oLed, oEsp32, oPortaD99))
            .build()
      );
   }

   @Test
   void buildRejeitaConexaoComControladorErrado() {
      Controlador oEsp32 = new Controlador("esp32");
      Porta oPortaD22 = new Porta("D22", 22);
      oEsp32.setPortas(oPortaD22);

      Controlador oArduino = new Controlador("arduino");

      Atuador oLed = new Atuador("led");

      assertThrows(IllegalStateException.class, () -> new ProjetoIoT("Teste")
            .controlador(oEsp32)
            .componentes(oLed)
            .conexoes(new Connection(oLed, oArduino, oPortaD22))
            .build()
      );
   }

   @Test
   void principalEhSingleton() {
      assertSame(Principal.getInstance(), Principal.getInstance());
   }
}
