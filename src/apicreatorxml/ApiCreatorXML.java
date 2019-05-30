/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apicreatorxml;

import java.util.ArrayList;

/**
 *
 * @author adgao
 */
public class ApiCreatorXML {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Writer writer=new Writer("Descendant.txt","p","2 * 4 + 3 ;");
        writer.writeTraductor();
        
        
        writer.addNode("EXP", false, 1);
        writer.addNode( "B", false, 2);
        writer.addNode( "num", true, 3);
        writer.addNode("C", false, 3);
        writer.addNode( "*", true, 4);
        writer.addNode( "num", true,4 );
        writer.addNode( "C1", false, 4);
        writer.addNode("λ", true, 5);
        writer.addNode( "A", false, 2);
        writer.addNode( "+", true, 3);
        writer.addNode( "B", false, 3);
        writer.addNode( "num", true, 4);
        writer.addNode( "C", false, 4);
        writer.addNode( "λ", true, 5);
        writer.addNode( "A1", false, 3);
        writer.addNode( "λ", true, 4);
        writer.addNode( ";", true, 2);
        writer.writeArbol(5);
        writer.addPaso("primero", "", "2 * 4 + 3", "EXP", null, null, null);
        writer.addPaso("derivacion", "", "2 * 4 + 3", "B", "B.resul=null","EXP::= B A ;" , 0);
        writer.addPaso("despDes", "2", "* 4 + 3", "num", "num.vlex=2","B::= num C", 1);
        writer.addPaso("derivacion", "2", "* 4 + 3", "C", "C.valor=2 C.resul=null", null, 1);
        writer.addPaso("despDes", "2 *", "4 + 3", "*", null, "C::= * num C1" , 3);
        writer.addPaso("despDes", "2 * 4", "+ 3", "num", "num.vlex=4", null, 3);
        writer.addPaso("derivacion", "2 * 4", "+ 3", "C1", "C.valor=8 C.resul=null", null, 3);
        Paso p=writer.addPaso("despDes", "2 * 4", "+ 3", "λ", null, "C::= λ" , 6);
        p.getChangedNodes().add(6);
        p.getChanges().add("C.valor=8 C.resul=8");
        p.getChangedNodes().add(3);
        p.getChanges().add("C.valor=2 C.resul=8");
        p.getChangedNodes().add(1);
        p.getChanges().add("B.resul=8");
        writer.addPaso("derivacion", "2 * 4", "+ 3 ;", "A", " A.valor=8 A.resul=null ", null, 0);
        writer.addPaso("despDes", "2 * 4 +", "3 ;", "+", null, "A::= + B A1", 8);
        writer.addPaso("derivacion", "2 * 4 +", "3 ;", "B", "B.resul=null", null, 8);
        writer.addPaso("despDes", "2 * 4 + 3", ";", "num", "num.vlex=3", "B::= num C", 10);
        writer.addPaso("derivacion", "2 * 4 + 3", ";", "C", "C.valor=3 C.resul=null ", null, 10);
        p=writer.addPaso("despDes", "2 * 4 + 3", ";", "λ", null, "C::= λ", 12);
        p.getChangedNodes().add(12);
        p.getChanges().add("C.valor=3 C.resul=3");
        p.getChangedNodes().add(10);
        p.getChanges().add("B.resul=3");
        writer.addPaso("derivacion", "2 * 4 + 3", ";", "A1", "A1.valor=11 A1.resul=null ", null, 8);
        p=writer.addPaso("despDes", "2 * 4 + 3", ";", "λ", null, "A::= λ", 14);
        p.getChangedNodes().add(14);
        p.getChanges().add("A1.valor=11 A1.resul=11");
        p.getChangedNodes().add(8);
        p.getChanges().add(" A.valor=8 A.resul=11");
        writer.addPaso("despDes", "2 * 4 + 3 ;", "", ";", null, null, 0);
        writer.writeContenido();
        writer.writeXML();
    }
    
}
