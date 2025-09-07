# 🔍 SEÇÃO I: REVISÃO DE PRÉ-REQUISITOS
## Questão 1 - Sistemas operacionais
### a) Explique a diferença entre processo e thread, destacando as implicações para sistemas distribuídos.

Um **processo** é uma instância de um programa em execução. Ele é uma entidade autônoma e independente que possui seu próprio espaço de endereçamento de memória, código, dados e recursos do sistema operacional. Em um sistema distribuído, os processos são a base para a distribuição de tarefas entre diferentes nós (máquinas). Cada nó da rede pode executar um ou mais processos independentes. A comunicação entre processos em máquinas distintas é tipicamente mais complexa e lenta, usando mecanismos como RPC (Remote Procedure Call) ou passagem de mensagens.

Uma **thread**, ou linha de execução, é uma unidade de execução que reside dentro de um processo. Múltiplas threads de um mesmo processo compartilham o mesmo espaço de memória, além de outros recursos. Por serem mais leves, a criação e a troca de contexto entre threads são mais rápidas do que entre processos. A principal implicação de threads para sistemas distribuídos é a capacidade de paralelizar tarefas dentro de um único nó. Por exemplo, um servidor em uma arquitetura cliente-servidor pode usar threads para atender a múltiplas requisições de clientes ao mesmo tempo. Isso melhora o desempenho e a responsividade sem a necessidade de criar um novo processo para cada requisição.

| Característica       | Processo                          | Thread                                    |
|-----------------------|-----------------------------------|-------------------------------------------|
| Independência         | Independente e isolado            | Dependente do processo principal          |
| Memória               | Espaço de memória próprio         | Compartilha memória do processo           |
| Custo                 | Criação e troca de contexto lentas| Criação e troca de contexto rápidas       |
| Comunicação           | Via IPC (lenta)                   | Via memória compartilhada (rápida)        |
| Tolerância a Falhas   | Uma falha não afeta outros processos | Uma falha pode derrubar o processo e suas outras threads |

### b) Descreva os cinco estados de um processo (novo, pronto, executando, esperando, terminado) e explique como esses estados se relacionam com processos distribuídos executando em diferentes máquinas.

Esses cinco estados são o ciclo de vida de um processo em execução. Abaixo apresento os cinco estados e suas descrições.
1. `Novo`: o processo está sendo criado. Ele ainda não está carregado na memória principal, mas já tem um Bloco de Controle de Processo (BCP) associado a ele.
2. `Pronto`: o processo está pronto para ser executado e está aguardando na fila de processos do escalonador do sistema operacional. Ele está na memória principal e espera apenas o CPU ficar disponível.
3. `Executando`: o processo está atualmente utilizando o CPU e suas instruções estão sendo executadas. Em um sistema com um único CPU, apenas um processo pode estar neste estado por vez.
4. `Esperando`: o processo foi temporariamente suspenso. Isso acontece quando ele precisa aguardar a conclusão de algum evento, como uma operação de entrada/saída (I/O) ou a liberação de um recurso.
5. `Terminado`: o processo concluiu sua execução. O sistema operacional está liberando todos os seus recursos e o processo é removido da memória.

Em um sistema distribuído, o gerenciamento desses estados se torna mais complexo, pois eles ocorrem em múltiplas máquinas que se comunicam através de uma rede.

- Estados locais: os estados `Pronto` e `Executando` são sempre locais a um nó (máquina) específico. Um processo só pode estar "executando" no CPU de um computador por vez. A gestão da fila de processos "prontos" também é feita localmente por cada sistema operacional.
- Comunicação entre máquinas: o estado de `Esperando` é afetado. Em um sistema distribuído, um processo pode entrar no estado de espera não apenas por uma operação de I/O local, mas também por eventos remotos. Por exemplo, um processo em um servidor pode estar esperando uma resposta de um cliente em outra máquina ou a conclusão de uma tarefa em um banco de dados remoto. A latência da rede pode prolongar significativamente o tempo de espera.
- Gerenciamento distribuído: os estados `Novo` e `Terminado` podem ser iniciados remotamente. Um processo em uma máquina pode solicitar a criação de um novo processo em outra máquina para realizar uma tarefa. Da mesma forma, a terminação de um processo pode ser coordenada remotamente. Algoritmos de balanceamento de carga em sistemas distribuídos buscam otimizar os estados `Pronto` e `Executando`, movendo processos entre nós para evitar que um único computador fique sobrecarregado.

A principal implicação é que, embora os estados de um processo sejam os mesmos, as transições entre eles em um ambiente distribuído são influenciadas pela rede, pela distância e pela comunicação entre os nós, o que introduz desafios como falhas de conexão e sincronização.

### c) Compare três mecanismos de IPC (Inter-Process Communication): pipes, sockets e memória compartilhada. Como estes conceitos se estendem para comunicação em sistemas distribuídos?

| Característica            | Pipes                                 | Memória Compartilhada                        | Sockets                                |
|----------------------------|---------------------------------------|----------------------------------------------|----------------------------------------|
| **Escopo**                 | Local (mesma máquina)                | Local (mesma máquina)                        | Local ou Remoto (via rede)             |
| **Velocidade**             | Rápida (para comunicação sequencial) | Muito Rápida (mais rápida de todas)          | Lenta (depende da latência da rede)    |
| **Complexidade**           | Simples                              | Alta (exige gerenciamento de sincronização)  | Média (programação de rede)            |
| **Aplicações Distribuídas**| Não aplicável                        | Conceito estendido via DSM                   | Fundamental e principal mecanismo       |

**Pipes**
- Como funcionam: são canais de comunicação unidirecionais (de mão única) que permitem a transferência de dados entre dois processos. Existem pipes anônimos (geralmente entre processos pai e filho) e pipes nomeados, que podem ser usados por processos não relacionados na mesma máquina.
- Implicações em sistemas distribuídos: o mecanismo totalmente local! Não podem ser usados para comunicação entre máquinas diferentes em uma rede! São úteis apenas para orquestrar processos dentro de um único computador.

**Memória compartilhada**
- Como funciona: vários processos acessam a mesma região da memória principal. É o método mais rápido de IPC porque evita a cópia de dados entre o espaço de memória do kernel e o espaço de memória dos processos. No entanto, exige um alto nível de sincronização para evitar condições de corrida, onde múltiplos processos tentam escrever na mesma área ao mesmo tempo.
- Implicações em sistemas distribuídos: o conceito, por si só, não se aplica a um ambiente distribuído, já que os processos estão em máquinas fisicamente separadas com memórias distintas! A ideia é estendida por meio da Memória Compartilhada Distribuída (DSM), um sistema de software complexo que cria a ilusão de um espaço de memória compartilhado entre os nós de uma rede. No entanto, a implementação é desafiadora, e a latência da rede e a consistência dos dados se tornam problemas críticos.

**Sockets**
- Como funcionam: um socket é um ponto final de comunicação que pode ser usado para trocar dados com outro socket. Ao contrário dos pipes e da memória compartilhada, os sockets foram projetados desde o início para a comunicação em rede! Eles podem ser configurados para se comunicar com outro socket na mesma máquina ou em qualquer máquina acessível pela rede.
- Implicações em sistemas distribuídos: os sockets são a base de comunicação dos sistemas distribuídos! Eles formam a camada fundamental sobre a qual protocolos de rede mais avançados, como TCP e UDP, são construídos. Mecanismos de comunicação de alto nível, como as Chamadas de Procedimento Remoto (RPC), usam sockets para permitir que um processo em uma máquina invoque uma função em um processo em outra máquina, fazendo com que a comunicação remota se pareça com uma chamada de função local.


