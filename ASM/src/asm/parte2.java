/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.Collator;
import java.util.Arrays;

/**
 *
 * @author Armando
 */
public class parte2 {
    //Instancio la parte 1 como objeto para usar las funciones q me sirvan
    parte1 funciones=new parte1();
    int decimal;
    String codMaquina;
    String p;
    String z;
    String s;
    void calculoCodMaquina(){
        String binario="654";
        String modoDireccionamiento;
        String valor;
        String etiqueta;
        String codop;
        String operando;
        //se usa para comparar string
        System.out.println("-----------------------------------PARTE 2");
        Collator comparar= Collator.getInstance();
        //Primary quiere decir que ve igual a mayusculas, minusculas y acentuadas (hay mas niveles)
        comparar.setStrength(Collator.PRIMARY);
        //inicializamos como si no estuviera
        try{
            File a=new File("P6tmp.txt");
            if (a.exists()){
                String linea;
                BufferedReader br=new BufferedReader(new FileReader(a));
                
                //La primer línea es solo para apoyo
                br.readLine();
                //Ciclo para leer el archivo
                while((linea=br.readLine())!=null){
                    //Separamos la línea en arreglos
                    String[] aLinea = linea.split("\t\t");
                    //Si hay uno con cadena "null" entonces ponle el valor null
                    for(int i=0;i<aLinea.length;i++){
                        if("null".equals(aLinea[i]))
                            aLinea[i]=null;
                    }
                    
                    
                    //Le damos su valor a su respectiva variable
                    System.out.println(Arrays.toString(aLinea));
                    valor=aLinea[1];
                    etiqueta=aLinea[2];
                    codop=aLinea[3];
                    operando=aLinea[4];
                    
                    
                    //Si operando no es null
                    if(operando!=null)
                        //convierto a decimal
                        operandoaDecimal(operando);
                        
                    modoDireccionamiento=validacionOperando(operando,codop,etiqueta);
                        
                    if(!buscarCODOP(codop, operando, modoDireccionamiento)){
                        System.out.println("Directiva");
                    }else{
                        if(null != modoDireccionamiento)switch (modoDireccionamiento) {
                            //inherente
                            case "INH":
                                System.out.println("INcódigo máquina: "+codMaquina);
                                break;
                            //inmediato
                            case "INM":
                                //si esta entre 8 bits entonces
                                if(decimal>=0&&decimal<=255){
                                    //el codigo máquina es el del tabop+elValorDeDecimalEnHex8Bits
                                    codMaquina=codMaquina+acomodarBits(Integer.toHexString(decimal),2);
                                }    
                                //si pasa los ocho pero no los 16 entonces
                                else if(decimal>=256&&decimal<=65535){
                                    //el codigo máquina es el del tabop+elValorDeDecimalEnHex16Bits
                                    codMaquina=codMaquina+acomodarBits(Integer.toHexString(decimal),4);
                                }
                                //imprimimpos informacion
                                System.out.println("IMcódigo máquina: "+codMaquina);
                                break;
                            //directo
                            case "DIR":
                                //el codigo máquina es el del tabop+elValorDeDecimalEnHex8Bits
                                codMaquina=codMaquina+acomodarBits(Integer.toHexString(decimal),2);
                                System.out.println("Dcódigo máquina: "+codMaquina);
                                break;
                            //extendido
                            case "EXT":
                                //Si el operando cumple con las condiciones de una etiqueta entonces
                                if(funciones.validacionEtiqueta(operando)){
                                    //Busca la etiqueta para comprobar que existe y conseguir su valor para el codigo máquina
                                    buscarEtiqueta(operando);
                                //si no entonces 
                                }else{
                                    //el codigo máquina es el del tabop+elValorDeDecimalEnHex16Bits
                                    codMaquina=codMaquina+acomodarBits(Integer.toHexString(decimal),4);
                                }
                                //imprimimos informacion
                                System.out.println("Ecódigo máquina: "+codMaquina);
                                break;
                            //indexado de 5 bits, post/pre incremento/decremento o de acumulador
                            case "IDX":
                                //binario es el valor del decimal en binario
                                binario=Integer.toBinaryString(decimal);
                                
                                //Diferenciar entre indexados de 5 bits, post/pre incremento/decremento y de acumulador
                                //5 bits
                                if(decimal>=-16&&decimal<=15&&operando!=null&&operando.toUpperCase().matches("^[0-9-]+,+(X|Y|SP|PC)+$")) {
                                    //binario es el valorDeRegistro+0+elDecimalEnBinario5bits
                                    binario=determinarRegistro(operando)+"0"+acomodarBits(binario,5);
                                //5 bits
                                }else if(decimal>=-16&&decimal<=15&&operando!=null&&operando.toUpperCase().matches("^,+(X|Y|SP|PC)+$")) {
                                    //binario es el valorDeRegistro+0+elDecimalEnBinario5bits
                                    binario=determinarRegistro(operando)+"0"+acomodarBits(binario,5);
                                //post/pre incremento/decremento
                                }else if(decimal >= -1&&decimal<=8&&operando!=null&&operando.toUpperCase().matches("^[0-9-]+,[//+|-]?+(X|Y|SP|PC)+[//+|-]?+$")) {
                                    //binario es el valorDeRegistro+1+banderaPost/Pre+elDecimalEnBinario5bits
                                    binario=determinarRegistro(operando)+"1"+p+acomodarBits(binario, 4);
                                //de acumulador
                                }else if (operando != null && (operando.toUpperCase().contains("A") || operando.toUpperCase().contains("B") || operando.toUpperCase().contains("D"))) {
                                    //binario es el valorDeRegistro+1+valorDeAcumulador
                                    binario="111"+determinarRegistro(operando)+"1"+determinarAcumulador(operando);
                                }
                                //el codigo máquina es el del tabop+elValorDeBinarioEnHex8Bits
                                codMaquina=codMaquina+acomodarBits(Integer.toHexString(Integer.parseInt(binario, 2)), 2).toUpperCase();
                                //imprimimos informacion
                                System.out.println("IDXcódigo máquina: "+codMaquina);
                                break;
                            //indexado de 9 bits
                            case "IDX1":
                                //binario es 111+valorDeRegistro+0+banderaZero+banderaSigno
                                binario="111"+determinarRegistro(operando)+"0"+z+s;
                                //el codigo máquina es el codmaqTABOP+valorDeBinarioEnHex8bits+valorDeDecimalEnHex8bits
                                codMaquina=codMaquina+acomodarBits(Integer.toHexString(Integer.parseInt(binario,2)),2).toUpperCase()+acomodarBits(Integer.toHexString(decimal),2).toUpperCase();
                                //imprimimos informacion
                                System.out.println("IDX1código máquina: "+codMaquina);
                                break;
                            //indexado de 16 bits
                            case "IDX2":
                                //binario es 111+valorDeRegistro+0+banderaZero+banderaSigno
                                binario="111"+determinarRegistro(operando)+"0"+z+s;
                                //el codigo máquina es el codmaqTABOP+valorDeBinarioEnHex8bits+valorDeDecimalEnHex16bits
                                codMaquina=codMaquina+acomodarBits(Integer.toHexString(Integer.parseInt(binario,2)),2).toUpperCase()+acomodarBits(Integer.toHexString(decimal),4).toUpperCase();
                                //imprime informacion
                                System.out.println("IDX2código máquina: "+codMaquina);
                                break;
                            //indexado de 16 bits indirecto
                            case "[IDX2]":
                                //binario es 111+valorDeRegistro+011
                                binario="111"+determinarRegistro(operando)+"011";
                                //el codigo máquina es el codmaqTABOP+valorDeBinarioEnHex8bits+valorDeDecimalEnHex8bits
                                codMaquina=codMaquina+acomodarBits(Integer.toHexString(Integer.parseInt(binario,2)),2).toUpperCase()+acomodarBits(Integer.toHexString(decimal),4).toUpperCase();
                                //Imprime informacion
                                System.out.println("[IDX2]código máquina: "+codMaquina);
                                break;
                            //indexado de acumulador indirecto de acumulador
                            case "[D,IDX]":
                                //binario es 111+valorDeRegistro+111
                                binario="111"+determinarRegistro(operando)+"111";
                                //el codigo máquina es el codmaqTABOP+valorDeBinarioEnHex8bits
                                codMaquina=codMaquina+acomodarBits(Integer.toHexString(Integer.parseInt(binario,2)),2).toUpperCase();
                                System.out.println("[D,IDX]código máquina: "+codMaquina);
                                break;
                            //Todo lo que aun no se pueda calcular no está disponible
                            default:
                                System.out.println(codMaquina+" no disponible");
                                break;
                        }
                    }
                    System.out.println("-------------------------------------");
                }
                
                
                br.close();
            }
            
        }catch(Exception e){System.out.println("error 1 al abrir el archivo"+e);}
        
        
        
        
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
                            //System.out.println("-------------------------------------");
                            codMaquina=aLinea[3];
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
    
    //Funcion para determinar el registro que se usa
    String determinarRegistro(String operando) {
        String binario = null;
        //X vale 00
        if (operando.toUpperCase().contains("X")) {
            binario = "00";
        //Y vale 01
        } else if (operando.toUpperCase().contains("Y")) {
            binario = "01";
        //SP vale 10
        } else if (operando.toUpperCase().contains("SP")) {
            binario = "10";
        //PC vale 11
        } else if (operando.toUpperCase().contains("PC")) {
            binario = "11";
        }
        return binario;
    }

    
    //Funcion para determinar el acumulador que se esta usando
    String determinarAcumulador(String operando) {
        String binario = null;
        //A vale 00
        if (operando.toUpperCase().contains("A")) {
            binario = "00";
        //B vale 01
        } else if (operando.toUpperCase().contains("B")) {
            binario = "01";
        //D vale 10
        } else if (operando.toUpperCase().contains("D")) {
            binario = "10";
        }
        return binario;
    }
    
    
    
    //validacion para operando
    String validacionOperando (String operando,String codop,String etiqueta){
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
            
        //Si tiene comillas entonces es ds.b ds.w dc.b dc.w o alguno de ellos, modo de direccionamiento es "" para evitar un error
        }else if(operando.charAt(0)=='"'&&operando.charAt(operando.length()-1)=='"'){
            System.out.println("Pertenece a directiva");
            modoDireccionamiento="";
        //si inicia con # entonces
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
            //en este caso se sobreentiende que el decimal es 0
            decimal=0;
        //Si comienza con digito y contiene ','
        }else if(operando.toUpperCase().matches("^[0-9-]+,[//+|-]?+(X|Y|SP|PC)+[//+|-]?+$")){
            //Si contiene ',+' y el decimal esta entre 1 y 8
            if(operando.contains(",+")&decimal>=1&decimal<=8){
                //indexado pre incremento
                System.out.println("Indexado pre incremento");
                modoDireccionamiento="IDX";
                //p es 0 porque es pre
                p="0";
                //a decimal le quitamos uno para sacar su valor en binario
                decimal--;
            }
                
            //Si contiene ',-' y el decimal esta entre 1 y 8
            else if(operando.contains(",-")&&decimal>=1&&decimal<=8){
                //Indexado pre decremento
                System.out.println("Indexado pre decremento");
                modoDireccionamiento="IDX";
                //p es 0 porque es pre
                p="0";
                //Decimal es negativo porque es decremento
                decimal*=-1;
            }
                
            //Si termina en '+' y el decimal esta entre 1 y 8
            else if(operando.endsWith("+")&&decimal>=1&&decimal<=8){
                //indexado post incremento
                System.out.println("Indexado post incremento");
                modoDireccionamiento="IDX";
                //p es 1 porque es post
                p="1";
                //a decimal le quitamos uno para sacar su valor en binario
                decimal--;
            }
                
            //Si termina en '-' y el decimal esta entre 1 y 8
            else if(operando.endsWith("-")&&decimal>=1&&decimal<=8){
                //Indexado post decremento
                System.out.println("Indexado post decremento");
                modoDireccionamiento="IDX";
                //p es 1 porque es post
                p="1";
                //Decimal es negativo porque es decremento
                decimal*=-1;
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
                //si decimal es negativo entonces
                if(decimal<0)
                    //bandera de signo es 1
                    s="1";
                //si es positivo entonces
                else
                    //bandera signo es 0
                    s="0";
                //bandera zero es 0
                z="0";
            //Si el decimal esta entre 256 y 65535
            }else if(decimal>=256&&decimal<=65535){
                //Indexado de 16 bits
                System.out.println("Indexado de 16 bits");
                modoDireccionamiento="IDX2";
                //si decimal es negativo entonces
                if(decimal<0)
                    //bandera de signo es 1
                    s="1";
                //si es positivo entonces
                else
                    //bandera signo es 0
                    s="0";
                //bandera zero es 1
                z="1";
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
            
        else if(funciones.validacionEtiqueta(operando)){
            if((operando.startsWith("LB")||operando.startsWith("B"))&&!comparar.equals(operando,"bita")&&!comparar.equals(operando,"bgnd")&&!comparar.equals(operando,"bitb")){
                modoDireccionamiento="REL";
            }else
                modoDireccionamiento="EXT";
        }
        //regreso el modo de direccionamiento
        return modoDireccionamiento;
    }
    
    
    boolean buscarEtiqueta(String etiqueta){
        //se usa para comparar string
        Collator comparar= Collator.getInstance();
        //Primary quiere decir que ve igual a mayusculas, minusculas y acentuadas (hay mas niveles)
        comparar.setStrength(Collator.PRIMARY);
        //inicializamos como si no estuviera
        boolean valido=false;
        String [] aLinea;

        try{
            File a=new File("TABSIM.txt");
            if (a.exists()){
                String linea;
                BufferedReader br=new BufferedReader(new FileReader(a));
                
                br.readLine();
                //Ciclo para leer el archivo
                while((linea=br.readLine())!=null){
                    //Separamos la línea en arreglos
                    aLinea=linea.split("\t\t");
                    if(comparar.equals(aLinea[1],etiqueta)){
                        codMaquina= codMaquina+aLinea[2];
                        valido=true;
                    }
                    
                }
                
                
                br.close();
            }
            
        }catch(Exception e){System.out.println("error 1 al abrir el archivo"+e);}
        
        
        return valido;
    }
    
    //funcion para rellenar con ceros o quitar a los negativos
    String acomodarBits(String cadena, int acomodar) {
        if (cadena.length() < acomodar) {
            for (int i = cadena.length(); i < acomodar; ++i) {
                cadena = "0" + cadena;
            }
        } else if (cadena.length() > acomodar) {
            cadena = cadena.substring(cadena.length() - acomodar);
        }
        return cadena;
    }
    
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
}
