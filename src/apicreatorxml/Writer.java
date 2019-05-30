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
import java.util.Set;
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
    private HashMap<String, ArrayList<String>> grammarWithActions;//antecedente, producciones// the grammar with the semantics actions
    //private HashMap<String, ArrayList<String>> grammar;//antecedente, producciones//the grammar without the semantics actions
    private String path;//path of the file of the grammar
    private String pathResult;
    private String entryChain;
    private Element espec;
    private Document doc;
    private Integer ruleCount=1;
    private ArrayList<String> antecedentes;
    private Integer numNodos=0;
    private ArrayList<Node> nodes;
    private ArrayList<Paso> steps;
    private HashMap<String, String> ruleId;//rule, id of the rule
    private Integer pasoCount=0;
    
    
    
    
    
    public Writer( String path, String pathResult, String entryChain) {
        //this.grammar = new HashMap<>();
        this.grammarWithActions = new HashMap<>();
        this.path=path;
        this.pathResult=pathResult;
        this.antecedentes=new ArrayList<>();
        readFile();
        //grammarWithoutActions();
        this.ruleId=new HashMap<>();
        
        this.entryChain=entryChain;
        this.nodes=new ArrayList<>();
        this.steps=new ArrayList<>();
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
    public Node addNode(String element, Boolean terminal, Integer nivel){
        Node node=new Node(numNodos, element, terminal, nivel);
        nodes.add(node);
        numNodos++;
        return node;
    } 
    /**
     * add a new step to the step list
     * @param tipo
     * tipe of the step
     * @param leido
     * read chain
     * @param pendiente
     * pend chain
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
    public Paso addPaso(String tipo,String leido, String pendiente,String element, String valor, String regla,Integer relNodo){
        Paso paso=new Paso(this.pasoCount, tipo, leido, pendiente, element, valor, relNodo, regla,1);
        steps.add(paso);
        pasoCount++;
        return paso;
    } 
    /**
     * write the XML
     * @return 
     * true if the operation is successful false if not
     */
    public Boolean writeXML(){
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
     * Process the entry chain
     * @param entryChain 
     * chain to process
     */
//    public void proccess(String entryChain, String pathResult){
//        this.entryChain=entryChain;
//        
//        DocumentBuilderFactory dbFactory =DocumentBuilderFactory.newInstance();
//        DocumentBuilder dBuilder=null;
//        try {
//            dBuilder = dbFactory.newDocumentBuilder();
//        } catch (ParserConfigurationException ex) {
//            Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        doc = dBuilder.newDocument();
//
//        Element rootElement = doc.createElement("raiz");
//        doc.appendChild(rootElement);
//        Element espec = doc.createElement("espec");
//        rootElement.appendChild(espec);
//        Attr attr = doc.createAttribute("nombre");
//	attr.setValue("Especificación del XML");
//        espec.setAttributeNode(attr); 
//        writeTraductor(espec);
//        Stack<String> stack=new Stack<>();
//        stack.push("$");
////        stack.push("$");
//        stack.push(axioma);
//        nivel=1;
////        Node axiom=new Node(numNodos,axioma , false, nivel);
////        numNodos++;
////        nodes.add(axiom);
//        Stack<String> stackChain=new Stack<>();
//        stackChain.push("$");
//        String stackChainRead="";
////        Stack<String> stackChainRead=new Stack<>();
////        stackChain.push("$");
//        //String[] chain=transformChain(entryChain).split(" ");
//        String[] chain=entryChain.split(" ");
//        for(int i=chain.length-1;i>=0;i--){
//            stackChain.push(chain[i]);
//        }
//        int count=0;
//        boolean aximaFinish=false;
//        while(!stack.isEmpty()){
//            
//            if(stack.peek().equals("$")&&stackChain.peek().equals("$")){
//                stack.pop();
//            }
//            else{
//                if(stack.peek().equals("#")){
//                    stack.pop();
//                    nivel--;
//                }
//                else{
//                    String pendChain=representChain(stackChain);
//                    if(!stackChain.isEmpty()&&Character.isDigit(stackChain.peek().charAt(0))){
//                        Integer value=Integer.parseInt(stackChain.pop());
//                        Stack<Integer> aux=values.get("num.vlex");
//                        if(values.get("num.vlex")==null){
//                            aux=new Stack<>();
//                            values.put("num.vlex", aux);
//
//                        }
//
//                        aux.push(value);
//                        stackChain.push("num");
//                    }
//                    if(!stackChain.isEmpty()&&stack.peek().equals(stackChain.peek())){
//                        String element=stack.pop();
//                        Paso p=null;
//                        String idRegla=null;
//                        Integer widthRegla=null;
//                        if(element.equals(idsRules.get(symbolRules.get(element).peek()).split(" ")[1])){
//                          idRegla=symbolRules.get(element).pop();
//                          widthRegla=idsRules.get(idRegla).length();  
//                        }
//                        if(element.equals("num")){
//                            stackChainRead+=" "+values.get("num.vlex").peek().toString();
//                            stackChain.pop();
//                            String pendingChain= representChain(stackChain);
//                            p=new Paso(paso, "despDes", stackChainRead.substring(1),pendingChain.substring(0,pendingChain.length()-2 ), element,"num.vlex="+ values.get("num.vlex").peek().toString(),relNodes.get(element).pop(), idRegla, widthRegla);
//                        }
//                        else{
//                            stackChainRead+=" "+stackChain.pop();
//                            String pendingChain= representChain(stackChain);
//                            p=new Paso(paso, "despDes", stackChainRead.substring(1), pendingChain.substring(0,pendingChain.length()-2 ), element, null,relNodes.get(element).pop(), idRegla, widthRegla);
//                        }
//
//
//                        Node node=new Node(numNodos,element , true, nivel);
//                        numNodos++;
//                        nodes.add(node);
//
//                        //Paso p=new Paso(paso, "despDes", stackChainRead.substring(1), representChain(stackChain), element, values.get("num.vlex").toString(),relNodes.get(element), null, null);
//
//                        steps.add(p);
//                        paso++;
//
//                    }
//                    else{
//                        if(!stack.peek().startsWith("{")){
//
//                            if(stack.peek().equals("λ")){
//                                String element=stack.pop();
//
//                                Node node=new Node(numNodos,element , true, nivel);
//                                numNodos++;
//                                nodes.add(node);
//                                String idRegla=null;
//                                Integer widthRegla=null;
//                                if(element.equals(idsRules.get(symbolRules.get(element).peek()).split(" ")[1])){
//                                    idRegla=symbolRules.get(element).pop();
//                                    widthRegla=idsRules.get(idRegla).length();  
//                                }
//                                String pendingChain=representChain(stackChain);
//                                Paso p=new Paso(paso, "derivacion", stackChainRead.substring(1), pendingChain.substring(0,pendingChain.length()-2 ), element, null, relNodes.get(element).pop(), idRegla, widthRegla);
//
//                                steps.add(p);
//                                paso++;
//                            }
//                            else{
//                                count=0;
//                                if(!grammarWithActions.containsKey(stack.peek())){
//                                    String symbol=stack.pop();
//                                    Integer index=getNumberIndex(symbol);
//                                    String element=symbol.substring(0, index);
//                                    stack.push(element);
//
//                                    Integer num=table.get(stack.pop()).get(stackChain.peek());
//                                    String production=numRules.get(num);
//                                    String[] symbols=production.split(" ");
//                                    stack.push("#");
//                                    
//                                    for(int i=symbols.length-1;i>0;i--){
//                                        if(symbols[i].startsWith("{")){
//                                            symbols[i]=symbols[i]+symbol;
//                                        }
//                                        stack.push(symbols[i]);
//                                        Stack<Integer> relNodeSim=relNodes.get(symbols[i]);
//                                        if(relNodeSim==null){
//                                            relNodeSim=new Stack<>();
//                                            relNodes.put(symbols[i],relNodeSim);
//                                        }
//                                        relNodeSim.push(paso);
//                                        Stack<String> ids=null;
//                                        if(symbolRules.get(symbols[i])==null){
//                                            ids=new Stack<>();
//                                            symbolRules.put(symbols[i], ids);
//                                        }
//                                        else{
//                                            ids=symbolRules.get(symbols[i]);
//                                        }
//                                        ids.push(ruleId.get(production));
//                                    }
//
//                                    nivel++;
//                                    altura=Math.max(altura, nivel);
//                                    Node node=new Node(numNodos,symbol , false, nivel-1);
//                                    numNodos++;
//                                    nodes.add(node);
//                                    String idRegla=null;
//                                    Integer widthRegla=null;
//                                    if(symbol.equals(idsRules.get(symbolRules.get(symbol).peek()).split(" ")[1])){
//                                        idRegla=symbolRules.get(symbol).pop();
//                                        widthRegla=idsRules.get(idRegla).length();  
//                                    }
//                                    String readChain=null;
//                                    if(!stackChainRead.isEmpty())
//                                            readChain=stackChainRead.substring(1);
//                                    if(!values.containsKey(element+".result")){
//                                        Stack<Integer> aux=new Stack<>();
//                                        values.put(element+".result", aux);
//                                        aux.push(null);
//                                    }
//                                    String value=getValues(element);
//                                    if (value==null)
//                                        value=symbol+".result=null";
//
//    //                                if(values.get(symbol)!=null)
//    //                                    value=values.get(symbol).toString();
//                                    String pendingChain=representChain(stackChain);
//                                    Paso p=new Paso(paso, "derivacion", readChain,pendingChain.substring(0,pendingChain.length()-2 ), symbol, value, relNodes.get(symbol).pop(), idRegla, widthRegla);
//
//                                    steps.add(p);
//                                    paso++;
//
//                                }
//                                else{
//                                    String element=stack.pop();
//                                    Integer num=table.get(element).get(stackChain.peek());
//                                    String production=numRules.get(num);
//                                    String[] symbols=production.split(" ");
//                                    stack.push("#");
//                                    for(int i=symbols.length-1;i>0;i--){
//                                        if(symbols[i].startsWith("{")){
//                                            symbols[i]=symbols[i]+element;
//                                        }
//                                        stack.push(symbols[i]);
//                                        Stack<Integer> relNodeSim=relNodes.get(symbols[i]);
//                                        if(relNodeSim==null){
//                                            relNodeSim=new Stack<>();
//                                            relNodes.put(symbols[i],relNodeSim);
//                                        }
//                                        relNodeSim.push(paso);
//                                        Stack<String> ids=null;
//                                        if(symbolRules.get(symbols[i])==null){
//                                            ids=new Stack<>();
//                                            symbolRules.put(symbols[i], ids);
//                                        }
//                                        else{
//                                            ids=symbolRules.get(symbols[i]);
//                                        }
//                                        ids.push(ruleId.get(production));
//                                    }
//
//                                    nivel++;
//                                    altura=Math.max(altura, nivel);
//                                    Node node=new Node(numNodos,element , false, nivel-1);
//                                    numNodos++;
//                                    nodes.add(node);
//                                    Paso p=null;
//                                    if(!aximaFinish && axioma.equals(element)){
//                                        String pendingChain=representChain(stackChain);
//                                        p=new Paso(paso, "primero", null,pendingChain.substring(0,pendingChain.length()-2 ) , element, null, null, null, null);
//                                        aximaFinish=true;
//                                    }
//                                    else{
//                                        String idRegla=null;
//                                        Integer widthRegla=null;
//                                        if(element.equals(idsRules.get(symbolRules.get(element).peek()).split(" ")[1])){
//                                            idRegla=symbolRules.get(element).pop();
//                                            widthRegla=idsRules.get(idRegla).length();  
//                                        }
//                                        String readChain=null;
//                                        if(!stackChainRead.isEmpty())
//                                            readChain=stackChainRead.substring(1);
//                                        Stack<Integer> aux;
//                                        if(!values.containsKey(element+".result")){
//                                            aux=new Stack<>();
//                                            values.put(element+".result", aux);
//
//                                        }
//                                        values.get(element+".result").push(null);
//                                        String value=getValues(element);
//                                        if (value==null)
//                                            value=element+".result=null";
//
//    //                                    if(values.get(element)!=null)
//    //                                        value=values.get(element).toString();
//                                        String pendingChain=representChain(stackChain);
//                                        p=new Paso(paso, "derivacion", readChain, pendingChain.substring(0,pendingChain.length()-2 ), element, value, relNodes.get(element).pop(), idRegla, widthRegla);
//                                    }
//                                    steps.add(p);
//                                    paso++;
//                                }
//                            }
//                        }
//                        else{
//                            String action=stack.pop();
//
//                            String recursive;
//                            //if(action.split("}").length>1){
//                                recursive=action.split("}")[1];
//                                action=action.substring(0,action.length()-recursive.length());
//                            //}
//                            String[] varValue=action.substring(1,action.length()-2).split("=");
//
//                            String noTerminalWithNumber=varValue[0];
//
//                            Integer position=getNumberIndex(varValue[0].split("\\.")[0]);
//                            Paso pasoActual=steps.get(steps.size()-1);
//                            if(varValue.length>1){
//    //                            String noTerminalWithNumber=varValue[0];
//                                if (position<varValue[0].split("\\.")[0].length()){
//                                    varValue[0]=varValue[0].split("\\.")[0].substring(0,position)+"."+varValue[0].split("\\.")[1];
//
//                                }
//                                position=getNumberIndex(varValue[1].split("\\.")[0]);
//                                if (position<varValue[1].split("\\.")[0].length()){
//                                    varValue[1]=varValue[1].split("\\.")[0].substring(0,position)+"."+varValue[1].split("\\.")[1];
//
//                                }
//
//                                    if(varValue[1].contains("+")||varValue[1].contains("-")||varValue[1].contains("/")||varValue[1].contains("*")){
//                                        Integer value=calculateValue(varValue[1]);
//                                        Stack<Integer> aux=values.get(varValue[0]);
//                                        if(aux==null){
//                                            aux=new Stack<>();
//                                            values.put(varValue[0], aux);
//                                        }
//                                        aux.push(value);
//        //                                if(!noTerminalWithNumber.equals(varValue[0])){
//        //                                    aux=values.get(noTerminalWithNumber);
//        //                                    if(aux==null){
//        //                                        aux=new Stack<>();
//        //                                        values.put(noTerminalWithNumber, aux);
//        //                                    }
//        //                                    aux.push(value);
//        //                                }
//                                    }
//
//                                    else{
//                                        Stack<Integer> aux=values.get(varValue[0]);
//                                        if(aux==null){
//                                            aux=new Stack<>();
//                                            values.put(varValue[0], aux);
//                                        }
//                                        aux.push(values.get(varValue[1]).peek());
//        //                                if(!noTerminalWithNumber.equals(varValue[0])){
//        //                                    aux=values.get(noTerminalWithNumber);
//        //                                    if(aux==null){
//        //                                        aux=new Stack<>();
//        //                                        values.put(noTerminalWithNumber, aux);
//        //                                    }
//        //                                    aux.push(values.get(varValue[1]).peek());
//        //                                }
//                                    }
//                                    //varValue[0]=noTerminalWithNumber;
//                                if(antecedentes.contains(noTerminalWithNumber.split("\\.")[0])){
//                                    ArrayList<Paso> stepsBefore=findSteps(varValue[0],pasoActual,recursive);
//                                    for (Paso stepBefore:stepsBefore){
//                                        pasoActual.getChangedNodes().add(stepBefore.getId());
//                                        String bucar=varValue[0].split("\\.")[0];
//                                        pasoActual.getChanges().add(getValues(varValue[0].split("\\.")[0]));//varValue[0]+"="+values.get(varValue[0]).peek());
//
//                                    }
//                                    if(count==0&&pasoActual.getElemento().equals("λ")&&values.get(varValue[0].split("\\.")[0]+".valor")!=null){
//
//                                        values.get(varValue[0].split("\\.")[0]+".valor").pop();
//                                    }
//                                    count=1;
//                                }
//                            }
//                        }
//                    }    
//               }
//           }
//       }
//    nivel++;
//    writeArbol(espec);
//    writeContenido(espec);
//    TransformerFactory transformerFactory = TransformerFactory.newInstance();
//    Transformer transformer=null;
//        try {
//            transformer = transformerFactory.newTransformer();
//        } catch (TransformerConfigurationException ex) {
//            Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    DOMSource source = new DOMSource(doc);
//    StreamResult result = new StreamResult(new File(pathResult+".xml"));
//        try {
//            transformer.transform(source, result);
//        } catch (TransformerException ex) {
//            Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//    // Output to console for testing
//    StreamResult consoleResult = new StreamResult(System.out);
//        try { 	
//            transformer.transform(source, consoleResult);
//        } catch (TransformerException ex) {
//            Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    /**
     * 
     * @param chain
     * @return 
     
    public String transformChain(String chain){
        String result="";
        String[] elements=chain.split(" ");
        for (int i=0;i<elements.length;i++){
            if(Character.isDigit(elements[i].charAt(0))){
                result+="num ";
            }
            else{
                result+=elements[i]+" ";
            }
        }
        return result.substring(0,result.length()-1);
    }*/
    /**
     * Produce a new grammar without semantics actions
     */
//    public void grammarWithoutActions(){
//        Set<String> antecedentes=grammarWithActions.keySet();
//        for(String antecedent:antecedentes){
//            ArrayList<String> productions=grammarWithActions.get(antecedent);
//            ArrayList<String>newProductions=new ArrayList<>();
//            for(String production:productions){
//                String newProduction=removeActions(production);
//                newProductions.add(newProduction);
//            }
//            grammar.put(antecedent, newProductions);
//        }
//    }
    /**
     * remove the semantics actions of a production
     * @param production
     * production where remove the semantics actions
     * @return
     * production without actions
     */
    public String removeActions(String production){
        String[] symbols=production.split(" ");
        String result="";
        for(int i=0;i<symbols.length;i++){
            String symbol=symbols[i];
            if(!symbol.startsWith("{")){
                if(grammarWithActions.containsKey(symbol)){
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
    public String removeOnlyActions(String production){
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
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-16"))) { //mas-accesos-servidor-nitflex.log
	            String line;
                    int contador=0;
	            while ((line = br.readLine()) != null) {
                        String[] antecedentProductions=line.split("::=");    
                        String[] productions=antecedentProductions[1].split("\\|");
                        if(!antecedentes.contains(antecedentProductions[0]))
                            antecedentes.add(antecedentProductions[0]);
                        ArrayList<String> productionsList = new ArrayList<>();
                        grammarWithActions.put(antecedentProductions[0], productionsList);
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
    public void writeTraductor() {
        Element traductor = doc.createElement("traductor");
        espec.appendChild(traductor); 
        Element tipo= doc.createElement("tipo");
        traductor.appendChild(tipo);
        tipo.setTextContent("Descendente");
        for(String antecedent:antecedentes){
            for(String production:grammarWithActions.get(antecedent)){
              addRule(antecedent,production,traductor);  
            }
        }
        Element cadena = doc.createElement("cadena");
        espec.appendChild(cadena);
        cadena.setTextContent(entryChain);
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
                if(grammarWithActions.get(antecedente).get(0).equals(production))
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
     * @param altura
     * heigth of the tree 
     */
    public void writeArbol(Integer altura) {
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
    public void writeContenido( ) {
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
                Element widthRegla = doc.createElement("widthRegla");
                widthRegla.setTextContent(step.getWidthRule().toString());
                pasoE.appendChild(widthRegla); 
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
                relNodos.setTextContent(step.getRelNodo().toString());
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
    
}