### d) Explique os algoritmos de substituição de página FIFO, LRU e Optimal. Como o gerenciamento de memória virtual se relaciona com sistemas de memória distribuída?

Os algoritmos de substituição de página são estratégias usadas por sistemas de memória virtual para decidir qual página remover da memória principal quando uma nova página precisa ser carregada.
- FIFO (First-In, First-Out): este é o algoritmo mais simples. Ele remove a página que está na memória há mais tempo, ou seja, a "primeira a entrar" é a "primeira a sair". A principal desvantagem é que ele não leva em conta a frequência de uso da página. Uma página muito utilizada pode ser removida apenas por ser a mais antiga, levando a um aumento desnecessário de falhas de página.
- LRU (Least Recently Used): o LRU remove a página que não foi usada pelo maior tempo. A ideia é que a página que não foi acessada por mais tempo é a que tem menor probabilidade de ser necessária no futuro próximo. Embora seja mais eficiente que o FIFO, o LRU exige um controle extra para monitorar o tempo de uso de cada página, o que pode aumentar a sobrecarga do sistema.
- Optimal: este algoritmo é considerado o ideal, mas é impraticável de ser implementado. Ele remove a página que não será usada pelo maior período de tempo no futuro. Para isso, o algoritmo precisaria ter conhecimento prévio de toda a sequência de acessos futuros, o que é impossível em um sistema real. O Optimal serve como uma referência teórica para avaliar o desempenho de outros algoritmos.

A memória virtual cria a ilusão de um espaço de memória grande e contíguo em um único computador, usando a hierarquia de memória (RAM e disco).

O conceito de Memória Compartilhada Distribuída (DSM) é a extensão desse princípio para um ambiente distribuído. Ele cria a ilusão de um espaço de memória único e compartilhado em um sistema composto por várias máquinas, cada uma com sua própria memória física. Os algoritmos de substituição de página são cruciais para a DSM, mas enfrentam novos desafios:
- Consistência dos dados: quando uma página é replicada em várias máquinas e uma delas a modifica, a DSM precisa garantir que as outras cópias sejam atualizadas. A escolha do algoritmo de substituição afeta a frequência de falhas e, consequentemente, a latência para manter a consistência.
- Latência da rede: em um sistema de memória virtual tradicional, a falha de página envolve acesso ao disco rígido. Em um sistema distribuído, essa falha requer uma comunicação pela rede para buscar a página em outro nó, o que é muito mais lento. Um algoritmo ineficiente como o FIFO pode aumentar drasticamente as falhas de página distribuídas, degradando o desempenho do sistema.
- Decisão global: a decisão de qual página remover pode não ser mais local. Pode ser necessário um algoritmo de substituição que leve em conta a utilização da página em todos os nós, e não apenas no nó local, para otimizar o uso da memória em todo o sistema.

Em resumo, o gerenciamento de memória virtual em sistemas distribuídos se torna muito mais complexo e a escolha do algoritmo de substituição de página é ainda mais crítica, pois o custo de uma falha de página é significativamente maior devido à latência da rede e à necessidade de manter a consistência dos dados.

## Questão 2 - Redes de Computadores
### a) Compare o modelo OSI (7 camadas) com o modelo TCP/IP (4 camadas). Quais camadas são mais relevantes para desenvolvimento de sistemas distribuídos e por quê?

O modelo OSI é um padrão conceitual, mais completo e detalhado. Ele divide a comunicação em 7 camadas distintas (Física, Enlace, Rede, Transporte, Sessão, Apresentação e Aplicação). Ele é usado principalmente para fins didáticos e para entender a estrutura geral das redes.  Já o modelo TCP/IP é um padrão prático, baseado nos protocolos que realmente impulsionam a internet. Ele simplifica a arquitetura para 4 camadas (Acesso à Rede, Internet, Transporte e Aplicação).

Para o desenvolvimento de sistemas distribuídos, as camadas mais relevantes são a de Transporte e a de Aplicação.

- Camada de Transporte: esta camada é crucial porque é nela que você decide como os processos em diferentes máquinas irão se comunicar. A escolha entre os protocolos TCP e UDP é uma das decisões mais importantes.
- Camada de Aplicação: esta é a camada mais próxima do desenvolvedor. Ela define os protocolos e a lógica de comunicação de alto nível para sua aplicação. É aqui que você trabalha com APIs, como APIs REST (que usam HTTP, um protocolo da camada de aplicação), gRPC ou web sockets. A forma como os processos da aplicação interagem uns com os outros é completamente definida nesta camada.

A camada de Transporte lida com a conexão entre os processos, enquanto a camada de Aplicação lida com a linguagem que eles usam para se comunicar. Juntas, elas formam a base para construir sistemas que se estendem por várias máquinas.

### b) Analise as diferenças entre TCP e UDP em termos de confiabilidade, performance e adequação para diferentes tipos de aplicações distribuídas. Forneça exemplos práticos.

| Característica      | TCP                                           | UDP                                         |
|---------------------|-----------------------------------------------|---------------------------------------------|
| Confiabilidade      | Confiável (com confirmação e retransmissão)   | Não confiável (sem garantias)               |
| Overhead            | Alto (devido a mecanismos de controle)        | Baixo (mínimo, apenas cabeçalho)            |
| Velocidade          | Mais lento (maior latência)                   | Mais rápido (baixa latência)                |
| Aplicações Típicas  | Transferência de arquivos, web, e-mail        | Streaming de vídeo/áudio, jogos online, VoIP|

**TCP (Transmission Control Protocol)**
É um protocolo orientado à conexão, o que significa que ele estabelece uma conexão confiável entre o remetente e o destinatário antes de iniciar a transferência de dados.
- `Confiabilidade`: é a principal característica do TCP. Ele garante que os dados cheguem na ordem correta, sem perdas ou duplicações. Isso é feito por do three-way handshake, numeração de pacotes e retransmissão automática de pacotes perdidos ou danificados.
- `Performance`: devido a todos os mecanismos de garantia de entrega, o TCP tem um overhead maior. A necessidade de confirmações e retransmissões adiciona latência, tornando-o mais lento que o UDP.
- `Adequação`: é ideal para aplicações onde a integridade dos dados é crucial e a latência secundária.

Exemplos práticos:
- `Navegação na Web` (HTTP/S): é inaceitável que partes de uma página web fiquem faltando ou cheguem fora de ordem. O TCP garante que você veja o site exatamente como ele foi enviado.
- `Transferência de Arquivos` (FTP): o download de um arquivo de software, por exemplo, exige que cada byte seja transferido corretamente, sem erros.
- `E-mail` (SMTP): a mensagem precisa ser entregue na íntegra para ser legível e útil.

**UDP (User Datagram Protocol)**
É um protocolo sem conexão. Ele opera de forma mais simples e direta, enviando pacotes de dados (chamados datagramas) sem qualquer garantia de entrega ou ordem. Pense nele como o serviço postal: você envia uma carta e espera que chegue, mas não há confirmação ou garantia.
- `Confiabilidade`: o UDP é não confiável. Ele não possui mecanismos de confirmação, retransmissão ou controle de fluxo. Os pacotes podem chegar fora de ordem, duplicados ou, simplesmente, não chegarem.
- `Performance`: como não há overhead de handshake ou retransmissão, o UDP é extremamente rápido e leve. É perfeito para aplicações que exigem baixa latência.
- `Adequação`: é a escolha certa para aplicações onde a velocidade é mais importante que a confiabilidade perfeita, e onde a perda de alguns pacotes é aceitável.

