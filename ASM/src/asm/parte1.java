/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.Collator;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

/**
 *
 * @author Armando
 */
public class parte1{ 
    
    int CONTLOC,DIR_INIC,decimal,sumCONTLOC;
    String[] arregloEtiquetas =new String[contadorFilas()];
    
    void validacionGeneral(){
        int contador=0;
        String comentario=null,etiqueta=null,codop=null,operando=null;
        //se usa para comparar string
        Collator comparar= Collator.getInstance();
        //Primary quiere decir que ve igual a mayusculas, minusculas y acentuadas (hay mas niveles)
        comparar.setStrength(Collator.PRIMARY);
        
        String modoDireccionamiento;
        
        
        boolean org = false, error=false;
        
        try{
            File asm=new File("P6ASM.txt");
            File temp=new File("P6tmp.txt");
            File tsim=new File("TABSIM.txt");
            temp.createNewFile();
            tsim.createNewFile();
            if (asm.exists()){
                String linea;
                BufferedReader brAsm=new BufferedReader(new FileReader(asm));
                BufferedWriter bwTemp=new BufferedWriter(new FileWriter(temp));
                BufferedWriter bwTsim=new BufferedWriter(new FileWriter(tsim));
                bwTemp.write("\t\tVALOR\t\tETIQUETA\tCODOP\t\tOPERANDO");
                bwTemp.newLine();
                bwTsim.write("\t\tETIQUETA\tVALOR");
                bwTsim.newLine();
                //Ciclo para leer ASM
                while((linea=brAsm.readLine())!=null){

                    contador++;
                    
                    //si la línea es null marca error
                    if(linea==null){
                        JOptionPane.showMessageDialog(null,"Error: linea "+(contador) );
                        error=true;
                        break;
                    }
                    //Si es ; entonces
                    if(linea.charAt(0)==59){
                        comentario=linea;
                        //Si la  validacion es falsa entonces error
                        if(!validacionComentario(comentario)){
                            JOptionPane.showMessageDialog(null, "Error: linea "+(contador));
                            error=true;
                            break;
                        }else
                            JOptionPane.showMessageDialog(null, "Comentario");
                    //Si no es ; , entonces
                    }else{
                        //se usa para separar tokens
                        StringTokenizer st = new StringTokenizer(linea);
                        
                        //Si son 3 tokens entonces ETIQUETA CODOP OPERANDO
                        if(st.countTokens()==3&&Character.isLetter(linea.charAt(0))){
                            
                            etiqueta=st.nextToken().toUpperCase();
                            
                            
                            codop=st.nextToken().toUpperCase();
                            
                            
                            operando=st.nextToken().toUpperCase();
                            
                            
                        //Si el primer caracter no es tab ni espacio y hay 2 tokens entonces ETIQUETA CODOP
                        }else if(linea.charAt(0)!=9&&linea.charAt(0)!=32&&st.countTokens()==2){
                            
                            etiqueta=st.nextToken().toUpperCase();
                           
                            
                            codop=st.nextToken().toUpperCase();
                            
                            
                        //Si el primer caracter es espacio o tab entonces...
                        }else if(linea.charAt(0)==9||linea.charAt(0)==32){
                            //Si hay 2 tokens entonces CODOP OPERANDO
                            if(st.countTokens()==2){
                                
                                codop=st.nextToken().toUpperCase();
                                
                                operando=st.nextToken().toUpperCase();

                                
                            //Si hay 1 token entonces CODOP
                            }else if(st.countTokens()==1){
                                
                                codop=st.nextToken().toUpperCase();
                                
                                
                            }else if(st.countTokens()==3){
                                codop=st.nextToken().toUpperCase();
                                operando=st.nextToken().toUpperCase()+" "+st.nextToken().toUpperCase();
                            }
                                
                            
                        }
                        
                        
                        
                        
                        //Si el codigo de operacion es "END" entonces termina el programa
                        if(comparar.equals(codop,"END")){
                            bwTemp.write("CONTLOC\t\t"+ponerCeros(Integer.toHexString(CONTLOC),4).toUpperCase()+"\t\t"+etiqueta+"\t\t"+codop+"\t\t"+operando);
                            break;
                        }
                        
                        //Si no es equ, org y no hay un org anteriormente entonces termina el programa
                        if(!comparar.equals(codop,"equ")&&!comparar.equals(codop,"org")&&org==false){
                            JOptionPane.showMessageDialog(null,"Error(se necesita primero un org): linea "+(contador) );
                            error=true;
                            break;
                        }
                        
                        
                        //Si operando no es null
                        if(operando!=null){
                            operandoaDecimal(operando);
                        }
                        
                        if(!validacionEtiqueta(etiqueta)){
                                JOptionPane.showMessageDialog(null,"Error(etiqueta no valida): linea "+(contador) );
                                error=true;
                                break;
                            }else if(etiqueta!=null){
                                //Valida que no haya otra etiqueta e imprime en tsim
                               arregloEtiquetas[contador-1]=etiqueta;
                                
                               validarunaEtiqueta(etiqueta,contador);
                               
                                if(!comparar.equals(codop,"equ")){
                                    bwTsim.write("CONT EtRe\t\t"+etiqueta+"\t\t"+ponerCeros(Integer.toHexString(decimal),4).toUpperCase());
                                    bwTsim.newLine();
                                }
                                
                            }else if(arregloEtiquetas[contador-1]==null)
                                arregloEtiquetas[contador-1]="";
                            
                            if((modoDireccionamiento=validacionOperando(operando,codop,contador))==null){
                                JOptionPane.showMessageDialog(null,"Error(operando no valido): linea "+(contador) );
                                error=true;
                                break;
                            }
                            System.out.println("modo demm direccionamiento "+modoDireccionamiento);
                        //Si db, dc.b o fcb y el operando es menor o igual a 8 bits entonces
                        if ((comparar.equals(codop,"db")||comparar.equals(codop,"dc.b")||comparar.equals(codop,"fcb"))&&decimal>=0&&decimal<=255&&operando!=null){
                            //Aumenta 1 a contloc e imprime info
                            
                            bwTemp.write("CONTLOC\t\t"+ponerCeros(Integer.toHexString(CONTLOC),4).toUpperCase()+"\t\t"+etiqueta+"\t\t"+codop+"\t\t"+operando);
                            bwTemp.newLine();
                            CONTLOC++;
                            
                        //Si dw, dc.w o fdb y el operando es menor o igual a 16 bits entonces
                        }else if((comparar.equals(codop,"dw")||comparar.equals(codop,"dc.w")||comparar.equals(codop,"fdb"))&&decimal>=0&&decimal<=65535&&operando!=null){
                            //Aumenta 2 a contloc e imprime info
                            
                            bwTemp.write("CONTLOC\t\t"+ponerCeros(Integer.toHexString(CONTLOC),4).toUpperCase()+"\t\t"+etiqueta+"\t\t"+codop+"\t\t"+operando);
                            bwTemp.newLine();
                            CONTLOC+=2;
                            
                        //Si fcc, y el operando termina y comienza con " entonces
                        }else if(comparar.equals(codop,"fcc")&&operando!=null&&operando.charAt(0)=='"'&&operando.charAt(operando.length()-1)=='"'){
                            //aumenta el tamaño de la cadena
                            
                            bwTemp.write("CONTLOC\t\t"+ponerCeros(Integer.toHexString(CONTLOC),4).toUpperCase()+"\t\t"+etiqueta+"\t\t"+codop+"\t\t"+operando);
                            bwTemp.newLine();
                            CONTLOC+=(operando.length()-2);
                        //Si ds, ds.b o rmb y el operando es menor o igual a 16 bits entonces
                        }else if ((comparar.equals(codop,"ds")||comparar.equals(codop,"ds.b")||comparar.equals(codop,"rmb"))&&decimal>=0&&decimal<=65535&&operando!=null){
                            //Aumenta a contloc el valor del operando por uno
                            
                            bwTemp.write("CONTLOC\t\t"+ponerCeros(Integer.toHexString(CONTLOC),4).toUpperCase()+"\t\t"+etiqueta+"\t\t"+codop+"\t\t"+operando);
                            bwTemp.newLine();
                            CONTLOC+=decimal;
                        //Si ds.w o rmw y el operando es menor o igual a 16 bits entonces
                        }else if((comparar.equals(codop,"ds.w")||comparar.equals(codop,"rmw"))&&decimal>=0&&decimal<=65535&&operando!=null){
                            //Aumenta a contloc el valor del operando por 2
                            
                            bwTemp.write("CONTLOC\t\t"+ponerCeros(Integer.toHexString(CONTLOC),4).toUpperCase()+"\t\t"+etiqueta+"\t\t"+codop+"\t\t"+operando);
                            bwTemp.newLine();
                            CONTLOC+=(decimal*2);
                        //Si no es directiva entonces
                        }else if(!comparar.equals(codop,"org")&&!comparar.equals(codop,"equ")&&!comparar.equals(codop,"nop")){
                            //valida
                            //si alguna validacion es incorrecta, manda error y termina programa

                            if(!validacionCodop(codop)){
                                JOptionPane.showMessageDialog(null,"Error(codop no valido): linea "+(contador) );
                                error=true;
                                break;
                            }
                            
                            //Si no lo encuentra marca error
                            if(!buscarCODOP(codop,operando,modoDireccionamiento)){
                                JOptionPane.showMessageDialog(null,"Error(codop no existe): linea "+(contador) );
                                error=true;
                                break;
                            }else{
                                bwTemp.write("CONTLOC\t\t"+ponerCeros(Integer.toHexString(CONTLOC),4).toUpperCase()+"\t\t"+etiqueta+"\t\t"+codop+"\t\t"+operando);
                                bwTemp.newLine();
                                CONTLOC+=sumCONTLOC;
                            }
                        }
                        
                        //Si es org y ya habia otro entonces
                        else if(comparar.equals(codop,"org")&&org==true){
                            //lanza el error
                            JOptionPane.showMessageDialog(null,"Error(más de un org): linea "+(contador) );
                            error=true;
                            break;
                            
                        //Si es equ entonces
                        }else if(comparar.equals(codop,"equ")){
                            //Imprime en temp y en tsim
                            bwTemp.write("V EQU\t\t"+ponerCeros(Integer.toHexString(decimal),4).toUpperCase()+"\t\t"+etiqueta+"\t\t"+codop+"\t\t"+operando);
                            bwTemp.newLine();
                            bwTsim.write("EQU EtAb\t\t"+etiqueta+"\t\t"+ponerCeros(Integer.toHexString(decimal),4).toUpperCase());
                            bwTsim.newLine();
                            
                        //Si es org y no hay otro
                        }else if(comparar.equals(codop,"org")&&org==false){
                            //Marca inicio dirinc e imprime informacion en temp
                            DIR_INIC=decimal;
                            CONTLOC=DIR_INIC;
                            bwTemp.write("DIR_INIC\t\t"+ponerCeros(Integer.toHexString(decimal),4).toUpperCase()+"\t\t"+etiqueta+"\t\t"+codop+"\t\t"+operando);
                            bwTemp.newLine();
                            org=true;
                            
                        //Si es nop entonces
                        }else if(comparar.equals(codop,"nop")){
                            //imprime info en temp
                            bwTemp.write("CONTLOC\t\t"+ponerCeros(Integer.toHexString(CONTLOC),4).toUpperCase()+"\t\t"+etiqueta+"\t\t"+codop+"\t\t"+operando);
                            bwTemp.newLine();
                        }else{
                            JOptionPane.showMessageDialog(null,"Error: linea "+(contador) );
                            error=true;
                            break;
                        }

                        
                        //Mostramos resultados
                        //JOptionPane.showMessageDialog(null,"ETIQUETA="+etiqueta+"\nCODOP="+codop+"\nOPERANDO="+operando);

                        
                    }
                    
                    
                    //limpiamos variables
                    etiqueta=null;
                    codop=null;
                    operando=null;
                    comentario=null;
                        
                }

                brAsm.close();
                bwTemp.close();
                bwTsim.close();
           
                if(!error){
                    parte2 p2=new parte2();
                    p2.calculoCodMaquina();
                }
            }
            
        }catch(Exception e){System.out.println("error 1 al abrir el archivo"+e);}
        
    }
    
