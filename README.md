# IoTFlow

Framework para modelagem de projetos IoT em Java, com geração automática do firmware C++
para ESP32 a partir do modelo.

Trabalho da disciplina de POO2.

## Como funciona

A ideia é declarar o projeto IoT inteiro em Java (controlador, sensores, atuadores, conexões
e regras do tipo "quando X então Y") e deixar o `GeradorFirmware` traduzir esse modelo para
o firmware C++ pronto para compilar e subir na placa. Cada condição e ação sabe se traduzir
para C++ pelo método `toCpp()` — o gerador só percorre o modelo e monta o arquivo.

## Requisitos

- Java 17 ou superior
- Maven 3.6+
- PlatformIO (para fazer upload na placa)

## Build

Gerar o JAR do framework:

```bash
cd framework
mvn clean package
```

O JAR será criado em `framework/target/iotflow-1.0.0.jar`.

## Rodando os exemplos

Executar os testes:
```bash
cd framework
mvn test
```

Gerar firmware a partir dos exemplos disponíveis:
```bash
cd framework

# Semáforo alternado (exemplo simples)
mvn compile exec:java -Dexec.mainClass=app.main.Main

# Monitoramento com DHT22 (temperatura + umidade)
mvn compile exec:java -Dexec.mainClass=app.main.MainTemperaturaUmidade

# Potenciômetro (controle contínuo)
mvn compile exec:java -Dexec.mainClass=app.main.MainPotenciometro

# Potenciômetro com sequenciamento de LEDs
mvn compile exec:java -Dexec.mainClass=app.main.MainPotenciometroSequencial

# Potenciômetro com seletor de LED
mvn compile exec:java -Dexec.mainClass=app.main.MainPotenciometroSeletor
```

Os arquivos C++ gerados ficam em `firmware-output/`. Para subir na placa:

```bash
cd firmware-output/seu-projeto
pio run --target upload
```

O PlatformIO baixa as dependências automaticamente conforme declarado no `platformio.ini`.

## Estrutura do projeto

- `framework/` - código do framework, testes JUnit e exemplos
- `firmware-output/` - firmware C++ gerado pelos exemplos
- `diagrama_classes/` - diagrama de classes (PlantUML + SVG)

## Design patterns

- Fluent Interface: `ProjetoIoT`, `Regra` e `ConfiguracaoFirmware` usam method chaining para configuração declarativa
- Composite: condições simples (`CondicaoComparacao`) e compostas (`CondicaoComposta`) implementam `Condicao`
- Singleton: `Principal.getInstance()` fornece acesso único ao ponto de entrada do framework
- Facade: `Principal` concentra registro de drivers, configuração e geração de firmware
- Strategy: interface `Driver` permite implementações específicas de sensores e atuadores sem afetar o núcleo
- Template Method: `GeradorFirmware` define a estrutura do firmware e delega seções ao driver

## Extensibilidade

Para adicionar suporte a novos sensores ou atuadores, implemente a interface `Driver`:

```java
public class MeuDriver implements Driver {
    public static final String CHAVE = "meu-driver";
    
    @Override
    public String chave() {
        return CHAVE;
    }
    
    @Override
    public String codigoGlobal(String sComponente, int iPino) {
        // Código global do firmware (variáveis, structs)
        return "";
    }
    
    @Override
    public String codigoSetup(String sComponente, int iPino) {
        // Código da função setup()
        return String.format("pinMode(%d, OUTPUT);", iPino);
    }
    
    @Override
    public String codigoLeituraLoop(String sComponente, int iPino) {
        // Código da função loop()
        return String.format("digitalWrite(%d, HIGH);", iPino);
    }
}
```

Depois registre no seu projeto:

```java
Principal.getInstance().registrarDriver(new MeuDriver());
```

## Convenções

Notação húngara nas variáveis: o (objeto), a (array/coleção), s (String), i (int),
f (float), b (boolean), d (double), x (Object). Nomes em português, mantendo em inglês
só o que é termo consagrado (Connection, Driver, firmware...).
