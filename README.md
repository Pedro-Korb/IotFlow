# IoTFlow

Framework para modelagem de projetos IoT em Java, com geração automática do firmware C++
para ESP32 a partir do modelo.

Trabalho da disciplina de POO2.

## Como funciona

A ideia é declarar o projeto IoT inteiro em Java (controlador, sensores, atuadores, conexões
e regras do tipo "quando X então Y") e deixar o `GeradorFirmware` traduzir esse modelo para
o firmware C++ pronto para compilar e subir na placa. Cada condição e ação sabe se traduzir
para C++ pelo método `toCpp()` — o gerador só percorre o modelo e monta o arquivo.

## Rodando

Precisa de Java 17 e Maven.

```
cd framework
mvn test                -> roda os testes
mvn compile exec:java   -> roda a demo do semáforo e gera o firmware em ../firmware-output
mvn compile exec:java -Dexec.mainClass=app.MainSensores   -> roda o exemplo com sensores reais
```

A demo (`app/Main.java`) modela um semáforo com ESP32 e dois LEDs (D22 e D23) que piscam
alternados a cada segundo. A saída vai para `firmware-output/semaforo_alternado/` — a pasta
é derivada do nome do projeto e os arquivos são sobrescritos a cada execução.

Há também um exemplo com sensores reais (`app/MainSensores.java`): DHT22 (temperatura e
umidade) e um painel de LEDs de status.

Para subir na placa: `pio run --target upload` dentro da pasta gerada (ex.:
`firmware-output/monitoramento_de_sensores`) — o PlatformIO baixa as bibliotecas
declaradas no `lib_deps` sozinho.

## Pastas

- `framework/` - código do framework e testes JUnit
- `firmware-output/` - firmware gerado pelos exemplos
- `diagrama_classes/` - diagrama de classes (PlantUML + SVG)
- `documentacao/` - javadoc (abrir o index.html no navegador)

## Design patterns

- Builder + interface fluente: `ProjetoIoT.builder(...)` e `Regra.quando(...).entao(...).e(...)`
- Composite: condições simples e compostas (E/OU) implementam a mesma interface `Condicao`
- Singleton: `Principal.getInstance()` — o único ponto de estado global do framework
- Facade: a classe `Principal` concentra os comportamentos globais do framework
  (registro de drivers, configuração e geração) num único ponto de entrada
- Strategy: a interface `Driver` isola o código específico de cada hardware, o núcleo não
  conhece marca/modelo de nada

## Convenções

Notação húngara nas variáveis: o (objeto), a (array/coleção), s (String), i (int),
f (float), b (boolean), d (double), x (Object). Nomes em português, mantendo em inglês
só o que é termo consagrado (Connection, Driver, firmware...).
