package iotflow.firmware;

import java.util.ArrayList;
import java.util.List;

public class ConfiguracaoFirmware {

   private int iBaudRate = 115200;
   private int iDelayMs;
   private String sPlataforma = "espressif32";
   private String sPlaca = "esp32dev";
   private final List<String> aIncludes = new ArrayList<String>(List.of("Arduino.h"));
   private final List<String> aLibDeps = new ArrayList<String>();

   private String sVariavelLeituraDelay;
   private int iMaxDelayVariavel;
   private int iMinDelayVariavel;
   private boolean bDebug;

   public int getIBaudRate() {
      return this.iBaudRate;
   }

   public void setIBaudRate(int iBaudRate) {
      this.iBaudRate = iBaudRate;
   }

   public int getIDelayMs() {
      return this.iDelayMs;
   }

   public void setIDelayMs(int iDelayMs) {
      this.iDelayMs = iDelayMs;
   }

   public String getSPlataforma() {
      return this.sPlataforma;
   }

   public void setSPlataforma(String sPlataforma) {
      this.sPlataforma = sPlataforma;
   }

   public String getSPlaca() {
      return this.sPlaca;
   }

   public void setSPlaca(String sPlaca) {
      this.sPlaca = sPlaca;
   }

   public List<String> getAIncludes() {
      return this.aIncludes;
   }

   public List<String> getALibDeps() {
      return this.aLibDeps;
   }

   public String getVariavelLeituraDelay() {
      return this.sVariavelLeituraDelay;
   }

   public ConfiguracaoFirmware setVariavelLeituraDelay(String sVariavelLeituraDelay) {
      this.sVariavelLeituraDelay = sVariavelLeituraDelay;
      return this;
   }

   public int getMinDelayVariavel() {
      return this.iMinDelayVariavel;
   }

   public ConfiguracaoFirmware setMinDelayVariavel(int iMinDelayVariavel) {
      this.iMinDelayVariavel = iMinDelayVariavel;
      return this;
   }

   public int getMaxDelayVariavel() {
      return this.iMaxDelayVariavel;
   }

   public ConfiguracaoFirmware setMaxDelayVariavel(int iMaxDelayVariavel) {
      this.iMaxDelayVariavel = iMaxDelayVariavel;
      return this;
   }

   public ConfiguracaoFirmware baudRate(int iBaudRate) {
      if (iBaudRate <= 0) {
         throw new IllegalArgumentException("Baud rate deve ser positivo");
      }
      this.iBaudRate = iBaudRate;
      return this;
   }

   public ConfiguracaoFirmware delayMs(int iDelayMs) {
      if (iDelayMs < 0) {
         throw new IllegalArgumentException("Delay não pode ser negativo");
      }
      this.iDelayMs = iDelayMs;
      return this;
   }

   public ConfiguracaoFirmware plataforma(String sPlataforma) {
      if (sPlataforma == null || sPlataforma.isBlank()) {
         throw new IllegalArgumentException("Plataforma não pode ser nula");
      }
      this.sPlataforma = sPlataforma;
      return this;
   }

   public ConfiguracaoFirmware placa(String sPlaca) {
      if (sPlaca == null || sPlaca.isBlank()) {
         throw new IllegalArgumentException("Placa não pode ser nula");
      }
      this.sPlaca = sPlaca;
      return this;
   }

   public ConfiguracaoFirmware addInclude(String sInclude) {
      if (sInclude == null || sInclude.isBlank()) {
         throw new IllegalArgumentException("Include não pode ser nulo");
      }
      if (!this.aIncludes.contains(sInclude)) {
         this.aIncludes.add(sInclude);
      }
      return this;
   }

   public ConfiguracaoFirmware addLibDep(String sLibDep) {
      if (sLibDep == null || sLibDep.isBlank()) {
         throw new IllegalArgumentException("Dependência não pode ser nula");
      }
      if (!this.aLibDeps.contains(sLibDep)) {
         this.aLibDeps.add(sLibDep);
      }
      return this;
   }

   public int getBaudRate() {
      return this.iBaudRate;
   }

   public int getDelayMs() {
      return this.iDelayMs;
   }

   public String getPlataforma() {
      return this.sPlataforma;
   }

   public String getPlaca() {
      return this.sPlaca;
   }

   public List<String> getIncludes() {
      return List.copyOf(this.aIncludes);
   }

   public List<String> getLibDeps() {
      return List.copyOf(this.aLibDeps);
   }

   public ConfiguracaoFirmware delayPorLeitura(String sVariavelLeituraDelay, int iMinDelayVariavel, int iMaxDelayVariavel) {
      this.setVariavelLeituraDelay(sVariavelLeituraDelay);
      this.setMinDelayVariavel(iMinDelayVariavel);
      this.setMaxDelayVariavel(iMaxDelayVariavel);
      return this;
   }

   public ConfiguracaoFirmware habilitarDebug() {
      this.bDebug = true;
      return this;
   }

   public boolean isDebug() {
      return this.bDebug;
   }
}
