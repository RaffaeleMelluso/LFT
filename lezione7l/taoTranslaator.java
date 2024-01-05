package lezione7l;
import lezione7l.other.*;
import java.io.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class taoTranslaator {
    private Lexer lex; // qui dichiariamo il lexere del main
    private BufferedReader pbr;
    private Token look;

    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count=0;

    public void Translator(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() { // idea == parser avanza al prossimo token da leggere nella sequenza

        look = lex.lexical_scan(pbr); // ogni chiamata chiama il lexical scan
        System.out.println("token = " + look);
    }

    void error(String s) { // in argomento ha una stringa e da in output un mex di errore
	throw new Error("near line " + Lexer.line + ": " + s);
    }

    void match(int t) { // prende un int in argomento e fa confronto tra int e il nome del token che stiamo analizzando
        if (look.tag == t) { // look.tag è il nome del tag che stiamo analizzando

            if (look.tag != Tag.EOF) move(); // se non siamo davanti all'ultimo elemento allora andiamo avanti
    
        } else error("syntax error");
    
        }
    
        public void prog() { // Scrivi come variabili d'accesso l'insieme guida della prima variabile che trovi
    
            if (look.tag == Tag.ASSIGN || look.tag == Tag.PRINT | look.tag == Tag.READ | look.tag == Tag.FOR| look.tag == '{' | look.tag == Tag.IF) {
                
                int lnext_prog = code.newLabel();
                statlist(lnext_prog); 
                code.emit(OpCode.GOto, lnext_prog);
                code.emitLabel(lnext_prog);
                match(Tag.EOF); // $ finale, match lo fai solo sui terminali
    
        try {
            code.toJasmin();
        }
        catch(java.io.IOException e) {
            System.out.println("IO error\n");
        };
        
        } else error("Errore su prog()");
    
        }
    
        public void statlist(int lnext_prog) {
    
            if (look.tag == Tag.ASSIGN || look.tag == Tag.PRINT | look.tag == Tag.READ | look.tag == Tag.IF| look.tag == Tag.FOR| look.tag == '{') { 
                
                lnext_prog = code.newLabel(); // viene incrementata l'etichetta
                stat();
                code.emit(OpCode.GOto, lnext_prog);
                code.emitLabel(lnext_prog);
                statlistp();
                
            } else error("Errore su statlist()");
    
        }
        public void statlistp() {

            switch (look.tag) {
                case ';':
                match(';');
                stat();
                statlistp();
                break;
    
                case '}':
                break;
    
                case Tag.EOF:
                break;
            
                default: error("Errore su statlistp()");
            }
            
        }
    
        public void stat() {
    
            int loop = code.newLabel();
    
            switch(look.tag) {
    
                case Tag.ASSIGN:
                move();
                assignlist();
                code.emit(OpCode.pop);
                break;
    
                case Tag.PRINT:
                match(Tag.PRINT);
                match('(');
                exprlist();
                code.emit(OpCode.invokestatic, 1);
                match(')');
                break;
    
                case Tag.READ:
                match(Tag.READ);
                match('(');
                code.emit(OpCode.invokestatic, 0);// codice 0 read        
                idlist(0);
                match(')'); 
                code.emit(OpCode.pop);
                break;
    
                case Tag.FOR:  // for (K 
                move();
                match('(');
                recover(loop);
                break;
    
                case Tag.IF: // due casi, se vero o
                move();
                match('(');
                int scelta[] = bexpr(loop);
                match(')');
                stat();
                code.emitLabel(scelta[0]);
                recovertwo();
                break;
    
                case '{': // SALTI PRIMA DI FINIRE IL FOR Crea nuova label
                move();
                int lnext_prog = code.newLabel(); 
                statlist(lnext_prog);
                code.emit(OpCode.GOto, lnext_prog);
                code.emitLabel(lnext_prog);
                match('}');
                break;
    
                default:
                error("Errore su stat()");
    
            }
    
        }
        public void recover(int loop) {  

            switch(look.tag){
                
                case Tag.ID:
                    int id_addr = st.lookupAddress(((Word)look).lexeme); // prende indirizzo della parola
                if (id_addr==-1) {
                    id_addr = count;
                    st.insert(((Word)look).lexeme,count++);
                }
                match(Tag.ID);
    
                    match(Word.init.tag);
                    expr();
                    match(';');
                    bexpr(loop);
                    match(')');
                    match(Tag.DO);
                    stat();
                    code.emitLabel(loop);   
                    break;
    
                case Tag.RELOP:
                    int scelta[] = bexpr(loop);
                    match(')');
                    match(Tag.DO);
                    stat();
                    code.emit(OpCode.GOto, loop);
                    code.emitLabel(scelta[0]);
                    break;
    
                default:
                    error("error in temp");
            }
            
    
        }
    
        public void recovertwo() {
    
            switch (look.tag) {
    
                case Tag.ELSE:
                move();
                stat();
                match(Tag.END);
                break;
    
                case Tag.END:
                move();
                break;
    
    
                default:
                    error("Errore su recovertwo()");
            }
    
        }
    
        public void assignlist() {
    
            match('[');
            expr();
            match(Tag.TO);
            idlist(1);
            match(']');
            assignlistp();
    
        }
    
        public void assignlistp() {
    
            if (look.tag == '[') {
                move();
                expr(); 
                match(Tag.TO);
                idlist(1);
                match(']');
                assignlistp();
    
            } else if (look.tag == Tag.END | look.tag == Tag.ELSE | look.tag == ';') {} else error("Errore su assignlistp()");
    
        }
        public void idlist(int caso) { // aggiungi insieme guida

            switch (look.tag) {
    
                case Tag.ID:
                int id_addr = st.lookupAddress(((Word)look).lexeme); // prende indirizzo della parola
                if (id_addr==-1) {
                    id_addr = count;
                    st.insert(((Word)look).lexeme,count++);
                }
                code.emit(OpCode.dup);
                code.emit(OpCode.istore, id_addr);            
                match(Tag.ID);
                idlistp(caso);
                break;
            
                default: error("Errore su idlist()");
            }
            
    
        }
    
        public void idlistp(int caso) {
        
        if (look.tag == ',') {
            move();
            int id_addr = st.lookupAddress(((Word)look).lexeme); // prende indirizzo della parola
                if (id_addr==-1) {
                    id_addr = count;
                    st.insert(((Word)look).lexeme,count++);
                }
                if (caso == 1) {
    
                    code.emit(OpCode.dup);
                    code.emit(OpCode.istore, id_addr); // qua nuovo indirizzo
    
                } else {
    
                    code.emit(OpCode.pop);
                    code.emit(OpCode.invokestatic, caso);
                    code.emit(OpCode.dup);
                    code.emit(OpCode.istore, id_addr);
    
                }
            
            match(Tag.ID);
            idlistp(caso);
    
        } else if (look.tag == ')' | look.tag == ']') {} else error("Errore su idlistp()");
    
        }
        
        public int[] bexpr(int loop) { // il tag relop contiene tutti i confronti
     
                int [] scelta = new int[2];
                String relop = ((Word)look).lexeme;
                match(Tag.RELOP);
                int id_addr_true = expr();
                int id_addr_false = expr();
    
                scelta[0] = id_addr_false;
                scelta[1] = id_addr_true;
                code.emitLabel(loop);
                switch(relop) {
    
                    case ">":
                    code.emit(OpCode.if_icmpgt, id_addr_true);
                    break;
    
                    case "<":               
                    code.emit(OpCode.if_icmplt, id_addr_true);
                    break;
    
                    case ">=":
                    code.emit(OpCode.if_icmpge, id_addr_true);
                    break;
    
                    case "<=":
                    code.emit(OpCode.if_icmplt, id_addr_true);
                    break;
    
                    case "<>":
                    code.emit(OpCode.if_icmpne, id_addr_true);
    
                    break;

                    case "==":
                code.emit(OpCode.if_icmpeq, id_addr_true);
                break;

                default: 
                break;
            }

            code.emit(OpCode.GOto, id_addr_false);
            code.emitLabel(id_addr_true);

            return scelta;
    }

    public int expr() {

        int lnext_prog = code.newLabel();

        switch(look.tag){
            case '+':
                move();
                match('(');
                exprlist();
                match(')');
                code.emit(OpCode.iadd);
                break;

            case '-':
                move();
                expr();
                expr();
                code.emit(OpCode.isub);
                break;

            case '*':
                move();
                match('(');
                exprlist();
                match(')');
                code.emit(OpCode.imul);
                break;

            case '/':
                move();
                expr();
                expr();
                code.emit(OpCode.idiv);
                break;

            case Tag.NUM:
                code.emit(OpCode.ldc, ((NumberTok) look).lexeme);
                move();
                break;

            case Tag.ID:
            int id_addr = st.lookupAddress(((Word)look).lexeme); // prende indirizzo della parola
            if (id_addr==-1) { // se indirizzo non esiste lo crea
                id_addr = count;
                st.insert(((Word)look).lexeme,count++);
            }
            match(Tag.ID);
            code.emit(OpCode.iload, id_addr);
            break;

            default:
                error("Errore in expr()");
        }

        return lnext_prog;
    }

    public void exprlist() {

        if (look.tag == '+'| look.tag == '-' |look.tag == '*' |look.tag == '/' | look.tag == Tag.ID |look.tag == Tag.NUM) {

            expr();
            exprlistp();

        } else error("Errore su exprlist()");

    }

    public void exprlistp() {

        switch (look.tag) {

        case ',':
        code.emit(OpCode.invokestatic, 1);
        move();
        expr();
        exprlistp();
        break;

        case ')':
        break;

        default: error("Errore su exprlistp()");
        }

    }

    public static void main(String[] args) {
        Lexer lex = new Lexer(); // alla fine analisi lessicale --> analisi semantica
        
        String path = "lezione7l/other/translator.txt"; // il percorso del file da leggere

        try {
            BufferedReader br = new BufferedReader(new FileReader(path));

            Translator traduttore = new Translator(lex, br);

            traduttore.prog(); // entra nel metodo, se da errore essendo un try/catch esce e chatca l'errore

            System.out.println("Input OK");

            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }

    
    
    
    
}
