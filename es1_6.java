public class es1_6 {
    public static boolean scan(String s)
    {
        int state=0;
        int i=0;
        while(state>=0 && i<s.length())
        {
            final char c = s.charAt(i++);
            switch(state)
            {
                case 0:
                    if(c=='/')
                        state=1;
                    else if(c=='a')
                        state=0;
                    else
                        state=-1;
                break;
                case 1:
                    if(c=='*')
                        state=2;
                    else
                        state=-1;
                break;
                case 2:
                    if(c=='*')
                        state=3;
                    else if(c=='a' || c=='/')
                        state=2;
                    else
                        state=-1;
                break;
                case 3:
                    if(c=='/')
                        state=4;
                    else if(c=='a' || c=='*')
                        state=3;
                    else
                        state=-1;
                break;
                case 4:
                    if(c!='a')
                        state=-1;
                break;
            }
        }
        return state==4;
    }
    public static void main(String[] args) {
        System.out.println(scan("aaaa/*a*//*a*/aaaa"));
    }
}