    //Convertir el valor del operando a decimal
    void operandoaDecimal(String operando){
        
        
        //Confirma el sistema numerico para convertir a decimal
        if(operando.matches("^\\$+[0-9a-fA-F]+$")||operando.matches("^#+\\$+[0-9a-fA-F]+$")){
            decimal=Integer.parseInt(operando.replaceAll("[^0-9a-fA-F]",""),16);
        }else if(operando.matches("^#+\\@+[0-7]+$")||operando.matches("^\\@+[0-7]+$")){
            decimal=Integer.parseInt(operando.replaceAll("[^0-7]",""),8);
        }else if(operando.matches("^#+\\%+[0-1]+$")||operando.matches("^\\%+[0-1]+$")){
            decimal=Integer.parseInt(operando.replaceAll("[^0-1]",""),2);
        }else if(operando.matches("^#+[0-9-]+$")||operando.matches("^[0-9-]+$")){
            decimal=Integer.parseInt(operando.replaceAll("[^0-9-]",""),10);
        }else if (operando.toUpperCase().matches("^[0-9-]+,[//+|-]?+(X|Y|SP|PC)+[//+|-]?+$")) {
            decimal=Integer.parseInt(operando.replaceAll("[-]+[^0-9]|([-]+$)","").replaceAll("[^0-9-]",""),10);
        } else if (operando.toUpperCase().matches("^[\\x{5B}]+([0-9-])+,+(X|Y|SP|PC)+[\\x{5D}]+$")) {
            decimal=Integer.parseInt(operando.replaceAll("[^0-9-]",""),10);
        }

        
    }
    