Exemplos práticos:
- Streaming de vídeo e áudio: em uma videochamada ou ao assistir a um filme, a perda de alguns milissegundos de áudio ou um quadro de vídeo é imperceptível para o usuário, mas a latência causada pela espera de um pacote perdido seria muito notável.
- Jogos online: a latência (ou "ping") é um fator crítico. É preferível perder um pacote de dados de movimento do que esperar por uma retransmissão que causaria atraso e faria a partida parecer "lenta".
- Sistema de nomes de domínio (DNS): as requisições DNS são pequenas e a velocidade é essencial. Se uma resposta for perdida, é mais rápido enviar a requisição novamente do que usar um protocolo complexo.

### c) Explique os conceitos de latência, largura de banda e throughput. Como esses fatores impactam o design de arquiteturas distribuídas?

| Conceito        | Medida       | Impacto no design |
|-----------------|--------------|-------------------------------------------|
| Latência        | Tempo (ms)   | Causa atrasos na comunicação. Requer comunicação assíncrona e caches para mitigar. |
| Largura de Banda| Volume (bps) | Limita a quantidade de dados. Requer compressão e otimizações de replicação.       |
| Throughput      | Volume (bps) | É a performance real. Requer balanceamento de carga e paralelismo para maximização. |


- `Latência`: é o tempo que um único pacote de dados leva para viajar do ponto A ao ponto B. Pense nela como o atraso ou o tempo de resposta. A latência é geralmente medida em milissegundos (ms) e é influenciada pela distância física, pelos roteadores e pela sobrecarga da rede. A a alta latência é um dos maiores inimigos da performance em sistemas distribuídos. Se um processo em um servidor precisa de dados de outro servidor para continuar, a alta latência o obriga a esperar. Isso é particularmente problemático em algoritmos de consenso, onde todos os nós precisam concordar sobre uma decisão. Para mitigar isso, os designers de sistemas usam:
1. Comunicação assíncrona: em vez de esperar uma resposta, um processo envia uma solicitação e continua seu trabalho, lidando com a resposta quando ela chega.
2. Cachês: manter uma cópia de dados utilizados com frequência perto do processo que os acessa reduz a necessidade de comunicação remota.

- `Largura de banda`: é a capacidade máxima de um canal de comunicação, ou seja, a quantidade de dados que pode ser transmitida por segundo. É a "estrada" da sua rede. A largura de banda é medida em bits por segundo (bps). Em sistemas com baixa largura de banda, transferir grandes arquivos ou replicar bancos de dados inteiros se torna impraticável. A solução é otimizar o uso da banda com:
1. Compressão de dados: reduz o tamanho da informação antes de enviá-la pela rede.
2. Replicação seletiva: em vez de replicar todos os dados, replicar apenas o que é essencial para o funcionamento de um nó, reduzindo a carga de rede.

- `Throughput` (Taxa de transferência): é a quantidade real de dados que um sistema transfere em um determinado período. Ao contrário da largura de banda (a capacidade máxima), o throughput é o desempenho efetivo, que pode ser menor devido a fatores como latência, perdas de pacotes ou lentidão no processamento. Se um sistema tem alta largura de banda e baixa latência, mas ainda assim o throughput é baixo, isso indica um gargalo em outro lugar (por exemplo, no processamento dos servidores ou no I/O do disco). Um bom design de sistema distribuído busca maximizar o throughput através de:
1. Paralelismo: dividir grandes tarefas em subtarefas menores que podem ser executadas em paralelo em diferentes nós.
2. Balanceamento de carga: Distribuir as requisições uniformemente entre os servidores para que nenhum nó seja sobrecarregado, mantendo a performance de todo o sistema.

### d) Discuta a importância das APIs de rede (sockets) e do modelo cliente-servidor básico como fundamento para comunicação distribuída.

O modelo cliente-servidor é a base conceitual. Ele estabelece uma divisão clara e simples de papéis: um processo atua como servidor, oferecendo um serviço, enquanto outro atua como cliente, solicitando e consumindo esse serviço. Essa arquitetura é crucial porque:

- Simplifica a lógica: a interação se torna previsível. O cliente sempre inicia a comunicação, e o servidor responde.
- Centraliza o controle: permite o gerenciamento de recursos, a segurança e a consistência dos dados em um único local, o servidor.

As APIs de rede, sendo os sockets o exemplo mais comum e fundamental, são a implementação prática desse modelo. Um socket é a interface de programação que permite que uma aplicação se comunique através da rede. Ele funciona como a "tomada de rede" de um programa, habilitando a troca de dados com outros programas, tanto na mesma máquina quanto em outra.

Em suma, o modelo cliente-servidor oferece a estrutura e a arquitetura para a comunicação, enquanto os sockets fornecem a ferramenta de baixo nível necessária para que essa comunicação realmente aconteça entre as máquinas. Juntos, eles formam o alicerce sobre o qual praticamente todos os sistemas distribuídos, como a web e o e-mail, são construídos.

# 🌐 SEÇÃO II: FUNDAMENTOS DE SISTEMAS DISTRIBUÍDOS
## Questão 3 - Definições e Características
### a) Forneça uma definição precisa de sistema distribuído segundo Coulouris et al. Quais são as três características que distinguem um sistema distribuído de um sistema centralizado?
Segundo o livro "Sistemas Distribuídos - Conceito e projeto" (4ª edição), de Coulouris et al., "um sistema distribuído é aquele no qual os componentes localizados em computadores interligados em rede se comunicam e coordenam suas ações apenas passando mensagens". As três características que distinguem um sistema distribuído são: concorrência de componentes, falta de um relógio global e falhas de componentes independentes.

### b) Explique detalhadamente quatro tipos de transparência em sistemas distribuídos, fornecendo exemplos práticos de cada um usando sistemas conhecidos (WhatsApp, Gmail, Google Drive, Netflix).
- `Transparência de acesso`: esconde as diferenças na representação dos dados e na forma como os recursos são acessados. O usuário interage com o recurso da mesma forma, independentemente de sua localização ou de como ele é armazenado internamente. Exemplo: no Google Drive, o usuário abre um arquivo do Google Drive da mesma maneira, tanto no seu computador, pelo aplicativo móvel, ou pela web. O sistema lida com as diferenças de formato e protocolo de forma transparente para que a experiência do usuário seja consistente.

- `Transparência de localização`: esconde a localização real dos recursos. O usuário não precisa saber onde um recurso está armazenado ou onde um serviço está sendo executado. Exemplo: na Netflix, quando um usuário assiste a um filme, a Netflix o transmite a partir de um dos milhares de servidores da sua CDN (Content Delivery Network) global. O sistema encontra automaticamente o servidor mais próximo e com melhor desempenho para você, sem que você precise saber onde ele está.

- `Transparência de replicação`: esconde o fato de que múltiplos cópias de um recurso (dados ou serviços) existem. A replicação é usada para aumentar a confiabilidade e o desempenho, mas o usuário enxerga apenas um único recurso.Exemplo: no Gmail, os e-mails estão armazenados em vários servidores para garantir que você possa acessá-los mesmo se um dos servidores falhar. No entanto, você interage com seu e-mail como se ele estivesse em um único lugar, e o sistema garante que as mudanças em uma cópia sejam refletidas nas outras de forma transparente.

