//Esercizio 5.1
package lezione7l;
import lezione7l.other.*;
import java.io.*;
public class Translator {
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

    public void prog() {        
    if(look.tag==Word.assign.tag || look.tag==Word.print.tag || look.tag==Word.read.tag || 
       look.tag==Word.fortok.tag || look.tag==Word.iftok.tag || look.tag==Token.lpg.tag)
    {
        int lnext_prog = code.newLabel();
        statlist(lnext_prog);
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
           stat();
           statlistp();
        }
        else
            error("No such guide for statlist");
    
    
    }
    private void statlistp() 
    {
        if(look.tag==Token.semicolon.tag)
        {
           match(Token.semicolon.tag);
           stat();
           statlistp();
        }
        else if(look.tag==Token.rpg.tag || look.tag==Tag.EOF)
        {

        }
        else
            error("No such guide for statlistp");
    
    
    }
    public void stat( /* completare */ ) {
        switch (look.tag) 
        {
            case Tag.ASSIGN:
                match(Word.assign.tag);
                assignlist();
                break;
            case Tag.PRINT:
                match(Word.print.tag);
                match(Token.lpt.tag);
                exprlist();
                match(Token.rpt.tag);
                break;
            case Tag.READ:
                match(Tag.READ);
                match('(');
	            idlist(/* completare */);
                match(')');
            
            case Tag.FOR:
                match(Word.fortok.tag);
                match(Token.lpt.tag);
                A();
                break;
            case Tag.IF:
                match(Word.iftok.tag);
                match(Token.lpt.tag);
                bexpr();
                match(Token.rpt.tag);
                stat();
                B();
                break;
            case '{':
                match(Token.lpg.tag);
                statlist();
                match(Token.rpg.tag);
                break;

            default:
                error("No such guide for stat");
                break;
            
        }
     }
     private void A()
     {
         switch (look.tag) {
             case Tag.ID:
                 match(Tag.ID);
                 match(Word.init.tag);
                 expr();
                 match(Token.semicolon.tag);
                 bexpr();
                 match(Token.rpt.tag);
                 match(Word.dotok.tag);
                 stat();
                 break;
             case Tag.RELOP:
                 bexpr();
                 match(Token.rpt.tag);
                 match(Word.dotok.tag);
                 stat();
                 break;
             default:
                 error("No such guide for stat");
                 break;
         }
     }
     private void B()
     {
         if(look.tag== Word.elsetok.tag)
         {
             match(Word.elsetok.tag);
             stat();
             match(Word.end.tag);
         }
         else if(look.tag==Word.end.tag){
             match(Word.end.tag);
         }
         else
             error("No such guide for stat");
                 
             
     }
    private void idlist(/* completare */) {
        switch(look.tag) {
	    case Tag.ID:
        	int id_addr = st.lookupAddress(((Word)look).lexeme);
                if (id_addr==-1) {
                    id_addr = count;
                    st.insert(((Word)look).lexeme,count++);
                }
                match(Tag.ID);
	// ... completare ...
    	}
    }

    private void expr( /* completare */ ) {
        switch(look.tag) {
	// ... completare ...
            case '-':
                match('-');
                expr();
                expr();
                code.emit(OpCode.isub);
                break;
	// ... completare ...
        }
    }

// ... completare ...
}
