
import java.util.Random;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Ejecucion:    $ java tarea1Manual --metodo [1|2|3] (-m M) < input > output
 *
 * (-m M) solo necesario en el metodo 3
 * Testeado en java version 1.7.0_06
 */

/**
 *
 * @author Mauricio Orellana Galaz
 */
 
public class tarea1Manual {  
    static int metodo = 0;
    static int M = 64;
    
    public static void main( String[] args ) throws IOException  {
        
        if(args.length < 2) {
            System.out.println("Error en el modo de ejecucion");
            System.out.println("Modo de ejecucion: tarea1Manual --metodo [1|2|3] (-m M)");
            System.exit (0);
        }
        
        // lectura de parametros ingresados al momento de la ejecucion
        for (int i=0; i<args.length; i++) {
            switch(args[i]){
                case "--metodo":
                    metodo = Integer.parseInt (args[i+1]);
                    break;
                case "-m":
                    M = Integer.parseInt (args[i+1]);
                    break;
            }
        }
        
        Random rnd = new Random ();
        Scanner in = new Scanner (System.in);
        BufferedWriter bw = new BufferedWriter(new FileWriter("tiempos.txt"));
        long tiempoInicial = 0;
        long tiempoFinal = 0;

        int n;
               
        while ((n = in.nextInt()) != 0) {    // lee cantidad de coeficientes. termina cuando n = 0
            int p[] = new int[n];
            int q[] = new int[n];
            int r[] = new int[2*n-1];    // polinomio resultante
            int i;
        
            // llenar array p
            for(i=0; i<n; i++){
                p[i] = in.nextInt ();
            }
        
            // llenar array q
            for(i=0; i<n; i++){
                q[i] = in.nextInt ();
            }
            
            switch (metodo) {
                case 1:
                    tiempoInicial = System.currentTimeMillis ();
                    r = metodoCuadratico (p, q);
                    tiempoFinal = System.currentTimeMillis ();
                    break;
                case 2:
                    tiempoInicial = System.currentTimeMillis ();
                    r = metodoSubcuadratico (p, q);
                    tiempoFinal = System.currentTimeMillis ();
                    break;
                case 3:
                    tiempoInicial = System.currentTimeMillis ();
                    r = metodoCombinado (p, q, M);
                    tiempoFinal = System.currentTimeMillis ();
                    break;
            }
            System.out.println (2*n-1);    // imprime el numero de coeficientes del polinomio resultante
            imprimir (r, n);                 // imprime el polinomio resultante
            escribirEnArchivo (bw, metodo, n, tiempoTranscurrido (tiempoInicial, tiempoFinal));    // escribe el tiempo que tarda en el archivo de salida
        }
        System.out.println (0);    // indica que no hubieron mas polinomios multiplicandose
        bw.close ();              // cierra el archivo de salida con los tiempos
    }

    private static int[] metodoCuadratico( int[] p, int[] q ) {
        int i, j, n = p.length;
        int[] c = new int[2*n-1];
        
        for(i=0; i<n; ++i){
            for(j=0; j<n; ++j){
                c[i+j] += p[i] * q[j];
            }
        }
 
        return c;
    }
    
