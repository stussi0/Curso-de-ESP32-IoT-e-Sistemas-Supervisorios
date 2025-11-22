package comunicador;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Iterator;

public class DataAquisition {
  public static void main(String[] args) throws FileNotFoundException {
    Scanner scanner = new Scanner(System.in);
    FileOutputStream file = new FileOutputStream("dados.txt");
    DataOutputStream data = new DataOutputStream(file);
    SerialPort coms[] = SerialPort.getCommPorts();
    String mensagem_op = "";
    for(int i = 0; i < coms.length; i++){
      mensagem_op += "Porta número: "+i+" - "+coms[i].getDescriptivePortName();
      mensagem_op += "\n";
    }
    mensagem_op += "Qual o número da porta escolhida: \n";
    System.out.println(mensagem_op);
    String opcao = scanner.nextLine();
    SerialPort comPort = SerialPort.getCommPorts()[Integer.parseInt(opcao)];
    System.out.println(comPort.getDescriptivePortName());
    comPort.setBaudRate(9600);
    comPort.openPort();
    UartListener uart = new UartListener(data);
    comPort.addDataListener(uart);
  }
}

class UartListener implements SerialPortDataListener{
  public LinkedList<Character> uartMsg;
  public DataOutputStream data;
  private StringBuilder buffer = new StringBuilder();
  
  public UartListener(DataOutputStream data){
    this.uartMsg = new LinkedList<>();
    this.data = data;
  }
  
  @Override
  public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_RECEIVED; }
  
  @Override
  public void serialEvent(SerialPortEvent event){
    byte[] newData = event.getReceivedData();
    char[] infor = new char[newData.length];
    
    for (int i = 0; i < newData.length; i++) {
      infor[i] = (char)newData[i];
      System.out.print((char)newData[i]);
      
      // Adiciona cada caractere ao buffer para processamento
      processCharacter((char)newData[i]);
    }
    
    //String msg = String.copyValueOf(infor);
    for(int i = 0; i < infor.length; i++) uartMsg.add(infor[i]);
    
    if(uartMsg.size() > 100){
      char[] msgArray = new char[uartMsg.size()];
      Iterator<Character> uart = uartMsg.iterator();
      int inc = 0;
      while (uart.hasNext()) {
        msgArray[inc] = uart.next().charValue();
        inc++;
      }
      String msg = String.copyValueOf(msgArray);
      try { data.writeBytes(msg); } catch (IOException ex) {
        Logger.getLogger(DataAquisition.class.getName()).log(Level.SEVERE, null, ex);
      }
      uartMsg.clear();
    }
  }
  
  private void processCharacter(char c) {
    buffer.append(c);
    
    // Verifica se o buffer contém a sequência especial "@#$"
    if (buffer.length() >= 3) {
      int markerIndex = buffer.indexOf("@#$");
      
      if (markerIndex != -1) {
        // Verifica se temos pelo menos 8 caracteres após o marcador (3 do marcador + 5 do comando)
        if (buffer.length() >= markerIndex + 8) {
          String comando = buffer.substring(markerIndex + 3, markerIndex + 8);
          
          // Processa o comando
          processarComando(comando);
          
          // Remove a sequência processada do buffer
          buffer.delete(markerIndex, markerIndex + 8);
        }
      }
      
      // Limita o tamanho do buffer para evitar crescimento excessivo
      if (buffer.length() > 50) {
        buffer.delete(0, buffer.length() - 50);
      }
    }
  }
  
  private void processarComando(String comando) {
    System.out.println("\nComando recebido: " + comando);
    
    switch (comando) {
      case "REFRI":
        System.out.println("Executando comando REFRI");
        Executar.refrigerante();
        break;
      case "SALGA":
        System.out.println("Executando comando SALGA");
        Executar.salgadinho();
        break;
      case "BALAS":
        System.out.println("Executando comando BALAS");
        Executar.balas();
        break;
      default:
        System.out.println("Comando desconhecido: " + comando);
        break;
    }
  }
}

// Classe Executar com os métodos necessários
class Executar {
  public static void refrigerante() {
    Process pr= null;
    Runtime rt = null;
    try {
      rt = Runtime.getRuntime();
      pr = rt.exec("timeout 5s eog paineis_refri.jpg -f");
    } catch (IOException e) {
      System.out.println("Excessão!");
    }
    System.out.println("Método refrigerante() executado");
  }
  
  public static void salgadinho() {
    Process pr= null;
    Runtime rt = null;
    try {
      rt = Runtime.getRuntime();
      pr = rt.exec("timeout 5s eog paineis_salgadinho.jpg -f");
    } catch (IOException e) {
      System.out.println("Excessão!");
    }
    System.out.println("Método salgadinho() executado");
  }
  
  public static void balas() {
    Process pr= null;
    Runtime rt = null;
    try {
      rt = Runtime.getRuntime();
      pr = rt.exec("timeout 5s eog paineis_balas.jpg -f");
    } catch (IOException e) {
      System.out.println("Excessão!");
    }
    System.out.println("Método balas() executado");
  }
}