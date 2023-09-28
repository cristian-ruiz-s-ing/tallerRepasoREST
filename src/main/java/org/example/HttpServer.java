package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

public class HttpServer {
    public static void main(String[] args) throws IOException, URISyntaxException {

        System.out.println("Starting Server");
        ServerSocket servidor = new ServerSocket(35000);

        Socket cliente = null;
        boolean running = true;

        while (running) {
            cliente = servidor.accept();

            /*Captura datos de entrada y salida para leer y mandar los datos*/
            PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));

            String inputLine = "";
            Boolean firstLine = true;
            String reqURI = "";

            while ((inputLine = in.readLine()) != null) {
                System.out.println("Llega: " + inputLine);
                if (firstLine) {
                    firstLine = false;
                    reqURI = inputLine.split(" ")[1];
                }
                if (!in.ready()) break;
            }

            URI uri = new URI(reqURI);
            if (uri.getPath().startsWith("/hello")) {
                out.println(getHello(uri.getQuery().split("=")[1]));
            } else if (uri.getPath().startsWith("/cos")) {
                out.println(getCos(uri.getQuery().split("=")[1]));
            }else {
                out.println(getDefaultForm());
            }

            out.close();
            in.close();
            cliente.close();

        }
        servidor.close();

    }

    private static String getCos(String s) {
        Double resp = Math.cos(Double.parseDouble(s));
        return "HTTP/1.1 200 OK \r\n"+
                "Content-Type: application/json\r\n"+
                "\r\n"+
                "{\"cos\":\"" + resp.toString() + "\"}";
    }

    private static String getHello(String value) {
        return "HTTP/1.1 200 OK \r\n"+
                "Content-Type: application/json\r\n"+
                "\r\n"+
                "{\"mensaje\":\"Hello " + value + "\"}";
    }

    private static String getDefaultForm() {
        return "HTTP/1.1 200 OK \r\n"+
                "\r\n"+
                "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "\t<title>Form Example</title>\n" +
                "\t<meta charset=\"UTF-8\">\n" +
                "\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Form with COS</h1>\n" +
                "<form action=\"/cos\">\n" +
                "\t<label for=\"value\">Valor:</label><br>\n" +
                "\t<input type=\"number\" id=\"value\" name=\"valor\" value=\"1\"><br><br>\n" +
                "\t<input type=\"button\" value=\"Submit\" onclick=\"loadCosMsg()\">\n" +
                "</form>\n" +
                "<div id=\"getcospmsg\"></div>\n" +
                "\n" +
                "<script>\n" +
                "            function loadCosMsg() {\n" +
                "                let nameVar = document.getElementById(\"value\").value;\n" +
                "                const xhttp = new XMLHttpRequest();\n" +
                "                xhttp.onload = function() {\n" +
                "                    document.getElementById(\"getcospmsg\").innerHTML =\n" +
                "                    this.responseText;\n" +
                "                }\n" +
                "                xhttp.open(\"GET\", \"/cos?value=\"+nameVar);\n" +
                "                xhttp.send();\n" +
                "            }\n" +
                "        </script>\n" +
                "<h1>Form with GET</h1>\n" +
                "<form action=\"/hello\">\n" +
                "\t<label for=\"name\">Name:</label><br>\n" +
                "\t<input type=\"text\" id=\"name\" name=\"name\" value=\"John\"><br><br>\n" +
                "\t<input type=\"button\" value=\"Submit\" onclick=\"loadGetMsg()\">\n" +
                "</form>\n" +
                "<div id=\"getrespmsg\"></div>\n" +
                "\n" +
                "<script>\n" +
                "            function loadGetMsg() {\n" +
                "                let nameVar = document.getElementById(\"name\").value;\n" +
                "                const xhttp = new XMLHttpRequest();\n" +
                "                xhttp.onload = function() {\n" +
                "                    document.getElementById(\"getrespmsg\").innerHTML =\n" +
                "                    this.responseText;\n" +
                "                }\n" +
                "                xhttp.open(\"GET\", \"/hello?name=\"+nameVar);\n" +
                "                xhttp.send();\n" +
                "            }\n" +
                "        </script>\n" +
                "\n" +
                "<h1>Form with POST</h1>\n" +
                "<form action=\"/hellopost\">\n" +
                "\t<label for=\"postname\">Name:</label><br>\n" +
                "\t<input type=\"text\" id=\"postname\" name=\"name\" value=\"John\"><br><br>\n" +
                "\t<input type=\"button\" value=\"Submit\" onclick=\"loadPostMsg(postname)\">\n" +
                "</form>\n" +
                "\n" +
                "<div id=\"postrespmsg\"></div>\n" +
                "\n" +
                "<script>\n" +
                "            function loadPostMsg(name){\n" +
                "                let url = \"/hellopost?name=\" + name.value;\n" +
                "\n" +
                "                fetch (url, {method: 'POST'})\n" +
                "                    .then(x => x.text())\n" +
                "                    .then(y => document.getElementById(\"postrespmsg\").innerHTML = y);\n" +
                "            }\n" +
                "        </script>\n" +
                "</body>\n" +
                "</html>";
    }
}