     boolean validarunaEtiqueta(String etiqueta,int contador){
        boolean valido=false;
        //se usa para comparar string
        Collator comparar= Collator.getInstance();
        //Primary quiere decir que ve igual a mayusculas, minusculas y acentuadas (hay mas niveles)
        comparar.setStrength(Collator.PRIMARY);
        for(int i=0;i<contador-1;i++){
            
            if(comparar.equals(arregloEtiquetas[i],etiqueta)){
                valido=true;
                break;
            }
        }
        return valido;
    }
    
    
    //Validacion para comentario
    boolean validacionComentario (String comentario){
        boolean valido=true;
        //Si es mayor a 81 se regresa que no es valido
       if(comentario.length()>81)
            valido=false;
            
        //regreso si es valido o no
        return valido;
    }
    
     //Validacion para etiqueta
    boolean validacionEtiqueta (String etiqueta){
        boolean valido=true;
        //Si existe etiqueta entonces valida
        if(etiqueta!=null){
            //Si es menor o igual a 8 se revisa cada caracter
            if(etiqueta.length()<=8&&Character.isLetter(etiqueta.charAt(0))){
                //separamos etiqueta en caracteres
                char [] carEtiqueta=etiqueta.toCharArray();
                //ciclo para revisar cada caracter
                 for(int i=0;i<carEtiqueta.length;i++){
                    //si es diferente a una letra, a guion bajo (95) y a algun digito entonces no es valido
                    if(!Character.isLetterOrDigit(carEtiqueta[i])&&carEtiqueta[i]!=95){
                        valido=false;
                    }
                }

            //Si no entonces no es valido
            }else
                valido=false;
            //regreso si es valido o no
        }
        
            
        return valido;
    }
    
