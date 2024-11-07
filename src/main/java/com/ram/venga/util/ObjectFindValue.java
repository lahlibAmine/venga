package com.ram.venga.util;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;


@Component
public class ObjectFindValue {




	    public List<String> extractValues(String xmlData, String[] fieldNames) throws Exception {
	    	try {
                //	System.out.println(xmlData);
	    		xmlData = "<root>" + xmlData + "</root>";
	            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder builder = factory.newDocumentBuilder();
	            Document document = builder.parse(new InputSource(new StringReader(xmlData)));

	            List<String> values = new ArrayList<>();
	            findValues(document.getDocumentElement(), fieldNames, 0, values);

	            return values;
	        } catch (Exception e) {
	        //	System.out.println(xmlData);
	            e.printStackTrace();
	            return new ArrayList<String>();
	            //throw new Exception("Erreur lors de l'analyse du XML.");
	        }
	    }

	    private void findValues(Node node, String[] fieldNames, int index, List<String> values) {
	        if (fieldNames == null || index >= fieldNames.length) {
	            return; // Tous les champs ont été parcourus
	        }

	        String fieldName = fieldNames[index];
	        NodeList nodeList = node.getChildNodes();

	        for (int i = 0; i < nodeList.getLength(); i++) {
	            Node childNode = nodeList.item(i);
	            if (childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equals(fieldName)) {
	                // Si c'est le dernier champ de la hiérarchie, ajoutez la valeur à la liste
	                if (index == fieldNames.length - 1) {
	                    values.add(getNodeValue(childNode));
	                } else {
	                    // Sinon, poursuivez la recherche en profondeur
	                    findValues(childNode, fieldNames, index + 1, values);
	                }
	            }
	        }
	    }
	    
	    
	    private String getNodeValue(Node node) {
	        NodeList childNodes = node.getChildNodes();

	        // Vérifiez si le nœud a des nœuds enfants autres que le texte
	        for (int i = 0; i < childNodes.getLength(); i++) {
	            Node child = childNodes.item(i);
	            if (child.getNodeType() == Node.ELEMENT_NODE) {
	                // Si le nœud a des nœuds enfants autres que du texte, renvoyez-le en tant qu'objet XML
	                return getNodeAsString(node);
	            }
	        }

	        // Sinon, renvoyez le texte en tant que chaîne de caractères
	        return node.getTextContent();
	    }
	    
	    
	    
	 // Méthode pour obtenir tout le contenu de la balise sous forme de chaîne de caractères
	    private String getNodeAsString(Node node) {
	        try {
	            TransformerFactory transformerFactory = TransformerFactory.newInstance();
	            Transformer transformer = transformerFactory.newTransformer();
	            StringWriter writer = new StringWriter();
	            
	            // Créez un nouveau document DOM contenant uniquement le nœud demandé
	            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	            Document tempDocument = docBuilder.newDocument();
	            Node importedNode = tempDocument.importNode(node, true);
	            tempDocument.appendChild(importedNode);
	            
	            // Configurez le transformer pour exclure la déclaration XML
	            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	            
	            // Transformez le nœud en une chaîne sans déclaration XML
	            transformer.transform(new DOMSource(tempDocument), new StreamResult(writer));
	            return writer.toString();
	        } catch (Exception e) {
	            e.printStackTrace();
	            return "";
	        }
	    }

	
	
}
