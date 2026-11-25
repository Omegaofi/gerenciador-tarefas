# NexProject para Android

Versão Android do `gerenciador-tarefas-cliente`. Ela conversa com o mesmo
`gerenciador-tarefas-servidor` (socket na porta 4321), usando os mesmos objetos do
pacote `modelDominio` e o mesmo chat cifrado de ponta a ponta. Por isso, mensagens
enviadas pelo desktop abrem no celular e vice-versa.

## Como abrir

1. Android Studio → **Open** → escolha a pasta `gerenciador-tarefas-android`.
2. Na primeira vez, aceite instalar o Android SDK 36 quando o Studio pedir.
3. Crie um emulador em **Device Manager** (qualquer aparelho com Android 8.0 ou mais novo).
4. Inicie o `gerenciador-tarefas-servidor` no computador (com o MySQL ligado).
5. Clique em **Run ▶**.

## Endereço do servidor

* **Emulador:** o padrão `10.0.2.2:4321` já aponta para o `localhost` do computador.
* **Celular de verdade:** na tela de login, toque em *Servidor · alterar* e informe o IP
  do computador na mesma rede Wi-Fi (por exemplo `192.168.0.10`). Libere a porta 4321
  no firewall do computador.

## Correspondência com o desktop

| Desktop (Swing)                       | Android                                   |
|---------------------------------------|-------------------------------------------|
| `TelaLogin1`, `TelaCadastro1`         | `LoginActivity`, `CadastroActivity`       |
| `MenuLateral`                         | Barra inferior da `MainActivity`          |
| `TelaPrincipal1` (dashboard)          | `InicioFragment`                          |
| `TabelaProjeto1`, `FormProjeto1`      | `ProjetosFragment`, `ProjetoFormActivity` |
| `TabelaTarefa1`, `FormTarefa1`        | `TarefasFragment`, `TarefaFormActivity`   |
| `TabelaEntrega1`, `FormEntrega1`      | `EntregasFragment`, `EntregaFormActivity` |
| `FormProcura` (explorar)              | `ExplorarFragment`                        |
| `TelaInscricoes`                      | `InscricoesActivity`                      |
| `TelaChat1`                           | `ConversasFragment`, `ChatActivity`       |
| `TelaTrocarSenha`                     | `TrocarSenhaActivity`                     |
| `ConexaoController`                   | `rede/ConexaoController` (mesmo protocolo)|
| `cripto/*`                            | `cripto/*` (mesmas constantes)            |

O Android não permite usar a rede na thread da tela. Por isso, toda chamada ao servidor
passa por `rede/Rede.java`, que roda os comandos em uma única thread em segundo plano.
Assim os comandos nunca se misturam no socket.

**Importante:** `modelDominio` precisa continuar idêntico nos três projetos (mesmo pacote,
mesmos campos, mesmos `serialVersionUID`). Se mudar uma classe no servidor, copie para o
desktop e para o Android.
