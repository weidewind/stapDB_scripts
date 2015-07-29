package scripts;

import javax.xml.transform.*;
import java.net.*;
import java.io.*;

public class HowToXSLT {
public static void main(String[] args) {
  try {

    TransformerFactory tFactory = TransformerFactory.newInstance();

    Transformer transformer =
      tFactory.newTransformer
         (new javax.xml.transform.stream.StreamSource
            ("C:/Users/weidewind/Documents/CMD/XML/stap.xsl"));

    transformer.transform
      (new javax.xml.transform.stream.StreamSource
            ("C:/Users/weidewind/Documents/CMD/XML/results.xml"),
       new javax.xml.transform.stream.StreamResult
            ( new FileOutputStream("C:/Users/weidewind/Documents/CMD/XML/javaresults.html")));
    }
  catch (Exception e) {
    e.printStackTrace( );
    }
  }
}