package lezione1l;
public class es1_4 {
    public static boolean scan(String s)
    {
        int state=0;
        int i=0;
        
        while(state>=0 && i<s.length())
        {
            final char c=s.charAt(i++);
            switch(state)
            {
                case 0:
                    if(c!='+' && c!='-' && c!='.' && (c<'0' || c>'9'))
                        state=-1;
                    else if(c=='.')
                        state=3;
                    else if(c=='+' || c=='-')
                        state=1;
                    else if(c>='0' && c<='9')
                        state=2;
                break;
                    
                case 1:
                    if(c!='.' && (c<'0' || c>'9'))
                        state=-1;
                    else if(c=='.')
                        state=3;
                    else if(c>='0' && c<='9')
                        state=2;
                break;

                case 2:
                    if(c!='e' && c!='.' && (c<'0' || c>'9'))
                        state=-1;
                    else if(c=='e')
                        state=5;
                    else if(c=='.')
                        state=3;
                break;

                case 3:
                    if(c<'0' || c>'9')
                        state=-1;
                    else if(c>='0' && c<='9')
                        state=4;
                break;

                case 4:
                    if(c!='e' && (c<'0' || c>'9'))
                        state=-1;
                    else if(c=='e')
                        state=5;
                break;

                case 5:
                    if(c!='+' && c!='-' && (c<'0' || c>'9'))
                        state=-1;
                    else
                        state=6;
                break;

                case 6:
                    if(c<'0' || c>'9')
                        state=-1;
                break;
            }
        }
        return state==2 || state==4 || state==6;
    }
    public static void main(String [] args)
    {
        System.out.println(scan("++3"));
    }
}
