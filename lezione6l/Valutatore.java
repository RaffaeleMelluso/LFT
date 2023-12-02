package lezione6l;
import java.io.*;



public class Valutatore {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Valutatore(Lexer l, BufferedReader br) { 
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
	throw new Error("near line " + Lexer.line + ": " + s);
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
        if(look.tag==Token.lpt.tag || look.tag==Tag.NUM)
        {
            int expr_val;
            expr_val=expr();
            match(Tag.EOF);
            System.out.println(expr_val);
        }
        else
            error("No such guide for start");

	// ... completare ...
    }

    private int expr() { 
	int term_val, exprp_val, expr_val=0,exprp_i;

    if(look.tag==Tag.NUM || look.tag==Token.lpt.tag)
    {
        term_val=term();
        exprp_i=term_val;
        exprp_val=exprp(exprp_i);
        expr_val=exprp_val;
        
    }
    else
    {
        error("No such guide for expr");
        
    }
        
    return expr_val;
	
    }

    private int exprp(int exprp_i) {
	int term_val, exprp_val=0, exprp1_i, exprp1_val;
	switch (look.tag) {
	case '+':
        match('+');
        term_val = term();
        exprp1_i = exprp_i+term_val;
        exprp1_val = exprp(exprp1_i);
        exprp_val = exprp1_val;
        break;

    case '-':
        match(Token.minus.tag);
        term_val=term();
        exprp1_i= exprp_i-term_val;
        exprp1_val = exprp(exprp1_i);
        exprp_val = exprp1_val;
        break;
    case Tag.EOF:
        exprp_val = exprp_i;
        break;
        
    case ')':
        exprp_val = exprp_i;
        break;
    default:
        error("No such guide for fact");
        
	}
    return exprp_val;
    }

    private int term() { 
	// ... completare ...
        int fact_val, termp_i, termp_val, term_val;
        fact_val=fact();
        termp_i=fact_val;
        termp_val=termp(termp_i);
        term_val=termp_val;
        return term_val;
    }
    
    private int termp(int termp_i) { 
        int fact_val, termp1_i, termp1_val, termp_val=0;
        switch (look.tag) 
        {
            case '*':
                match(Token.mult.tag);
                fact_val=fact();
                termp1_i=termp_i*fact_val;
                termp1_val=termp(termp1_i);
                termp_val=termp1_val;
                break;
            case '/':
                match(Token.div.tag);
                fact_val=fact();
                termp1_i=termp_i/fact_val;
                termp1_val=termp(termp1_i);
                termp_val=termp1_val;
                break;
            case Tag.EOF:
                termp_val=termp_i;
                break;
            case ')':
                termp_val=termp_i;
                break;
            case '+':
                termp_val=termp_i;
                break;
            case '-':
                termp_val=termp_i;
                break;
            default:
                error("No such guide for fact");
                
        }
        return termp_val;
    }
    
    private int fact() { 
        int expr_val, fact_val=0;
        switch (look.tag) 
        {
            case '(':
                match(Token.lpt.tag);
                expr_val=expr();
                fact_val=expr_val;
                match(Token.rpt.tag);
                break;
            case Tag.NUM:
                fact_val=((NumberTok)look).lexeme;
                match(Tag.NUM);
                break;
            default:
                error("No such guide for fact");
                
        }
        return fact_val;
    }
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "lezione6l/lexer.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Valutatore valutatore = new Valutatore(lex, br);
            valutatore.start();
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}
