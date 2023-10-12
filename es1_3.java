public class es1_3 {
    public static boolean scan(String s)
    {
        int state=0;
        int i=0;
        while(state >= 0 && i<s.length)
        {
            final char ch = s.charAt(i++);
            switch (state)
            {
                case 0:
                    if(ch>='0' && ch<='9')
                    {
                        if((int)ch%2!=0)
                            state=1;
                        else
                            state=2;
                    }
                    else
                        state=-1;
                break;

                case 1:
                    if(ch>='0' && ch<='9'&& (int)ch%2==0)
                        state=2;
                    else if((ch>='L' && ch<='Z') && (ch>='l' && ch<='z'))
                        state=3;
                    else
                        state=-1;
                break;

                case 2:
                if(ch>='0' && ch<='9'&& (int)ch%2!=0)
                    state=2;
                else if((ch>='A' && ch<='K') && (ch>='a' && ch<='k'))
                    state=3;
                else
                    state=-1;
                break;

                case 3:
                    if( ch>='A' && ch<='z')
                        state=-1;
                break;
            }

        }
        return state==3;
    }
    public static void main(String [] args)
    {
        System.out.println(scan("1045495"));7
    }
}
