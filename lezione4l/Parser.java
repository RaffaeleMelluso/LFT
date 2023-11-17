package lezione4l;

import java.io.*;

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

    void match(int t) {
	if (look.tag == t) {
	    if (look.tag != Tag.EOF) move();
	} else error("syntax error");
    }

    public void start() {
	// ... completare ...
	expr();
	match(Tag.EOF);
	// ... completare ...
    }

    private void expr() {
	// ... completare ...
    term();
    exprp();
    }

    private void exprp() {
	switch (look.tag) {
	case '+':
        term();
        exprp();

        break;
    case '-':
        term();
        exprp();


        break;
    case -1:
        break;
	// ... completare ...
	}
    }

    private void term() {
        fact();
        termp();
        // ... completare ...
    }

    private void termp() {
        switch (look.tag) 
        {
            case '*':
                term();
                exprp();

                break;
            case '/':
                term();
                exprp();


                break;
            case -1:
                break;
        }
        // ... completare ...
    }

    private void fact() {
        switch (look.tag) {
            case '(':
                expr();
                match(')');
                break;
            
            case 19:
                
                break;
        }
        // ... completare ...
    }
		
    public static void main(String[] args) {
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