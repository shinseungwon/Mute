package com.shinsw.seungwon.mute;

/**
 * Created by seungwon on 2016-05-04.
 */
import java.io.InputStream;
import java.net.URL;
import java.util.Vector;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import android.os.AsyncTask;

public class GetRss extends AsyncTask<Void, Void, Void> {
    Vector<String> titlevec = new Vector<>();
    Vector<String> artistvec = new Vector<>();
    String uri = "http://www.billboard.com/rss/charts/hot-100";
    URL url;
    String tagname;
    String title="";
    String artist="";
    Boolean flag = false;
    @Override
    protected Void doInBackground(Void... params) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            //factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            url = new URL(uri);
            InputStream in = url.openStream();
            xpp.setInput(in, "utf-8");
            int eventType = xpp.getEventType();
            while(true){
                eventType = xpp.getEventType();
                if(eventType == XmlPullParser.START_TAG&&xpp.getName().equals("item"))
                    break;
                else
                    xpp.next();
            }
            while(eventType != XmlPullParser.END_DOCUMENT ) {
                if(eventType == XmlPullParser.START_TAG) {
                    tagname = xpp.getName();
                } else if(eventType == XmlPullParser.TEXT) {
                    if(tagname.equals("title")&&title.equals("")) {
                        //Log.i(xpp.getText(),"check");
                        title += xpp.getText();
                    }
                    else if(tagname.equals("artist")&&artist.equals("")) {
                        artist += xpp.getText();
                        //Log.i("//"+xpp.getText()+"//","start");
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    tagname = xpp.getName();
                    //Log.i("rss",title+"//"+artist);
                    if(tagname.equals("item")) {
                        if(!title.equals("")&&!artist.equals("")) {
                            titlevec.add(title);
                            artistvec.add(artist);
                            title="";
                            artist="";
                        }
                    }
                }
                eventType = xpp.next();
            }//Log.i(titlevec.size()+"//"+artistvec.size(),"size");
            flag = true;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
