package com.netavin.quran.classes;


import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * Created by mehdi on 23/11/2016.
 */
public class baseClass {
    private static final Pattern DIR_SEPORATOR = Pattern.compile("/");
    Context context;

    public baseClass(Context context) {
        this.context = context;
    }

    /*public Vector prepare()
    {
        Vector vec_path=new Vector();

        //btn3
        HashSet<String> out = new HashSet<String>();
        out=getExternalMounts();
        String st[]=out.toArray(new String[out.size()]);


        //btn4
        String[] sr= getStorageDirectories();

        if(sr.length>=st.length)
        {
            for(int i=0;i<sr.length;i++)
            {
                Vector temp=new Vector();
                String path=sr[i];
                String free=getFreeSizeStorage(path);
                String total=getTotalSizeStorage(path);
                temp.add(path);
                temp.add(free);
                temp.add(total);
                vec_path.add(temp);
            }
        }
        else
        {
            for(int i=0;i<st.length;i++)
            {
                Vector temp=new Vector();
                String path=st[i];
                String free=getFreeSizeStorage(path);
                String total=getTotalSizeStorage(path);
                temp.add(path);
                temp.add(free);
                temp.add(total);
                vec_path.add(temp);
            }
        }

        Vector last_vec=new Vector();
        for(int i=0;i<vec_path.size();i++)
        {
            Vector temp=new Vector();
            temp= (Vector) vec_path.elementAt(i);
            String path=temp.elementAt(0)+"";
            String free=temp.elementAt(1)+"";
            String total=temp.elementAt(2)+"";

            File f=new File(path, "alaki10/nahj");
            f.mkdirs();
            boolean is_writable=f.canWrite();
            if(f.exists())
            {
                f.delete();
            }
            if(is_writable==false)
            {
                path=path+ "/Android/data/" + context.getPackageName() + "/Files";
                context.getExternalFilesDir("MyFileStorage");
            }
            temp=new Vector();
            temp.add(path);
            temp.add(free);
            temp.add(total);
            last_vec.add(temp);
        }


        return last_vec;
    }*/
    public Vector prepare() {
        Vector vec_path = new Vector();

        //btn3
        HashSet<String> out = new HashSet<String>();
        out = getExternalMounts();
        String st[] = out.toArray(new String[out.size()]);


        //btn4
        //getExternalStorageDirectory
        String[] sr = getStorageDirectories();

        /*if(sr.length>=st.length)
        {
            for(int i=0;i<sr.length;i++)
            {
                Vector temp=new Vector();
                String path=sr[i];
                String free=getFreeSizeStorage(path);
                String total=getTotalSizeStorage(path);
                temp.add(path);
                temp.add(free);
                temp.add(total);
                vec_path.add(temp);
            }
        }
        else
        {
            for(int i=0;i<st.length;i++)
            {
                Vector temp=new Vector();
                String path=st[i];
                String free=getFreeSizeStorage(path);
                String total=getTotalSizeStorage(path);
                temp.add(path);
                temp.add(free);
                temp.add(total);
                vec_path.add(temp);
            }
        }*/

        //btn4
        for (int i = 0; i < sr.length; i++) {
            Vector temp = new Vector();
            String path = sr[i];
            path = path.replace("mnt/media_rw", "storage");
            String free = getFreeSizeStorage(path);
            String total = getTotalSizeStorage(path);
            if (total.equals("0 B") == true) {
                continue;
            }
            temp.add(path);
            temp.add(free);
            temp.add(total);
            vec_path.add(temp);
        }

        Vector control_vec = new Vector();
        control_vec = vec_path;


        //btn3
        for (int i = 0; i < st.length; i++) {
            String path = st[i];
            //0 B
            path = path.replace("mnt/media_rw", "storage");
            String free = getFreeSizeStorage(path);
            String total = getTotalSizeStorage(path);
            if (total.equals("0 B") == true) {
                continue;
            }
            boolean is_exist = false;
            for (int j = 0; j < control_vec.size(); j++) {
                Vector temp = new Vector();

                temp = (Vector) control_vec.elementAt(j);
                String temp_path = temp.elementAt(0) + "";
                String temp_free = temp.elementAt(1) + "";
                String temp_total = temp.elementAt(2) + "";

                if (path.equals(temp_path)) {
                    is_exist = true;
                }
                if (free.equals(temp_free) && total.equals(temp_total)) {
                    is_exist = true;
                }
            }
            if (is_exist == false) {
                Vector temp = new Vector();
                temp.add(path);
                temp.add(free);
                temp.add(total);
                vec_path.add(temp);
            }
        }

        Vector last_vec = new Vector();
        /*for (int i = 0; i < vec_path.size(); i++)
        {
            Vector temp = new Vector();
            temp = (Vector) vec_path.elementAt(i);
            String path = temp.elementAt(0) + "";
            String free = temp.elementAt(1) + "";
            String total = temp.elementAt(2) + "";

            System.out.println("path="+path);
            System.out.println("free="+free);
            System.out.println("total="+total);
            System.out.println("*********************************************");
            System.out.println("*********************************************");
            System.out.println("*********************************************");

            File f = new File(path, "alaki10/nahj");
            f.mkdirs();
            boolean is_writable = f.canWrite();
            if (f.exists()) {
                f.delete();
            }
            if (is_writable == false) {
                path = path + "/Android/data/" + context.getPackageName() + "/Files";
                context.getExternalFilesDir("MyFileStorage");
            }
            temp = new Vector();
            //path=path.replace("mnt/media_rw","storage");
            temp.add(path);
            temp.add(free);
            temp.add(total);
            last_vec.add(temp);
        }*/
        for (int i = 0; i < vec_path.size(); i++)
        {
            Vector temp = new Vector();
            temp = (Vector) vec_path.elementAt(i);
            String path = temp.elementAt(0) + "";
            String free = temp.elementAt(1) + "";
            String total = temp.elementAt(2) + "";

            System.out.println("path="+path);
            System.out.println("free="+free);
            System.out.println("total="+total);
            System.out.println("*********************************************");
            System.out.println("*********************************************");
            System.out.println("*********************************************");

            File f = new File(path);
            boolean is_writable1 = f.canWrite();
            boolean is_writable2 = false;
            if (f.exists()) {
                is_writable2=true;
            }

            File ff=new File(path, "alaki10/nahj");
            ff.mkdirs();
            boolean is_writable3 = false;
            if(ff.exists())
            {
                is_writable3=true;
                ff.delete();
            }

            if(is_writable1&&is_writable2&&is_writable3)
            {
                temp = new Vector();
                temp.add(path);
                temp.add(free);
                temp.add(total);
                last_vec.add(temp);
            }


            path = path + "/Android/data/" + context.getPackageName() + "/Files";
            context.getExternalFilesDir("MyFileStorage");
            f = new File(path);
            is_writable1 = f.canWrite();
            is_writable2 = false;
            if (f.exists()) {
                is_writable2=true;
            }

            if(is_writable1&&is_writable2)
            {
                temp = new Vector();
                temp.add(path);
                temp.add(free);
                temp.add(total);
                last_vec.add(temp);
            }


        }


        return last_vec;
    }

