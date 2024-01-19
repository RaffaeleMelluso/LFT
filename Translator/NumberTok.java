
public class NumberTok extends Token {
	public int lexeme = 0;
    public NumberTok(int tag, int i) { super(tag); lexeme=i; }
    public String toString() { return "<" + tag + ", " + lexeme + ">"; }
}
