package iotflow.driver;

public interface Driver {

   String chave();

   default String codigoGlobal(String sComponente, int iPino) {
      return "";
   }

   default String codigoSetup(String sComponente, int iPino) {
      return "";
   }

   default String codigoLeituraLoop(String sComponente, int iPino) {
      return "";
   }
}