- `Transparência de falha`: esconde as falhas dos componentes, permitindo que o sistema continue funcionando sem que o usuário perceba a interrupção. Exemplo: no WhatsApp, se o servidor que está gerenciando a conversa de um usuário falhar, o sistema de mensagens o substitui por outro de forma transparente. A mensagem que você enviou pode ter um pequeno atraso, mas ela será entregue e você não será notificado sobre o erro no servidor.

### c) Analise os principais desafios inerentes aos sistemas distribuídos: heterogeneidade, falhas, concorrência e segurança. Como cada desafio impacta o projeto de arquiteturas distribuídas?

- `Heterogeneidade`: o desafio é fazer com que sistemas, redes, hardware e linguagens de programação diferentes trabalhem juntos. A arquitetura deve incluir uma camada intermediária (middleware) que traduza a comunicação e mascare as diferenças para o programador.

- `Falhas`: falhas de rede, hardware ou software são comuns. O design deve ser resiliente, com redundância (replicação de dados), mecanismos de detecção de falhas e algoritmos de consenso para garantir que o sistema continue funcionando mesmo com a queda de componentes.

- `Concorrência`: múltiplos processos em diferentes máquinas podem tentar acessar o mesmo recurso simultaneamente. O projeto deve usar mecanismos de sincronização distribuída, como bloqueios e protocolos de transação, para evitar inconsistências nos dados.

- `Segurança`: a natureza aberta das redes torna os sistemas distribuídos vulneráveis a ataques. O design deve priorizar a segurança com criptografia, autenticação forte e controle de acesso para proteger os dados e a comunicação.

## Questão 4 - Vantagens e Trade-offs
### a) Identifique e explique quatro vantagens fundamentais dos sistemas distribuídos em relação aos sistemas centralizados.

Os sistemas distribuídos oferecem vantagens significativas sobre os centralizados, principalmente por não dependerem de um único ponto de falha. Aqui estão quatro das vantagens mais importantes:

1. `Confiabilidade e tolerância a falhas`: em um sistema centralizado, se a máquina principal falha, todo o sistema para. Já em um sistema distribuído, a falha de um componente (um nó ou servidor) não necessariamente paralisa o sistema inteiro. As tarefas podem ser transferidas para outros nós em funcionamento, garantindo a continuidade do serviço. Essa redundância torna o sistema muito mais robusto.

2. `Escalabilidade`: sistemas centralizados têm um limite físico de crescimento. Para aumentar sua capacidade, é necessário fazer um upgrade de hardware (escalabilidade vertical), o que é caro e complexo. Em sistemas distribuídos, a escalabilidade é alcançada ao simplesmente adicionar mais nós à rede (escalabilidade horizontal).

3. `Performance e concorrência`: sistemas distribuídos podem dividir uma tarefa grande e complexa em subtarefas menores e executá-las em paralelo em diferentes máquinas. Isso resulta em um aumento significativo da performance e da taxa de transferência (throughput). Por exemplo, um serviço de renderização de vídeo pode usar centenas de máquinas para processar frames simultaneamente.

4. `Custo e distribuição geográfica`: é mais econômico construir um sistema com múltiplas máquinas de baixo custo do que investir em um único supercomputador. Além disso, a natureza distribuída permite que os recursos sejam alocados geograficamente, posicionando servidores mais próximos dos usuários. Isso reduz a latência de comunicação e melhora a experiência do usuário.

### b) Analise os trade-offs entre escalabilidade, confiabilidade e segurança em sistemas distribuídos. Como esses trade-offs influenciam decisões arquiteturais? Forneça um exemplo prático.

- `Escalabilidade vs. confiabilidade`: a escalabilidade busca aumentar a capacidade do sistema adicionando mais componentes (máquinas, servidores). No entanto, um sistema com 1000 servidores é estatisticamente mais propenso a ter pelo menos um deles falhando em um dado momento do que um sistema com apenas 10 servidores. Aumentar a escala aumenta a probabilidade de falha de um componente. **Decisão arquitetural**: para manter a confiabilidade em um sistema escalável, o design deve incluir mecanismos de redundância, detecção de falhas e recuperação automática. O preço dessa confiabilidade é a complexidade e a sobrecarga de gerenciar e sincronizar múltiplas réplicas, o que pode reduzir o desempenho.

- `Escalabilidade vs. segurança`: a segurança em um sistema centralizado é mais fácil de gerenciar, pois há um único perímetro a ser protegido. Ao escalar um sistema e distribuir seus componentes por diferentes máquinas e redes, a superfície de ataque aumenta significativamente. A comunicação entre os nós precisa ser segura, e cada novo nó pode ser um ponto de vulnerabilidade. **Decisão arquitetural**: garantir a segurança em um sistema distribuído exige a implementação de controles de acesso granulares, criptografia em trânsito e em repouso, e um sistema de autenticação e autorização robusto. Essas medidas podem adicionar latência e complexidade, impactando a performance e a simplicidade da arquitetura, que são vantagens inerentes à escalabilidade.

- `Confiabilidade vs. segurança`: a confiabilidade muitas vezes depende da replicação de dados e serviços para garantir que o sistema permaneça disponível mesmo em caso de falha. No entanto, replicar dados sensíveis em várias localizações aumenta o risco de um vazamento de dados, pois há mais cópias a serem protegidas. **Decisão arquitetural**: para alcançar a confiabilidade sem comprometer a segurança, é necessário usar criptografia forte para os dados replicados e implementar um rigoroso controle de acesso em cada réplica. Isso pode tornar a recuperação de falhas mais lenta, pois o sistema precisa decifrar e validar os dados antes de torná-los disponíveis, impactando a confiabilidade em termos de tempo de recuperação.

**Exemplo prático: sistema bancário distribuído**
Um sistema bancário que precisa processar milhões de transações por minuto (escalabilidade), garantir que nenhuma transação seja perdida (confiabilidade) e proteger as informações financeiras dos clientes (segurança).

- `Escalabilidade`: O sistema é projetado com uma arquitetura de microsserviços e um banco de dados distribuído para lidar com o volume de transações. **Trade-off**: a escalabilidade para milhões de usuários exige replicar dados e processamento em muitas máquinas. Isso, no entanto, torna a confiabilidade mais complexa, pois o sistema deve coordenar transações em réplicas remotas. A segurança também é comprometida, pois a informação sensível agora está em múltiplos locais, aumentando a superfície de ataque.
- `Confiabilidade`: O banco de dados é replicado em três regiões geográficas diferentes. Se um data center inteiro falhar devido a um desastre natural, o sistema continua operando a partir das outras réplicas. **Trade-off**: para manter a confiabilidade, o sistema usa um algoritmo de consenso para garantir que todas as réplicas concordem com o estado da transação. Isso adiciona latência e pode ser um gargalo de desempenho.
- `Segurança`: Cada transação é criptografada e os dados do usuário são armazenados de forma criptografada. A comunicação entre os microsserviços também é criptografada. **Trade-off**: para garantir a segurança, cada transação pode exigir múltiplas validações e criptografia, o que adiciona sobrecarga computacional e aumenta a latência, impactando o desempenho.

A decisão final de design não é sobre "qual é o mais importante", mas sim sobre equilibrar esses três pilares. Para o sistema bancário, a segurança e a confiabilidade são inegociáveis. Portanto, a arquitetura sacrificará um pouco de desempenho (e, por consequência, a escalabilidade máxima) para garantir que essas duas propriedades sejam prioritárias.