    //validacion para codigo de operacion
    boolean validacionCodop (String codop){
        boolean valido=true;
        //Si existe codop entonces valida
        if(codop!=null){
            boolean punto=true;
            //Si es menor o igual a 5, entonces revisar cada caracter
            if(codop.length()<=5&&Character.isLetter(codop.charAt(0))){
                //Separamos el codigo de operacion en caracteres
                char [] carCodop=codop.toCharArray();
                //ciclo para revisar cada caracter
                for(int i=0;i<carCodop.length;i++){
                    //Si el caracter es diferente a una letra y tambien a un punto entonces no es valido
                    if(!Character.isLetter(carCodop[i])&&carCodop[i]!='.'){
                        valido=false;
                    }
                    //Si el caracter es un punto y el booleano punto es falso entonces no es valido
                    if(carCodop[i]=='.'&&!punto)
                        valido=false;
                    //Si el caracter es un punto entonces ya no podrá usar otro
                    else if(carCodop[i]=='.')
                        punto=false;
                }
            //Si es mayor entonces no es valido
            }else
                valido=false;
            //regreso si es valido o no
        }
            
        return valido;
    }
    
    //validacion para operando
    String validacionOperando (String operando,String codop,int contador){
        String modoDireccionamiento=null;
        modoDireccionamiento=null;
        //System.out.println("------------------");
        //System.out.println(operando);
        //se usa para comparar string
        Collator comparar= Collator.getInstance();
        //Primary quiere decir que ve igual a mayusculas, minusculas y acentuadas (hay mas niveles)
        comparar.setStrength(Collator.PRIMARY);
        //Si operando null entonces 
        if(operando==null){
            //inherente
            System.out.println("Inherente");
            modoDireccionamiento="INH";
            
        //si inicia con # entonces
        }else if(operando.charAt(0)=='"'&&operando.charAt(operando.length()-1)=='"'){
            System.out.println("Pertenece a directiva");
            modoDireccionamiento="";
        }else if(operando.matches("^#+\\$+[0-9a-fA-F]+$")||operando.matches("^#+\\@+[0-7]+$")||operando.matches("^#+\\%+[0-1]+$")||operando.matches("^#+[0-9]+$")){
            //inmediato
            System.out.println("Inmediato");
            modoDireccionamiento="INM";
            
        //si inicia con $%@ o digito entonces
        }else if((operando.matches("^\\$+[0-9a-fA-F]+$")||operando.matches("^\\@+[0-7]+$")||operando.matches("^\\%+[0-1]+$")||operando.matches("[0-9]+$"))&&!operando.contains(",")){
                //si esta entre 8 bits entonces
                if(decimal>=0&&decimal<=255){
                    //directo
                    System.out.println("Directo");
                    modoDireccionamiento="DIR";
                }   
                //si pasa los ocho pero no los 16 entonces
                else if(decimal>=256&&decimal<=65535){
                    //extendido
                    System.out.println("Extendido");
                    modoDireccionamiento="EXT";
                }
            
        //Si comienza con coma
        }else if(operando.toUpperCase().matches("^,+(X|Y|SP|PC)+$")){
            //Indexado de 5 bits
            System.out.println("Indexado de 5 bits");
            modoDireccionamiento="IDX";
            decimal=0;
            
        //Si comienza con digito y contiene ','
        }else if(operando.toUpperCase().matches("^[0-9-]+,[//+|-]?+(X|Y|SP|PC)+[//+|-]?+$")){
            //Si contiene ',+' y el decimal esta entre 1 y 8
            if(operando.contains(",+")&decimal>=1&decimal<=8){
                //indexado pre incremento
                System.out.println("Indexado pre incremento");
                modoDireccionamiento="IDX";
            }
                
            //Si contiene ',-' y el decimal esta entre 1 y 8
            else if(operando.contains(",-")&&decimal>=1&&decimal<=8){
                //Indexado pre decremento
                System.out.println("Indexado pre decremento");
                modoDireccionamiento="IDX";
            }
                
            //Si termina en '+' y el decimal esta entre 1 y 8
            else if(operando.endsWith("+")&&decimal>=1&&decimal<=8){
                //indexado post incremento
                System.out.println("Indexado post incremento");
                modoDireccionamiento="IDX";
            }
                
            //Si termina en '-' y el decimal esta entre 1 y 8
            else if(operando.endsWith("-")&&decimal>=1&&decimal<=8){
                //Indexado post decremento
                System.out.println("Indexado post decremento");
                modoDireccionamiento="IDX";
            }
                
            //Si decimal esta entre -16 y 15
            else if(decimal>=-16&&decimal<=15){
                //indexado de 5 bits
                System.out.println("Indexado de 5 bits");
                modoDireccionamiento="IDX";
            }
                
            //Si decimal esta entre -256 y -17 o entre 16 y 255
            else if((decimal>=-256&&decimal<=-17)||(decimal>=16&&decimal<=255)){
                //indexado de 9 bits
                System.out.println("Indexado de 9 bits");
                modoDireccionamiento="IDX1";
            }else if(decimal>=256&&decimal<=65535){
                //Indexado de 16 bits
                System.out.println("Indexado de 16 bits");
                modoDireccionamiento="IDX2";
            }
                
            
        //Si comienza y termina con corchetes
        }else if(operando.toUpperCase().matches("^\\x{5B}+([0-9-]|A|B|D)+,+(X|Y|SP|PC)+\\x{5D}+$")){
            //si tiene A a B b D d 
            if(operando.toUpperCase().contains("A") || operando.toUpperCase().contains("B") || operando.toUpperCase().contains("D")){
                //Indexado acumulador indirecto
                System.out.println("Indexado indirecto de acumulador "+operando.charAt(1)+" ");
                modoDireccionamiento="[D,IDX]";
            }
            //si esta entre 0 y 65535
            else if(decimal>=0&&decimal<=65535){
                //indexado indirecto 16 bits
                System.out.println("Indexado indirecto de 16 bits");
                modoDireccionamiento="[IDX2]";
            }
                
            
        //Si incia con A a B b D d 
        }else if(operando.toUpperCase().matches("^(A|B|D)+,+(X|Y|SP|PC)+$")){
            //Indexado de acumulador
            System.out.println("Indexado de acumulador");
            modoDireccionamiento="IDX";
        }
            
        else if(validacionEtiqueta(operando)){
            if((operando.startsWith("LB")||operando.startsWith("B"))&&!comparar.equals(operando,"bita")&&!comparar.equals(operando,"bgnd")&&!comparar.equals(operando,"bitb")){
                modoDireccionamiento="REL";
            }else if(validarunaEtiqueta(operando, contador))
                modoDireccionamiento="EXT";
        }
        System.out.println(modoDireccionamiento);
        //regreso el modo de direccionamiento
        return modoDireccionamiento;
    }
    
