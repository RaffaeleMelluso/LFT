package lezione3l;

import java.io.*; 
import java.util.*;

public class Lexer {

    public static int line = 1;
    private char peek = ' ';
    
    private void readch(BufferedReader br) {
        try {
            
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }
    private void setcursor(BufferedReader br){
        try {
            br.mark(1);
        } catch (IOException exc) {
            peek = (char) -1; // error
        }
    }
    private void resetch(BufferedReader br){
        try {
            br.reset();
        } catch (IOException exc) {
            peek = (char) -1; // error
        }
    }

    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') {
            if (peek == '\n') line++;
            readch(br);
        }
        String s="";
        switch (peek) {
            case '!':
                peek = ' ';
                return Token.not;
            case '(':
                peek = ' ';
                return Token.lpt;
            case ')':
                peek = ' ';
                return Token.rpt;
            case '[':
                peek = ' ';
                return Token.lpq;
            case ']':
                peek = ' ';
                return Token.rpq;
            case '{':
                peek = ' ';
                return Token.lpg;
            case '}':
                peek = ' ';
                return Token.rpg;
            case '+':
                peek = ' ';
                return Token.plus;
            case '-':
                peek = ' ';
                return Token.minus;
            case '*':
                peek = ' ';
                return Token.mult;
            case '/':  //controlla anche commenti
                
                readch(br);
                if(peek=='*') //caso commento lungo /* */
                {
                    readch(br);
                    while(peek!='*')
                    {
                        readch(br);
                    }
                    if(peek=='/')
                        return (Token) null;

                }
                else if(peek=='/') //caso commento corto
                {
                    while(peek!='\n' || peek==(char)-1)
                        readch(br);
                    if(peek==(char)-1)
                        return new Token(Tag.EOF);
                    else
                    {
                        peek=' ';
                    }
                        
                }
                else
                {
                    peek = ' ';
                    return Token.div;
                }
                
            case ';':
                peek = ' ';
                return Token.semicolon;
            case ',':
                peek = ' ';
                return Token.comma;
            

	// ... gestire i casi di ( ) [ ] { } + - * / ; , ... //
	
            case '&':
                readch(br);
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    System.err.println("Erroneous character"
                            + " after & : "  + peek );
                    return null;
                }
            case '|':
                readch(br);
                if (peek == '|') {
                    peek = ' ';
                    return Word.or;
                } else {
                    System.err.println("Erroneous character"
                            + " after | : "  + peek );
                    return null;
                }
            case '<':
                readch(br);
                if(peek== ' ' || Character.isDigit(peek) || Character.isAlphabetic(peek))
                    {
                        peek=' ';
                        return Word.lt;
                    }
                else if(peek=='=')
                    {
                        peek=' ';
                        return Word.le;
                    }
                else if(peek=='>')
                    {
                        peek=' ';
                        return Word.ne;
                    }
                else{
                    System.err.println("Erroneous character"
                            + " after < : "  + peek );
                    return null;
                }
            
            case '>':
                readch(br);
                if(peek== ' ' || Character.isDigit(peek) || Character.isLetter(peek))
                {
                    peek=' ';
                    return Word.gt;
                }
                    
                else if(peek=='=')
                {
                    peek = ' ';
                    return Word.ge;
                }
                else{
                    System.err.println("Erroneous character"
                            + " after > : "  + peek );
                    return null;
                }
            
            case '=':
                readch(br);
                if(peek=='=')
                {
                    peek=' ';
                    return Word.eq;
                }
                else{
                    System.err.println("Erroneous character"
                            + " after = : "  + peek );
                    return null;
                }

	// ... gestire i casi di || < > <= >= == <> ... //
          
            case (char)-1:
                return new Token(Tag.EOF);

            default:
                if (Character.isLetter(peek) || peek=='_') {
                    s="";
                    int c=0;
                    while(Character.isLetter(peek) || Character.isDigit(peek) || peek=='_')
                    {
                        if(peek=='_')
                            c++;
                        s+=peek;
                        readch(br);
                    }
                    if(s.equals("assign"))
                    {
                        peek=' ';
                        return Word.assign;
                    }
                    else if(s.equals("to"))
                    {
                        peek=' ';
                        return Word.to;
                    }
                    else if(s.equals("if"))
                    {
                        peek=' ';
                        return Word.iftok;
                    }
                    else if(s.equals("else"))
                    {
                        peek=' ';
                        return Word.elsetok;
                    }
                    else if(s.equals("do"))
                    {
                        peek=' ';
                        return Word.dotok;

                    }
                    else if(s.equals("for"))
                    {
                        peek=' ';
                        return Word.fortok;
                    }
                    else if(s.equals("begin"))
                    {
                        peek=' ';
                        return Word.begin;
                    }
                    else if(s.equals("end"))
                    {
                        peek=' ';
                        return Word.end;
                    }
                    else if(s.equals("print"))
                    {
                        peek=' ';
                        return Word.print;
                    }
                    else if(s.equals("read"))
                    {
                        peek=' ';
                        return Word.read;
                    }
                    else if(!Character.isDigit(s.charAt(0)) && c<s.length())
                    {
                            peek=' ';
                            return new Word(Tag.ID, s);
                    }
                    else
                    {
                        System.err.println("Erroneous id or keyword "  + s );
                        return null;   
                    }

                
	// ... gestire il caso degli identificatori e delle parole chiave //

                } 
                else if (Character.isDigit(peek)) 
                {
                    s="";
                    while(Character.isDigit(peek))
                    {
                        s+=peek;
                        readch(br);
                    }
                    int num=Integer.parseInt(s);
                    return new NumberTok(Tag.NUM,num);
                    

	// ... gestire il caso dei numeri ... //

                } else {
                        System.err.println("Erroneous character: " 
                                + peek );
                        return null;
                }
         }
    }
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "lezione3l/lexer.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Token tok;
            do {
                tok = lex.lexical_scan(br);
                System.out.println("Scan: " + tok);
            } while (tok.tag != Tag.EOF);
            br.close();
        } catch (IOException e) {e.printStackTrace();}    
    }

}