# 🏗️ SEÇÃO III: ALGORITMOS DE CONSENSO
## Questão 5 - Problema do Consenso
### a) Explique por que o problema do consenso é fundamental em sistemas distribuídos. Quais são os principais desafios que tornam o consenso difícil em ambiente distribuído?

O problema do consenso é fundamental em sistemas distribuídos porque ele garante que todos os nós (computadores) em uma rede cheguem a um acordo sobre um único valor ou estado, mesmo que alguns nós falhem. Sem o consenso, a consistência de um sistema seria impossível de ser mantida. Alcançar o consenso é difícil em um ambiente distribuído devido a três desafios principais:
- `Falhas`: um nó pode simplesmente travar ou ficar inacessível. O sistema precisa ser capaz de continuar operando e, eventualmente, chegar a um acordo sem a participação do nó que falhou.
- `Incerteza da rede` (Tempo assíncrono): não há garantia sobre o tempo que uma mensagem leva para ir de um nó a outro. Uma mensagem pode ser perdida, duplicada ou chegar atrasada. Isso torna impossível saber se a falta de resposta de um nó é porque ele falhou ou apenas está com um atraso de comunicação. Este é o cerne do Teorema da Impossibilidade FLP, que mostra que, em um sistema assíncrono, é impossível garantir o consenso se houver a possibilidade de falha de apenas um nó.
- `Falhas bizantinas`: este é o cenário mais complexo, onde um nó falho pode agir de forma maliciosa. Em vez de simplesmente travar, ele pode enviar informações conflitantes para diferentes nós, tentando sabotar o sistema e impedir o consenso.

Esses desafios exigem a criação de algoritmos de consenso complexos, como Raft e Paxos, que são projetados para funcionar de forma confiável mesmo sob condições de falha.

### b) Defina as três propriedades fundamentais do consenso: Acordo, Validade e Terminação. Por que todas as três são necessárias?

As três propriedades fundamentais do consenso garantem que um algoritmo de consenso seja correto, útil e capaz de progredir. Elas são:

1. `Acordo`: também conhecida como consistência. Afirma que todos os processos que não falham devem chegar à mesma decisão. Em outras palavras, se um processo decide por um valor v, nenhum outro processo que não falhe pode decidir por um valor diferente de v. Esta é a propriedade central do consenso; se ela for violada, o sistema não é coerente. Um algoritmo que satisfaça `Validade` e `Terminação`, mas não `Acordo`, poderia levar a um cenário em que cada processo decide por um valor diferente, destruindo a consistência do sistema.

2. `Validade`: garante que o valor decidido deve ser um dos valores que foram propostos. Isso impede que o algoritmo de consenso "invente" um valor. Por exemplo, se os processos propõem os valores 5 e 8, a decisão final deve ser 5 ou 8, e nunca 10. A `Validade` é essencial para que o consenso seja significativo. Um algoritmo que satisfaça Acordo e Terminação, mas não `Validade`, poderia fazer com que os processos concordassem em um valor que nunca foi proposto. Por exemplo, um algoritmo que sempre decide por zero violaria esta regra, mesmo que todos concordassem.

3. `Terminação`: afirma que todos os processos que não falham devem eventualmente chegar a uma decisão. Em outras palavras, o algoritmo deve garantir o progresso do sistema. Sem a `Terminação`, um algoritmo de consenso poderia rodar para sempre sem chegar a um resultado, tornando-se inútil na prática. Um algoritmo que satisfaça `Acordo` e `Validade`, mas não `Terminação`, poderia simplesmente não decidir por um valor, ficando em um loop infinito de votação.

Em resumo, o `Acordo` garante a coerência, a `Validade` garante a relevância e a `Terminação` garante o progresso. Um algoritmo de consenso só é considerado correto se puder provar que satisfaz todas as três propriedades, mesmo em face de falhas.

### c) Descreva o algoritmo Raft explicando: os três estados possíveis de um nó (Leader, Follower, Candidate), o processo de eleição de líder, como funciona a replicação de log

**Os três estados possíveis de um nó (Leader, Follower, Candidate)**
Cada nó em um cluster Raft pode estar em um de três estados:
- `Follower`: este é o estado inicial para todos os nós. Um Follower passivamente recebe instruções do Leader, como a replicação de entradas de log. Ele não envia mensagens para outros nós e apenas responde às mensagens recebidas. Se ele não receber uma mensagem do Leader por um certo período de tempo (timeout de eleição), ele se tornará um Candidate.
- `Candidate`: um nó se torna um Candidate quando um período de eleição expira. O Candidate se candidata a ser o novo Leader, incrementa seu "termo" (um número que funciona como um identificador para uma eleição) e envia votos de requisição para os outros nós. Ele espera por votos dos outros nós.
- `Leader`: o Leader é o único nó que pode aceitar novas entradas de log e replicá-las para os Followers. Ele se comunica constantemente com os Followers (através de mensagens de heartbeat) para mantê-los em sincronia e para evitar que eles se tornem Candidates.

**O processo de eleição de líder**
A eleição de um Leader é o mecanismo que o Raft usa para garantir que o cluster tenha um único nó responsável por toda a comunicação.
- `Início da eleição`: quando um Follower não recebe um heartbeat do Leader atual por um período de tempo aleatório, seu timeout de eleição expira. Ele então assume que o Leader atual falhou e se torna um Candidate.
- `Votação`: o Candidate incrementa seu termo e envia uma mensagem de "requisição de voto" a todos os outros nós. Cada nó vota uma única vez por termo.
- `Resultado da eleição`: o Candidate pode vencer a eleição de três maneiras:
    - Ele recebe a maioria dos votos dos nós do cluster e se torna o novo Leader.
    - Outro nó se torna um Leader. Se um Candidate descobrir um novo Leader com um termo maior, ele retorna ao estado de Follower.
    - Nenhum Candidate obtém a maioria dos votos, o que pode acontecer em uma "eleição dividida". Nesse caso, uma nova eleição é iniciada.

**Como funciona a replicação de log**
Uma vez que um Leader é eleito, ele se torna responsável por replicar o log de transações. O log é a única fonte da verdade no Raft.
- `Nova entrada`: o Leader recebe uma nova entrada de log (por exemplo, uma transação de escrita). Ele anexa essa entrada ao seu próprio log.
- `Envio para followers`: o Leader envia uma mensagem de AppendEntries para todos os seus Followers, que inclui a nova entrada de log.
- `Confirmação e compromisso`: quando um Follower recebe a entrada de log, ele a anexa ao seu próprio log e envia uma confirmação de sucesso para o Leader. O Leader aguarda as confirmações da maioria dos Followers. Quando a maioria dos nós confirma que a entrada foi replicada, o Leader a marca como comprometida (committed) em seu próprio log e executa a transação. Em seguida, o Leader notifica os Followers que a entrada foi comprometida. Os Followers também a marcam como comprometida e a executam.

Este processo garante que todos os nós do cluster tenham uma cópia idêntica e consistente do log, e que as transações só sejam consideradas permanentes após a confirmação da maioria. Se o Leader falhar durante a replicação, o processo de eleição é acionado novamente, e o novo Leader restaurará a consistência do log.

