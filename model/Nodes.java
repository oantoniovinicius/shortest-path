/* ***************************************************************
* Autor............: Antonio Vinicius Silva Dutra
* Matricula........: 202110810
* Inicio...........: 20/09/2024
* Ultima alteracao.: 21/09/2024
* Nome.............: Nodes.java
* Funcao...........: classe responsavel por gerenciar os roteadores. 
Isso inclui as informacoes do roteador, suas conexoes, criacao e encaminhamento de pacotes,
verificacoes, etc
****************************************************************/
package model;

import java.util.ArrayList;
import control.mainControl;
import javafx.scene.control.Label;
import javafx.scene.shape.Polyline;

public class Nodes {
  private final int id;
  mainControl mainController = new mainControl();
  private ArrayList<Integer> nodeConnection = new ArrayList<>(); // conexoes do roteador
  private ArrayList<Polyline> pathConnection = new ArrayList<>(); // caminho em px desse roteador ate o outro roteador que ele conecta
  private ArrayList<Packets> packetsCreated = new ArrayList<>(); // pacotes gerados por esse roteador
  boolean receive = false;
  private ArrayList<Label> pathCost = new ArrayList<>(); // caminho em do roteador atual ate o roteador conectado a ele
  Packets packet;
  private int distance = -1;
  private int previous = -1;
  private boolean visited = false;

  public Nodes(){
    this.id = 0;
  }

  public Nodes(int id){
    this.id = id;
  }

  public int getId(){
    return id;
  }

  public void setController(mainControl mainController){
    this.mainController = mainController;
  }

  /* ******************************************************************
  * Metodo: addConnection(int connected, Polyline route)
  * Funcao: Adiciona uma conexao de roteador com outro roteador e armazena o caminho visual correspondente.
  * Parametros: 
  *  - int connected: ID do roteador conectado
  *  - Polyline route: Caminho visual que conecta os dois roteadores
  * Retorno: void
  ****************************************************************** */
  public void addConnection(int connected, Polyline route, Label peso){
    nodeConnection.add(connected);
    pathConnection.add(route);
    pathCost.add(peso);
  } //fim do metodo addConnection

  
  /* ******************************************************************
  * Metodo: getShortestPath()
  * Funcao: calcula e atualiza o menor caminho a partir do roteador atual para seus roteadores conectados. 
  Ele ajusta a distancia minima dos roteadores nao visitados, marca o roteador atual como visitado, e, 
  ao final, chama o controlador principal para encontrar o proximo roteador com a menor distancia para continuar o calculo
  * Parametros: null
  * Retorno: void
  ****************************************************************** */
  public void getShortestPath() {
    ArrayList<Nodes> aux = mainController.getNodes(); 

    if (mainController.getNodeSender() == this.id) { //se o roteador atual for ele mesmo a distancia eh zero
      distance = 0;
    }
    setVisited(true); // if roteador ja foi visitado

    processConnectedNodes(aux);

    mainController.findShorterPath(); //verifica qual a menor distancia, para calcular ela
  }//fim do metodo getShortestPath()

  /* ******************************************************************
  * Metodo: processConnectedNodes(ArrayList<Nodes> nodesList)
  * Funcao: processa todos os roteadores conectados
  * Parametros: ArrayList<Nodes> nodesList = lista de roteadores
  * Retorno: void
  ****************************************************************** */
  public void processConnectedNodes(ArrayList<Nodes> nodesList) {
    for (int connectionIndex = 0; connectionIndex < nodeConnection.size(); connectionIndex++) {
      int connectedNodeIndex = nodeConnection.get(connectionIndex) - 1;

      Nodes connectedNode = nodesList.get(connectedNodeIndex);

      // Somente processa se o roteador nao foi visitado
      if (!connectedNode.getVisited()) {
        int newDistance = getDistance() + Integer.parseInt(pathCost.get(connectionIndex).getText());

        if (newDistance < connectedNode.getDistance() || connectedNode.getDistance() == -1) {
          connectedNode.setDistance(newDistance);
          connectedNode.setPrevious(this.id); // define o predecessor
        }
      }
    }
  }//fim do metodo processConnectedNodes()
  