    boolean buscarCODOP(String codop,String operando,String modoDireccionamiento){
        //se usa para comparar string
        Collator comparar= Collator.getInstance();
        //Primary quiere decir que ve igual a mayusculas, minusculas y acentuadas (hay mas niveles)
        comparar.setStrength(Collator.PRIMARY);
        //inicializamos como si no estuviera
        boolean valido=false;
        String [] aLinea;
        try{
            File a=new File("TABOP.txt");
            if (a.exists()){
                String linea;
                BufferedReader br=new BufferedReader(new FileReader(a));

                //Ciclo para leer el archivo
                while((linea=br.readLine())!=null){
                    //Separamos la línea en arreglos
                    aLinea=linea.split("\t");
                    
                    //Si son iguales entonces 
                    if(comparar.equals(aLinea[0],codop)&&comparar.equals(aLinea[2],modoDireccionamiento)){
                        //Si necesita operando y operando está null termina el ciclo (devolverá falso)
                        if(comparar.equals(aLinea[1],"true")&&operando==null){
                            break;
                        //Si no necesita el operando y el operando no es null entonces termina el ciclo (devolverá falso)
                        }else if(comparar.equals(aLinea[1],"false")&&operando!=null){
                            break;
                        //Si no aplica en ninguno entonces imrime la línea
                        }else{
                            
                            /*System.out.println("_____________CODOP INFO______________");
                            System.out.println("CODOP: "+aLinea[0]+" Operando: "+aLinea[1]+" addr: "+aLinea[2]+" HEX: "+aLinea[3]+" BC: "+aLinea[4]+" B/C: "+aLinea[5]+" SB: "+aLinea[6]);
                            System.out.println("-------------------------------------");*/
                            //System.out.println(modoDireccionamiento+", "+aLinea[6]+" Bytes");
                            sumCONTLOC=(Integer.parseInt(aLinea[4]));
                            System.out.println("-------------------------------------");
                            
                            valido=true;
                        }
                    }
                    
                    //Si es valido y ya son diferentes entonces termina el ciclo;
                    if(valido&&!comparar.equals(aLinea[0],codop))
                        break;
                }
                
                
                br.close();
            }
            
        }catch(Exception e){System.out.println("error 1 al abrir el archivo"+e);}
        return valido;
    }
    
    
    int contadorFilas(){
        int contador = 0;
        
        try{
            File a=new File("P6ASM.txt");
            if (a.exists()){
                BufferedReader br=new BufferedReader(new FileReader(a));
                while(br.readLine()!=null)
                    contador++;
                br.close();
            }
            
        }catch(Exception e){System.out.println("error 1 al abrir el archivo"+e);}
        
        
        
        return contador;
    }
    
    String ponerCeros(String hex16,int poner) {
        for (int i=hex16.length();i<poner;++i) {
            hex16="0"+hex16;
        }
        return hex16;
    }

    
}