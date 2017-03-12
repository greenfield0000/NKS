package utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtil {

    private String fileName;
    private String projectPath;
    private DocumentBuilderFactory factory;
    private Document document;

    public FileUtil() throws ParserConfigurationException {
        projectPath = System.getProperty("user.dir");
        factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        document = factory.newDocumentBuilder().newDocument();
    }


    /**
     * @param numberNeuronsField     - count neuron
     * @param numberBinarySignsField - count binarySignsField
     * @param coefficientList        - all Images
     * @throws IOException
     */
    public void saveFile(String numberNeuronsField, String numberBinarySignsField,
                         List<List<Integer>> coefficientList) throws IOException {
        File file = new File(projectPath + "/src/main/resources/" + fileName);
        if (!file.exists()) {
            file.createNewFile();
        }

        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "true");
            transformer.transform(
                    new DOMSource(formDocument(numberNeuronsField, numberBinarySignsField, coefficientList)),
                    new StreamResult(file));
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }

    private Document formDocument(String numberNeuronsField, String numberBinarySignsField,
                                  List<List<Integer>> coefficientList) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            document = factory.newDocumentBuilder().newDocument();

            Element root = document.createElement("root");
            root.setAttribute("xmlns", "http://www.javacore.ru/schemas/");
            document.appendChild(root);

            Element numberNeuronsFieldElem = document.createElement("numberNeuronsFieldElem");
            numberNeuronsFieldElem.setTextContent(numberNeuronsField);
            root.appendChild(numberNeuronsFieldElem);

            Element numberBinarySignsFieldElem = document.createElement("numberBinarySignsFieldElem");
            numberBinarySignsFieldElem.setTextContent(numberBinarySignsField);
            root.appendChild(numberBinarySignsFieldElem);

            Element imagesElem = document.createElement("Images");
            root.appendChild(imagesElem);

            for (int i = 0; i < coefficientList.size(); i++) {
                Element imageElem = document.createElement("Image");
                imageElem.setAttribute("id", String.valueOf(i));
                imagesElem.appendChild(imageElem);

                List<Integer> coefficient = coefficientList.get(i);
                for (int j = 0; j < coefficient.size(); j++) {
                    Element coef = document.createElement("coef");
                    coef.setAttribute("id", String.valueOf(j));
                    coef.setTextContent(String.valueOf(coefficient.get(j)));
                    imageElem.appendChild(coef);
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return document;
    }

    public Map<String, Object> loadFile() {
        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("isSuccess", false);

        File file = new File(projectPath + "/src/main/resources/" + fileName);

        if ((file == null) || (!file.exists())) return resMap;

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(file);
            document.getDocumentElement().normalize();

            Node numberNeuronsFieldNode = document
                    .getDocumentElement()
                    .getElementsByTagName("numberNeuronsFieldElem").item(0);
            resMap.put("numberNeuronsField", numberNeuronsFieldNode.getTextContent());

            Node numberBinarySignsFielNode = (Node) document
                    .getDocumentElement()
                    .getElementsByTagName("numberBinarySignsFieldElem").item(0);
            resMap.put("numberBinarySignsField", numberBinarySignsFielNode.getTextContent());

            NodeList nodeImageList = document.getElementsByTagName("Image");

            // Создаем хранилище для списка коэффициентов.
            List<List<Integer>> coefficientList = new ArrayList<>();

            for (int i = 0; i < nodeImageList.getLength(); i++) {
                Node imageNode = nodeImageList.item(i);
                List<Integer> coefficient = new ArrayList<>();

                if (imageNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) imageNode;
                    NodeList coefNode = eElement.getElementsByTagName("coef");
                    for (int j = 0; j < coefNode.getLength(); j++) {
                        coefficient.add(Integer.valueOf(coefNode.item(j).getTextContent()));
                    }
                }
                coefficientList.add(coefficient);
            }
            resMap.put("coefficientList", coefficientList);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        resMap.put("isSuccess", true);
        return resMap;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return this.fileName;
    }
}
