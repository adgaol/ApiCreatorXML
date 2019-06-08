/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apicreatorxml;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
/**
 *
 * @author adgao
 */
public class Writer {
    private HashMap<String, ArrayList<String>> grammar;//antecedente, producciones// the grammar with the semantics actions
    //private HashMap<String, ArrayList<String>> grammar;//antecedente, producciones//the grammar without the semantics actions
    private String path;//path of the file of the grammar
    private String pathResult;
    private Stack<String> pendChain;
    private String readChain;
    private Element espec;
    private Document doc;
    private Integer ruleCount=1;
    private ArrayList<String> antecedentes;
    private Integer numNodos=0;
    private ArrayList<Node> nodes;
    private ArrayList<Paso> steps;
    private HashMap<String, String> ruleId;//rule, id of the rule
    private Integer pasoCount=0;
    private String traductorType;
    private HashMap<Integer,Paso>stepMaps;//step id , step
    
    
    
    public Writer( String path, String pathResult, String entryChainPath, Boolean isDescendat) {
        //this.grammar = new HashMap<>();
        this.grammar = new HashMap<>();
        this.path=path;
        this.pathResult=pathResult;
        this.antecedentes=new ArrayList<>();
        this.pendChain=new Stack<>();
        this.stepMaps=new HashMap<>();
        readFile();
        readChain(entryChainPath);
        //grammarWithoutActions();
        this.ruleId=new HashMap<>();
        
        
        this.nodes=new ArrayList<>();
        this.steps=new ArrayList<>();
        if(isDescendat){
            traductorType="Descendente";
        }
        else{
            traductorType="Ascendente";
        }
        
        DocumentBuilderFactory dbFactory =DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder=null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, null, ex);
        }
        doc = dBuilder.newDocument();

        Element rootElement = doc.createElement("raiz");
        doc.appendChild(rootElement);
        this.espec = doc.createElement("espec");
        
        rootElement.appendChild(espec);
        Attr attr = doc.createAttribute("nombre");
	attr.setValue("Especificación del XML");
        espec.setAttributeNode(attr); 
        
        writeTraductor();
        
        //this.table= new Integer[this.noTerminals.size()][this.terminals.size()];
        
        
        
        
    }
    /**
     * add a new node to the node list
     * @param element
     * element of the node
     * @param terminal
     * true if is terminal false if not
     * @param nivel
     * level of the node in the tree 
     * @return 
     * the new node
     */
    public Node addNode(String element, Boolean terminal,Boolean haveBrother){
        Node node=new Node(numNodos, element, terminal, 0,haveBrother);
        nodes.add(node);
        numNodos++;
        return node;
    } 
    /**
     * add a new step to the step list
     * @param isDisplacement
     * true if is displacement false if not
     * @param elementoLeido
     * read element of the chain if exist 
     * @param element
     * element that is being processed
     * @param valor
     * value of the element
     * @param regla
     * rule of the element (only if the symbol is the first of the rule)
     * @param relNodo
     * father node
     * @return 
     * the new step
     */
    public Paso addPaso(Boolean isDisplacement,String elementoLeido,String element, String valor, String regla,HashSet<Integer> relNodo){
        Paso paso=null;
        if(elementoLeido!=null){
             readChain+=pendChain.pop()+" ";
        }
        String pendiente=writePendChain();
        if(isDisplacement && traductorType.equals("Descendente"))
            paso=new Paso(this.pasoCount, "despDes", readChain, pendiente, element, valor, relNodo, regla);
        else if(isDisplacement && !traductorType.equals("Descendente"))
            paso=new Paso(this.pasoCount, "desplazamiento", readChain, pendiente, element, valor, relNodo, regla);
        else if(!isDisplacement && traductorType.equals("Descendente"))
            paso=new Paso(this.pasoCount, "derivacion", readChain, pendiente, element, valor, relNodo, regla);
        else 
            paso=new Paso(this.pasoCount, "reduccion", readChain, pendiente, element, valor, relNodo, regla);
        steps.add(paso);
        stepMaps.put(this.pasoCount, paso);
        pasoCount++;
        return paso;
    } 
    /**
     * add a new step to the step list
     * @param isDisplacement
     * true if is displacement false if not
     * @param elementoLeido
     * read element of the chain if exist 
     * @param element
     * element that is being processed
     * @param valor
     * value of the element
     * @param regla
     * rule of the element (only if the symbol is the first of the rule)
     * @return 
     * the new step
     */
    public Paso addPaso(Boolean isDisplacement,String elementoLeido,String element, String valor, String regla){
        Paso paso=null;
        if(elementoLeido!=null){
             readChain+=pendChain.pop()+" ";
        }
        String pendiente=writePendChain();
        if(isDisplacement && traductorType.equals("Descendente"))
            paso=new Paso(this.pasoCount, "despDes", readChain, pendiente, element, valor, null, regla);
        else if(isDisplacement && !traductorType.equals("Descendente"))
            paso=new Paso(this.pasoCount, "desplazamiento", readChain, pendiente, element, valor, null, regla);
        else if(!isDisplacement && traductorType.equals("Descendente"))
            paso=new Paso(this.pasoCount, "derivacion", readChain, pendiente, element, valor, null, regla);
        else 
            paso=new Paso(this.pasoCount, "reduccion", readChain, pendiente, element, valor, null, regla);
        steps.add(paso);
        stepMaps.put(this.pasoCount, paso);
        pasoCount++;
        return paso;
    } 
    /**
     * add a new step to the step list
     * @param element
     * element that is being processed
     * @param valor
     * value of the element
     * @param regla
     * rule of the element (only if the symbol is the first of the rule)
     * @return 
     * the new step
     */
    public Paso addPasoPrimero(String element, String valor, String regla){
        String pendiente=writePendChain();
        Paso paso=new Paso(this.pasoCount, "primero", readChain, pendiente, element, valor, null, regla);
        steps.add(paso);
        stepMaps.put(this.pasoCount, paso);
        pasoCount++;
        return paso;
    } 
    /**
     * 
     * add a new step to the step list
     * @param isDisplacement
     * true if is displacement false if not
     * @param elementoLeido
     * read element of the chain if exist 
     * @param element
     * element that is being processed
     * @param valor
     * value of the element
     * @param regla
     * rule of the element (only if the symbol is the first of the rule)
     * @param relNodo
     * related nodes or parents nodes
     * @return 
     * the new step
     */
    public Paso addPaso(Boolean isDisplacement,String elementoLeido,String element, String valor, String regla,Integer relNodo){
        HashSet<Integer> relNodes=new HashSet<>();
        relNodes.add(relNodo);
        if(elementoLeido!=null){
             readChain+=pendChain.pop()+" ";
        }
        String pendiente=writePendChain();
        Paso paso=null;
        if(isDisplacement && traductorType.equals("Descendente"))
            paso=new Paso(this.pasoCount, "despDes", readChain, pendiente, element, valor, relNodes, regla);
        else if(isDisplacement && !traductorType.equals("Descendente"))
            paso=new Paso(this.pasoCount, "desplazamiento", readChain, pendiente, element, valor, relNodes, regla);
        else if(!isDisplacement && traductorType.equals("Descendente"))
            paso=new Paso(this.pasoCount, "derivacion", readChain, pendiente, element, valor, relNodes, regla);
        else 
            paso=new Paso(this.pasoCount, "reduccion", readChain, pendiente, element, valor, relNodes, regla);
        steps.add(paso);
        stepMaps.put(this.pasoCount, paso);
        pasoCount++;
        return paso;
    } 
    /**
     * write the XML
     * @return 
     * true if the operation is successful false if not
     */
    public Boolean writeXML(){
        writeArbol();
        writeContenido();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer=null;
            try {
                transformer = transformerFactory.newTransformer();
            } catch (TransformerConfigurationException ex) {
                Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(pathResult+".xml"));
            try {
                transformer.transform(source, result);
            } catch (TransformerException ex) {
                Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }

        // Output to console for testing
        StreamResult consoleResult = new StreamResult(System.out);
            try { 	
                transformer.transform(source, consoleResult);
            } catch (TransformerException ex) {
                Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        return true;
    }

 
    /**
     * remove the semantics actions of a production
     * @param production
     * production where remove the semantics actions
     * @return
     * production without actions
     */
    private String removeActions(String production){
        String[] symbols=production.split(" ");
        String result="";
        for(int i=0;i<symbols.length;i++){
            String symbol=symbols[i];
            if(!symbol.startsWith("{")){
                if(grammar.containsKey(symbol)){
                    result+=symbol+" ";
                }
                else{
                    result+=symbol.substring(0, getNumberIndex(symbol))+" ";
                }
            }
        }
        return result.substring(0, result.length()-1);
    }
    /**
     * Remove the semantics actions only
     * @param production
     * production where remove the actions
     * @return 
     * a production without the actions
     */
    private String removeOnlyActions(String production){
        String[] symbols=production.split(" ");
        String result="";
        for(int i=0;i<symbols.length;i++){
            String symbol=symbols[i];
            if(!symbol.startsWith("{")){
                result+=symbol+" ";
                
                
            }
        }
        return result.substring(0, result.length()-1);
    }
    /**
     * find the position where finnish the letters and begin the digits 
     * @param symbol
     * Symbol to find index
     * @return 
     * index of the symbol
     */
    private Integer getNumberIndex(String symbol){
        char[] letters=symbol.toCharArray();
        Integer index=letters.length;
        for(int i=0;i<letters.length;i++){
            if(Character.isDigit(letters[i])){
               index=i;
               return index;
            }
        }
        return index;
    }
    /**
     * read and save the grammar with actions semantics 
     */
    private void readFile() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"))) { //mas-accesos-servidor-nitflex.log
	            String line;
                    int contador=0;
                    br.mark(1);
                    if (br.read() != 0xFEFF)
                        br.reset();
	            while ((line = br.readLine()) != null) {
                        String[] antecedentProductions=line.split("::=");    
                        String[] productions=antecedentProductions[1].split("\\|");
                        if(!antecedentes.contains(antecedentProductions[0]))
                            antecedentes.add(antecedentProductions[0]);
                        ArrayList<String> productionsList = new ArrayList<>();
                        grammar.put(antecedentProductions[0], productionsList);
                        for(int i=0;i<productions.length;i++){
                            String production=productions[i];
                            productionsList.add(production);
                        }
                        contador++;
                    }
        }
        catch (IOException e) {
	    e.printStackTrace();
	}
    }
         
       
    /**
    * write the part of <traductor> and <cadena> in a xml
    */
    private void writeTraductor() {
        Element traductor = doc.createElement("traductor");
        espec.appendChild(traductor); 
        Element tipo= doc.createElement("tipo");
        traductor.appendChild(tipo);
        tipo.setTextContent(traductorType);
        for(String antecedent:antecedentes){
            for(String production:grammar.get(antecedent)){
              addRule(antecedent,production,traductor);  
            }
        }
        Element cadena = doc.createElement("cadena");
        espec.appendChild(cadena);
        cadena.setTextContent(readChain+writePendChain());
    }
    /**
     * write a rule in xml
     * @param antecedent
     * antecendent of the rule
     * @param production
     * production of the antecedent to write
     * @param traductor 
     * element where to add the rule in the xml
     */
    private void addRule(String antecedent ,String production,Element traductor) {
        String id="R"+ruleCount;
        ruleId.put(antecedent+"::="+removeOnlyActions(production), id);
        Element regla=doc.createElement("regla");
        traductor.appendChild(regla);
        Attr attrRegla = doc.createAttribute("id");
	attrRegla.setValue(id);
        regla.setAttributeNode(attrRegla);
        ArrayList<String> actions=actions(production);
        for(String action:actions){
            Element actionXml=doc.createElement("accionSemantica");
            regla.appendChild(actionXml);
            Integer pos=getPos(action,production);
            Attr attrAccion = doc.createAttribute("pos");
            attrAccion.setValue(pos.toString());
            actionXml.setAttributeNode(attrAccion);
            actionXml.setTextContent(action.substring(1,action.length()-1));
            if(pos<removeActions(production).split(" ").length){
                Element intermedio=doc.createElement("intermedio");
                actionXml.appendChild(intermedio);
                intermedio.setTextContent("si");
                
            }
            
        }
        addSymbols(production,regla,antecedent);
        ruleCount++;
    }
    /**
     * group the actions of one production
     * @param production
     * production where the actions are
     * @return 
     * A ArrayList with the actions
     */
    private ArrayList<String> actions(String production) {
        ArrayList<String> result=new ArrayList<>();
        String[] actions=production.split(" ");
        for(int i=1;i<actions.length;i++){
            if(actions[i].contains("{")){
                result.add(actions[i]);
            }
                
        }
        return result;
    }
    /**
     * obtain the position of the action in the production
     * @param action
     * action to find
     * @param production
     * production where find
     * @return 
     * the index of the symbol before the action
     */
    private Integer getPos(String action, String production) {
        Integer pos=0;
        int i=0;
        String[] symbols=production.split(" ");
        while(!symbols[i].equals(action)){
           
            if(!symbols[i].contains("{"))
                pos++;
            i++;
        }
        return pos;
    }
    /**
     * write the symbols of one rule in the xml
     * @param production
     * production where the symbols are
     * @param regla
     * element where to add the symbols in the xml
     * @param antecedente 
     * antecedent of the production
     */
    private void addSymbols(String production, Element regla,String antecedente) {
        String[] symbols=production.split(" ");
        for(int i=0;i<symbols.length;i++){
            if (i==0){
                Element simbolo=doc.createElement("simbolo");
                regla.appendChild(simbolo);
                Element valor=doc.createElement("valor");
                simbolo.appendChild(valor);
                if(grammar.get(antecedente).get(0).equals(production))
                    valor.setTextContent(antecedente+"::=");
                else
                    valor.setTextContent("|");
                Element terminal=doc.createElement("terminal");
                simbolo.appendChild(terminal);
                
                terminal.setTextContent("false");
                
            }
            else if(!symbols[i].contains("{")){
                Element simbolo=doc.createElement("simbolo");
                regla.appendChild(simbolo);
                Element valor=doc.createElement("valor");
                simbolo.appendChild(valor);
                valor.setTextContent(symbols[i]);
                Element terminal=doc.createElement("terminal");
                simbolo.appendChild(terminal);
                if(Character.isUpperCase(symbols[i].charAt(0)))
                    terminal.setTextContent("false");
                else
                    terminal.setTextContent("true");
            }
        }
    }
    /**
     * write the part of <arbol> of the xml 
     */
    private void writeArbol() {
       Integer altura=updateNode();
       Element arbol = doc.createElement("arbol");
       espec.appendChild(arbol);
       Element numNodosE = doc.createElement("num_nodos");
       arbol.appendChild(numNodosE);
       numNodosE.setTextContent(nodes.size()+"");
       Element alturaE = doc.createElement("altura");
       arbol.appendChild(alturaE);
       alturaE.setTextContent(altura.toString());
       for(Node node:nodes){
           Element nodo = doc.createElement("nodo");
           Attr id = doc.createAttribute("id");
           id.setValue(node.getId()+"");
           nodo.setAttributeNode(id);
           arbol.appendChild(nodo);
           Element element = doc.createElement("elemento");
           nodo.appendChild(element);
           element.setTextContent(node.getElement());
           Element level = doc.createElement("nivel");
           nodo.appendChild(level);
           level.setTextContent(node.getNivel().toString());
           Element terminal = doc.createElement("terminal");
           nodo.appendChild(terminal);
           terminal.setTextContent(node.getTerminal().toString());
       }
    }
  
   
   
    /**
     * write the part of <contenido> of the xml
     */
    private void writeContenido( ) {
        Element contenido = doc.createElement("contenido");
        espec.appendChild(contenido);
        for(Paso step:steps){
            Element pasoE = doc.createElement("paso");
            Attr id = doc.createAttribute("id");
            id.setValue(step.getId()+"");
            pasoE.setAttributeNode(id);
            contenido.appendChild(pasoE);
            Element tipo = doc.createElement("tipo");
            tipo.setTextContent(step.getTipo());
            pasoE.appendChild(tipo);
            if(step.getRegla()!=null){
                Element nuevaRegla = doc.createElement("nuevaRegla");
                Attr refRegla = doc.createAttribute("refRegla");
                refRegla.setValue(ruleId.get(step.getRegla()));
                nuevaRegla.setTextContent(step.getRegla());
                nuevaRegla.setAttributeNode(refRegla); 
                pasoE.appendChild(nuevaRegla); 
                
            }

            Element cadena = doc.createElement("cadena");
            pasoE.appendChild(cadena);
            Element leido = doc.createElement("leido");
            leido.setTextContent(step.getLeido());
            cadena.appendChild(leido);
            Element pendiente = doc.createElement("pendiente");
            pendiente.setTextContent(step.getPendiente());
            cadena.appendChild(pendiente);
            Element elemento = doc.createElement("elemento");
            elemento.setTextContent(step.getElemento());
            pasoE.appendChild(elemento);
            if(step.getRelNodo()!=null){
                Element relNodos = doc.createElement("relNodos");
                String relNodes=writeRelNodes(step.getRelNodo());
                relNodos.setTextContent(relNodes);
                pasoE.appendChild(relNodos);
            }
            Element valor = doc.createElement("valor");
            valor.setTextContent(step.getValor());
            pasoE.appendChild(valor);
            if(!step.getChangedNodes().isEmpty()){
                Element accionSemanticaEjecutada = doc.createElement("accionSemanticaEjecutada");
                pasoE.appendChild(accionSemanticaEjecutada);
                for(int i=0;i<step.getChangedNodes().size();i++){
                    Element nodo = doc.createElement("nodo");
                    accionSemanticaEjecutada.appendChild(nodo);
                    Element refNodo = doc.createElement("refNodo");
                    refNodo.setTextContent(step.getChangedNodes().get(i).toString());
                    nodo.appendChild(refNodo);
                    Element atributos = doc.createElement("atributos");
                    atributos.setTextContent(step.getChanges().get(i));
                    nodo.appendChild(atributos);
                    
                }
            }
        }
    }
    /**
     * transform the HashSet relNode into a String
     * @param relNodo
     * Hashset to transform
     * @return 
     * the String of relational nodes
     */
    private String writeRelNodes(HashSet<Integer> relNodo) {
        String result="";
        for(Integer node:relNodo){
            result+=" "+node;
        }
        return result.substring(1);
    }
    /**
     * read the chain
     * @param entryChainPath
     * path of the file .txt with the chain
     */
    private void readChain(String entryChainPath) {
       readChain="";
       try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(entryChainPath), "UTF-8"))) { 
	            String line;
                    int contador=0;
	            while ((line = br.readLine()) != null) {
                        String[] chainElements=line.split(" ");
                        for(int i=chainElements.length-1;i>=0;i--){
                            pendChain.push(chainElements[i]);
                        }
                    }
        }
        catch (IOException e) {
	    e.printStackTrace();
	} 
    }
    /**
     * transform the pendingChain Stack into a String 
     * @return 
     * pending chain in form of String
     */
    private String writePendChain() {
        String result="";
        for(String elem:pendChain){
            result=elem+" "+result;
        }
        return result.substring(0,result.length());
    }

    public Integer getPasoCount() {
        return pasoCount;
    }
    /**
     * update the level in the sintactic tree of the nodes
     * @return 
     * the heigth of the sintactic tree
     */
    private Integer updateNode(){
        Paso raiz;
        if(this.traductorType.equals("Ascendente")) 
            raiz=steps.get(steps.size()-1);
        else
            raiz=steps.get(0);
        Integer altura=1;
        for(Node nodo:nodes){
            Integer nivel=distanciaARaiz(raiz,nodo);
            nodo.setNivel(nivel);
            altura=Math.max(altura, nivel);
        }
        return altura;
    }
    /**
     * Calculate the distance between the node and the root of the sintactic tree
     * @param raiz
     * root of the sintactic tree
     * @param objetivo
     * node to start
     * @return 
     * distance between the node and the root of the sintactic tree in form of Integer
     */
    private Integer distanciaARaiz(Paso raiz, Node objetivo) {
        Integer i=1;
        Node actual=objetivo;
        while (!actual.getId().equals(raiz.getId())){
            i++;
            actual=actual.getFatherNode();
        }
        return i;
        
    }
    /**
     * return the step corresponding with the id 
     * @param id
     * id of the step
     * @return 
     * step 
     */
    public Paso getStep(Integer id){
        return stepMaps.get(id);
        
    }
    public HashMap<String, ArrayList<String>> getGrammar() {
        return grammar;
    }

    public String getPath() {
        return path;
    }

    public String getPathResult() {
        return pathResult;
    }

    public Element getEspec() {
        return espec;
    }

    public Document getDoc() {
        return doc;
    }

    public Integer getRuleCount() {
        return ruleCount;
    }

    public Integer getNumNodos() {
        return numNodos;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public ArrayList<Paso> getSteps() {
        return steps;
    }

    public String getTraductorType() {
        return traductorType;
    }

    
    
}
