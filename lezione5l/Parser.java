package lezione5l; //parser 3.2

import java.io.*;

import lezione5l.Tag;
import lezione5l.Token;

public class Parser 
{
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser(Lexer l, BufferedReader br) 
    {
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
        statlist();
        match(Tag.EOF);
    }
	else
        error("No such guide for prog");
	
    }

    private void statlist() 
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

    private void stat() 
    {
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
                match(Token.lpt.tag);
                idlist();
                match(Token.rpt.tag);
                break;
            
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

    private void assignlist() 
    {
        if(look.tag==Token.lpq.tag)
        {
            match(Token.lpq.tag);
            expr();
            match(Word.to.tag);
            idlist();
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
            idlist();
            match(Token.rpq.tag);
            assignlistp();
        }
        else if(look.tag==Word.end.tag || look.tag==Tag.EOF || look.tag==Token.rpg.tag || look.tag==Token.semicolon.tag)
        {
            
        }
        else
            error("No such guide for assignlistp");
    }
    private void idlist()
    {
        if(look.tag==Tag.ID)
        {
            match(Tag.ID);
            idlistp();
        }
        else
            error("No such guide for idlist");

    }
    private void idlistp()
    {
        if(look.tag==Token.comma.tag)
        {
            match(Token.comma.tag);
            match(Tag.ID);
            idlistp();
        }
        else if(look.tag==Token.rpq.tag || look.tag==Token.rpt.tag)
        {

        }
        else
            error("No such guide for idlistp");
    }
    private void bexpr()
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
    private void expr() 
    {
        switch (look.tag) 
        {
            case '+':
                match(Token.plus.tag);
                match(Token.lpt.tag);
                exprlist();
                match(Token.rpt.tag);

                break;
            case '*':
                match(Token.mult.tag);
                match(Token.lpt.tag);
                exprlist();
                match(Token.rpt.tag);
                break;
            case '-':
                match(Token.minus.tag);
                expr();
                expr();
                break;
            case '/':
                match(Token.div.tag);
                expr();
                expr();
                break;
            case Tag.NUM:
                match(Tag.NUM);
                break;
            case Tag.ID:
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
    public static void main(String[] args) 
    {
        Lexer lex = new Lexer();
        String path = "lezione5l/lexer.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}
//ciao