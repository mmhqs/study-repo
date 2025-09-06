# 🔍 SEÇÃO I: REVISÃO DE PRÉ-REQUISITOS
## Questão 1 - Sistemas Operacionais
**a) Explique a diferença entre processo e thread, destacando as implicações para sistemas distribuídos.**
Um <u>processo</u> é uma instância de um programa em execução. Ele é uma entidade autônoma e independente que possui seu próprio espaço de endereçamento de memória, código, dados e recursos do sistema operacional.

Em um sistema distribuído, os processos são a base para a distribuição de tarefas entre diferentes nós (máquinas). Cada nó da rede pode executar um ou mais processos independentes. A comunicação entre processos em máquinas distintas é tipicamente mais complexa e lenta, usando mecanismos como RPC (Remote Procedure Call) ou passagem de mensagens.

Uma <u>thread</u>, ou linha de execução, é uma unidade de execução que reside dentro de um processo. Múltiplas threads de um mesmo processo compartilham o mesmo espaço de memória, além de outros recursos. Por serem mais leves, a criação e a troca de contexto entre threads são mais rápidas do que entre processos.

A principal implicação de threads para sistemas distribuídos é a capacidade de paralelizar tarefas dentro de um único nó. Por exemplo, um servidor em uma arquitetura cliente-servidor pode usar threads para atender a múltiplas requisições de clientes ao mesmo tempo. Isso melhora o desempenho e a responsividade sem a necessidade de criar um novo processo para cada requisição.

| Característica       | Processo                          | Thread                                    |
|-----------------------|-----------------------------------|-------------------------------------------|
| Independência         | Independente e isolado            | Dependente do processo principal          |
| Memória               | Espaço de memória próprio         | Compartilha memória do processo           |
| Custo                 | Criação e troca de contexto lentas| Criação e troca de contexto rápidas       |
| Comunicação           | Via IPC (lenta)                   | Via memória compartilhada (rápida)        |
| Tolerância a Falhas   | Uma falha não afeta outros processos | Uma falha pode derrubar o processo e suas outras threads |

**b) Descreva os cinco estados de um processo (novo, pronto, executando, esperando, terminado) e explique como esses estados se relacionam com processos distribuídos executando em diferentes máquinas.**
Esses cinco estados são o ciclo de vida de um processo em execução. Abaixo apresento os cinco estados e suas descrições.
1. `Novo` (New): o processo está sendo criado. Ele ainda não está carregado na memória principal, mas já tem um Bloco de Controle de Processo (BCP) associado a ele.
2. `Pronto` (Ready): o processo está pronto para ser executado e está aguardando na fila de processos do escalonador do sistema operacional. Ele está na memória principal e espera apenas o CPU ficar disponível.
3. `Executando` (Running): o processo está atualmente utilizando o CPU e suas instruções estão sendo executadas. Em um sistema com um único CPU, apenas um processo pode estar neste estado por vez.
4. `Esperando` (Waiting): o processo foi temporariamente suspenso. Isso acontece quando ele precisa aguardar a conclusão de algum evento, como uma operação de entrada/saída (I/O) ou a liberação de um recurso.
5. `Terminado` (Terminated): o processo concluiu sua execução. O sistema operacional está liberando todos os seus recursos e o processo é removido da memória.

Em um sistema distribuído, o gerenciamento desses estados se torna mais complexo, pois eles <u>ocorrem em múltiplas máquinas que se comunicam através de uma rede</u>.

- Estados locais: os estados `Pronto` e `Executando` são sempre locais a um nó (máquina) específico. Um processo só pode estar "executando" no CPU de um computador por vez. A gestão da fila de processos "prontos" também é feita localmente por cada sistema operacional.
- Comunicação entre máquinas: o estado de `Esperando` é afetado. Em um sistema distribuído, um processo pode entrar no estado de espera não apenas por uma operação de I/O local, mas também por eventos remotos. Por exemplo, um processo em um servidor pode estar esperando uma resposta de um cliente em outra máquina ou a conclusão de uma tarefa em um banco de dados remoto. A latência da rede pode prolongar significativamente o tempo de espera.
- Gerenciamento distribuído: os estados `Novo` e `Terminado` podem ser iniciados remotamente. Um processo em uma máquina pode solicitar a criação de um novo processo em outra máquina para realizar uma tarefa. Da mesma forma, a terminação de um processo pode ser coordenada remotamente. Algoritmos de balanceamento de carga em sistemas distribuídos buscam otimizar os estados `Pronto` e `Executando`, movendo processos entre nós para evitar que um único computador fique sobrecarregado.

A principal implicação é que, embora os estados de um processo sejam os mesmos, as transições entre eles em um ambiente distribuído são influenciadas pela rede, pela distância e pela comunicação entre os nós, o que introduz desafios como falhas de conexão e sincronização.

**c) Compare três mecanismos de IPC (Inter-Process Communication): pipes, sockets e memória compartilhada. Como estes conceitos se estendem para comunicação em sistemas distribuídos?**

**d) Explique os algoritmos de substituição de página FIFO, LRU e Optimal. Como o gerenciamento de memória virtual se relaciona com sistemas de memória distribuída?**

## Questão 2 - Redes de Computadores
**a) Compare o modelo OSI (7 camadas) com o modelo TCP/IP (4 camadas). Quais camadas são mais relevantes para desenvolvimento de sistemas distribuídos e por quê?**

**b) Analise as diferenças entre TCP e UDP em termos de confiabilidade, performance e adequação para diferentes tipos de aplicações distribuídas. Forneça exemplos práticos.**

