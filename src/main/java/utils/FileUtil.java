package utils;

import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
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
        File file = new File(projectPath + "src/main/resources/" + fileName);
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

            Element numberNeuronsFieldElem = document.createElement("numberNeuronsField");
            numberNeuronsFieldElem.setAttribute("value", numberNeuronsField);
            root.appendChild(numberNeuronsFieldElem);

            Element numberBinarySignsFieldElem = document.createElement("numberBinarySignsFieldElem");
            numberBinarySignsFieldElem.setAttribute("value", numberBinarySignsField);
            root.appendChild(numberBinarySignsFieldElem);

            Element imagesElem = document.createElement("Images");
            root.appendChild(imagesElem);

            for (int i = 0; i < coefficientList.size(); i++) {
                Element imageElem = document.createElement("Image" + (i + 1));
                imagesElem.appendChild(imageElem);

                List<Integer> coefficient = coefficientList.get(i);
                for (int j = 0; j < coefficient.size(); j++) {
                    Element coef = document.createElement("coef");
                    coef.setAttribute("value", String.valueOf(coefficient.get(j)));
                    imageElem.appendChild(coef);
                }
            }


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return document;
    }

    public Map<String, Object> loadFile() {
        return new HashMap<>();
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return this.fileName;
    }
}