    public HashSet<String> getExternalMounts() {
        final HashSet<String> out = new HashSet<String>();
        String reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
        String s = "";
        try {
            final Process process = new ProcessBuilder().command("mount")
                    .redirectErrorStream(true).start();
            process.waitFor();
            final InputStream is = process.getInputStream();
            final byte[] buffer = new byte[1024];
            while (is.read(buffer) != -1) {
                s = s + new String(buffer);
            }
            is.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }

        // parse output
        final String[] lines = s.split("\n");
        for (String line : lines) {
            if (!line.toLowerCase(Locale.US).contains("asec")) {
                if (line.matches(reg)) {
                    String[] parts = line.split(" ");
                    for (String part : parts) {
                        if (part.startsWith("/"))
                            if (!part.toLowerCase(Locale.US).contains("vold"))
                                out.add(part);
                    }
                }
            }
        }
        return out;
    }

    /**
     * Raturns all available SD-Cards in the system (include emulated)
     * <p/>
     * Warning: Hack! Based on Android source code of version 4.3 (API 18)
     * Because there is no standart way to get it.
     * TODO: Test on future Android versions 4.4+
     *
     * @return paths to all available SD-Cards in the system (include emulated)
     */
    public static String[] getStorageDirectories() {
        // Final set of paths
        final Set<String> rv = new HashSet<String>();
        // Primary physical SD-CARD (not emulated)
        final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
        // All Secondary SD-CARDs (all exclude primary) separated by ":"
        final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
        // Primary emulated SD-CARD
        final String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
        if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
            // Device has physical external storage; use plain paths.
            if (TextUtils.isEmpty(rawExternalStorage)) {
                // EXTERNAL_STORAGE undefined; falling back to default.
                rv.add("/storage/sdcard0");
            } else {
                rv.add(rawExternalStorage);
            }
        } else {
            // Device has emulated storage; external storage paths should have
            // userId burned into them.
            final String rawUserId;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                rawUserId = "";
            } else {
                final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                final String[] folders = DIR_SEPORATOR.split(path);
                final String lastFolder = folders[folders.length - 1];
                boolean isDigit = false;
                try {
                    Integer.valueOf(lastFolder);
                    isDigit = true;
                } catch (NumberFormatException ignored) {
                }
                rawUserId = isDigit ? lastFolder : "";
            }
            // /storage/emulated/0[1,2,...]
            if (TextUtils.isEmpty(rawUserId)) {
                rv.add(rawEmulatedStorageTarget);
            } else {
                rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
            }
        }
        // Add all secondary storages
        if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
            // All Secondary SD-CARDs splited into array
            final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
            Collections.addAll(rv, rawSecondaryStorages);
        }
        return rv.toArray(new String[rv.size()]);
    }

    public String humanReadableByteCount(long bytes, boolean si) {
        //false behtare
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public String getFreeSizeStorage(String path) {
        long bytesAvailable = 0;
        try {
            StatFs stat = new StatFs(path);
            bytesAvailable = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
        } catch (Exception e) {
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee111");
            System.out.println("e.getMessage()=" + e.getMessage());
            bytesAvailable = 0;
        }


        return humanReadableByteCount(bytesAvailable, false) + "";
    }


    public String getFreeSizeStorageByte(String path) {
        long bytesAvailable = 0;
        try {
            StatFs stat = new StatFs(path);
            bytesAvailable = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
        } catch (Exception e) {
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee111");
            System.out.println("e.getMessage()=" + e.getMessage());
            bytesAvailable = 0;
        }


        return bytesAvailable + "";
    }
    private String getTotalSizeStorage(String path) {
        long bytesAvailable = 0;
        try {
            StatFs stat = new StatFs(path);
            bytesAvailable = (long) stat.getBlockSize() * (long) stat.getBlockCount();
        } catch (Exception e) {
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee2222");
            System.out.println("e.getMessage()=" + e.getMessage());
            bytesAvailable = 0;
        }


        return humanReadableByteCount(bytesAvailable, false) + "";
    }
}