## Questão 6 - Comparação de Algoritmos
### Compare os algoritmos Raft, Paxos e PBFT considerando: complexidade de compreensão e implementação, tolerância a falhas, performance e latência, e casos de uso adequados. Quando você recomendaria cada algoritmo?
| Característica | Raft | Paxos | PBFT (Practical Byzantine Fault Tolerance) |
|----------------|------|-------|--------------------------------------------|
| Complexidade de Compreensão e Implementação | Baixa. Projetado para ser compreensível e fácil de implementar. É o mais popular para iniciantes e para a maioria dos casos de uso. | Muito alta. Conhecido por ser extremamente difícil de entender e implementar corretamente. | Média. Mais complexo que o Raft devido à necessidade de lidar com falhas bizantinas, mas mais prático que o Paxos. |
| Tolerância a Falhas                | Tolerância a falhas de crash. Um nó pode falhar ao travar ou se desconectar. Requer que a maioria (mais de 50%) dos nós esteja funcionando. | Tolerância a falhas de crash. Tem a mesma tolerância a falhas do Raft e também exige que a maioria dos nós esteja online. | Tolerância a falhas bizantinas. Um nó pode agir de forma maliciosa (enviar mensagens falsas). Tolera falhas de até ⌊(N−1)/3⌋ nós. |
| Performance e Latência             | Boa performance. Em operação normal, apenas o líder processa as solicitações, o que é rápido. A latência aumenta durante uma eleição de líder. | Performance variável. O algoritmo básico tem alta latência. Versões otimizadas como o Paxos Multi-Decree melhoram a performance ao preço de maior complexidade. | Média a baixa performance. O overhead de criptografia para assinar mensagens e os múltiplos estágios de comunicação levam a uma performance inferior em comparação com o Raft. |
| Casos de Uso Adequados             | Ambientes com confiança entre nós. É a escolha padrão para sistemas distribuídos em que os nós pertencem à mesma organização e a falha se deve a causas técnicas (por exemplo, um servidor que trava). | Casos históricos ou acadêmicos. É pouco usado em novas implementações devido à sua complexidade. Muitos sistemas que afirmam usar Paxos, na verdade, usam variantes simplificadas. | Ambientes com nós não confiáveis. Ideal para sistemas em que os participantes não confiam uns nos outros, como redes de blockchain privadas ou consórcios entre diferentes empresas. |

- `Raft`: recomenda-se o Raft para a grande maioria das aplicações de sistema distribuído. Sua simplicidade de implementação e robustez para lidar com falhas de crash o tornam a melhor opção. É perfeito para replicar logs, gerenciar serviços de configuração (como o etcd ou Consul) e manter estados consistentes em data centers.
- `Paxos`: atualmente, a não ser que haja uma necessidade muito específica, não é recomendado o uso do Paxos. O Raft cobre os mesmos casos de uso com um algoritmo muito mais fácil de entender e manter. O Paxos é mais relevante para o estudo acadêmico ou para sistemas legados que já o utilizam.
- `PBFT`: recomenda-se o PBFT para cenários onde a segurança contra ataques maliciosos é a principal prioridade. É a escolha adequada para sistemas onde os participantes são anônimos ou pertencem a diferentes organizações, como em uma rede de blockchain privada ou um sistema de votação.

# 🏛️ SEÇÃO IV: MODELOS E ARQUITETURAS
## Questão 7 - Arquiteturas Fundamentais
### a) Compare detalhadamente as arquiteturas Cliente-Servidor e Peer-to-Peer (P2P): características principais de cada uma, vantagens e desvantagens exemplos práticos de sistemas que utilizam cada arquitetura

| Característica       | Cliente-Servidor | Peer-to-Peer (P2P) |
|---------------------|-----------------|------------------|
| Características principais | Modelo Centralizado. Clientes solicitam serviços a um servidor central, que processa e responde. A comunicação é sempre iniciada pelo cliente. O servidor é um ponto fixo e crucial. | Modelo Descentralizado. Todos os nós (peers) são iguais, atuando tanto como clientes quanto como servidores. A comunicação pode ocorrer diretamente entre os peers. |
| Vantagens            | - Controle Centralizado: Facilita a segurança, o gerenciamento de dados e a auditoria.<br>- Estabilidade: Se o servidor for robusto, o sistema é previsível e estável.<br>- Simplicidade de Busca: Encontrar um recurso é simples, pois ele está em um local conhecido (o servidor). | - Escalabilidade: Fácil de escalar; basta adicionar mais peers para aumentar a capacidade.<br>- Tolerância a Falhas: A falha de um nó não afeta o sistema como um todo.<br>- Custo Baixo: Não exige um servidor central de alto custo. |
| Desvantagens         | - Ponto Único de Falha: Se o servidor cair, o sistema inteiro fica indisponível.<br>- Gargalo de Performance: O servidor pode ficar sobrecarregado com muitas requisições simultâneas.<br>- Custo de Infraestrutura: Exige a manutenção de um ou mais servidores dedicados. | - Segurança: Difícil de gerenciar e auditar, pois os dados estão espalhados por vários nós.<br>- Gerenciamento: Difícil de controlar e atualizar o sistema, já que não há um ponto central.<br>- Busca de Recursos: A busca por um recurso pode ser mais lenta e complexa. |
| Exemplos Práticos    | Navegação na Web (HTTP): Seu navegador (cliente) se conecta ao servidor do Google para obter uma página.<br>E-mail (SMTP/IMAP): Seu cliente de e-mail se conecta ao servidor do Gmail para enviar ou receber mensagens. | Compartilhamento de Arquivos: O BitTorrent permite que você baixe um arquivo de vários usuários ao mesmo tempo.<br>Criptomoedas (Bitcoin): Cada nó na rede P2P verifica e valida as transações diretamente com outros nós. |


### b) Explique o conceito de arquiteturas híbridas e como sistemas como a Netflix combinam elementos centralizados e distribuídos (CDN, microserviços).

Uma **arquitetura híbrida** é um modelo de sistema que combina as vantagens dos sistemas centralizados e distribuídos. Em vez de operar inteiramente a partir de um único servidor ou ser completamente descentralizado, ela utiliza uma abordagem mista, onde cada componente é otimizado para a sua função.

A Netflix não depende de um servidor central para gerenciar e entregar todo o seu conteúdo, mas também não é um sistema P2P (Peer-to-Peer). Em vez disso, ela combina elementos de ambas as arquiteturas para garantir a melhor experiência possível.

A **parte centralizada** da Netflix é responsável por gerenciar dados críticos que exigem alta consistência e controle. Essa lógica é complexa e deve ser mantida de forma segura e consistente em um local central para evitar erros. Já a maior parte da operação da Netflix é **distribuída** e construída em uma arquitetura de microsserviços. Em vez de ter um único e grande programa (monolito), a lógica do aplicativo é dividida em centenas de serviços menores e independentes. Além disso, é utilizado CDN para armazenar o conteúdo em milhares de servidores espalhados pelo mundo, localizados estrategicamente em pontos de troca de internet ou dentro das redes dos próprios provedores.

Em resumo, a Netflix utiliza um modelo híbrido para capitalizar as vantagens de cada arquitetura. Ela usa o controle e a consistência dos sistemas centralizados para os dados críticos de negócio e a escalabilidade e a baixa latência dos sistemas distribuídos para a entrega de conteúdo.