  /********************************************************************
  * Metodo: sendPackets(int TTL, int firstNode)
  * Funcao: Envia pacotes para os roteadores conectados, com base na versao selecionada na interface
  * Parametros: 
  *  - int TTL: Time To Live (TTL) do pacote, representando o numero de saltos restantes
  *  - int firstNode: ID do roteador que enviou o pacote originalmente
  * Retorno: void
  ****************************************************************** */
  public void sendPackets(ArrayList<Integer> Receptor){
      if(mainController.getNodeReceiver() != this.id ){
        Receptor.remove(0);
        for (int i = 0; i < nodeConnection.size(); i++) {
          if (nodeConnection.get(i) == Receptor.get(0)) {
            packet = new Packets(this.id, Receptor.get(0), pathConnection.get(i), mainController.getRoot(), mainController, Receptor);
            packet.start();
            break;
          }
        }
      } else {
        receivePacket();
      }
  } //fim do metodo sendPackets()

  /* *******************************************************************
  * Metodo: receivePacket()
  * Funcao: Registra o recebimento de um pacote e atualiza o estado de controle de recebimento
  * Parametros: Nenhum
  * Retorno: void
  ****************************************************************** */
  private void receivePacket() {
    if (!receive) {
      char letter = (char) (id + 64);
      System.out.println("Roteador [ " + Character.toString(letter) + " ] recebeu o Pacote");
      receive = true;
      mainController.packetReceived(this.id);
      mainController.setReceived(true);
    }
  } //fim do metodo receivePacket()]

  /* *******************************************************************
  * Metodo: adjustInterface()
  * Funcao: ajusta a opacidade das conexoes visuais entre os roteadores na interface grafica. 
  Ele deixa totalmente visiveis as conexoes e os pesos especificos dos caminhos relacionados ao indice fornecido ou ao predecessor do roteador atual
  * Parametros: int indice = indice dos pesos e conexoes
  * Retorno: void
  ****************************************************************** */
  public void ajustInterface(int indice) {
    for (int i = 0; i < pathConnection.size(); i++) {
      if (i == indice || nodeConnection.get(i) == previous) {
        pathConnection.get(i).setOpacity(1);
        pathCost.get(i).setOpacity(1);
      }
    }
  } //fim do metodo adjustInterface()

  /* ******************************************************************
  * Metodo: stopPackages()
  * Funcao: para todos os pacotes em transito e limpa a lista de pacotes criados
  * Parametros: nenhum
  * Retorno: void
  ****************************************************************** */
  public void stopPackages(){
    if(packet != null){
      packet.setControlFinished(false);
      packet.breakAnimation();
      packet.interrupt();
    }
    packetsCreated.clear();
  }//fim do metodo stopPackages()

  /* ******************************************************************
  * Metodo: setOpacityInterface()
  * Funcao: ajusta a opacidade de todos os caminhos e pesos associados ao roteador
  * Parametros: nenhum
  * Retorno: void
  ****************************************************************** */
  public void setOpacityInterface() {
    for (int i = 0; i < pathConnection.size(); i++) {
      pathConnection.get(i).setOpacity(0.15);
      pathCost.get(i).setOpacity(0.15);
    }
  }//fim do metodo setOpacityGui()

  public void setDistance(int distancia) {
    distance = distancia;
  }

  public int getDistance() {
    return distance;
  }

  public int getPrevious() {
    return previous;
  }

  public void setVisited(boolean visitado) {
    this.visited = visitado;
  }

  public boolean getVisited() {
    return visited;
  }
  
  public void setPrevious(int predecessor) {
    previous = predecessor;
  }

  public ArrayList<Integer> getNodeConnection() {
    return nodeConnection;
  }
}