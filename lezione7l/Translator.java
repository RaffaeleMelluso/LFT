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
           
           stat(lnext_prog);
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
        switch (look.tag) 
        {
            case Tag.ASSIGN:
                lnext_prog = code.newLabel();
                match(Word.assign.tag);
                assignlist(lnext_prog);
                code.emitLabel(lnext_prog);
                break;
            case Tag.PRINT:
                lnext_prog = code.newLabel();
                match(Word.print.tag);
                match(Token.lpt.tag);
                exprlist();
                match(Token.rpt.tag);
                code.emit(OpCode.invokestatic,1); 
                code.emit(OpCode.GOto,lnext_prog);
                code.emitLabel(lnext_prog);
                break;
            case Tag.READ:
                lnext_prog = code.newLabel();
                match(Tag.READ);
                match('(');
	            idlist(lnext_prog);
                match(')');
                code.emit(OpCode.invokestatic,0);



                code.emit(OpCode.istore);
                code.emit(OpCode.GOto,lnext_prog);
                code.emitLabel(lnext_prog);
                break;
            
            case Tag.FOR:
                lnext_prog = code.newLabel();
                match(Word.fortok.tag);
                match(Token.lpt.tag);
                
                A(lnext_prog);
                
                break;
            case Tag.IF:
                lnext_prog = code.newLabel();
                match(Word.iftok.tag);
                match(Token.lpt.tag);
                bexpr(lnext_prog);
                match(Token.rpt.tag);
                stat(lnext_prog);
                B(lnext_prog);
                break;
            case '{':
                lnext_prog = code.newLabel();
                match(Token.lpg.tag);
                statlist(lnext_prog);
                match(Token.rpg.tag);
                break;

            default:
                error("No such guide for stat");
                break;
            
        }
     }
     private void A(int lnext_prog)
     {
         switch (look.tag) {
             case Tag.ID:
                 
                int id_addr = st.lookupAddress(((Word)look).lexeme);
                if (id_addr==-1) {
                    id_addr = count;
                    st.insert(((Word)look).lexeme,count++);
                }
                code.emit(OpCode.iload,id_addr);
                match(Tag.ID);
                 match(Word.init.tag);
                 expr();
                 match(Token.semicolon.tag);
                 bexpr(lnext_prog);
                 match(Token.rpt.tag);
                 match(Word.dotok.tag);
                 stat(lnext_prog);
                 break;
             case Tag.RELOP:
                 
                 bexpr(lnext_prog);
                 match(Token.rpt.tag);
                 match(Word.dotok.tag);
                 stat(lnext_prog);
                 break;
             default:
                 error("No such guide for stat");
                 break;
         }
     }
     private void B(int lnext_prog)
     {
         if(look.tag== Word.elsetok.tag)
         {
             
             match(Word.elsetok.tag);
             stat(lnext_prog);
             match(Word.end.tag);
         }
         else if(look.tag==Word.end.tag){
             
             match(Word.end.tag);
         }
         else
             error("No such guide for stat");
                 
             
     }
     private void assignlist(int lnext_prog) 
     {
         if(look.tag==Token.lpq.tag)
         {
             match(Token.lpq.tag);
             expr();
             match(Word.to.tag);
             idlist(lnext_prog);
             match(Token.rpq.tag);
             assignlistp(lnext_prog);
             code.emit(OpCode.GOto, lnext_prog);
         }
         else
             error("No such guide for assignlist");
         
     }
    private void assignlistp(int lnext_prog)
    {
        if(look.tag==Token.lpq.tag)
        {
            match(Token.lpq.tag);
            expr();
            match(Word.to.tag);
            idlist(lnext_prog);
            match(Token.rpq.tag);
            assignlistp(lnext_prog);
        }
        else if(look.tag==Word.end.tag || look.tag==Tag.EOF || look.tag==Token.rpg.tag || look.tag==Token.semicolon.tag)
        {
            
        }
        else
            error("No such guide for assignlistp");
    }
    private void idlist(int lnext_prog) {
        switch(look.tag) {
	    case Tag.ID:
        	int id_addr = st.lookupAddress(((Word)look).lexeme);
                if (id_addr==-1) {
                    id_addr = count;
                    st.insert(((Word)look).lexeme,count++);
                }
                code.emit(OpCode.istore,id_addr);
                match(Tag.ID);
                idlistp(lnext_prog);
        break;
        default:
            error("No such guide for idlist");
        
    	}
    }
    private void idlistp(int lnext_prog)
    {
        if(look.tag==Token.comma.tag)
        {
            
            match(Token.comma.tag);

            int id_addr = st.lookupAddress(((Word)look).lexeme);
                if (id_addr==-1) {
                    id_addr = count;
                    st.insert(((Word)look).lexeme,count++);
                }
            code.emit(OpCode.istore,id_addr);
            match(Tag.ID);
            idlistp(lnext_prog);
        }
        else if(look.tag==Token.rpq.tag || look.tag==Token.rpt.tag)
        {
            
        }
        else
            error("No such guide for idlistp");
    }
    private void bexpr(int lnext_prog)
    {
        if(look.tag==Tag.RELOP)
        {
            
            match(Tag.RELOP);
            expr();
            expr();
        }
        else
            error("No such guide for bexpr");
    }

    private void expr() {
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
                match('-');
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
                if (id_addr==-1) {
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