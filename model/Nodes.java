/* ***************************************************************
* Autor............: Antonio Vinicius Silva Dutra
* Matricula........: 202110810
* Inicio...........: 27/08/2024
* Ultima alteracao.: 08/08/2024
* Nome.............: Nodes.java
* Funcao...........: classe responsavel por gerenciar os roteadores. 
Isso inclui as informacoes do roteador, suas conexoes, criacao e replicacao de pacotes,
verificacoes, etc
****************************************************************/

package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import control.mainControl;
import javafx.scene.control.Label;
import javafx.scene.shape.Polyline;

public class Nodes {
  private final int id;
  mainControl mainController = new mainControl();
  private ArrayList<Integer> nodeConnection = new ArrayList<>(); // conexoes do roteador
  private ArrayList<Polyline> pathConnection = new ArrayList<>(); // caminho em px desse roteador ate o outro roteador que ele conecta
  private ArrayList<Packets> packetsCreated = new ArrayList<>(); // pacotes gerados por esse roteador
  boolean controlRecebimento = false;
  private Map<Integer, Boolean> routingTable = new HashMap<>();
  private ArrayList<Label> pesosCaminho = new ArrayList<>(); // Caminho em px desse roteador ate o No que ele conecta
  Packets packet;
  private int distance = -1;
  private int Predecessor = -1;
  private boolean visitado = false;

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
    pesosCaminho.add(peso);
  } //fim do metodo addConnection

  public void getShortestPath() {
    ArrayList<Nodes> aux = mainController.getNodes(); 

    if (mainController.getNodeSender() == this.id) {
      distance = 0;
    }
    setVisitado(true); // if roteador ja foi visitado

    for (int i = 0; i < nodeConnection.size(); i++) { //buscando prox roteador
      int index = nodeConnection.get(i) - 1; // obtem o roteador que esse roteador conecta

      if (!aux.get(index).getVisitado()) { // verifica se o roteador ja foi visitado

        int distanceFinal = getDistance() + Integer.parseInt(pesosCaminho.get(i).getText());

        // Verifica se a distancia no prox no eh maior, caso seja, essa menor eh setada
        if (aux.get(index).getDistance() > distanceFinal || aux.get(index).getDistance() == -1) {
          aux.get(index).setDistance(distanceFinal);
          aux.get(index).setPredecessor(this.id);// Seta o Pai dele no menor caminho
        }
      }
    }
    mainController.findShorterPath(); // Verifica qual a menor distancia, para calcular ela
  }

  
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
    if (!controlRecebimento) {
      char letter = (char) (id + 64);
      System.out.println("Roteador [ " + Character.toString(letter) + " ] recebeu o Pacote");
      controlRecebimento = true;
      mainController.packetReceived(this.id);
      mainController.setReceived(true);
    }
  } //fim do metodo receivePacket()

  /* *******************************************************************
  * Metodo: resetRoutingTable()
  * Funcao: Reseta a tabela de roteamento limpando todas as entradas
  * Parametros: Nenhum
  * Retorno: void
  ****************************************************************** */
  public void resetRoutingTable() {
    routingTable.clear();
  } //fim do metodo resetRoutingTable()

  public void ajustInterface(int indice) {
    for (int i = 0; i < pathConnection.size(); i++) {
      if (i == indice || nodeConnection.get(i) == Predecessor) {
        pathConnection.get(i).setOpacity(1);
        pesosCaminho.get(i).setOpacity(1);
      }
    }
  }

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

  public void setOpacityGui() {
    for (int i = 0; i < pathConnection.size(); i++) {
      pathConnection.get(i).setOpacity(0.15);
      pesosCaminho.get(i).setOpacity(0.15);
    }
  }

  public void setDistance(int distancia) {
    distance = distancia;
  }

  public int getDistance() {
    return distance;
  }

  public int getPredecessor() {
    return Predecessor;
  }

  public void setVisitado(boolean visitado) {
    this.visitado = visitado;
  }

  public boolean getVisitado() {
    return visitado;
  }
  
  public void setPredecessor(int predecessor) {
    Predecessor = predecessor;
  }

  public ArrayList<Integer> getNodeConnection() {
    return nodeConnection;
  }
}