    private static int[] metodoSubcuadratico( int[] p, int[] q ) {
        /*
        Si el metodo seleccionado es el 3, el metodo SubCuadratico llamarA al metodo Cuadratico
        cuando el numero de coeficientes de los polinomio a multiplicar sea infererior al M critico ingresado.
        El M critico optimo es el 64.
        */
        if(metodo == 3 && p.length <= M)
            return metodoCuadratico (p, q);
        
        // caso base: cuando los polinomios tienen solo un coeficiente cada uno
        if( p.length == 1 && q.length == 1 ){
            int c[] = new int[1];
            c[0] = p[0] * q[0];
            return c;
        }
        
        // subpolinomios right y left de p y q.
        int pl[], pr[], ql[], qr[];
        
        // en caso de que los polinomios sean impares, se hace par agregandole un 0
        if( p.length % 2 == 1 ) {
            pr = new int[p.length+1];
            qr = new int[p.length+1];
            
            for( int i=0; i<p.length; i++ ) {
                pr[i] = p[i];
                qr[i] = q[i];
            }
            p = pr;
            q = qr;
        }

        pl = copiarArray (p, 0, (p.length/2)-1);
        pr = copiarArray (p, p.length/2, p.length-1);
        
        ql = copiarArray (q, 0, (q.length/2)-1);
        qr = copiarArray (q, q.length/2, q.length-1);
        
        // polinomios auxiliares
        int s [] = metodoSubcuadratico (pl, ql);
        int u [] = metodoSubcuadratico (pr, qr);
        int t [] = metodoSubcuadratico (sumaP(pl,pr), sumaP (ql,qr));
        t = restaP (t, sumaP(s, u));
        
        // polinomio final
        int c [] = new int[p.length+q.length-1];
        
        int i, j, n=c.length;

        // calcula el polinomios final en base a los auxiliares
        for( i=0; i<(n+1)/2-1; i++ ) {
            c[i] = c[i] + s[i];
        }
        for( i=(n+1)/4, j=0; i<((3*(n+1)/2)/2)-1; i++, j++ ) {
            c[i] = c[i] + t[j];
        }
        for( i=(n+1)/2, j=0; i<n; i++, j++ ) {
            c[i] = c[i] + u[j];
        }
        return c;
    }
        
    public static int[] copiarArray( int[] a, int inicio, int fin ) { // ambos indices incluidos
        int c[] = new int[fin-inicio+1];
        int largo=fin-inicio+1;
        int i, j;
        
        for( i=0, j=inicio; i<largo; i++ ) {
            c[i] = a[inicio];
            inicio++;
        }
        
        return c;
    }
    
    public static int[] sumaP( int[] a, int[] b ) {
        int[] c = new int[a.length];
    
        for( int i=0; i<a.length; i++ ) {
            c[i] = a[i] + b[i];
        }
        
        return c;
    }

    private static int[] restaP( int[] a, int[] b ) {
        int c[] = new int[a.length];
        
        for( int i=0; i<a.length; i++ ){
            c[i] = a[i] - b[i];
        }
        
        return c; 
    }

    private static void imprimir( int[] pol, int n ) {
        for(int i=0; i<2*n-1; i++){
            if((i+1)%10==0 || i==2*n-2) {    // imprime los resultados en 10 columnas
                System.out.println (pol[i]);
            }
            else {
                System.out.print (pol[i] + " ");    // deja espacio sabiendo que vendrA otro coeficiente a continuacion
            }
        }
    }

    private static int[] metodoCombinado( int[] p, int[] q, int M ) {
        if (p.length < M)
            return metodoCuadratico (p, q);
        return metodoSubcuadratico (p, q);    // el metodo SubCuadratico hace uso del Cuadratico para optimizar el proceso
    }
    
    private static String tiempoTranscurrido( long tiempoInicial, long tiempoFinal ) {
        long transcurrido = tiempoFinal - tiempoInicial;   // tiempo en milisegundos
        long horas = transcurrido / 3600000;
        long restoHoras = transcurrido % 3600000;
        long minutos = restoHoras / 60000;
        long restoMinutos = restoHoras % 60000;
        long segundos = restoMinutos / 1000;
        long restoSegundos = restoMinutos % 1000;
        
        return "Tiempo: " + horas + ":" + minutos + ":" + segundos + "." + restoSegundos;    // formato: Horas:Minutos:Segundos.Milisegundos
    }
    
    public static void escribirEnArchivo( BufferedWriter bw, int metodo, int N, String tiempo ) throws IOException{
        bw.write (metodo + "  " + N + "  " + tiempo + "\n");    // formato: NumeroDeCoeficientes  TiempoQueDemora
    }
}
