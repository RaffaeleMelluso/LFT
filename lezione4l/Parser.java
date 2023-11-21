package lezione4l;

import java.io.*;

import lezione4l.Tag;
import lezione4l.Token;

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

    public void start() {
	// ... completare ...
    if(look.tag==Token.lpt.tag || look.tag==Tag.NUM)
    {
        expr();
        match(Tag.EOF);
    }
	else
        error("No such guide for start");
	// ... completare ...
    }

    private void expr() 
    {
	    // ... completare ...
        if(look.tag==Tag.NUM || look.tag==Token.lpt.tag)
        {
            term();
            exprp();
        }
        else
            error("No such guide for expr");
    
    
    }

    private void exprp() 
    {
        switch (look.tag) 
        {
            case '+':
                match(Token.plus.tag);
                term();
                exprp();

                break;
            case '-':
                match(Token.minus.tag);
                term();
                exprp();


                break;
            case Tag.EOF:
                break;
            
            case ')':
                break;
            default:
                error("No such guide for fact");
                break;
            // ... completare ...
        }
    }

    private void term() 
    {
        if(look.tag==Tag.NUM || look.tag==Token.lpt.tag)
        {
            fact();
            termp();       
         }
        else
            error("No such guide for term");
        // ... completare ...
    }

    private void termp() 
    {
        switch (look.tag) 
        {
            case '*':
                match(Token.mult.tag);
                fact();
                termp();

                break;
            case '/':
                match(Token.div.tag);
                fact();
                termp();


                break;
            case Tag.EOF:
                break;
            case ')':
                break;
            case '+':
                break;
            case '-':
                break;
            default:
                error("No such guide for fact");
                break;
        }
        // ... completare ...
    }

    private void fact() 
    {
        switch (look.tag) 
        {
            case '(':
                match(Token.lpt.tag);
                expr();
                match(Token.rpt.tag);
                break;
            
            case Tag.NUM:
                match(Tag.NUM);
                break;
            default:
                error("No such guide for fact");
        }
        // ... completare ...
    }
		
    public static void main(String[] args) 
    {
        Lexer lex = new Lexer();
        String path = "lezione4l/lexer.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}
//ciao