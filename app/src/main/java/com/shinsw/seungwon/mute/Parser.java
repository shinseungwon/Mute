package com.shinsw.seungwon.mute;

/**
 * Created by seungwon on 2016-03-29.
 */
public class Parser {
    Parser(){
    }

    static String trimmer(String str){
        byte x[]=str.getBytes();int p=0;String k;
        for(int i=0;i<str.length();i++){
            //all lowercase , delete inside () ,
            if((int)x[i]==40)
                p=i;
            if((int)x[i]>64 && (int)x[i]<91)
                x[i] = (byte)((int) x[i] + 32);
        }
        if(p==0)
            k=new String(x,0,x.length);
        else
            k=new String(x,0,p);
        return k;
    }

    static boolean compare(String str1,String str2){
        String one;
        String two;
        int leng;
        int point=0;
        one=trimmer(str1);
        two=trimmer(str2);
        byte[] a=one.getBytes();
        byte[] b=two.getBytes();
        double result;
        if(a.length>b.length) {
            leng = a.length - b.length;

            if(leng<3) {
                for (int i = 0; i <b.length;i++){
                    if(a[i]!=b[i])
                        point++;
                }

            }else
                return false;

            result=(double)(a.length-point)/(double)(a.length);

                return result>0.8;
        }
        else {
            leng = b.length - a.length;

            if(leng<3) {
                for (int i = 0; i <a.length;i++){
                    if(a[i]!=b[i])
                        point++;
                }

            }else
                return false;
        }
        result=(double)(b.length-point)/(double)(b.length);
        return result>0.8;
    }

    static String billboardTitleParser(String x){
        byte[] b=x.getBytes();
        int start=0;
        for(int i=0;i<b.length;i++){
            if((int)b[i]==58) {
                //Log.i(start+" ","start"+(int)b[i]);
                start = i;
                break;
            }
        }
        byte[] c=new byte[b.length-start-2];
        for(int i=0;i<c.length;i++)
            c[i]=b[start+2+i];

        return new String(c,0,c.length);

    }

    public static int[] valueParser(String s){
        int[] result=new int[4];
        byte[] trim=s.getBytes();
        String temp="";
        int count=0;
        for(int i=0;i<trim.length;i++){
            if(trim[i]=='@')
                break;
            if(trim[i]=='/'){
                result[count]=Integer.parseInt(temp);
                count++;
                temp="";
                continue;
            }
            temp=temp+(char)trim[i];
        }
        if(result[0]==0&&result[1]==0&&result[2]==0&&result[3]==0)
            return null;
        else
            return result;
    }
    public static String frontParser(String s){
        byte[] k=s.getBytes();
        int count=0;
        for(int i=0;i<k.length;i++){
            if(!((int)k[i]>47&&(int)k[i]<58)&&!((int)k[i]>64&&(int)k[i]<91)&&!((int)k[i]>96&&(int)k[i]<123)&&(int)k[i]!=32){
                count++;
            }else break;
        }
        byte[] get=new byte[k.length-count];
        for(int i=0;i<get.length;i++)
            get[i]=k[count+i];
        return new String(get,0,get.length);
    }

    public static String spaceParser(String s){
        byte[] k=s.getBytes();
        for(int i=0;i<k.length;i++){
            if(k[i]==' ')
                k[i]='_';
            if(k[i]=='#')
                k[i]='-';
        }
        return new String(k,0,k.length);
    }
}