### c) Discuta as variações da arquitetura cliente-servidor: thin client, thick client e multi-tier. Quando cada variação é mais adequada?
- `Thin Client`: na arquitetura thin client, a maior parte do processamento da aplicação ocorre no servidor. O cliente, que pode ser um dispositivo de baixo poder de processamento, é responsável apenas por exibir a interface gráfica e enviar as entradas do usuário. Essa abordagem é mais adequada para ambientes que exigem controle e segurança centralizados, como caixas de banco ou redes corporativas, onde o acesso aos dados é restrito ao servidor.
- `Thick Client`: a maior parte da lógica e do processamento reside no lado do cliente. O cliente é um software robusto, que usa o servidor principalmente para armazenamento de dados e sincronização. Essa arquitetura é ideal para aplicações que precisam de alto desempenho e podem funcionar offline, como softwares de edição de vídeo, jogos de computador ou aplicativos de desktop complexos.
- `Multi-Tier` (Múltiplas Camadas): a arquitetura multi-tier divide o sistema em múltiplas camadas lógicas, como a camada de apresentação (o cliente), a camada de lógica de negócio e a camada de dados. Cada camada pode ser executada em servidores separados, permitindo que a aplicação seja mais modular, flexível e escalável. Essa é a arquitetura padrão para a maioria dos grandes sistemas distribuídos, como serviços de e-commerce ou plataformas de streaming, pois ela permite que cada camada seja escalada de forma independente para atender à demanda.

## Questão 8 - Modelos de Interação
### a) Explique as diferenças entre comunicação síncrona e assíncrona em sistemas distribuídos. Quais são as implicações de cada modelo para performance e confiabilidade?

A comunicação **síncrona** é um modelo onde o emissor envia uma mensagem e aguarda a resposta antes de continuar qualquer outra tarefa. Pense nela como uma chamada telefônica: você fala e espera a outra pessoa responder para poder continuar.
- Implicações: a principal vantagem é a simplicidade de implementação. No entanto, ela introduz alta latência, pois o processo fica bloqueado. Se o receptor falhar, o emissor também pode travar, comprometendo a confiabilidade do sistema.

A comunicação **assíncrona** é um modelo onde o emissor envia uma mensagem e continua seu trabalho imediatamente, sem esperar pela resposta. A resposta é processada posteriormente, muitas vezes por meio de um mecanismo de notificação ou evento.
- Implicações: a performance é muito melhor! Pois os processos não ficam bloqueados, permitindo que a aplicação faça várias coisas ao mesmo tempo. Isso aumenta a tolerância a falhas e a confiabilidade, já que a falha de um serviço não impede o progresso do emissor. A desvantagem é a complexidade de implementação, já que o código precisa gerenciar retornos e eventos.

### b) Analise diferentes tipos de falhas em sistemas distribuídos (crash, omissão, bizantina) e como cada tipo impacta o design do sistema.
- `Falha de crash`: ocorre quando um nó simplesmente para de funcionar de forma abrupta. É a falha mais simples de lidar. O impacto no design é a necessidade de redundância e mecanismos de failover. O sistema deve ser projetado para que outro nó possa assumir a tarefa do nó que falhou, garantindo a continuidade do serviço.

- `Falha de omissão`: acontece quando um nó falha em enviar ou receber mensagens. O nó em si pode estar funcionando, mas a comunicação é interrompida. O impacto no design é a implementação de timeouts e retransmissões. O sistema precisa ser capaz de detectar a ausência de resposta e reenviar a mensagem, ou considerar o nó como falho temporariamente.

- `Falha Bizantina`: é a mais complexa e grave, pois um nó age de forma maliciosa. Ele pode enviar dados falsos ou mensagens contraditórias para diferentes nós, tentando sabotar o sistema. O impacto no design é a necessidade de algoritmos de consenso bizantino (como o PBFT) que exigem múltiplos estágios de comunicação e validações criptográficas para garantir que os nós honestos cheguem a um acordo mesmo com a presença de nós traidores.

# 🔬 SEÇÃO V: ESTUDO DE CASO E APLICAÇÃO
## Questão 9 - Análise Crítica da Netflix
### a) Identifique cinco tipos de transparência que a Netflix implementa para seus usuários. Explique como cada transparência é alcançada na prática.
Os sistemas distribuídos criam a ilusão de um sistema único e coeso. A Netflix utiliza vários tipos de transparência para fazer isso, e aqui estão cinco exemplos:
- `Transparência de acesso`: o usuário acessa o conteúdo com uma URL simples, sem se preocupar com o formato ou protocolo dos dados, que é tratado internamente pela Netflix.
- `Transparência de localização`: o usuário não precisa saber onde o filme está armazenado. A Netflix utiliza sua CDN global para automaticamente conectar o usuário ao servidor mais próximo, reduzindo a latência.
- `Transparência de replicação`: o usuário não tem conhecimento de que o mesmo filme está armazenado em várias cópias ao redor do mundo. A Netflix faz isso para garantir que o conteúdo esteja sempre disponível, mesmo com alta demanda.
- `Transparência de falha`: se um dos servidores de streaming cair, o sistema automaticamente e de forma transparente redireciona a conexão do usuário para outro servidor em funcionamento. Isso evita interrupções na reprodução.
- `Transparência de concorrência`: vários usuários podem assistir ao mesmo filme simultaneamente sem interferir uns nos outros. O sistema lida com o acesso de forma coordenada e sem que o usuário perceba.

### b) Analise a arquitetura da Netflix como um sistema híbrido: quais componentes são centralizados Quais componentes são distribuídos? Como a CDN (Content Delivery Network) contribui para performance e escalabilidade?
- **Componentes centralizados**: a parte centralizada da Netflix é responsável por gerenciar dados críticos que exigem alta consistência e controle. Esta camada inclui a gestão de contas de usuário, o sistema de autenticação, os pagamentos, o sistema de recomendação (que personaliza o catálogo para cada usuário) e o gerenciamento do catálogo de filmes e séries. Essa lógica é complexa e deve ser mantida de forma segura e consistente em um local central para evitar erros.

- **Componentes distribuídos**: a maior parte da operação da Netflix é distribuída e construída em uma arquitetura de microsserviços. Em vez de ter um único e grande programa (monolito), a lógica do aplicativo é dividida em centenas de serviços menores e independentes. Um microsserviço cuida do login, outro do histórico de visualizações, outro do carregamento da interface do usuário, e assim por diante. Essa abordagem permite que a Netflix escale cada serviço de forma independente para atender à demanda, garantindo alta disponibilidade.

- **Papel da CDN** (Content Delivery Network): a peça mais importante do modelo distribuído da Netflix é a sua CDN global, chamada Open Connect. Em vez de entregar o streaming de um servidor central, o conteúdo é armazenado em milhares de servidores espalhados pelo mundo, localizados estrategicamente em pontos de troca de internet ou dentro das redes dos próprios provedores.
        - Performance: quando o usuário clica "play", o vídeo é transmitido do servidor mais próximo. Isso reduz a latência e o tempo de carregamento.
        - Escalabilidade: a carga de streaming não sobrecarrega os servidores centrais da Netflix. Em vez disso, a demanda é distribuída para a CDN, que pode atender a milhões de usuários simultaneamente.

### c) Como a Netflix lida com os desafios de heterogeneidade (diferentes dispositivos, redes, regiões geográficas) e falhas?
A Netflix usa uma arquitetura de microsserviços para que cada componente (como a interface de usuário ou a lógica de recomendação) possa ser adaptado para diferentes dispositivos, de TVs a celulares. Além disso, ela utiliza streaming de vídeo adaptável, ajustando automaticamente a qualidade do vídeo de acordo com a velocidade da rede do usuário, garantindo uma experiência fluida em qualquer conexão.

Para as falhas, a estratégia da Netflix é a redundância e a resiliência. Dados e serviços são replicados em múltiplos servidores e em diferentes data centers. Se um servidor falha, o tráfego é automaticamente redirecionado para outro que esteja funcionando. Essa abordagem garante que o sistema permaneça disponível mesmo em caso de falha de um ou mais componentes.

