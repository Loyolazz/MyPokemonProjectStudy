# MyPokémon Project Study

Aplicativo em Jetpack Compose que consome a [PokéAPI](https://pokeapi.co/) para criar uma aventura: escolher equipes, simular batalhas contra treinadores, gerar encontros aleatórios por região, capturar Pokémon e registrar tudo em uma Pokédex viva.

## Principais telas e fluxo
- **Home/Equipe**: escolha uma equipe pré-definida ou monte uma equipe a partir de capturas já realizadas.
- **Treinadores**: selecione um treinador e simule batalhas rápidas. Cada batalha registra o Pokémon enfrentado na Pokédex com nome e número.
- **Encontros Aleatórios**: sorteia um Pokémon usando o endpoint de Pokédex por região; registra como "visto" e permite tentar captura imediata.
- **Captura por Nome**: busca na PokéAPI, calcula chance (ajustada para lendários) e registra a captura com detalhes completos (tipos, habilidades, altura/peso, sprite). Falhas mantêm o registro como visto.
- **Pokédex**: lista viva com status (visto/capturado), indicando lendários e exibindo detalhes completos apenas para capturados.
- **Histórico de Batalhas**: cards com resultado de vitórias/derrotas contra treinadores selecionados.

## Endpoints utilizados
- `GET /pokemon/{name}` — detalhes completos: id (usado como número da Pokédex), tipos, altura, peso, habilidades e sprites.
- `GET /pokemon-species/{name}` — identifica se é lendário/mítico, taxa de captura e habitat para calcular chance e contextualizar encontros.
- `GET /pokedex/{region}` — lista de espécies por região (kanto, johto, etc.) usada para gerar encontros aleatórios autênticos.

## Bibliotecas
- **Retrofit + Gson**: chamadas HTTP e desserialização da PokéAPI.
- **OkHttp Logging Interceptor**: inspeção de requisições/respostas.
- **Coroutines**: execução assíncrona de rede.
- **Lifecycle ViewModel + Compose**: estado reativo da UI sem recriações.
- **Coil Compose**: carregamento de sprites oficiais dos Pokémon.
- **Material3 + Compose**: construção das telas e componentes responsivos.

## Rodando o projeto (Android)
1. Instale o Android Studio Iguana ou superior e configure o Android SDK (API 34+).
2. Clone o repositório e sincronize o Gradle:
   ```bash
   ./gradlew :app:assembleDebug
   ```
3. Execute no emulador ou dispositivo físico via Android Studio ou com:
   ```bash
   ./gradlew :app:installDebug
   ```

## iOS e multiplataforma
Todo o fluxo de UI está em Jetpack Compose, facilitando portar para Compose Multiplatform. Para gerar um app iOS:
1. Crie um módulo `shared` Kotlin Multiplatform e mova as classes de ViewModel/estado para esse módulo comum.
2. Adicione um alvo `iosArm64/iosSimulatorArm64` com o plugin `org.jetbrains.compose` e habilite Compose Multiplatform.
3. No projeto Xcode, use o framework gerado pelo módulo compartilhado e reutilize os composables em uma `ComposeUIViewController`.
4. As mesmas chamadas da PokéAPI e lógica de Pokédex permanecem no código compartilhado.

> Observação: o repositório atual está pronto para ser dividido em módulo compartilhado, mas a configuração específica de Xcode/Compose Multiplatform deve ser feita no ambiente iOS.

## Notas de uso
- Selecionar um treinador + "Simular batalha" marca o Pokémon adversário como **visto** na Pokédex.
- Encontros aleatórios usam regiões reais da API e já registram como visto; capturas acrescentam detalhes completos.
- Lendários/míticos têm chance de captura reduzida automaticamente.

## Testes
- `./gradlew test` (pode exigir Android SDK configurado no ambiente).
