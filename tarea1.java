
import java.util.Random;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Ejecucion:    $ java tarea1 --metodo [1|2|3] -n N (-m M)
 *
 * (-m M) solo necesario en el metodo 3
 * Testeado en java version 1.7.0_06
 */

/**
 *
 * @author Mauricio Orellana Galaz
 */
 
public class tarea1 {  
    static int metodo = 0;
    static int N = 0;
    static int M = 0;
    
    public static void main( String[] args ) throws IOException {    
    
		if(args.length < 4) {
            System.out.println ("Error en el modo de ejecucion");
            System.out.println ("Modo de ejecucion: tarea1Manual --metodo [1|2|3] (-m M)");
            System.exit (0);
        }
        
        for (int i=0; i<args.length; i++) {
            switch(args[i]){
                case "--metodo":
                    metodo = Integer.parseInt (args[i+1]);
                    break;
                case "-n":
                    N = Integer.parseInt (args[i+1]);
                    break;
                case "-m":
                    M = Integer.parseInt (args[i+1]);
                    break;
            }
        }
        
        int[] p = generarPolinomio (N);
        int[] q = generarPolinomio (N);
        int[] r = null;
        long tiempoInicial = 0, tiempoFinal = 0;

        switch (metodo) {
            case 1:
                tiempoInicial = System.currentTimeMillis ();
                r = metodoCuadratico (p, q);
                tiempoFinal = System.currentTimeMillis ();
                break;
            case 2:
                tiempoInicial = System.currentTimeMillis ();
                r = metodoSubCuadratico (p, q);
                tiempoFinal = System.currentTimeMillis ();
                break;
            case 3:
                tiempoInicial = System.currentTimeMillis ();
                r = metodoCombinado (p, q, M);
                tiempoFinal = System.currentTimeMillis ();
                break;
        }
        mostrarTiempoTranscurrido (tiempoInicial, tiempoFinal);
    }

    private static int[] metodoCuadratico( int[] p, int[] q ) {
        int n = p.length;
        int[] c = new int[2*n-1];
        
        for( int i=0; i<n; ++i){
            for( int j=0; j<n; ++j){
                c[i+j] += p[i] * q[j];
            }
        }
        
        return c;
    }
    
    private static int[] metodoSubCuadratico( int[] p, int[] q ) {
		/*
        Si el metodo seleccionado es el 3, el metodo SubCuadratico llamarA al metodo Cuadratico
        cuando el numero de coeficientes de los polinomio a multiplicar sea infererior al M critico ingresado.
        El M critico optimo es el 64.
        */
        if(metodo == 3){
            if(p.length <= M)
                return metodoCuadratico (p, q);
        }

        // caso base: cuando los polinomios tienen solo un coeficiente cada uno
        if( p.length == 1 && q.length == 1 ){
            int c[] = new int[1];
            c[0] = p[0] * q[0];
            return c;
        }
        
        // subpolinomios right y left de p y q.
        int pl[], pr[], ql[], qr[];
        
        // en caso de que los polinomios sean impares, se hace par agregandole un 0
        if(p.length % 2 == 1) {
            pr = new int[p.length+1];
            qr = new int[p.length+1];
            
            for(int i=0; i<p.length; i++) {
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
        int s [] = metodoSubCuadratico (pl, ql);
        int u [] = metodoSubCuadratico (pr, qr);
        int t [] = metodoSubCuadratico (sumaP (pl,pr), sumaP (ql,qr));
        t = restaP (t, sumaP (s, u));
        
        // polinomio final
        int c [] = new int[p.length+q.length-1];
        
        int i, j, n=c.length;

        // calcula el polinomios final en base a los auxiliares
        for(i=0; i<(n+1)/2-1; i++) {
            c[i] += s[i];
        }
        for(i=(n+1)/4, j=0; i<((3*(n+1)/2)/2)-1; i++, j++) {
            c[i] += t[j];
        }
        for( i=(n+1)/2, j=0; i<n; i++, j++ ) {
            c[i] += u[j];
        }
        return c;
    }
        
    public static int[] copiarArray( int[] a, int inicio, int fin ) { // ambos incluidos
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

    private static int[] generarPolinomio( int N ) {
        int[] polinomio = new int[N];
        Random rnd = new Random();
        
        for(int i=0;i<N;i++){          
            polinomio[i] = (rnd.nextInt(10) - rnd.nextInt(10));
        }
        
        return polinomio;
    }

    private static void imprimir( int[] pol ) {
        for(int i=0; i<pol.length; i++){
            System.out.print (pol[i] + " ");
        }
        System.out.println ("");
    }

    private static int[] metodoCombinado( int[] p, int[] q, int M ) {
        if (p.length <= M) {
            return metodoCuadratico (p, q);
        }
        else {
            return metodoSubCuadratico(p, q);
        }
    }
    
    private static void mostrarTiempoTranscurrido( long tiempoInicial, long tiempoFinal ) {
        long transcurrido = tiempoFinal - tiempoInicial;   // tiempo en milisegundos
        long horas = transcurrido / 3600000;
        long restoHoras = transcurrido % 3600000;
        long minutos = restoHoras / 60000;
        long restoMinutos = restoHoras % 60000;
        long segundos = restoMinutos / 1000;
        long restoSegundos = restoMinutos % 1000;
        
        System.out.println ( "Tiempo: " + horas + ":" + minutos + ":" + segundos + "." + restoSegundos );
    }
    
}