## Questão 10 - Mapeamento Conceitual
Demonstre como os conceitos de Sistemas Operacionais se estendem para Sistemas Distribuídos. Preencha cada linha com o equivalente distribuído e uma breve explicação

1. `Processo`: o equivalente é um Processo Distribuído. Um processo em um sistema distribuído é uma unidade de execução que opera em uma máquina separada, comunicando-se com outros processos através de uma rede.
2. `IPC`: o equivalente é um RPC (Remote Procedure Call). O IPC, que em um S.O. é local, se estende para a comunicação entre máquinas. Mecanismos como RPC fazem com que uma chamada de função remota pareça uma chamada local.
3. `Sincronização`: o equivalente são algoritmos de consenso. Como não existe um único relógio global, a sincronização se baseia em relógios lógicos ou algoritmos de consenso para estabelecer uma ordem consistente de eventos entre diferentes nós.
4. `Gerenciamento de memória`: o equivalente é a Memória Compartilhada Distribuída (DSM). A DSM cria a ilusão de um único espaço de memória compartilhado por várias máquinas. Os dados são replicados e gerenciados para parecerem locais para os processos.
5. `Sistema de arquivos`: o equivalente é um Sistema de Arquivos Distribuído (DFS). Um DFS permite que um usuário acesse arquivos armazenados em um servidor remoto como se estivessem na sua máquina local, gerenciando de forma transparente o acesso e a consistência dos dados.

# 📚 SEÇÃO VI: QUESTÕES DISSERTATIVAS AVANÇADAS
## Questão 11 - Análise Comparativa
Uma empresa precisa escolher entre uma arquitetura cliente-servidor tradicional e uma arquitetura P2P para um novo sistema de compartilhamento de arquivos corporativo. Analise este cenário considerando:
- Requisitos de segurança corporativa
- Controle administrativo
- Escalabilidade para 10.000 usuários
- Tolerância a falhas
- Custos de infraestrutura
Justifique sua recomendação com base nos conceitos estudados.

Segue uma análise de cada uma das arquiteturas.

**Arquitetura cliente-servidor**
- `Requisitos de segurança e controle`: esta arquitetura é ideal para ambientes corporativos. A segurança e o controle administrativo podem ser gerenciados de forma centralizada no servidor. É fácil implementar políticas de acesso rigorosas, autenticação de usuários, criptografia de dados em trânsito e em repouso, e realizar auditorias regulares.
- `Escalabilidade e tolerância a falhas`: a escalabilidade pode ser um desafio, pois o servidor pode se tornar um gargalo de desempenho. No entanto, é um problema conhecido e solucionável com estratégias como a escalabilidade horizontal (adicionando mais servidores) e o uso de balanceadores de carga. A tolerância a falhas é um risco, pois a falha do servidor central derruba o sistema. Isso pode ser mitigado com redundância e sistemas de failover, embora com custos adicionais.
- `Custos de infraestrutura`: exige um investimento inicial maior em servidores dedicados e infraestrutura de rede robusta.

**Arquitetura Peer-to-Peer (P2P)**
- `Requisitos de segurança e controle`: a segurança é o principal ponto fraco do P2P em um ambiente corporativo. A descentralização da rede torna a segurança e o controle administrativo praticamente impossíveis de gerenciar. Os dados corporativos estariam espalhados em milhares de dispositivos, muitos fora do controle da TI, criando um risco enorme de vazamento de dados e vulnerabilidade a malwares.
- `Escalabilidade e tolerância a falhas`: esta é a grande vantagem do P2P. A rede escala de forma natural e a tolerância a falhas é inerente, já que a falha de um nó não afeta a disponibilidade do sistema. A capacidade da rede aumenta à medida que mais usuários se conectam.
- `Custos de infraestrutura`: o custo inicial é baixo, pois a arquitetura utiliza a infraestrutura já existente dos usuários, como seus próprios computadores e redes.

Com base na análise do cenário e nos conceitos de arquitetura de sistemas distribuídos, a arquitetura cliente-servidor tradicional é a escolha mais adequada para um sistema de compartilhamento de arquivos corporativo. Embora o modelo P2P ofereça vantagens em escalabilidade e custo, ele falha em atender às exigências mais críticas para uma empresa: segurança e controle administrativo. Em um sistema corporativo, a integridade e a confidencialidade dos dados são inegociáveis. O modelo P2P não oferece mecanismos confiáveis para garantir que os dados estejam seguros. As questões de escalabilidade e tolerância a falhas do modelo cliente-servidor podem ser resolvidas com soluções técnicas e investimento, mas a falta de controle do P2P é uma falha fundamental para este caso.

## Questão 12 - Projeto Conceitual
Desafio: Projete conceitualmente um sistema distribuído para votação eletrônica que deve garantir:
- Transparência de acesso (votar de qualquer local)
- Transparência de falha (sistema sempre disponível)
- Segurança e auditabilidade
- Escalabilidade nacional

**Arquitetura escolhida e justificativa**
Para um sistema de votação eletrônica, a arquitetura ideal seria **híbrida**. A parte centralizada seria responsável pelo registro e autenticação dos eleitores. Uma autoridade central garante que cada cidadão tenha uma única identidade e possa votar apenas uma vez. Esta camada também cuidaria da contagem final de votos.

A parte distribuída, por sua vez, seria uma rede de nós (peers) que receberiam, validariam e replicariam os votos. Essa abordagem combina a segurança e o controle da arquitetura centralizada com a escalabilidade, a resiliência e a transparência de uma rede descentralizada.

**Algoritmo de consenso adequado**
Quanto ao algoritmo de consenso, o adequado para este sistema é o **PBFT** (Practical Byzantine Fault Tolerance). A principal razão para essa escolha é que o PBFT é projetado para lidar com falhas bizantinas, onde um nó pode agir de forma maliciosa, tentando corromper o sistema. Em um sistema de votação, não podemos assumir que todos os nós são confiáveis. O PBFT garante que o sistema chegue a um consenso e mantenha a integridade do log de votos.

**Principais desafios e como serão tratados**
- `Segurança e integridade do voto`: cada voto seria criptografado com chaves públicas e privadas e tratado como uma transação imutável. A auditabilidade é garantida por um log de transações distribuído e público, onde cada voto é registrado de forma anônima e transparente.

- `Escalabilidade nacional`: a rede distribuída de nós lidaria com o alto volume de requisições. Para otimizar a performance, o sistema poderia usar sharding, dividindo a rede em grupos (ou "shards") para processar votos por região geográfica, aliviando a carga sobre a rede.

- `Transparência de falha`: a replicação de dados em múltiplos nós com o algoritmo PBFT garante que, se um ou mais servidores falharem, a rede continue funcionando e o log de votos não seja perdido. O sistema automaticamente redireciona o tráfego para os nós em funcionamento.

**Tipos de transparência implementados**
- `Transparência de acesso`: os eleitores podem votar de qualquer lugar, de forma simples, sem se preocupar com os detalhes técnicos de acesso. O sistema lida com a conexão de forma transparente.

- `Transparência de falha`: o sistema se mantém disponível mesmo com a falha de nós. A falha de um componente não é perceptível para o eleitor.

- `Transparência de concorrência`: vários eleitores podem votar ao mesmo tempo. O sistema gerencia as transações de forma transparente, garantindo que não haja conflitos.

- `Transparência de localização`: o eleitor não precisa saber onde o voto é processado ou armazenado, pois o sistema se conecta automaticamente ao servidor mais próximo e disponível para processar a transação.