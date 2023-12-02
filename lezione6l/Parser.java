package lezione6l;

import java.io.*;

import lezione6l.Tag;
import lezione6l.Token;

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
        int expr_val;
        expr_val=expr();
        match(Tag.EOF);
        System.out.println(expr_val);
    }
	else
        error("No such guide for start");
    }

    private int expr() 
    {
	    // ... completare ...
        int term_val,exprp_val;
        if(look.tag==Tag.NUM || look.tag==Token.lpt.tag)
        {
            term_val=term();
            exprp_val=exprp();
            return exprp_val;
        }
        else
            error("No such guide for expr");
    
    
    }

    private int exprp() 
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
        String path = "lezione6l/lexer.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}