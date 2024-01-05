//Esercizio 5.1
package lezione7l;
import lezione7l.other.*;
import java.io.*;
public class Translator 
{
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;
    
    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count=0;

    public Translator(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() 
    {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
	throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) 
    {
        if (look.tag == t) 
        {
            if (look.tag != Tag.EOF) 
                move();
        } 
        else error("syntax error");
    }
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "lezione7l/other/translator.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Translator parser = new Translator(lex, br);
            parser.prog();
            System.out.println("Input OK");
            br.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            
        }
    }
    public void prog() {        
    if(look.tag==Word.assign.tag || look.tag==Word.print.tag || look.tag==Word.read.tag || 
       look.tag==Word.fortok.tag || look.tag==Word.iftok.tag || look.tag==Token.lpg.tag)
    {
        int lnext_prog = code.newLabel();
        statlist(lnext_prog);
        code.emit(OpCode.GOto,lnext_prog);
        code.emitLabel(lnext_prog);
        match(Tag.EOF);
        try {
        	code.toJasmin();
        }
        catch(java.io.IOException e) {
        	System.out.println("IO error\n");
        };
    }
    else
        error("No such guide for prog");
        
    }

    private void statlist(int lnext_prog) 
    {
        if(look.tag==Word.assign.tag || look.tag==Word.print.tag || look.tag==Word.read.tag || 
            look.tag==Word.fortok.tag || look.tag==Word.iftok.tag || look.tag==Token.lpg.tag)
        {
            lnext_prog = code.newLabel(); // viene incrementata l'etichetta
            stat(lnext_prog);
            code.emitLabel(lnext_prog);
            statlistp(lnext_prog);
        }
        else
            error("No such guide for statlist");
    
    
    }
    private void statlistp(int lnext_prog) 
    {
        if(look.tag==Token.semicolon.tag)
        {
           
           match(Token.semicolon.tag);
           stat(lnext_prog);
           statlistp(lnext_prog);
        }
        else if(look.tag==Token.rpg.tag || look.tag==Tag.EOF)
        {

        }
        else
            error("No such guide for statlistp");
    
    
    }
    public void stat(int lnext_prog) {

        int loop = code.newLabel();
        switch (look.tag) 
        {
            case Tag.ASSIGN:
                match(Word.assign.tag);
                assignlist();
                code.emit(OpCode.pop);
            break;
            case Tag.PRINT:
                match(Word.print.tag);
                match(Token.lpt.tag);
                exprlist();
                code.emit(OpCode.invokestatic, 1);
                match(Token.rpt.tag);
                break;
            case Tag.READ:
                match(Tag.READ);
                match(Token.lpt.tag);
	            code.emit(OpCode.invokestatic, 0);// codice 0 read        
                idlist(0);
                match(Token.rpt.tag);
                code.emit(OpCode.pop);
                break;
            
            case Tag.FOR:
                match(Word.fortok.tag);
                match(Token.lpt.tag);
                A(loop);
                break;
            case Tag.IF:
                match(Word.iftok.tag);
                match(Token.lpt.tag);
                int scelta[] = bexpr(loop);
                match(Token.rpt.tag);
                stat(lnext_prog);
                code.emit(OpCode.GOto, lnext_prog);
                code.emitLabel(scelta[0]);
                B(loop);
                break;
            case '{':
                match(Token.lpg.tag);
                lnext_prog = code.newLabel(); 
                statlist(lnext_prog);
                code.emit(OpCode.GOto, lnext_prog);
                code.emitLabel(lnext_prog);
                match(Token.rpg.tag);
                break;

            default:
                error("No such guide for stat");
                break;
            
        }
     }
     private void A(int loop)
     {
        int lnext_prog;
        int scelta[];
         switch (look.tag) {
             case Tag.ID:
                 
                int id_addr = st.lookupAddress(((Word)look).lexeme);
                if (id_addr==-1) {
                    id_addr = count;
                    st.insert(((Word)look).lexeme,count++);
                }
                match(Tag.ID);
                match(Word.init.tag);
                expr();
                match(Token.semicolon.tag);
                scelta=bexpr(loop);
                match(Token.rpt.tag);
                match(Word.dotok.tag);
                stat(loop);
                code.emit(OpCode.GOto, loop);
                code.emitLabel(scelta[0]);
                break;
             case Tag.RELOP:
                scelta= bexpr(loop);
                match(Token.rpt.tag);
                match(Word.dotok.tag);
                lnext_prog = code.newLabel();
                stat(lnext_prog);
                code.emit(OpCode.GOto, loop);
                code.emitLabel(scelta[0]);
                break;
             default:
                 error("No such guide for stat");
                 break;
         }
     }
     private void B(int loop)
     {
        if(look.tag== Word.elsetok.tag)
         {
            match(Word.elsetok.tag);
            int lnext_prog=code.newLabel();
            stat(lnext_prog);

            match(Word.end.tag);
         }
        else if(look.tag==Word.end.tag)
        {
            match(Word.end.tag);
        }
        else
            error("No such guide for stat");
                 
             
     }
     private void assignlist() 
     {
         if(look.tag==Token.lpq.tag)
         {
            match(Token.lpq.tag);
            expr();
            match(Word.to.tag);
            idlist(0);
            match(Token.rpq.tag);
            assignlistp();
         }
         else
            error("No such guide for assignlist");
         
     }
    private void assignlistp()
    {
        if(look.tag==Token.lpq.tag)
        {
            match(Token.lpq.tag);
            expr();
            match(Word.to.tag);
            idlist(1);
            match(Token.rpq.tag);
            assignlistp();
        }
        else if(look.tag==Word.end.tag || look.tag==Tag.EOF || look.tag==Token.rpg.tag || look.tag==Token.semicolon.tag)
        {}
        else
            error("No such guide for assignlistp");
    }
    private void idlist(int caso) {
        switch(look.tag) {
	    case Tag.ID:
        	int id_addr = st.lookupAddress(((Word)look).lexeme);
            if (id_addr==-1) 
            {
                id_addr = count;
                st.insert(((Word)look).lexeme,count++);
            }
            if (caso == 1) {
    
                code.emit(OpCode.dup);
                code.emit(OpCode.istore, id_addr); // qua nuovo indirizzo
                code.emit(OpCode.dup);
            }
            else
            {
                code.emit(OpCode.istore, id_addr);
                caso=1;

            }
            
            match(Tag.ID);
            idlistp(caso);
        break;
        default:
            error("No such guide for idlist");
        
    	}
    }
    private void idlistp(int caso)
    {
        if(look.tag==Token.comma.tag)
        {
            
            match(Token.comma.tag);

            int id_addr = st.lookupAddress(((Word)look).lexeme);
            if (id_addr==-1) 
            {
                id_addr = count;
                st.insert(((Word)look).lexeme,count++);
            }
            if (caso == 1) {
    
                code.emit(OpCode.dup);
                code.emit(OpCode.istore, id_addr); // qua nuovo indirizzo

            }
            else
            {
                code.emit(OpCode.pop);
                code.emit(OpCode.invokestatic, caso);
                code.emit(OpCode.dup);
                code.emit(OpCode.istore, id_addr);
            }
            match(Tag.ID);
            idlistp(caso);
        }
        else if(look.tag==Token.rpq.tag || look.tag==Token.rpt.tag)
        {}
        else
            error("No such guide for idlistp");
    }
    private int[] bexpr(int loop)
    {
        int [] scelta = new int[2];
        String relop = ((Word)look).lexeme;
        if(look.tag==Tag.RELOP)
        {
            
            match(Tag.RELOP);
            int id_add_true=expr();
            int id_add_false=expr();
            scelta[0] = id_add_false;
            scelta[1] = id_add_true;
            code.emitLabel(loop);
            switch(relop) 
            {
    
                case ">":
                code.emit(OpCode.if_icmpgt, id_add_true);
                break;
    
                case "<":               
                code.emit(OpCode.if_icmplt, id_add_true);
                break;
    
                case ">=":
                code.emit(OpCode.if_icmpge, id_add_true);
                break;

                case "<=":
                code.emit(OpCode.if_icmplt, id_add_true);
                break;

                case "<>":
                code.emit(OpCode.if_icmpne, id_add_true);
                break;

                case "==":
                code.emit(OpCode.if_icmpeq, id_add_true);
                break;

                default: 
                break;
            }
            code.emit(OpCode.GOto, id_add_false);
            code.emitLabel(id_add_true);
            

        }
        else
            error("No such guide for bexpr");

        return scelta;
    }

    private int expr() {
        int lnext_prog = code.newLabel();
        switch(look.tag) {
            case '+':
                match(Token.plus.tag);
                match(Token.lpt.tag);
                exprlist();
                code.emit(OpCode.iadd);
                match(Token.rpt.tag);
                break;
            case '*':
                match(Token.mult.tag);
                match(Token.lpt.tag);
                exprlist();
                code.emit(OpCode.imul);
                match(Token.rpt.tag);
                break;
            case '-':
                match(Token.minus.tag);
                expr();
                expr();
                code.emit(OpCode.isub);
                break;
            case '/': 
                match(Token.div.tag);
                expr();
                expr();
                code.emit(OpCode.idiv);
                break;
            case Tag.NUM:
                code.emit(OpCode.ldc, ((NumberTok)look).lexeme);
                match(Tag.NUM);
                break;
            case Tag.ID:
                int id_addr = st.lookupAddress(((Word)look).lexeme);
                if (id_addr==-1) 
                {
                    id_addr = count;
                    st.insert(((Word)look).lexeme,count++);
                }
                code.emit(OpCode.iload,id_addr);
                match(Tag.ID);
                break;
            default:
                error("No such guide for expr");
                break;
        }
        return lnext_prog;
    }
    private void exprlist() 
    {
        switch (look.tag) 
        {
            case '+':
            case '*':
            case '-':
            case '/':
            case Tag.NUM:
            case Tag.ID:
                expr();
                exprlistp();
                break;
            default:
                error("No such guide for exprlist");
                break;
        }
        
    }
	private void exprlistp()
    {
        if(look.tag==Token.comma.tag)
        {
            match(Token.comma.tag);
            expr();
            exprlistp();
        }
        else if(look.tag==Token.rpt.tag)
        {}
        else 
            error("No such guide for exprlistp");
    }
    
}