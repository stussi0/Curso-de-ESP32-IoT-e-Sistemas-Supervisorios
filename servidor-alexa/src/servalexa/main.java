package servalexa;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.util.logging.Level;
import java.util.logging.Logger;
//import javax.swing.JOptionPane;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;

public class main {

    static String messageString = "#RESET";

    public static void SerialLig(SerialPort comPort, boolean liga, int relay) throws FileNotFoundException {
        
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Enviando Dados...");
        OutputStream os = comPort.getOutputStream();
        System.out.println("Pegou o output stream!");
        try {
            String msg = "#"+(liga ? "LIG" : "DSL")+relay;
            System.out.println("Mensagem enviada: "+msg);
            os.write(msg.getBytes());
            os.flush();
            os.close();
            System.out.println("Aguardando os Dados de Resposta...");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {

        FileOutputStream file = new FileOutputStream("dados.txt");
        DataOutputStream data = new DataOutputStream(file);
        SerialPort coms[] = SerialPort.getCommPorts();
        String mensagem_op = "";
        for(int i = 0; i < coms.length; i++){
            mensagem_op += "Porta número: "+i+" - "+coms[i].getDescriptivePortName();
            mensagem_op += "\n";
        }
        mensagem_op += "Qual o número da porta escolhida: \n";
        //String opcao = JOptionPane.showInputDialog(mensagem_op);
        String opcao = "0";
        SerialPort comPort = SerialPort.getCommPorts()[Integer.parseInt(opcao)];
        System.out.println(comPort.getDescriptivePortName());
        comPort.setBaudRate(9600);
        comPort.openPort();
        System.out.println("Porta Aberta...");
        comPort.addDataListener(new SerialPortDataListener() {
           @Override
           public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_RECEIVED; }
           @Override
           public void serialEvent(SerialPortEvent event)
           {
              byte[] newData = event.getReceivedData();
              char[] infor = new char[newData.length];
              //System.out.println("Received data of size: " + newData.length);
              for (int i = 0; i < newData.length; ++i){
                  infor[i] = (char)newData[i];
                  System.out.print((char)newData[i]);
              }
              String msg = String.copyValueOf(infor);
               try {
                   data.writeBytes(msg);
               } catch (IOException ex) {
                   Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
               }
              //System.out.println("\n");
           }
        });

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        //server.createContext("/upload", new UploadHandler());
        //server.createContext("/access", new AccessHandler());
        server.createContext("/liga1", new LigarRelay(comPort, true, 1));
        server.createContext("/liga2", new LigarRelay(comPort, true, 2));
        server.createContext("/liga3", new LigarRelay(comPort, true, 3));
        server.createContext("/liga4", new LigarRelay(comPort, true, 4));
        server.createContext("/liga5", new LigarRelay(comPort, true, 5));
        server.createContext("/liga6", new LigarRelay(comPort, true, 6));
        server.createContext("/liga7", new LigarRelay(comPort, true, 7));
        server.createContext("/liga8", new LigarRelay(comPort, true, 8));
        server.createContext("/desliga1", new LigarRelay(comPort, false, 1));
        server.createContext("/desliga2", new LigarRelay(comPort, false, 2));
        server.createContext("/desliga3", new LigarRelay(comPort, false, 3));
        server.createContext("/desliga4", new LigarRelay(comPort, false, 4));
        server.createContext("/desliga5", new LigarRelay(comPort, false, 5));
        server.createContext("/desliga6", new LigarRelay(comPort, false, 6));
        server.createContext("/desliga7", new LigarRelay(comPort, false, 7));
        server.createContext("/desliga8", new LigarRelay(comPort, false, 8));

        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Servidor iniciado na porta 8080...");
    }

    static class LigarRelay implements HttpHandler {

        private SerialPort comPort;
        private boolean ligar;
        private int relay;

        public LigarRelay(SerialPort comPort, boolean ligar, int number){
            this.comPort = comPort;
            this.ligar = ligar;
            this.relay = number;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            SerialLig(this.comPort, this.ligar, this.relay);
            String resposta = String.format(
                "{\n" +
                "  \"version\": \"1.0\",\n" +
                "  \"response\": {\n" +
                "    \"outputSpeech\": {\n" +
                "      \"type\": \"PlainText\",\n" +
                "      \"text\": \"Ligando a porta %d.\"\n" +
                "    },\n" +
                "    \"shouldEndSession\": true\n" +
                "  }\n" +
                "}", this.relay
            );

            byte[] responseBytes = resposta.getBytes("UTF-8");
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(200, responseBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();
        }

    }

    static class UploadHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                // Recebe o arquivo enviado pelo cliente
                InputStream inputStream = exchange.getRequestBody();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder requestBody = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    requestBody.append(line);
                }

                // Armazena as informações em um arquivo JSON
                String jsonFilePath = "file.json";
                FileWriter fileWriter = new FileWriter(jsonFilePath);
                fileWriter.write(requestBody.toString());
                fileWriter.close();

                // Retorna uma resposta para o cliente
                String response = "Arquivo recebido e armazenado com sucesso.";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, 0);
            } finally {
                exchange.close();
            }
        }
    }

    static class AccessHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                // Retorna a página HTML com um formulário para enviar um arquivo
                String htmlResponse = "<html><body>" +
                        "<h1>Envie um arquivo e informações:</h1>" +
                        "<form method=\"POST\" enctype=\"multipart/form-data\" action=\"/upload\">" +
                        "Arquivo: <input type=\"file\" name=\"file\"><br>" +
                        "Informações: <input type=\"text\" name=\"info\"><br>" +
                        "<input type=\"submit\" value=\"Enviar\">" +
                        "</form>" +
                        "</body></html>";

                // Envia a resposta HTML para o cliente
                exchange.sendResponseHeaders(200, htmlResponse.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(htmlResponse.getBytes());
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, 0);
            } finally {
                exchange.close();
            }
        }
    }
    
}