**c) Explique os conceitos de latência, largura de banda e throughput. Como esses fatores impactam o design de arquiteturas distribuídas?**

**d) Discuta a importância das APIs de rede (sockets) e do modelo cliente-servidor básico como fundamento para comunicação distribuída.**

# 🌐 SEÇÃO II: FUNDAMENTOS DE SISTEMAS DISTRIBUÍDOS
## Questão 3 - Definições e Características
**a) Forneça uma definição precisa de sistema distribuído segundo Coulouris et al. Quais são as três características que distinguem um sistema distribuído de um sistema centralizado?**

**b) Explique detalhadamente quatro tipos de transparência em sistemas distribuídos, fornecendo exemplos práticos de cada um usando sistemas conhecidos (WhatsApp, Gmail, Google Drive, Netflix).

**c) Analise os principais desafios inerentes aos sistemas distribuídos: heterogeneidade, falhas, concorrência e segurança. Como cada desafio impacta o projeto de arquiteturas distribuídas?

## Questão 4 - Vantagens e Trade-offs
**a) Identifique e explique quatro vantagens fundamentais dos sistemas distribuídos em relação aos sistemas centralizados.

**b) Analise os trade-offs entre escalabilidade, confiabilidade e segurança em sistemas distribuídos. Como esses trade-offs influenciam decisões arquiteturais? Forneça um exemplo prático.

# 🏗️ SEÇÃO III: ALGORITMOS DE CONSENSO
## Questão 5 - Problema do Consenso
**a) Explique por que o problema do consenso é fundamental em sistemas distribuídos. Quais são os principais desafios que tornam o consenso difícil em ambiente distribuído?

**b) Defina as três propriedades fundamentais do consenso: Acordo, Validade e Terminação. Por que todas as três são necessárias?

**c) Descreva o algoritmo Raft explicando:
- Os três estados possíveis de um nó (Leader, Follower, Candidate)
- O processo de eleição de líder
- Como funciona a replicação de log

## Questão 6 - Comparação de Algoritmos
Compare os algoritmos Raft, Paxos e PBFT considerando:

Complexidade de compreensão e implementação

Tolerância a falhas

Performance e latência

Casos de uso adequados

Quando você recomendaria cada algoritmo?

# 🏛️ SEÇÃO IV: MODELOS E ARQUITETURAS
## Questão 7 - Arquiteturas Fundamentais
**a) Compare detalhadamente as arquiteturas Cliente-Servidor e Peer-to-Peer (P2P)**
- Características principais de cada uma
- Vantagens e desvantagens
- Exemplos práticos de sistemas que utilizam cada arquitetura

**b) Explique o conceito de arquiteturas híbridas e como sistemas como a Netflix combinam elementos centralizados e distribuídos (CDN, microserviços).**

c) Discuta as variações da arquitetura cliente-servidor: thin client, thick client e multi-tier. Quando cada variação é mais adequada?

## Questão 8 - Modelos de Interação
a) Explique as diferenças entre comunicação síncrona e assíncrona em sistemas distribuídos. Quais são as implicações de cada modelo para performance e confiabilidade?

b) Analise diferentes tipos de falhas em sistemas distribuídos (crash, omissão, bizantina) e como cada tipo impacta o design do sistema.

# 🔬 SEÇÃO V: ESTUDO DE CASO E APLICAÇÃO
## Questão 9 - Análise Crítica da Netflix
A Netflix é frequentemente citada como exemplo de sistema distribuído bem-sucedido. Com base no conteúdo estudado:

a) Identifique cinco tipos de transparência que a Netflix implementa para seus usuários. Explique como cada transparência é alcançada na prática.

b) Analise a arquitetura da Netflix como um sistema híbrido:
- Quais componentes são centralizados?
- Quais componentes são distribuídos?
- Como a CDN (Content Delivery Network) contribui para performance e escalabilidade?

c) Como a Netflix lida com os desafios de heterogeneidade (diferentes dispositivos, redes, regiões geográficas) e falhas?

## Questão 10 - Mapeamento Conceitual
Demonstre como os conceitos de Sistemas Operacionais se estendem para Sistemas Distribuídos, preenchendo a tabela conceitual:

Conceito SO	Equivalente Distribuído	Explicação
Processo		
IPC		
Sincronização		
Gerenciamento de Memória		
Sistema de Arquivos		

Exportar para as Planilhas
(Preencha cada linha com o equivalente distribuído e uma breve explicação)

# 📚 SEÇÃO VI: QUESTÕES DISSERTATIVAS AVANÇADAS
## Questão 11 - Análise Comparativa
Cenário: Uma empresa precisa escolher entre uma arquitetura cliente-servidor tradicional e uma arquitetura P2P para um novo sistema de compartilhamento de arquivos corporativo.

Analise este cenário considerando:

Requisitos de segurança corporativa

Controle administrativo

Escalabilidade para 10.000 usuários

Tolerância a falhas

Custos de infraestrutura

Justifique sua recomendação com base nos conceitos estudados.

## Questão 12 - Projeto Conceitual
Desafio: Projete conceitualmente um sistema distribuído para votação eletrônica que deve garantir:

Transparência de acesso (votar de qualquer local)

Transparência de falha (sistema sempre disponível)

Segurança e auditabilidade

Escalabilidade nacional

Especifique:

Arquitetura escolhida e justificativa

Algoritmo de consenso adequado

Principais desafios e como serão tratados

Tipos de transparência implementados